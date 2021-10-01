package com.example.eventmap.presentation.utils

import android.util.Log
import com.example.eventmap.data.Event
import com.example.eventmap.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.toObject

fun checkIfLoggedIn(): Boolean {
    val auth = FirebaseAuth.getInstance()
    return auth.currentUser==null
}


fun getCurrentUser(): User?{
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("users_db").document(auth.currentUser!!.uid)
    var user: User? = null
    docRef.get().addOnSuccessListener {
        user = it.toObject<User>()
    }
    Log.d("User_log", "User returning: ${user}")
    return user
}

fun saveUser(user: User){
    val auth = FirebaseAuth.getInstance()
    FirebaseFirestore.getInstance().collection("users_db").document(auth.currentUser!!.uid).set(user)
}

fun updateUsername(username: String){
    val auth = FirebaseAuth.getInstance()
    FirebaseFirestore.getInstance().collection("users_db").document(auth.currentUser!!.uid).update("username", username)
}

fun updateLocation(uid: String, location: GeoPoint){
    val auth = FirebaseAuth.getInstance()
    FirebaseFirestore.getInstance().collection("users_db").document(auth.currentUser!!.uid).update("location", location)
}

fun saveEvent(event: Event){
    val auth = FirebaseAuth.getInstance()
    FirebaseFirestore.getInstance().collection("events_db").add(event)
    FirebaseFirestore.getInstance().collection("users_db").document(auth.currentUser!!.uid).update("numOfEvents", FieldValue.increment(1))
}