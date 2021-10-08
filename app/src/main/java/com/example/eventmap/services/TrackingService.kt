package com.example.eventmap.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.media.RingtoneManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.eventmap.R
import com.example.eventmap.presentation.MainActivity
import com.example.eventmap.utils.Constants.ACTION_PAUSE_SERVICE
import com.example.eventmap.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.eventmap.utils.Constants.ACTION_STOP_SERVICE
import com.example.eventmap.utils.Constants.FASTEST_LOCATION_INTERVAL
import com.example.eventmap.utils.Constants.LOCATION_UPDATE_INTERVAL
import com.example.eventmap.utils.Constants.NEARBY_EVENTS_NOTIFICATION_ID
import com.example.eventmap.utils.Constants.NEARBY_USERS_NOTIFICATION_ID
import com.example.eventmap.utils.Constants.TRACKING_NOTIFICATION_ID
import com.example.eventmap.utils.DbAdapter.getNearbyEvents
import com.example.eventmap.utils.DbAdapter.getNearbyUsers
import com.example.eventmap.utils.DbAdapter.updateLocation
import com.example.eventmap.utils.LocationUtil.fusedLocationProviderClient
import com.example.eventmap.utils.LocationUtil.gpsStatusListener
import com.example.eventmap.utils.LocationUtil.hasLocationPermissions
import com.example.eventmap.utils.LocationUtil.locationPermissionStatusListener
import com.example.eventmap.utils.TrackingCommands.stopTrackingService
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TRACKING_CHANNEL_ID = "tracking_channel"
private const val NEARBY_CHANNEL_ID = "nearby_channel"

class TrackingService() : LifecycleService(){

    private var isFirstRun = true
    private var serviceKilled = false
    private lateinit var curNotification: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager
    //private lateinit var nearbyUsersNotification: NotificationCompat.Builder
    //private lateinit var nearbyEventsNotification: NotificationCompat.Builder

    companion object{
        val isTracking = MutableLiveData<Boolean>()
        val location = MutableLiveData<GeoPoint>()
        var isServiceRunning = false
    }

    private fun postInitialValues(){
        isTracking.postValue(false)
        location.postValue(null)
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //Log.d("Gps_Debug", "Servis kreiran")
        //notification "na default"
        curNotification = getDefaultNotificationBuilder()
        postInitialValues()
        isTracking.observe(this, {
            updateNotificationTrackingState(it)
            updateLocationTracking(it)
        })
        location.observe(this, {
            //upisi promene u bazu
            updateLocation(it)
            it?.let{
                checkNearby(it)
            }
        })
        //gps status observer
        gpsStatusListener.observe(this,{
            if(!it && isServiceRunning){
                stopTrackingService(this)
            }
        })
        //location permission observer
        locationPermissionStatusListener.observe(this,{
            if(!it && isServiceRunning){
                stopTrackingService(this)
            }
        })
    }

    private fun checkNearby(location: GeoPoint){
        val loc = Location("").apply {
            this.latitude=location.latitude
            this.longitude=location.longitude
        }
        CoroutineScope(Dispatchers.IO).launch {
            getNearbyUsers(loc).also { users ->
                //Log.d("Nearby_Debug", "Received list from db: $users")
                if (users.isNotEmpty()) {
                    var message = ""
                    users.forEachIndexed{ index, user ->
                        message += user.username ?: user.email
                        if(index < users.size-1){
                            message += ", "
                        }
                    }
                    val notification = getNearbyNotificationBuilder()
                        .setContentTitle("Detected users nearby")
                        .setContentText(message)
                    notificationManager.notify(NEARBY_USERS_NOTIFICATION_ID, notification.build())
                }
            }
            getNearbyEvents(loc).also { events ->
                //Log.d("Nearby_Debug", "Received list from db: $users")
                if (events.isNotEmpty()) {
                    var message = ""
                    events.forEachIndexed{ index, event ->
                        message += event.title
                        if(index < events.size-1){
                            message += ", "
                        }
                    }
                    val notification = getNearbyNotificationBuilder()
                        .setContentTitle("Detected events nearby")
                        .setContentText(message)
                    notificationManager.notify(NEARBY_EVENTS_NOTIFICATION_ID, notification.build())
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let{
            when(it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    isServiceRunning = true
                    if(isFirstRun){
                        startForegroundService()
                        isFirstRun = false
                        serviceKilled = false
                    }else{
                        isTracking.postValue(true)
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    killService()
                }
                else -> {}
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean){
        Log.d("Service_Debug", "Update location tracking: isTracking=$isTracking")
        if(isTracking) {
            if (hasLocationPermissions(this)){
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object: LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if(isTracking.value!!){
                result.locations.let{ locations->
                    for(newLocation in locations){
                        location.postValue(GeoPoint(newLocation.latitude,newLocation.longitude))
                        //Log.d("Service_Debug", location.value.toString())
                    }
                }
            }
        }
    }

    private fun startForegroundService(){
        //val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager = notificationManager)
            createChannelForNearbyNotifications(notificationManager = notificationManager)
        }
        isTracking.postValue(true)
        startForeground(TRACKING_NOTIFICATION_ID, curNotification.build())
        if(!serviceKilled) {
            val notification = curNotification
            notificationManager.notify(TRACKING_NOTIFICATION_ID, notification.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName="Tracking"
        val channel = NotificationChannel(TRACKING_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannelForNearbyNotifications(notificationManager: NotificationManager){
        val channelName="Nearby"
        val channel = NotificationChannel(NEARBY_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), Notification.AUDIO_ATTRIBUTES_DEFAULT)
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun killService() {
        serviceKilled = true
        isFirstRun = true
        isServiceRunning = false
        pauseService()
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }

    private fun pauseService() {
        isTracking.postValue(false)
        location.postValue(null)
    }

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }
        //val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //remove previous action
        curNotification.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotification, ArrayList<NotificationCompat.Action>())
        }
        //nova notifikacija u zavisnosti dal je resume il pause
        if(!serviceKilled) {
            curNotification = getDefaultNotificationBuilder()
                .addAction(R.drawable.ic_baseline_pause_blue_24, notificationActionText, pendingIntent)
            notificationManager.notify(TRACKING_NOTIFICATION_ID, curNotification.build())
        }
    }

    private fun getDefaultNotificationBuilder() = NotificationCompat.Builder(this, TRACKING_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.logo)
        .setContentTitle("Event map")
        .setContentText("Tracking service is running")
        .setContentIntent(getMainActivityPendingIntent())

    private fun getNearbyNotificationBuilder() = NotificationCompat.Builder(this, NEARBY_CHANNEL_ID)
        .setSmallIcon(R.drawable.logo)
        .setAutoCancel(true)
        .setOnlyAlertOnce(true)
        .setContentIntent(getMainActivityPendingIntent())

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java),
        FLAG_UPDATE_CURRENT
    )
}