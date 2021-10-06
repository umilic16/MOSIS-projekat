package com.example.eventmap.utils

import android.util.Log
import com.example.eventmap.data.User
import com.example.eventmap.notification.NotificationAdapter
import com.example.eventmap.notification.NotificationData
import com.example.eventmap.notification.PushNotification
import com.example.eventmap.utils.DbAdapter.addFriendToDatabase
import com.example.eventmap.utils.DbAdapter.addSendRequestToUsers
import com.example.eventmap.utils.DbAdapter.removeRequestsFromDatabase

object FriendRequestsUtil {
    fun sendFriendRequest(currentUser: User, sendingToUser: User) {
        try {
            //upisi u bazu
            addSendRequestToUsers(sendingTo = sendingToUser)
            val nameOrMail = if (currentUser.username.isNullOrEmpty()) {
                currentUser.email
            } else {
                currentUser.username
            }
            val recipientToken = sendingToUser.token
            if (recipientToken != null) {
                //napravi push notifikaciju
                PushNotification(
                    NotificationData(
                        "Friend request",
                        "You received a friend request from: $nameOrMail"
                    ),
                    recipientToken
                ).also {
                    //posalji
                    NotificationAdapter.sendNotification(it)
                }
            }
        } catch (e: Exception) {
            Log.d("FReq_Exception", e.message.toString())
        }
    }

    fun acceptFriend(currentUser: User, newFriend: User) {
        try {
            //obrisi iz baze
            removeRequestsFromDatabase(newFriend = newFriend)
            addFriendToDatabase(newFriend = newFriend)
            val nameOrMail = if (currentUser.username.isNullOrEmpty()) {
                currentUser.email
            } else {
                currentUser.username
            }
            val recipientToken = newFriend.token
            if (recipientToken != null) {
                //napravi push notifikaciju
                PushNotification(
                    NotificationData(
                        "New friend",
                        "$nameOrMail accepted your request"
                    ),
                    recipientToken
                ).also {
                    //posalji
                    NotificationAdapter.sendNotification(it)
                }
            }
        } catch (e: Exception) {
            Log.d("FReq_Exception", e.message.toString())
        }
    }
}