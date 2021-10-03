package com.example.eventmap.presentation.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.eventmap.data.User

class UsersViewModel: ViewModel() {
    private var _data = mutableStateOf(listOf<User>())
    val data: MutableState<List<User>> = _data

    private var _pictures = mutableStateOf(hashMapOf<String, Bitmap>())
    val pictures: MutableState<HashMap<String, Bitmap>> = _pictures

    fun addPicture(id: String, bitmap: Bitmap){
        Log.d("Users_Debug", "Added in view model")
        _pictures.value[id] = bitmap
    }

    fun setAllUsers(users: List<User>){
        _data.value = users
    }
}