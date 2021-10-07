package com.example.eventmap.presentation.viewmodels

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eventmap.data.User
import com.google.firebase.firestore.ListenerRegistration

class UsersViewModel: ViewModel() {
    private var _allUsers = mutableStateOf(null) as MutableState<MutableList<User>?>
    val allUsers: MutableState<MutableList<User>?> = _allUsers

    private var _listenerRegistration = mutableStateOf(null) as MutableState<ListenerRegistration>
    var listenerRegistration: MutableState<ListenerRegistration> = _listenerRegistration

    private var _currentUser = mutableStateOf(null) as MutableState<User?>
    var currentUser: MutableState<User?> = _currentUser

    private var _allPicturesAsThumbnails = mutableStateOf(HashMap<String,Bitmap>())
    var allPicturesAsThumbnails: MutableState<HashMap<String, Bitmap>> = _allPicturesAsThumbnails

    //da tracking service observe da li je korisnik login da zna da li da prati
    private var _loggedIn = MutableLiveData(false)
    var loggedIn: MutableLiveData<Boolean> = _loggedIn

    private var _imageUrl = mutableStateOf(null) as MutableState<Uri?>
    var imageUrl: MutableState<Uri?> = _imageUrl

    fun setLoggedIn(value: Boolean) {
        _loggedIn.postValue(value)
    }

    fun setCurrentPictureUrl(url: Uri?){
        _imageUrl.value = url
    }

    fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }

    fun setAllUsers(users: MutableList<User>?) {
        _allUsers.value = users
    }

    fun addThumbnailToList(key: String, image: Bitmap){
        Log.d("Icon_Debug","Adding to view model: $image")
        _allPicturesAsThumbnails.value[key] = image
    }

    fun setListenerRegistration(registration: ListenerRegistration){
        _listenerRegistration.value = registration
    }


    /*private var _pictures = mutableStateOf(hashMapOf<String, Bitmap>())
    val pictures: MutableState<HashMap<String, Bitmap>> = _pictures

    fun addPicture(id: String, bitmap: Bitmap){
        //Log.d("Users_Debug", "Added in view model")
        _pictures.value[id] = bitmap
    }*/
}