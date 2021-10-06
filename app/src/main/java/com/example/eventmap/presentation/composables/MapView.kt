package com.example.eventmap.presentation.composables

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.eventmap.presentation.MainActivity.Companion.fusedLocationProviderClient
import com.example.eventmap.presentation.utils.rememberMapViewLifecycle
import com.example.eventmap.services.TrackingService.Companion.isTracking
import com.example.eventmap.utils.LocationUtil.hasLocationPermissions
import com.example.eventmap.utils.LocationUtil.isGpsEnabled
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.model.markerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun MapView(navController: NavController) {
    val context = LocalContext.current
    val mapView = rememberMapViewLifecycle()
    if(isGpsEnabled(context)){

    }
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
                            Toast.makeText(context, "Your gps is turned off!", Toast.LENGTH_SHORT).show()
                            true
                        }
                        false
                    }
                }else{
                    Toast.makeText(context, "No permission to see your location!", Toast.LENGTH_SHORT).show()
                }
                //tracking service is running
                if(isTracking.value!!){
                    fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                        map.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    it.latitude,
                                    it.longitude
                                ), 12f
                            )
                        )
                    }
                    //markUsers(map, viewModel)
                    //markEvents(map,viewModel)
                    //
                }
            }
        }
    }
    //
}

/*fun markUsers(map: GoogleMap, viewModel: MainActivityViewModel){
    FirebaseFirestore.getInstance().collection("Users").get().addOnSuccessListener {
        for(document in it.documents){
           // Log.d("MARKERSDOC", document.toString())
            val email = document.data?.get("email")
            val location = document.getGeoPoint("location")
            if(location!=null) {
                //Log.d("MARKERSLOC", location.toString())
                //Log.d("MARKERSLL", "$email LOCATION: $location.latitude, $location.longitude")
                map.addMarker(markerOptions {
                    this.title("User: $email")
                    this.position(LatLng(location.latitude, location.longitude))
                })
            }
        }
    }
}*/

fun markEvents(map: GoogleMap){
    FirebaseFirestore.getInstance().collection("Events").get().addOnSuccessListener {
        for(document in it.documents){
            val title = document.data?.get("title")
            val desc = document.data?.get("description")
            val location = document.getGeoPoint("location")
            if(location!=null) {
                map.addMarker(markerOptions {
                    this.title("$title: $desc")
                    this.position(LatLng(location.latitude, location.longitude))
                    this.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                })
            }
        }
    }
}
