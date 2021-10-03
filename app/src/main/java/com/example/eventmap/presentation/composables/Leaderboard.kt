package com.example.eventmap.presentation.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventmap.data.User
import com.example.eventmap.presentation.theme.ui.*
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.utils.Constants.USERS_DB
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

@Composable
fun Leaderboard(navController: NavController, usersViewModel: UsersViewModel) {
    val sortedUsers = usersViewModel.data.value.sortedByDescending { it.points }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.93f)
            .padding(PaddingMedium)
            .clip(RoundedCornerShape(5.dp))
            .shadow(elevation = 5.dp)
    ) {
        LazyColumn(
        ) {
            itemsIndexed(items = sortedUsers) { i, user ->
                LeaderboardItem(i + 1, user.email, user.points)
            }
        }
    }
    //getUsersByPoints()
}

@Composable
fun LeaderboardItem(position: Int, email: String, points: Int){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(DefaultWhite)
            .fillMaxWidth()
            .padding(PaddingMedium)
            .clip(RoundedCornerShape(5.dp))
    ){
        Text(
            text=position.toString(),
            color= DarkText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text=email,
            color= DarkBlue,
            fontSize = 16.sp
        )
        Text(
            text= "points: ${points.toString()}",
            color= DarkBlue,
            fontSize = 18.sp
        )
    }
}