package com.example.eventmap.presentation.composables

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventmap.components.CustomTextField
import com.example.eventmap.presentation.MainActivityViewModel
import com.example.eventmap.presentation.theme.ui.*
import com.example.eventmap.presentation.utils.updateUsername
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AccountView(navController: NavController, viewModel: MainActivityViewModel) {
    val auth = FirebaseAuth.getInstance()
    val username = remember { mutableStateOf("") }
    val user = viewModel.currentUser.value
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(PaddingMedium)
    ) {
        Spacer(modifier = Modifier.padding(PaddingExtra))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            Text(
                text = "Account Information",
                color = DefaultWhite,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(PaddingMedium))
            CustomTextField(
                text = username.value,
                onValueChange = { username.value = it },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = DarkText,
                    backgroundColor = DefaultWhite,
                    placeholderColor = DarkText
                ),
                hint = if (user.username!!.isNotEmpty()) user.username else "Enter your username",
            )
            Spacer(modifier = Modifier.padding(PaddingSmall))
            CustomTextField(
                text = user.email,
                readOnly = true,
                style = TextStyle(
                    fontWeight = FontWeight.W400,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.padding(PaddingSmall))
            CustomTextField(
                text = "Friends: ${user.numOfFriends}",
                readOnly = true,
                style = TextStyle(
                    fontWeight = FontWeight.W400,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.padding(PaddingSmall))
            CustomTextField(
                text = "Events: ${user.numOfEvents}",
                readOnly = true,
                style = TextStyle(
                    fontWeight = FontWeight.W400,
                    fontSize = 16.sp
                )
            )
        }
        Spacer(modifier = Modifier.padding(PaddingSmall))
        CustomTextField(
            text = "Points: ${user.points}",
            readOnly = true,
            style = TextStyle(
                fontWeight = FontWeight.W400,
                fontSize = 16.sp
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
                navController.navigate("Login") {
                }
            })
        )
    }
}