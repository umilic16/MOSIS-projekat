package com.example.eventmap.presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventmap.presentation.theme.ui.DefaultBlue
import com.example.eventmap.presentation.theme.ui.PaddingLarge
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AccountView(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(PaddingLarge), contentAlignment = Alignment.Center) {
        Text(text = "You are on account view!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DefaultBlue)
    }
}