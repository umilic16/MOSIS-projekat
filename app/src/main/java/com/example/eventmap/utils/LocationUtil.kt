package com.example.eventmap.utils

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import pub.devrel.easypermissions.EasyPermissions
import android.R

import android.content.Intent

import android.content.DialogInterface
import android.location.LocationManager.GPS_PROVIDER
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.GeoPoint
import java.lang.Exception


object LocationUtil {
    fun hasLocationPermissions(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                //Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    fun isGpsEnabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            lm.isProviderEnabled(GPS_PROVIDER)
        } else {
            val mode = Settings.Secure.getInt(
                context.contentResolver, Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )
            mode != Settings.Secure.LOCATION_MODE_OFF
        }
    }
}