package com.example.eventmap.presentation.viewmodels

import android.graphics.Bitmap
import android.util.Log
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

    private var _currentUser = mutableStateOf(null) as MutableState<User>
    var currentUser: MutableState<User> = _currentUser

    private var _picture = mutableStateOf(null) as MutableState<Bitmap>
    var picture : MutableState<Bitmap> = _picture

    fun setCurrentPicture(bitmap: Bitmap){
        _picture.value = bitmap
    }

    fun setCurrentUser(user: User){
        //Log.d("LogDebug", "Pozvan sam ${user}")
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