package com.example.eventmap.presentation.composables

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.eventmap.data.User
import com.example.eventmap.presentation.utils.MarkersHashMap.allMarkers
import com.example.eventmap.presentation.utils.rememberMapViewLifecycle
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.utils.Constants.EVENTS_DB
import com.example.eventmap.utils.LocationUtil.hasLocationPermissions
import com.example.eventmap.utils.LocationUtil.isGpsEnabled
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.model.markerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
@SuppressLint("MissingPermission")
@Composable
fun MapView(navController: NavController, viewModel: UsersViewModel) {
    val context = LocalContext.current
    val mapView = rememberMapViewLifecycle()
    val allUsersWithoutCurrent = viewModel.allUsers.value
    val allIconsWithoutCurrent = viewModel.allPicturesAsThumbnails.value
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView({ mapView }) {
            CoroutineScope(Dispatchers.Main).launch {
                val map = mapView.awaitMap()
                //map my location trazi permisije bez njih baca exception
                if(hasLocationPermissions(context)) {
                    map.isMyLocationEnabled = true
                    map.setOnMyLocationButtonClickListener {
                        //ako je gps iskljucen iskljuci default ponasanje na button click i prikazi toast
                        if (!isGpsEnabled(context)) {
                            Toast.makeText(context, "Your gps is turned off!", Toast.LENGTH_SHORT)
                                .show()
                        }
                        false
                    }
                }else{
                    Toast.makeText(context, "No permission to see your location!", Toast.LENGTH_SHORT).show()
                }
                markEvents(map)
                markUsers(map, allUsersWithoutCurrent, allIconsWithoutCurrent)
            }
        }
    }
}

fun markUsers(map: GoogleMap,users: List<User>?, icons: HashMap<String,Bitmap>?){
    users?.let{
        for(user in it){
            //kreiraj hash map za markere
            if(user.location != null) {
                if(allMarkers[user.userId] == null){
                    allMarkers[user.userId] = map.addMarker(markerOptions {
                        this.title(
                            if (user.username.isNullOrEmpty()) {
                                user.email
                            } else {
                                user.username
                            }
                        )
                        this.position(LatLng(user.location.latitude, user.location.longitude))
                        icons?.let{
                            if(icons[user.userId] != null){
                                this.icon(BitmapDescriptorFactory.fromBitmap(icons[user.userId]))
                            }else{
                                this.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                            }
                        }
                    })
                }else{
                    allMarkers[user.userId]!!.position = LatLng(user.location.latitude, user.location.longitude)
                }
                //ako je kreiran marker a lokacija je postala null (ne prati se vise) brisi marker
            }else {
                //brisi iz hashmape i google mape
                allMarkers[user.userId]?.remove()
                allMarkers.remove(user.userId)
            }
        }
    }
}

fun markEvents(map: GoogleMap){
    FirebaseFirestore.getInstance().collection(EVENTS_DB).get().addOnSuccessListener {
        for(document in it.documents){
            val title = document.data?.get("title").toString()
            val desc = document.data?.get("description").toString()
            val location = document.getGeoPoint("location")
            if(location!=null) {
                map.addMarker(markerOptions {
                    this.title(title)
                    this.snippet(desc)
                    this.position(LatLng(location.latitude, location.longitude))
                    this.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                })
            }
        }
    }
}
