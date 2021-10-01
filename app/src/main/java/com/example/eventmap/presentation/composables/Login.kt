package com.example.eventmap.presentation.composables

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventmap.R
import com.example.eventmap.components.CustomTextField
import com.example.eventmap.presentation.theme.ui.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Login(navController: NavController) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val showPassword = remember { mutableStateOf(false) }
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
        //logo
        Spacer(modifier = Modifier.padding(PaddingExtra))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            //zbog testiranja
            //modifier = Modifier.background(DefaultBlue)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                alignment = Alignment.Center
            )
        }
        Spacer(modifier = Modifier.padding(PaddingExtra))
        //input boxes i forgot pass
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            //zbog testiranja
            //modifier = Modifier.background(LightGray)
        )
        {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                //username / email
                CustomTextField(
                    text = email.value,
                    onValueChange = { email.value = it },
                    hint = "Email",
                    keyboardType = KeyboardType.Email
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                //password
                CustomTextField(
                    text = password.value,
                    onValueChange = { password.value = it },
                    hint = "Password",
                    keyboardType = KeyboardType.Password,
                    isPasswordVisible = showPassword.value,
                    onPasswordToggleClick = {
                        showPassword.value = !showPassword.value
                    }
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                //forgot password
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Forgot your password?",
                        color = DefaultBlue,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.padding(PaddingLarge))
                Button(
                    onClick = {
                        if (email.value.isEmpty() || password.value.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Email and password cant be empty!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            auth.signInWithEmailAndPassword(email.value, password.value)
                                .addOnSuccessListener {
                                    navController.navigate("Home")
                                }
                                .addOnFailureListener {
                                    //Log.d("Login_Exception", it.toString())
                                    Toast.makeText(
                                        context,
                                        "Email or password are not correct",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    },
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
    //Create account
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = PaddingMedium, vertical = PaddingLarge),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = "Create an account?",
            color = DefaultWhite,
            modifier = Modifier.clickable(onClick = {
                navController.navigate("Register") {
                }
            })
        )
    }
}