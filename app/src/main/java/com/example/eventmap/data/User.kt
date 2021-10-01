package com.example.eventmap.data

import com.google.android.libraries.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

data class User(
    val email: String = "",
    val username: String? = "",
    val numOfFriends: Int = 0,
    val numOfEvents: Int = 0,
    val points: Int=0,
    val password: String = "",
    //val location: GeoPoint? = GeoPoint(0.0,0.0)
)



