package com.example.eventmap.presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventmap.presentation.theme.ui.DefaultBlue
import com.example.eventmap.presentation.theme.ui.PaddingLarge
import com.example.eventmap.presentation.utils.BottomNavBar
import com.example.eventmap.presentation.utils.BottomNavItem
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Home(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    Scaffold(
        bottomBar = {
            BottomNavBar(
                showBar = !(currentRoute in listOf("Login","Register")),
                items = listOf(
                    BottomNavItem(
                        name = "Home",
                        route = "Home",
                        icon = Icons.Default.Home
                    ),
                    BottomNavItem(
                        name = "Map",
                        route = "Map",
                        icon = Icons.Default.Map
                    ),
                    BottomNavItem(
                        name = "New Event",
                        route = "CreateEvent",
                        icon = Icons.Default.PostAdd
                    ),
                    BottomNavItem(
                        name = "Leaderboard",
                        route = "Leaderboard",
                        icon = Icons.Default.Leaderboard
                    ),
                    BottomNavItem(
                        name = "Account",
                        route = "Account",
                        icon = Icons.Default.AccountCircle
                    )
                ),
                navController = navController
            ) {
                navController.navigate(it.route)
            }
        }) {

    }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(PaddingLarge), contentAlignment = Alignment.Center) {
        Text(text = "You are home!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DefaultBlue)
    }
}