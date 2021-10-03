package com.example.eventmap.presentation.composables

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventmap.components.ImageHolder
import com.example.eventmap.data.User
import com.example.eventmap.notification.NotificationAdapter.sendNotification
import com.example.eventmap.notification.NotificationData
import com.example.eventmap.notification.PushNotification
import com.example.eventmap.presentation.theme.ui.*
import com.example.eventmap.presentation.utils.addUsersListener
import com.example.eventmap.presentation.viewmodels.MainActivityViewModel
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.utils.*

const val TOPIC = "/topics/myTopic"

@Composable
fun Home(navController: NavController, viewModel: UsersViewModel) {
    //Log.d("LogDebug",navController.previousBackStackEntry.toString())
    val users = viewModel.data.value
    val currentUser = viewModel.currentUser.value
    //baca exception da je current user null
    if(currentUser!= null) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.93f)
                .padding(PaddingMedium)
        ) {
            LazyColumn(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .shadow(elevation = 5.dp)
            ) {
                //Log.d("HelpMe", "USERS: $users\nCURRENT: $currentUser")
                items(items = users) { user ->
                    if (user.userId != currentUser.userId)
                        UserLazyColumn(user = user, currentUser = currentUser)
                }
            }
        }
    }
}

@Composable
fun UserLazyColumn(user: User, currentUser: User){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(DefaultWhite)
            .fillMaxWidth()
            .clip(RoundedCornerShape(5.dp))
            .padding(PaddingSmall)
    ){
        //prvi red slika
        //ImageHolder(bitmap = picture.asImageBitmap(), modifier = Modifier.size(50.dp))
        //username i info add friend
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingSmall)
            ) {
                Text(
                    text = if (user.username.isNullOrEmpty()) {
                        "No username"
                    } else {
                        user.username
                    },
                    color = DarkBlue,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W400
                )
                Text(
                    text = user.email,
                    color = DefaultBlue,
                    fontSize = 14.sp
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingSmall)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(0.5f)) {
                    Text(
                        text = "friends: ${user.numOfFriends}",
                        color = DefaultBlue,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "events: ${user.numOfEvents}",
                        color = DefaultBlue,
                        fontSize = 15.sp
                    )
                }
                Row(horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(0.5f)) {
                    if (checkIfFriends(currentUser,user.userId)) {
                        Text(
                            text = "Friend",
                            color = DefaultBlue,
                            fontSize = 14.sp
                        )
                    } else if(
                        checkIfRequestSent(currentUser,user.userId)) {
                        Text(
                            text = "Request sent!",
                            color = DefaultBlue,
                            fontSize = 14.sp
                        )
                    }else if(checkIfRequestReceived(currentUser,user.userId)) {
                        Text(
                            text = "Accept",
                            color = DarkBlue,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                //accept request
                            }
                        )
                    }
                    else {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = DarkBlue,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                //send request
                                sendFriendRequest(currentUser = currentUser, sendingToUser = user)
                            }
                        )
                    }
                }
            }
        }
    }
}


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
                sendNotification(it)
            }
        }
    } catch (e: Exception) {
        Log.d("FReq_Exception", e.message.toString())
    }
}

