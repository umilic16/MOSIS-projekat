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

    private var _locationPermissionGranted = MutableLiveData(false)
    var locationPermissionGranted : LiveData<Boolean> = _locationPermissionGranted
    
    fun permissionGrand(setGranted: Boolean){
        _locationPermissionGranted.value = setGranted
    }

}