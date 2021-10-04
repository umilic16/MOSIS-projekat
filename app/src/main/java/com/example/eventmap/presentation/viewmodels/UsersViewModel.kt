package com.example.eventmap.presentation.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eventmap.data.User
import com.google.firebase.firestore.ListenerRegistration

class UsersViewModel: ViewModel() {
    private var _allUsers = mutableStateOf(null) as MutableState<List<User>?>
    val allUsers: MutableState<List<User>?> = _allUsers

    private var _listenerRegistration = mutableStateOf(null) as MutableState<ListenerRegistration>
    var listenerRegistration: MutableState<ListenerRegistration> = _listenerRegistration

    private var _currentUser = mutableStateOf(null) as MutableState<User?>
    var currentUser: MutableState<User?> = _currentUser

    private var _picture = mutableStateOf(null) as MutableState<Bitmap?>
    var picture : MutableState<Bitmap?> = _picture

    //da tracking service observe da li je korisnik login da zna da li da prati
    private var _loggedIn = MutableLiveData(false)
    var loggedIn: MutableLiveData<Boolean> = _loggedIn

    fun setLoggedIn(value: Boolean) {
        _loggedIn.postValue(value)
    }

    fun setCurrentPicture(bitmap: Bitmap?) {
        _picture.value = bitmap
    }

    fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }

    fun setAllUsers(users: List<User>?) {
        _allUsers.value = users
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