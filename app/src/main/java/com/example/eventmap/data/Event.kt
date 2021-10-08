package com.example.eventmap.data

import android.location.Location
import com.google.android.libraries.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

data class Event(
    val userId: String = "",
    val title: String = "",
    val description: String? = "",
    val location: GeoPoint? = null,
    val liked: List<String> = listOf<String>()
//slika mozda
)
