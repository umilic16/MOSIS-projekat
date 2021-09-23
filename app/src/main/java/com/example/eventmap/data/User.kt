package com.example.eventmap.data

data class User(
    val email: String,
    val username: String = "",
    val numOfFriends: Int=0,
    val numOfEvents: Int=0,
    val points: Int=0,
    val password: String
)



