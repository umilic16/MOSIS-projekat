package com.example.eventmap.utils

import android.net.Uri
import com.example.eventmap.data.User
import com.google.firebase.auth.FirebaseAuth

fun getRandomString(length: Int) : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

/*fun addRandomUsers(numOfUsers: Int, lengthOfEmail: Int){
    for (i in 0..numOfUsers){
        val email = "${getRandomString(lengthOfEmail)}@gmail.com"
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, "test123")
            .addOnSuccessListener {
                saveUser(
                    User(email = email, username = "test123")
                )
            }
    }
}*/