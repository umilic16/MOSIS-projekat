package com.example.eventmap.presentation.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventmap.data.User
import com.example.eventmap.presentation.theme.ui.*
import com.example.eventmap.presentation.utils.addUsersListener
import com.example.eventmap.presentation.viewmodels.MainActivityViewModel
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.utils.setCurrentUser

@Composable
fun Home(navController: NavController,viewModel: MainActivityViewModel, usersViewModel: UsersViewModel) {
    //Log.d("LogDebug",navController.previousBackStackEntry.toString())
    val sortedUsers = usersViewModel.data.value
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
            items(items = sortedUsers) { user ->
                UserLazyColumn(user = user)
            }
        }
    }
}

@Composable
fun UserLazyColumn(user: User){
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
            text=user.email,
            color= DarkBlue,
            fontSize = 16.sp
        )
    }
}

