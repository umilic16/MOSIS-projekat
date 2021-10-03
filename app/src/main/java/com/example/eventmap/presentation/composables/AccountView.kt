package com.example.eventmap.presentation.composables

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventmap.R
import com.example.eventmap.components.CustomTextField
import com.example.eventmap.components.ImageHolder
import com.example.eventmap.data.User
import com.example.eventmap.presentation.viewmodels.MainActivityViewModel
import com.example.eventmap.presentation.theme.ui.*
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.services.FirebaseService.Companion.token
import com.example.eventmap.utils.Constants
import com.example.eventmap.utils.Constants.MAX_DOWNLOAD_SIZE
import com.example.eventmap.utils.updateUsername
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun AccountView(navController: NavController, viewModel: UsersViewModel) {
    val auth = FirebaseAuth.getInstance()
    val username = remember { mutableStateOf("") }
    val context = LocalContext.current
    val user = viewModel.currentUser.value
    val picture = viewModel.picture
    //Log.d("HelpMe", "USER: $user")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(PaddingMedium)
    ) {
        Text(
            text = "Account Information",
            color = DefaultWhite,
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(PaddingLarge))
        picture.value?.let {
            ImageHolder(
                bitmap = it.asImageBitmap(),
                modifier = Modifier
                    .size(150.dp)
                    .rotate(90f)
            )
        }
        Spacer(modifier = Modifier.padding(PaddingMedium))
        CustomTextField(
            text = username.value,
            onValueChange = { username.value = it },
            colors = TextFieldDefaults.textFieldColors(
                textColor = DarkText,
                backgroundColor = DefaultWhite,
                placeholderColor = DarkText
            ),
            style = TextStyle(
                fontWeight = FontWeight.W400,
            ),
            hint = if (user.username!!.isNotEmpty()) user.username else "Enter your username",
        )
        Spacer(modifier = Modifier.padding(PaddingSmall))
        CustomTextField(
            text = user.email,
            readOnly = true,
            style = TextStyle(
                fontWeight = FontWeight.W400,
            )
        )
        Spacer(modifier = Modifier.padding(PaddingSmall))
        CustomTextField(
            text = "Friends: ${user.numOfFriends}",
            readOnly = true,
            style = TextStyle(
                fontWeight = FontWeight.W400,
            )
        )
        Spacer(modifier = Modifier.padding(PaddingSmall))
        CustomTextField(
            text = "Events: ${user.numOfEvents}",
            readOnly = true,
            style = TextStyle(
                fontWeight = FontWeight.W400,
            )
        )
        Spacer(modifier = Modifier.padding(PaddingSmall))
        CustomTextField(
            text = "Points: ${user.points}",
            readOnly = true,
            style = TextStyle(
                fontWeight = FontWeight.W400,
            )
        )
        Spacer(modifier = Modifier.padding(PaddingLarge))
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if (username.value.isNotEmpty()) {
                        if (username.value != user.username) {
                            updateUsername(username.value)
                            Toast.makeText(
                                context,
                                "Username changed!",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("Home")
                        } else {
                            Toast.makeText(
                                context,
                                "You entered the same username!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Username is empty!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(40.dp)
                    .shadow(elevation = 5.dp)
                    .background(DefaultBlue)
            ) {
                Text(
                    text = "Save",
                    fontSize = 16.sp,
                    color = DefaultWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = PaddingMedium, vertical = 80.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Text(
            text = "Sign out",
            color = DefaultWhite,
            modifier = Modifier.clickable(onClick = {
                auth.signOut()
                navController.popBackStack("Home", true)
                navController.navigate("Login")
            })
        )
    }
}

/*fun downloadImage(name: String) = CoroutineScope(Dispatchers.IO).launch{
    try {
        val auth = FirebaseAuth.getInstance()
        val image = Firebase.storage.reference
            .child("images/$auth.currentUser?.uid").getBytes(Constants.MAX_DOWNLOAD_SIZE).await()
        val bmp = BitmapFactory.decodeByteArray(image, 0, image.size)
    } catch (e: Exception) {
        Log.d("Download_Exception", e.message.toString())
    }
}*/