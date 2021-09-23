package com.example.eventmap.presentation.composables

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.eventmap.presentation.MainActivityViewModel
import com.example.eventmap.presentation.theme.ui.DefaultBlue
import com.example.eventmap.presentation.theme.ui.PaddingLarge
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.android.libraries.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import androidx.activity.viewModels
import androidx.compose.material.Surface
import androidx.compose.runtime.livedata.observeAsState
import com.example.eventmap.presentation.theme.ui.DarkBlue
import com.example.eventmap.presentation.utils.rememberMapViewLifecycle

@Composable
fun MapView(navController: NavController, viewModel: MainActivityViewModel) {
    /*Box(modifier = Modifier
        .fillMaxSize()
        .padding(PaddingLarge), contentAlignment = Alignment.Center) {
    }*/
    val newDestinationSelected = viewModel.isNewLocationSelected.observeAsState(false)

    val destination = if (newDestinationSelected.value) LatLng(
        viewModel.selectedLat.value,
        viewModel.selectedLng.value
    ) else LatLng(43.32472, 21.90333)

    Surface(color = DarkBlue) {

        MapViewContainer(
            viewModel = viewModel,
            destination = destination
        )

    }
}

@Composable
fun MapViewContainer(viewModel: MainActivityViewModel, destination: LatLng) {

    val mapView = rememberMapViewLifecycle()
    //Log.d("MAPDEBUG","MAPVIEWCONTAINER ENTER")
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView({ mapView }) {
            CoroutineScope(Dispatchers.Main).launch {
                val map = mapView.awaitMap()
               // Log.d("MAPDEBUG",map.toString())
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 6f))

                /*val markerOptions =
                    MarkerOptions().title("Start position").position(viewModel.pickUp)
                map.addMarker(markerOptions)

                val markerOptionsDestination =
                    MarkerOptions().title("Destination").position(destination)

                map.addMarker(markerOptionsDestination)

                map.addPolyline(
                    PolylineOptions().clickable(true).add(viewModel.pickUp, destination).width(4f)
                        .geodesic(true)
                )*/

                map.setOnMapLongClickListener { c ->
                    viewModel.getSelectedLocation(LatLng(c.latitude, c.longitude))
                    viewModel.updateSelectedLocation(true)
                }
            }
        }
    }
   // Log.d("MAPDEBUG","MAPVIEWCONTAINER EXIT")
}