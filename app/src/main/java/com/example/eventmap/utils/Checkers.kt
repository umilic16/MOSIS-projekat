package com.example.eventmap.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import com.example.eventmap.data.User
import com.example.eventmap.services.TrackingService
import com.example.eventmap.utils.LocationUtil.hasLocationPermissions
import com.example.eventmap.utils.LocationUtil.isGpsEnabled
import com.google.firebase.auth.FirebaseAuth

fun checkIfCanTrackLocation(context: Context) = hasLocationPermissions(context) && isGpsEnabled(context)

fun checkIfFriends(currentUser: User, checkingUserId: String) = currentUser.friends?.contains(checkingUserId) as Boolean

fun checkIfRequestSent(currentUser: User, checkingUserId: String) = currentUser.sentRequests?.contains(checkingUserId) as Boolean

fun checkIfRequestReceived(currentUser: User, checkingUserId: String) = currentUser.receivedRequests?.contains(checkingUserId) as Boolean

/*fun checkIfRequestSent(currentUser: User, checkingUserId: String): Boolean{
    if(currentUser.sentRequests != null){
        return currentUser.sentRequests.contains(checkingUserId)
    }
    return false
}*/
/*fun checkIfRequestReceived(currentUser: User, checkingUserId: String): Boolean{
    if(currentUser.receivedRequests != null){
        return currentUser.receivedRequests.contains(checkingUserId)
    }
    return false
}*/
/*fun checkIfFriends(currentUser: User, checkingUserId: String): Boolean{
    if(currentUser.friends != null){
        return currentUser.friends.contains(checkingUserId)
    }
    return false
}*/