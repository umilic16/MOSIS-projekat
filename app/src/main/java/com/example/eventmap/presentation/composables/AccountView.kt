package com.example.eventmap.presentation.composables

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventmap.R
import com.example.eventmap.components.CustomTextField
import com.example.eventmap.data.User
import com.example.eventmap.presentation.theme.ui.*
import com.example.eventmap.presentation.utils.getUserFromDb
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AccountView(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val user = getUserFromDb()
    val username = remember { mutableStateOf("") }
    val dbRefUsers = FirebaseFirestore.getInstance().collection("users-test");
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            //.background(DarkBlue)
            .padding(PaddingMedium)
            .background(LightGray)
    ) {
        Spacer(modifier = Modifier.padding(PaddingExtra))
        //kolone
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            //zbog testiranja
            //modifier = Modifier.background(LightGray)
        )
        {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                //username
                CustomTextField(
                    text = user.username,
                    onValueChange = { username.value = it },
                    hint = "Username",
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                //password
                CustomTextField(
                    text = user.email,
                    onValueChange = {  },
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                //password
                CustomTextField(
                    text = "Number of friends: ${user.numOfFriends}",
                    onValueChange = {  },
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                //password
                CustomTextField(
                    text = "Number of events: ${user.numOfFriends}",
                    onValueChange = {  },
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                //password
                CustomTextField(
                    text = "Points: ${user.numOfFriends}",
                    onValueChange = {  },
                )
                Spacer(modifier = Modifier.padding(PaddingLarge))
                Button(
                    onClick = {
                    },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(50.dp)
                        .shadow(elevation = 5.dp)
                ) {
                    Text(text = "Login", fontSize = 20.sp)
                }
            }
        }
        Spacer(modifier = Modifier.padding(PaddingExtra))
    }
}