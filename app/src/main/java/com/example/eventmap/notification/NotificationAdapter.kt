package com.example.eventmap.notification

import android.util.Log
import com.example.eventmap.retrofit.RetrofitInstance
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NotificationAdapter {
    fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try{
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful){
                Log.d("Notification_Debug", Gson().toJson(response).toString())
            }
            else{
                Log.d("Notification_Debug", response.errorBody().toString())
            }
        }
        catch(e: Exception){
            Log.d("Notification_Exception", e.message.toString())
        }
    }
}