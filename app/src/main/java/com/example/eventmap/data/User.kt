package com.example.eventmap.data

data class User(
    val userId: String = "",
    val email: String = "",
    val username: String? = "",
    val numOfFriends: Int = 0,
    val numOfEvents: Int = 0,
    val points: Int=0,
    val password: String = "",
    val friends: List<String>? = listOf<String>(),
    val receivedRequests: List<String>? = listOf<String>(),
    val sentRequests: List<String>? = listOf<String>(),
    val token: String = ""
    //var picture: Boolean = false
    //val location: GeoPoint? = GeoPoint(0.0,0.0)
)



