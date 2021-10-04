package com.example.eventmap.utils

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.eventmap.data.Event
import com.example.eventmap.data.User
import com.example.eventmap.notification.NotificationAdapter.sendNotification
import com.example.eventmap.notification.NotificationData
import com.example.eventmap.notification.PushNotification
import com.example.eventmap.presentation.viewmodels.MainActivityViewModel
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.utils.Constants.EVENTS_DB
import com.example.eventmap.utils.Constants.MAX_DOWNLOAD_SIZE
import com.example.eventmap.utils.Constants.USERS_DB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

fun checkIfLoggedIn(): Boolean {
    val auth = FirebaseAuth.getInstance()
    return auth.currentUser!=null
}

fun saveUser(user: User, picture: Uri) = CoroutineScope(Dispatchers.IO).launch{
    try {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        FirebaseFirestore.getInstance().collection(USERS_DB).document(userId)
            .set(user).await()
        Firebase.storage.reference.child("images/$userId").putFile(picture).await()
    }catch (e: Exception){
        Log.d("DbAdapter_Exception1", e.message.toString())
    }
}

fun updateUsername(username: String) = CoroutineScope(Dispatchers.IO).launch{
    try {
        val auth = FirebaseAuth.getInstance()
        FirebaseFirestore.getInstance().collection(USERS_DB).document(auth.currentUser!!.uid)
            .update("username", username).await()
    }    catch (e: Exception) {
        Log.d("DbAdapter_Exception2", e.message.toString())
    }
}

fun addSendRequestToUsers(sendingTo: User) = CoroutineScope(Dispatchers.IO).launch {
    try {
        val auth = FirebaseAuth.getInstance()
        //dodaje u listu poslatih zahteva trenutnog korisnika id korinsika kom se salje
        FirebaseFirestore.getInstance().collection(USERS_DB).document(auth.currentUser!!.uid)
            .update("sentRequests", FieldValue.arrayUnion(sendingTo.userId)).await()
        //dodaje u listu primljenih zahteva currentUser.id korinsiku kom se salje zahtev
        FirebaseFirestore.getInstance().collection(USERS_DB).document(sendingTo.userId)
            .update("receivedRequests", FieldValue.arrayUnion(auth.currentUser!!.uid)).await()
    }    catch (e: Exception) {
        Log.d("DbAdapter_Exception3", e.message.toString())
    }
}

fun removeRequestsFromDatabase(newFriend: User) = CoroutineScope(Dispatchers.IO).launch {
    try {
        val auth = FirebaseAuth.getInstance()
        //obrisi iz svoje liste dobijenih requesta usera koji si prihvatio
        FirebaseFirestore.getInstance().collection(USERS_DB).document(auth.currentUser!!.uid)
            .update("receivedRequests", FieldValue.arrayRemove(newFriend.userId)).await()
        //obrisi iz liste poslatih zahteva u dokumentu novog prijatelja svoj id
        FirebaseFirestore.getInstance().collection(USERS_DB).document(newFriend.userId)
            .update("sentRequests", FieldValue.arrayRemove(auth.currentUser!!.uid)).await()
    }    catch (e: Exception) {
        Log.d("DbAdapter_Exception3", e.message.toString())
    }
}

fun addFriendToDatabase(newFriend: User) = CoroutineScope(Dispatchers.IO).launch {
    try {
        val auth = FirebaseAuth.getInstance()
        //dodaj u svoju listu novog prijatelja i inkrementiraj broj prijatelja
        val user1 = FirebaseFirestore.getInstance().collection(USERS_DB).document(auth.currentUser!!.uid)
        user1.update("friends", FieldValue.arrayUnion(newFriend.userId)).await()
        user1.update("numOfFriends", FieldValue.increment(1)).await()
        //dodaj u listu novog prijatelja sebe i inkrementiraj broj prijatelja
        val user2 = FirebaseFirestore.getInstance().collection(USERS_DB).document(newFriend.userId)
        user2.update("friends", FieldValue.arrayUnion(auth.currentUser!!.uid)).await()
        user2.update("numOfFriends", FieldValue.increment(1)).await()
    }    catch (e: Exception) {
        Log.d("DbAdapter_Exception3", e.message.toString())
    }
}

fun updateToken(token: String) = CoroutineScope(Dispatchers.IO).launch {
    try {
        val auth = FirebaseAuth.getInstance()
        FirebaseFirestore.getInstance().collection(USERS_DB).document(auth.currentUser!!.uid)
            .update("token", token).await()
    }    catch (e: Exception) {
        Log.d("DbAdapter_Exception4", e.message.toString())
    }
}

fun saveEvent(event: Event)  = CoroutineScope(Dispatchers.IO).launch {
    try {
        val auth = FirebaseAuth.getInstance()
        FirebaseFirestore.getInstance().collection(EVENTS_DB).add(event).await()
        FirebaseFirestore.getInstance().collection(USERS_DB).document(auth.currentUser!!.uid)
            .update("numOfEvents", FieldValue.increment(1)).await()
    }catch(e: Exception){
        Log.d("DbAdapter_Exception5", e.message.toString())
    }
}

fun setCurrentUser(viewModel: UsersViewModel) = CoroutineScope(Dispatchers.IO).launch {
    try {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(USERS_DB).document(auth.currentUser?.uid.toString())
        val user = docRef.get().await()
        viewModel.setCurrentUser(user.toObject<User>()!!)
        Firebase.storage.reference.child("images/${auth.currentUser?.uid}").getBytes(Constants.MAX_DOWNLOAD_SIZE).await()
        //val picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        //viewModel.setCurrentPicture(picture)
    }catch(e: Exception){
        Log.d("DbAdapter_Exception6", e.message.toString())
    }
}

fun setCurrentPicture(viewModel: UsersViewModel) = CoroutineScope(Dispatchers.IO).launch {
    try {
        val auth = FirebaseAuth.getInstance()
        val bytes = Firebase.storage.reference.child("images/${auth.currentUser?.uid}").getBytes(Constants.MAX_DOWNLOAD_SIZE).await()
        val picture = BitmapFactory.decodeByteArray(bytes,0, bytes.size)
        withContext(Dispatchers.Main){
            viewModel.setCurrentPicture(picture)
        }
    }catch(e: Exception){
        Log.d("DbAdapter_Exception7", e.message.toString())
    }
}

fun getPicture(viewModel: UsersViewModel, id: String) = CoroutineScope(Dispatchers.IO).launch {
    try{
        //delay(1000)
        val bytes = Firebase.storage.reference.child("images/$id").getBytes(Constants.MAX_DOWNLOAD_SIZE).await()
        val picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        //Log.d("Add_Debug", "Got picture")
        viewModel.addPicture(id,picture)
    }catch(e: Exception){
        Log.d("DbAdapter_Exception", e.message.toString())
    }
}