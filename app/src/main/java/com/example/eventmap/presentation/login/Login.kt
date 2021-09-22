package com.example.eventmap.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventmap.R
import com.example.eventmap.presentation.theme.DefaultBlue
import com.example.eventmap.presentation.theme.ui.PaddingLarge
import com.example.eventmap.presentation.theme.ui.PaddingMedium
import com.example.eventmap.presentation.theme.ui.PaddingSmall

@Composable
fun Login(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
        ){
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            alignment = Alignment.CenterEnd
        )
        Spacer(modifier = Modifier.height(PaddingLarge))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = PaddingSmall)
        ) {
            CustomTextField(onValueChange = { /*TODO*/ })
            Spacer(modifier = Modifier.height(PaddingSmall))
            CustomTextField(onValueChange = { /*TODO*/ })
            Spacer(modifier = Modifier.height(PaddingSmall))
            Text(text="Forgot password?", color = DefaultBlue)
            Spacer(modifier = Modifier.height(PaddingMedium))
            Button(onClick = { /*TODO*/ }) {

            }
            Spacer(modifier = Modifier.height(PaddingLarge))
            Button(onClick = { /*TODO*/ }) {
                
            }
        }
    }
}