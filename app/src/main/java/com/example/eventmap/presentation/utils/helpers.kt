package com.example.eventmap.presentation.utils

import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.eventmap.data.User
import com.google.android.libraries.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

fun checkIfLoggedIn(): Boolean {
    val auth = FirebaseAuth.getInstance()
    return auth.currentUser==null
}

//suspend
fun saveUser(uid: String, user: User){
    FirebaseFirestore.getInstance().collection("users-test2").document(uid).set(user)
}
/*fun saveUser(user: User) = CoroutineScope(Dispatchers.IO).launch {
    val dbRefUsers = FirebaseFirestore.getInstance().collection("users-test");
    try{
        dbRefUsers.add(user).await()
    }catch (e:Exception) {
        Log.d("SAVEUSER", e.message.toString())
    }
}*/
fun updateLocation(uid: String, location: LatLng){
    FirebaseFirestore.getInstance().collection("users-test2").document(uid).update("location", location)
}