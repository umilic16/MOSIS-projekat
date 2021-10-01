package com.example.eventmap.presentation.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.eventmap.data.User

class UsersViewModel: ViewModel() {
    private var _data = mutableStateOf(listOf<User>())
    val data: MutableState<List<User>> = _data

    fun setAllUsers(users: List<User>){
        _data.value = users
    }
}