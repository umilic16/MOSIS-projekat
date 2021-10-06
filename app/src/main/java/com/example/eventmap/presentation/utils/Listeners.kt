package com.example.eventmap.presentation.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.eventmap.data.User
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.utils.Constants
import com.example.eventmap.utils.LocationUtil.hasLocationPermissions
import com.example.eventmap.utils.LocationUtil.isGpsEnabled
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

fun addUsersListener(viewModel: UsersViewModel){
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val docRef = FirebaseFirestore.getInstance().collection(Constants.USERS_DB)
    viewModel.setListenerRegistration(docRef.addSnapshotListener { snapshot, e ->
        if (e != null) {
            Log.d("Listener_Debug", "Listen failed.", e)
            return@addSnapshotListener
        }
        if (snapshot != null) {
            //Log.d("Listener_Debug", "Current data: ${snapshot.documents}")
            val users = mutableListOf<User>()
            for (document in snapshot.documents) {
                val user = document.toObject<User>()!!
                if(user.userId==userId){
                    viewModel.setCurrentUser(user)
                }
                users.add(user)
            }
            //Log.d("Add_Debug", "$users")
            viewModel.setAllUsers(users)
        } else {
            Log.d("Listener_Debug", "Current data: null")
        }
    })
}

class GpsStatusListener(private val context: Context) : LiveData<Boolean>() {

    private val gpsSwitchStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) = checkGpsAndReact()
    }

    override fun onInactive() = unregisterReceiver()

    override fun onActive() {
        registerReceiver()
        checkGpsAndReact()
    }
    private fun checkGpsAndReact() = if (isGpsEnabled(context)) {
        postValue(true)
    } else {
        postValue(false)
    }

    private fun registerReceiver() = context.registerReceiver(gpsSwitchStateReceiver,
        IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
    )

    private fun unregisterReceiver() = context.unregisterReceiver(gpsSwitchStateReceiver)
}

class PermissionStatusListener(private val context: Context) : LiveData<Boolean>() {

    override fun onActive() = handlePermissionCheck()

    private fun handlePermissionCheck() {
        if (hasLocationPermissions(context))
            postValue(true)
        else
            postValue(false)
    }
}


/*fun addLocationListener(viewModel: UsersViewModel){
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val docRef = FirebaseFirestore.getInstance().collection(Constants.LOCATIONS_DB)
    docRef.addSnapshotListener { snapshot, e ->
        if (e != null) {
            Log.d("Listener_Debug", "Listen failed.", e)
            return@addSnapshotListener
        }
        if (snapshot != null) {
            //Log.d("Listener_Debug", "Current data: ${snapshot.documents}")
            val users = mutableListOf<User>()
            for (document in snapshot.documents) {
                val user = document.toObject<User>()!!
                if(user.userId==userId){
                    viewModel.setCurrentUser(user)
                }
                users.add(user)
            }
            //Log.d("Add_Debug", "$users")
            viewModel.setAllUsers(users)
        } else {
            Log.d("Listener_Debug", "Current data: null")
        }
    }
}*/