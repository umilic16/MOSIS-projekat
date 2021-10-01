package com.example.eventmap.utils

import android.util.Log
import com.example.eventmap.data.Event
import com.example.eventmap.data.User
import com.example.eventmap.presentation.viewmodels.MainActivityViewModel
import com.example.eventmap.utils.Constants.EVENTS_DB
import com.example.eventmap.utils.Constants.USERS_DB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

fun checkIfLoggedIn(): Boolean {
    val auth = FirebaseAuth.getInstance()
    return auth.currentUser==null
}

fun getCurrentUser(): User?{
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection(USERS_DB).document(auth.currentUser!!.uid)
    var user: User? = null
    docRef.get().addOnSuccessListener {
        user = it.toObject<User>()
    }
    //Log.d("User_log", "User returning: $user")
    return user
}

fun saveUser(user: User) = CoroutineScope(Dispatchers.IO).launch{
    try {
        val auth = FirebaseAuth.getInstance()
        FirebaseFirestore.getInstance().collection(USERS_DB).document(auth.currentUser!!.uid)
            .set(user).await()
    }catch (e: Exception){
        Log.d("SaveUser_Exception", e.message.toString())
    }
}

fun updateUsername(username: String) = CoroutineScope(Dispatchers.IO).launch{
    try {
        val auth = FirebaseAuth.getInstance()
        FirebaseFirestore.getInstance().collection(USERS_DB).document(auth.currentUser!!.uid)
            .update("username", username).await()
    }    catch (e: Exception) {
        Log.d("UpdateUser_Exception", e.message.toString())
    }
}

fun updateLocation(location: GeoPoint){
    val auth = FirebaseAuth.getInstance()
    FirebaseFirestore.getInstance().collection("users_db").document(auth.currentUser!!.uid).update("location", location)
}

fun saveEvent(event: Event)  = CoroutineScope(Dispatchers.IO).launch {
    try {
        val auth = FirebaseAuth.getInstance()
        FirebaseFirestore.getInstance().collection(EVENTS_DB).add(event).await()
        FirebaseFirestore.getInstance().collection(USERS_DB).document(auth.currentUser!!.uid)
            .update("numOfEvents", FieldValue.increment(1)).await()
    }catch(e: Exception){
        Log.d("SaveEvent_Exception", e.message.toString())
    }
}

fun setCurrentUser(viewModel: MainActivityViewModel) = CoroutineScope(Dispatchers.IO).launch {
    try {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(USERS_DB).document(auth.currentUser?.uid.toString())
        val user = docRef.get().await()
        viewModel.setCurrentUser(user.toObject<User>()!!)
    }catch(e: Exception){
        Log.d("CurrentUser_Exception", e.message.toString())
    }
}

fun getUsersByPoints() = CoroutineScope(Dispatchers.IO).launch {
    try {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(USERS_DB)
        val users = mutableListOf<User>()
        val querySnapshot = docRef.orderBy("points").get().await()
        for(document in querySnapshot){
            val user = document.toObject<User>()
            users.add(user)
        }
        Log.d("Users_log", users.toString())

    } catch (e: Exception) {
        Log.d("CurrentUser_Exception", e.message.toString())
    }
}