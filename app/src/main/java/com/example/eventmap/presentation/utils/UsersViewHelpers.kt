package com.example.eventmap.presentation.utils

import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventmap.data.User
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.utils.Constants
import com.example.eventmap.utils.getPicture
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

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
                val user = document.toObject<User>()!!
                users.add(user)
            }
            Log.d("Add_Debug", "add all users")
            usersViewModel.setAllUsers(users)
        } else {
            Log.d("Listener_Debug", "Current data: null")
        }
    }
}