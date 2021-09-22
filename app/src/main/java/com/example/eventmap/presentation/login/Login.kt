package com.example.eventmap.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventmap.R
import androidx.compose.ui.unit.sp
import com.example.eventmap.components.CustomTextField
import com.example.eventmap.presentation.theme.ui.*

@Composable
fun Login(navController: NavController) {
    val userName = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    //val passwordVisibility = remember { mutableStateOf(false) }
    //val focusRequester = remember { FocusRequester() }
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
                    text = userName.value,
                    onValueChange = { userName.value = it },
                    hint = "Email address",
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
                    onClick = { navController.navigate("Home") },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(50.dp)
                ) {
                    Text(text = "Login", fontSize = 20.sp)
                }
            }
        }
        Spacer(modifier = Modifier.padding(PaddingExtra))
    }
    //Create account
    Box(
        modifier = Modifier.fillMaxSize().padding(horizontal = PaddingMedium, vertical = PaddingLarge),
        contentAlignment = Alignment.BottomStart){
        Text(
            text = "Create an account?",
            color = DefaultWhite,
            modifier = Modifier.clickable(onClick = {
                navController.navigate("Register") {
                }
            }))
    }
}