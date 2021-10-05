package com.example.eventmap.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import com.example.eventmap.data.User
import com.example.eventmap.services.TrackingService
import com.google.firebase.auth.FirebaseAuth

fun checkIfFriends(currentUser: User, checkingUserId: String): Boolean{
    if(currentUser.friends != null){
        return currentUser.friends.contains(checkingUserId)
    }
    return false
}

fun checkIfRequestSent(currentUser: User, checkingUserId: String): Boolean{
    if(currentUser.sentRequests != null){
        return currentUser.sentRequests.contains(checkingUserId)
    }
    return false
}

fun checkIfRequestReceived(currentUser: User, checkingUserId: String): Boolean{
    if(currentUser.receivedRequests != null){
        return currentUser.receivedRequests.contains(checkingUserId)
    }
    return false
}

/*fun addRandomUsers(numOfUsers: Int, lengthOfEmail: Int){
    for (i in 0..numOfUsers){
        val email = "${getRandomString(lengthOfEmail)}@gmail.com"
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, "test123")
            .addOnSuccessListener {
                saveUser(
                    User(email = email, username = "test123")
                )
            }
    }
}

fun getRandomString(length: Int) : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

*/