package com.example.eventmap.data

import com.google.android.libraries.maps.model.LatLng

data class User(
    val email: String,
    val username: String = "",
    val numOfFriends: Int=0,
    val numOfEvents: Int=0,
    val points: Int=0,
    val password: String,
    var location: LatLng = LatLng(0.0,0.0)
)



