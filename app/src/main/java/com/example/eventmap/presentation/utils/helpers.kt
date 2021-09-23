package com.example.eventmap.presentation.utils

import android.util.Log
import com.example.eventmap.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

fun checkIfLoggedIn(): Boolean {
    val auth = FirebaseAuth.getInstance()
    return auth.currentUser==null
}

//suspend
fun getUserFromDb(): User{
    val auth = FirebaseAuth.getInstance()
    val dbRefUsers = FirebaseFirestore.getInstance().collection("users-test");
    val user = dbRefUsers.whereEqualTo("userId", auth.currentUser?.uid.toString()).get().result!!.documents.first().toObject(User::class.java)!!
    Log.d("USERFROMDB", user.toString())
    return user
}

fun updateUsername(){

}