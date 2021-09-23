package com.example.eventmap.presentation.composables

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.unit.sp
import com.example.eventmap.components.CustomTextField
import com.example.eventmap.presentation.theme.ui.*
import com.google.firebase.auth.FirebaseAuth


@Composable
fun Register(navController: NavController) {
    val email = remember { mutableStateOf("") }
    //val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val repeatedPassword = remember { mutableStateOf("")}
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    //ceo container
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
        Text(
            text = "Create your account",
            color = DefaultWhite,
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth())
        //input boxes i forgot pass
        Spacer(modifier = Modifier.padding(PaddingMedium))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            //zbog testiranja
            //modifier = Modifier.background(LightGray)
        )
        {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                //email
                CustomTextField(
                    text = email.value,
                    onValueChange = { email.value = it },
                    hint = "Email address",
                    keyboardType = KeyboardType.Email
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                //password
                CustomTextField(
                    text = password.value,
                    onValueChange = { password.value = it },
                    hint = "Password",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                //repeat password
                CustomTextField(
                    text = repeatedPassword.value,
                    onValueChange = { repeatedPassword.value = it },
                    hint = "Repeat password",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.padding(PaddingLarge))
                Button(
                    onClick = {
                        if (email.value.isEmpty() || password.value.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Email and password cant be empty!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (password.value != repeatedPassword.value) {
                            Toast.makeText(
                                context,
                                "Passwords dont match!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            auth.createUserWithEmailAndPassword(email.value, password.value)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        navController.navigate("Home")
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "User already exists",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(50.dp)
                        .shadow(elevation = 5.dp)
                ) {
                    Text(text = "Register", fontSize = 20.sp)
                }
            }
        }
        Spacer(modifier = Modifier.padding(PaddingExtra))
    }
    //Sing in
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = PaddingMedium, vertical = PaddingLarge),
        contentAlignment = Alignment.BottomStart){
        Text(
            text = "Already have an account?",
            color = DefaultWhite,
            modifier = Modifier.clickable(onClick = {
                navController.navigate("Login") {
                }
            }))
    }
}