package com.example.eventmap.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventmap.R
import com.example.eventmap.presentation.theme.ui.PaddingLarge
import com.example.eventmap.presentation.theme.ui.PaddingMedium
import com.example.eventmap.presentation.theme.ui.PaddingSmall
import androidx.compose.ui.unit.sp
import com.example.eventmap.presentation.theme.*
import com.example.eventmap.presentation.theme.ui.PaddingExtra

@Composable
fun Login(navController: NavController) {
    val emailValue = remember { mutableStateOf("") }
    val passwordValue = remember { mutableStateOf("") }

    val passwordVisibility = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    //ceo container
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(DarkBlue)
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
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            //zbog testiranja
            //modifier = Modifier.background(LightGray)
        )
        {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                //username / email
                CustomTextField(
                    text = emailValue.value,
                    onValueChange = { emailValue.value = it },
                    hint = "Email address",
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                //password
                CustomTextField(
                    text = passwordValue.value,
                    onValueChange = { passwordValue.value = it },
                    hint = "Password"

                )
                //forgot password
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth().padding(PaddingMedium)) {
                    Text(
                        text = "Forgot password?",
                        color = DefaultBlue,
                        fontSize = 14.sp
                    )}
                Spacer(modifier = Modifier.padding(PaddingLarge))
                Button(
                    onClick = { TODO() },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(50.dp)
                ) {
                    Text(text = "Login", fontSize = 20.sp)
                }
            }
        }
        Spacer(modifier = Modifier.padding(PaddingExtra))
        //Create account
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth().padding(horizontal = PaddingSmall)
            //zbog testiranja
            //.background(HintGray)
        ) {
            Text(
                text = "Create account",
                color = DefaultWhite,
                modifier = Modifier.clickable(onClick = {
                    navController.navigate("Register") {
                    }
                })
            )
        }
    }
}