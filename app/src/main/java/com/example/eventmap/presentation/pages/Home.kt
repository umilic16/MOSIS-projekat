package com.example.eventmap.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventmap.presentation.theme.ui.DefaultBlue
import com.example.eventmap.presentation.theme.ui.PaddingLarge

@Composable
fun Home(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().padding(PaddingLarge), contentAlignment = Alignment.Center) {
        Text(text = "You are home!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DefaultBlue)
    }
}