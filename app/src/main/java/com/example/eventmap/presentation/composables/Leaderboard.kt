package com.example.eventmap.presentation.composables

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

@Composable
fun Leaderboard(navController: NavController, viewModel: UsersViewModel) {
    val allUsers = viewModel.allUsers.value?.add(viewModel.currentUser.value!!) as List<User>
    val sortedUsers = allUsers.sortedByDescending { it.points }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.93f)

    ) {
        LazyColumn(
            modifier = Modifier
                .padding(PaddingMedium)
                .clip(RoundedCornerShape(5.dp))
                .shadow(elevation = 5.dp)
        ) {
            sortedUsers?.let{
                itemsIndexed(items = it) { i, user ->
                    LeaderboardItem(i + 1, user.email, user.points)
                }
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
            text= "points: $points",
            color= DarkBlue,
            fontSize = 18.sp
        )
    }
}