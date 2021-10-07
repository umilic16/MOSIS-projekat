package com.example.eventmap.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.eventmap.data.Event
import com.example.eventmap.data.User
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.services.FirebaseService.Companion.token
import com.example.eventmap.utils.Constants.DISTANCE_FOR_NEARBY_TRACKING
import com.example.eventmap.utils.Constants.EVENTS_DB
import com.example.eventmap.utils.Constants.USERS_DB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

object DbAdapter {
    fun checkIfLoggedIn(): Boolean {
        val auth = FirebaseAuth.getInstance()
        return auth.currentUser != null
    }

    suspend fun getNearbyUsers(location: Location): List<User>{
        val users = mutableListOf<User>()
        val waitFor = CoroutineScope(Dispatchers.IO).launch {
            val result = FirebaseFirestore.getInstance().collection(USERS_DB).get().await()
            //posotji samo 1 user
            if (result.size() > 1) {
                for (document in result.documents) {
                    val user = document.toObject<User>()
                    user?.let { current ->
                        val currentId = FirebaseAuth.getInstance().currentUser?.uid
                        //da li se ispituje za current usera
                        if (current.userId != currentId) {
                            //ako lokacija nije null
                            current.location?.let {
                                val loc = Location("").apply {
                                    this.latitude = it.latitude
                                    this.longitude = it.longitude
                                }
                                val between = location.distanceTo(loc)
                                //Log.d("Nearby_Debug", "Current measured distance: $between")
                                //ako je distanca manje od prosledjene dodaj u listu za return
                                if (between <= DISTANCE_FOR_NEARBY_TRACKING) {
                                    users.add(current)
                                }
                            }
                        }
                    }
                }
            }
        }
        runBlocking {
            waitFor.join()
            //Log.d("Nearby_Debug", "Returning list from db: $users")
        }
        return users
    }

    suspend fun getNearbyEvents(location: Location): List<Event>{
        val events = mutableListOf<Event>()
        val waitFor = CoroutineScope(Dispatchers.IO).launch {
            val result = FirebaseFirestore.getInstance().collection(EVENTS_DB).get().await()
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            for (document in result.documents) {
                val event = document.toObject<Event>()
                event?.let { current ->
                    //ako je event napravljen od strane drugog korisnika
                    if (current.userId != currentUserId) {
                        //ako lokacija nije null
                        current.location?.let {
                            val loc = Location("").apply {
                                this.latitude = it.latitude
                                this.longitude = it.longitude
                            }
                            val between = location.distanceTo(loc)
                            if (between <= DISTANCE_FOR_NEARBY_TRACKING) {
                                events.add(current)
                            }
                        }
                    }
                }
            }
        }
        runBlocking {
            waitFor.join()
            //Log.d("Nearby_Debug", "Returning list from db: $users")
        }
        return events
    }

    fun updateLocation(newLocation: GeoPoint?) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val auth = FirebaseAuth.getInstance()
            FirebaseFirestore.getInstance().collection(USERS_DB).document(auth.currentUser!!.uid)
                .update("location", newLocation).await()
        } catch (e: Exception) {
            Log.d("DbAdapter_Exception", e.message.toString())
        }

    }

    fun saveUser(user: User, picture: Uri) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val auth = FirebaseAuth.getInstance()
            val userId = auth.currentUser!!.uid
            FirebaseFirestore.getInstance().collection(USERS_DB).document(userId)
                .set(user).await()
            Firebase.storage.reference.child("images/$userId").putFile(picture).await()
        } catch (e: Exception) {
            Log.d("DbAdapter_Exception", e.message.toString())
        }
    }

    fun updateUsername(username: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val auth = FirebaseAuth.getInstance()
            FirebaseFirestore.getInstance().collection(USERS_DB).document(auth.currentUser!!.uid)
                .update("username", username).await()
        } catch (e: Exception) {
            Log.d("DbAdapter_Exception", e.message.toString())
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
        } catch (e: Exception) {
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
        } catch (e: Exception) {
            Log.d("DbAdapter_Exception", e.message.toString())
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
        } catch (e: Exception) {
            Log.d("DbAdapter_Exception", e.message.toString())
        }
    }

    fun updateToken(token: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val auth = FirebaseAuth.getInstance()
            FirebaseFirestore.getInstance().collection(USERS_DB).document(auth.currentUser!!.uid)
                .update("token", token).await()
        } catch (e: Exception) {
            Log.d("DbAdapter_Exception", e.message.toString())
        }
    }

    fun saveEvent(event: Event) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val auth = FirebaseAuth.getInstance()
            FirebaseFirestore.getInstance().collection(EVENTS_DB).add(event).await()
            FirebaseFirestore.getInstance().collection(USERS_DB).document(auth.currentUser!!.uid)
                .update("numOfEvents", FieldValue.increment(1)).await()
        } catch (e: Exception) {
            Log.d("DbAdapter_Exception", e.message.toString())
        }
    }

    fun setCurrentUser(viewModel: UsersViewModel) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection(USERS_DB).document(auth.currentUser!!.uid)
            val result = docRef.get().await()
            val user = result.toObject<User>()!!
            //funkcija updateToken se poziva na onNew token
            //ovde se proverava da li je potreban update na login
            if(user.token != token){
                docRef.update("token", token)
            }
            viewModel.setCurrentUser(user)
            //val bytes = Firebase.storage.reference.child("images/${auth.currentUser?.uid}").getBytes(Constants.MAX_DOWNLOAD_SIZE).await()
            //val picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            //viewModel.setCurrentPicture(picture)
        } catch (e: Exception) {
            Log.d("DbAdapter_Exception", e.message.toString())
        }
    }

    fun setCurrentPictureUrl(viewModel: UsersViewModel) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val auth = FirebaseAuth.getInstance()
            val imageRef = Firebase.storage.reference.child("images/${auth.currentUser?.uid}")
            //val bytes = imageRef.getBytes(Constants.MAX_DOWNLOAD_SIZE).await()
            val imageUrl = imageRef.downloadUrl.await()
            //val picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            withContext(Dispatchers.Main) {
                //viewModel.setCurrentPicture(picture)
                viewModel.setCurrentPictureUrl(imageUrl)
            }
        } catch (e: Exception) {
            Log.d("DbAdapter_Exception", e.message.toString())
        }
    }

    fun getPictureAndAddToViewModel(viewModel: UsersViewModel, userId: String, context: Context) = CoroutineScope(Dispatchers.IO).launch {
        //lateinit var bitmap: Bitmap
        try {
            Log.d("Icon_Debug", "Finding image for: $userId")
            val url = Firebase.storage.reference.child("images/$userId").downloadUrl.await()
            Glide
                .with(context)
                .asBitmap()
                .load(url)
                .override(100, 100)
                .circleCrop()
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        viewModel.addThumbnailToList(userId, resource)
                        Log.d("Icon_Debug", "getting from db : $resource")
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        } catch (e: Exception) {
            Log.d("DbAdapter_Exception", e.message.toString())
        }
    }
}