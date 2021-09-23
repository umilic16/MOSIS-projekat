package com.example.eventmap.presentation.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception

@Composable
fun Leaderboard(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(PaddingLarge), contentAlignment = Alignment.TopCenter) {
        //Log.d("UDOC", usersByPoints.toString())
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(PaddingSmall),
        ){
            getUsersByPoints()
            itemsIndexed(items=getUsersByPoints()){ i, user ->
                LeaderboardItem(position = i+1, email = user)
            }
        }
    }
}
/*fun getUsersByPoints() = CoroutineScope(Dispatchers.IO).launch {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("users-test2")
    val usersByPoints = mutableListOf<String>("testiranje@mail.com","neki@mail.com","mail123@yahoo.com")
    val users = mutableListOf<User>()
    try{
        val querySnapshot = docRef.get().await()
        for(document in querySnapshot.documents){
            val currUser = document.toObject<User>()!!
            users.add(currUser)
        }
    }catch (e:Exception){

    }
}*/

fun getUsersByPoints(): List<String> {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("users-test2")
    val usersByPoints = mutableListOf<String>("testiranje@mail.com","neki@mail.com","mail123@yahoo.com")
    docRef.orderBy("points").get().addOnSuccessListener { result ->
            for (document in result) {
                val email = document.data.get("email").toString()
                Log.d("EmailRes", email)
                usersByPoints.add(document.data["email"].toString())
                //Log.d("RES2", "${document.id} => ${document.data.get("username")}")
            }
        }
        .addOnFailureListener { exception ->
            Log.d("RES23", "Error getting documents: ", exception)
        }
    Log.d("ResList", usersByPoints.toString())
    return usersByPoints
}

@Composable
fun LeaderboardItem(position: Int, email: String){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(DefaultWhite)
            .fillMaxWidth()
            .padding(PaddingMedium)
    ){
        Text(
            text=position.toString(),
            color= DarkText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text=email,
            color= DarkBlue,
            fontSize = 14.sp
        )
    }
}