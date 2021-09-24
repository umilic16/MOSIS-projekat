package com.example.eventmap.presentation.composables

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventmap.R
import com.example.eventmap.components.CustomTextField
import com.example.eventmap.data.User
import com.example.eventmap.presentation.theme.ui.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AccountView(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val username = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val numOfFriends = remember { mutableStateOf("") }
    val numOfEvents = remember { mutableStateOf("") }
    val points = remember { mutableStateOf("") }
    //val user = FirebaseFirestore.getInstance().collection("Users").document(auth.currentUser?.uid.toString()).get()
    //Log.d("ALOOOOO", "EJ")
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("Users").document(auth.currentUser?.uid.toString())
    docRef.get().addOnSuccessListener { documentSnapshot ->
        username.value = documentSnapshot.data?.get("username").toString()
        email.value = documentSnapshot.data?.get("email").toString()
        numOfFriends.value = documentSnapshot.data?.get("numOfFriends").toString()
        numOfEvents.value = documentSnapshot.data?.get("numOfEvents").toString()
        points.value = documentSnapshot.data?.get("points").toString()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            //.background(DarkBlue)
            .padding(PaddingMedium)
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
                    text = username.value,
                    onValueChange = { username.value = it },
                    hint = "Username",
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                //password
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(DefaultWhite)
                    .clip(
                        RoundedCornerShape(4.dp)
                    )) {
                    Text(
                        text = email.value,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(PaddingMedium)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }
                Spacer(modifier = Modifier.padding(PaddingSmall))
                //password
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(DefaultWhite)
                    .clip(
                        RoundedCornerShape(4.dp)
                    )) {
                    Text(
                        text = "Friends: ${numOfFriends.value}",
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(PaddingMedium)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }
                Spacer(modifier = Modifier.padding(PaddingSmall))
                //password
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(DefaultWhite)
                    .clip(
                        RoundedCornerShape(4.dp)
                    )) {
                    Text(
                        text = "Events: ${numOfEvents.value}",
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(PaddingMedium)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }
                Spacer(modifier = Modifier.padding(PaddingSmall))
                //password
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(DefaultWhite)
                    .clip(
                        RoundedCornerShape(10.dp)
                    )) {
                    Text(
                        text = "Points: ${points.value}",
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(PaddingMedium)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }
                Spacer(modifier = Modifier.padding(PaddingLarge))
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                        },
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .height(40.dp)
                            .shadow(elevation = 5.dp)
                            .background(DefaultBlue)
                    ) {
                        Text(text = "Save", fontSize = 16.sp, color = DefaultBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
    //Create account
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = PaddingMedium, vertical = 80.dp),
        contentAlignment = Alignment.BottomEnd){
        Text(
            text = "Sign out",
            color = DefaultWhite,
            modifier = Modifier.clickable(onClick = {
                auth.signOut()
                navController.navigate("Login") {
                }
            }))
    }
}