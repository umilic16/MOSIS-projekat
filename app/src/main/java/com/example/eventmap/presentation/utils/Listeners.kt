package com.example.eventmap.presentation.utils

import android.util.Log
import com.example.eventmap.data.User
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.utils.Constants
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