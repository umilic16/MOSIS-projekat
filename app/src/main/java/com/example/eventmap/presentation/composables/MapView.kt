package com.example.eventmap.presentation.composables

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.eventmap.components.CustomTextField
import com.example.eventmap.data.Event
import com.example.eventmap.data.User
import com.example.eventmap.presentation.theme.ui.*
import com.example.eventmap.presentation.utils.MarkersHashMap.allMarkers
import com.example.eventmap.presentation.utils.rememberMapViewLifecycle
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.utils.Constants.EVENTS_DB
import com.example.eventmap.utils.LocationUtil.hasLocationPermissions
import com.example.eventmap.utils.LocationUtil.isGpsEnabled
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.Marker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.model.markerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.N)
@SuppressLint("MissingPermission", "ResourceType")
@Composable
fun MapView(navController: NavController, viewModel: UsersViewModel) {
    val context = LocalContext.current
    val mapView = rememberMapViewLifecycle()
    val searchInput = remember { mutableStateOf("") }
    val usersSlider = remember { mutableStateOf(0f) }
    val eventsSlider = remember { mutableStateOf(0f) }
    val filterOptionsHidden = remember { mutableStateOf(true)}
    val allUsersWithoutCurrent = viewModel.allUsers.value
    val markerHashMap = remember { mutableMapOf<String,Marker>() }
    val filter = remember { mutableStateOf("")}
    lateinit var map: GoogleMap
    //val allIconsWithoutCurrent = viewModel.allPicturesAsThumbnails.value
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView({ mapView }) {
            CoroutineScope(Dispatchers.Main).launch {
                map = mapView.awaitMap()
                //map my location trazi permisije bez njih baca exception
                if(hasLocationPermissions(context)) {
                    map.isMyLocationEnabled = true
                    map.setPadding(0,200,20,0)
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
                markEvents(map, markerHashMap, filter.value)
                markUsers(map, allUsersWithoutCurrent)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingMedium)
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
            ) {
                CustomTextField(
                    text = searchInput.value,
                    onValueChange = {
                        searchInput.value = it
                        if(it.isNullOrEmpty()){
                            filter.value = ""
                        }
                    },
                    hint = "Filter events by name",
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
                Button(
                    modifier = Modifier.height(50.dp),
                    onClick = {
                        //search
                        /*for(item in markerHashMap.values){
                            Log.d("Marker_Debug","Removing: $item")
                            item.remove()
                        }*/
                        filter.value = searchInput.value
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = DefaultWhite,
                    )
                }
            }
            /*Spacer(modifier = Modifier.padding(PaddingSmall))
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    //.fillMaxWidth()
                    .clip(RoundedCornerShape(5.dp))
                    .background(DefaultWhite)
                    .padding(PaddingMedium, 0.dp)

            ) {
                Text(
                    text = "Friends",
                    fontWeight = FontWeight.W500,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = if (usersSlider.value == 0f) {
                        DarkBlue
                    } else {
                        DefaultBlue
                    },
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                Slider(
                    value = usersSlider.value,
                    onValueChange = { usersSlider.value = it },
                    valueRange = 0f..1f,
                    onValueChangeFinished = {
                        if(usersSlider.value<0.5){
                            usersSlider.value = 0f
                        }else{
                            usersSlider.value = 1f
                        }
                    },
                    steps = 0,
                    modifier = Modifier.fillMaxWidth(0.2f)
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                Text(text = "Everyone",
                    fontWeight = FontWeight.W500,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = if(usersSlider.value==1f){
                    DarkBlue
                }else{
                    DefaultBlue
                })
                Spacer(modifier = Modifier.padding(PaddingSmall))
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    //.fillMaxWidth()
                    .clip(RoundedCornerShape(5.dp))
                    .background(DefaultWhite)
                    .padding(PaddingMedium, 0.dp)

            ) {
                Text(
                    text = "Friends",
                    fontWeight = FontWeight.W500,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = if (usersSlider.value == 0f) {
                        DarkBlue
                    } else {
                        DefaultBlue
                    },
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                Slider(
                    value = usersSlider.value,
                    onValueChange = { usersSlider.value = it },
                    valueRange = 0f..1f,
                    onValueChangeFinished = {
                        if(usersSlider.value<0.5){
                            usersSlider.value = 0f
                        }else{
                            usersSlider.value = 1f
                        }
                    },
                    steps = 0,
                    modifier = Modifier.fillMaxWidth(0.2f)
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                Text(text = "Everyone",
                    fontWeight = FontWeight.W500,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = if(usersSlider.value==1f){
                    DarkBlue
                }else{
                    DefaultBlue
                })
            }*/
        }
    }
}

fun markUsers(map: GoogleMap,users: List<User>?){
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
                        this.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
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

fun markEvents(map: GoogleMap,hashMap: MutableMap<String, Marker> ,filter: String = "") {
    FirebaseFirestore.getInstance().collection(EVENTS_DB).get().addOnSuccessListener { snapshot ->
        for (document in snapshot.documents) {
            val event = document.toObject<Event>()
            event?.location?.let {
                //Log.d("Marker_Debug", "Contain test ${event.title} containts ${filter} = ${event.title.contains(filter)}")
                if (event.title.lowercase().contains(filter.lowercase())) {
                    if(hashMap[document.id] == null) {
                        hashMap[document.id] = map.addMarker(markerOptions {
                            this.title(event.title)
                            this.snippet(event.description)
                            this.position(LatLng(it.latitude, it.longitude))
                            this.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        })
                    }
                    else{
                        //ako se promeni lokacija eventa iako nema ta funkcionalnost :) (npr u bazi direkt)
                        hashMap[document.id]!!.position = LatLng(event.location.latitude, event.location.longitude)
                    }
                } else{
                    hashMap[document.id]?.remove()
                    hashMap.remove(document.id)

                }
            }
        }
    }
}
