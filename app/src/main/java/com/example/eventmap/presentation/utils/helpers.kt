package com.example.eventmap.presentation.utils

import com.google.firebase.auth.FirebaseAuth

fun checkIfLoggedIn(): Boolean {
    val auth = FirebaseAuth.getInstance()
    return auth.currentUser==null
}