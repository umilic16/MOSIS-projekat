package com.example.eventmap.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.eventmap.R
import com.example.eventmap.presentation.MainActivity
import com.example.eventmap.presentation.MainActivity.Companion.fusedLocationProviderClient
import com.example.eventmap.presentation.MainActivity.Companion.gpsStatusListener
import com.example.eventmap.presentation.MainActivity.Companion.locationPermissionStatusListener
import com.example.eventmap.presentation.utils.GpsStatusListener
import com.example.eventmap.presentation.utils.LocationPermissionStatusListener
import com.example.eventmap.utils.Constants.ACTION_PAUSE_SERVICE
import com.example.eventmap.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.eventmap.utils.Constants.ACTION_STOP_SERVICE
import com.example.eventmap.utils.Constants.FASTEST_LOCATION_INTERVAL
import com.example.eventmap.utils.Constants.LOCATION_UPDATE_INTERVAL
import com.example.eventmap.utils.Constants.NOTIFICATION_ID
import com.example.eventmap.utils.LocationUtil.hasLocationPermissions
import com.example.eventmap.utils.checkIfCanTrackLocation
import com.example.eventmap.utils.startOrResumeTrackingService
import com.example.eventmap.utils.stopTrackingService
import com.example.eventmap.utils.updateLocation
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.GeoPoint

private const val CHANNEL_ID = "tracking_channel"

class TrackingService(): LifecycleService(){

    private var isFirstRun = true
    private var serviceKilled = false
    private lateinit var curNotification: NotificationCompat.Builder

    companion object{
        val isTracking = MutableLiveData<Boolean>()
        val location = MutableLiveData<GeoPoint>()
    }

    private fun postInitialValues(){
        isTracking.postValue(false)
        location.postValue(null)
    }

    override fun onCreate() {
        Log.d("Gps_Debug", "Servis kreiran")
        super.onCreate()
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
        })
        gpsStatusListener.observe(this,{
            if(!it){
                stopTrackingService(this)
            }
        })
        //location permission observer
        locationPermissionStatusListener.observe(this,{
            if(!it){
                stopTrackingService(this)
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let{
            when(it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
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
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager = notificationManager)
        }
        isTracking.postValue(true)
        startForeground(NOTIFICATION_ID, curNotification.build())
        if(!serviceKilled) {
            val notification = curNotification
            notificationManager.notify(NOTIFICATION_ID, notification.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName="Tracking"
        val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

    private fun killService() {
        serviceKilled = true
        isFirstRun = true
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
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //remove previous action
        curNotification.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotification, ArrayList<NotificationCompat.Action>())
        }
        //nova notifikacija u zavisnosti dal je resume il pause
        if(!serviceKilled) {
            curNotification = getDefaultNotificationBuilder()
                .addAction(R.drawable.ic_baseline_pause_blue_24, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, curNotification.build())
        }
    }

    private fun getDefaultNotificationBuilder() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.logo)
        .setContentTitle("Event map")
        .setContentText("Tracking service is running")
        .setContentIntent(getMainActivityPendingIntent())

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java),
        FLAG_UPDATE_CURRENT
    )
}