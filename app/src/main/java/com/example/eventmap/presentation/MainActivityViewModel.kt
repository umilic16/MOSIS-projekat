package com.example.eventmap.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eventmap.data.User
import com.google.android.libraries.maps.model.LatLng

class MainActivityViewModel:ViewModel() {

    private var _userCurrentLng = mutableStateOf(0.0)
    var userCurrentLng : MutableState<Double> = _userCurrentLng

    private var _userCurrentLat = mutableStateOf(0.0)
    var userCurrentLat : MutableState<Double> = _userCurrentLat

    private var _locationPermissionGranted = MutableLiveData(false)
    var locationPermissionGranted : LiveData<Boolean> = _locationPermissionGranted

    private var _currentUser = mutableStateOf(User())
    var currentUser: MutableState<User> = _currentUser

    fun setCurrentUser(user: User){
        _currentUser.value = user
    }

    fun currentUserGeoCoord(latLng: LatLng){
        _userCurrentLat.value = latLng.latitude
        _userCurrentLng.value = latLng.longitude
    }

    fun permissionGrand(setGranted: Boolean){
        _locationPermissionGranted.value = setGranted
    }

}