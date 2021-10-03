package com.example.eventmap.presentation.utils

import android.util.Log
import com.example.eventmap.data.User
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

fun addUsersListener(usersViewModel: UsersViewModel){
    val docRef = FirebaseFirestore.getInstance().collection(Constants.USERS_DB)
    docRef.addSnapshotListener { snapshot, e ->
        if (e != null) {
            Log.d("Listener_Debug", "Listen failed.", e)
            return@addSnapshotListener
        }
        if (snapshot != null) {
            Log.d("Listener_Debug", "Current data: ${snapshot.documents}")
            val users = mutableListOf<User>()
            for (document in snapshot.documents) {
                users.add(document.toObject<User>()!!)
            }
            usersViewModel.setAllUsers(users)
        } else {
            Log.d("Listener_Debug", "Current data: null")
        }
    }
}