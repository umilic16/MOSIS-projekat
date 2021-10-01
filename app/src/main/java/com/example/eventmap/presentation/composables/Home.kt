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
import com.example.eventmap.data.User
import com.example.eventmap.presentation.MainActivityViewModel
import com.example.eventmap.presentation.theme.ui.DefaultBlue
import com.example.eventmap.presentation.theme.ui.PaddingLarge
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

@Composable
fun Home(navController: NavController, viewModel: MainActivityViewModel) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("users_db").document(auth.currentUser?.uid.toString())
    docRef.get().addOnSuccessListener {
        viewModel.setCurrentUser(it.toObject<User>()!!)
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(PaddingLarge), contentAlignment = Alignment.Center) {
        Text(text = "You are home!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DefaultBlue)
    }
}

