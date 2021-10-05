package com.example.eventmap.presentation.composables

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventmap.R
import com.example.eventmap.components.CustomTextField
import com.example.eventmap.components.ImageHolder
import com.example.eventmap.data.User
import com.example.eventmap.presentation.theme.ui.*
import com.example.eventmap.presentation.viewmodels.UsersViewModel
import com.example.eventmap.services.FirebaseService.Companion.token
import com.example.eventmap.utils.saveUser
import com.google.firebase.auth.FirebaseAuth


@Composable
fun Register(navController: NavController, viewModel: UsersViewModel) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val repeatedPassword = remember { mutableStateOf("") }
    val showPassword = remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    //slika
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap>(BitmapFactory.decodeResource(context.resources, R.drawable.profile_circle)) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        //zbog ispitivanja uslova
        imageUri=uri
        if(imageUri != null) {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap.value = MediaStore.Images
                    .Media.getBitmap(context.contentResolver, imageUri)

            } else {
                val source = ImageDecoder
                    .createSource(context.contentResolver, imageUri!!)
                bitmap.value = ImageDecoder.decodeBitmap(source)
            }
        }
    }
    //ceo container
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(PaddingMedium)
    ) {
        //image
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            //Log.d("Image_debug", imageUri.toString())
            ImageHolder(
                bitmap = bitmap.value.asImageBitmap(),
                modifier = Modifier
                    .size(120.dp)
                    .clickable {
                        launcher.launch("image/*")
                    }
            )
        }
        Spacer(modifier = Modifier.padding(PaddingSmall))
        Text(
            text = "Select image",
            color = DefaultWhite,
            modifier = Modifier.clickable(onClick = {
                launcher.launch("image/*")
            })
        )
        //input boxes i forgot pass
        Spacer(modifier = Modifier.padding(PaddingMedium))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                //email
                CustomTextField(
                    text = email.value,
                    onValueChange = { email.value = it },
                    hint = "Email address",
                    keyboardType = KeyboardType.Email,
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                //password
                CustomTextField(
                    text = password.value,
                    onValueChange = { password.value = it },
                    hint = "Password",
                    keyboardType = KeyboardType.Password,
                    isPasswordVisible = showPassword.value,
                    showIcon = false
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                //repeat password
                CustomTextField(
                    text = repeatedPassword.value,
                    onValueChange = { repeatedPassword.value = it },
                    hint = "Repeat password",
                    keyboardType = KeyboardType.Password,
                    isPasswordVisible = showPassword.value,
                    showIcon = false
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text =
                        if (showPassword.value) {
                            "Hide password"
                        } else {
                            "Show password"
                        },
                        color = DefaultWhite,
                        modifier = Modifier.clickable(onClick = {
                            showPassword.value = !showPassword.value
                        })
                    )
                }
                Spacer(modifier = Modifier.padding(PaddingMedium))
                Button(
                    onClick = {
                        if (email.value.isEmpty() || password.value.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Email and password cant be empty!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (password.value != repeatedPassword.value) {
                            Toast.makeText(
                                context,
                                "Passwords dont match!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (imageUri == null) {
                            Toast.makeText(
                                context,
                                "Choose an profile picture!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            auth.createUserWithEmailAndPassword(email.value, password.value)
                                .addOnSuccessListener {
                                    val user = User(
                                        userId = it.user?.uid.toString(),
                                        email = email.value,
                                        password = password.value,
                                        token = token!!
                                    )
                                    saveUser(
                                        user, imageUri!!
                                    )
                                    //viewModel.setCurrentUser(user)
                                    viewModel.setCurrentPicture(bitmap = bitmap.value)
                                    viewModel.setLoggedIn(true)
                                    //setCurrentPicture(viewModel)
                                    //Log.d("LogDebug", navController.graph.startDestinationRoute.toString())
                                    navController.popBackStack("Login", true)
                                    navController.navigate("Home")
                                }
                                .addOnFailureListener {
                                    //Log.d("Register_Exception", it.toString())
                                    Toast.makeText(
                                        context,
                                        "Authentication error\n$it.localizedMessage",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(40.dp)
                        .shadow(elevation = 5.dp)
                ) {
                    Text(text = "Register", fontSize = 18.sp)
                }
            }
        }
        Spacer(modifier = Modifier.padding(PaddingExtra))
    }
    //Sign in
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = PaddingMedium, vertical = PaddingLarge),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = "Already have an account?",
            color = DefaultWhite,
            modifier = Modifier.clickable(onClick = {
                navController.navigate("Login") {
                }
            })
        )
    }
}

@Composable
fun ImageContainer() {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(BitmapFactory.decodeResource(context.resources, R.drawable.profile_circle)) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        //zbog ispitivanja uslova
        imageUri=uri
        if (Build.VERSION.SDK_INT < 28) {
            bitmap.value = MediaStore.Images
                .Media.getBitmap(context.contentResolver, imageUri)

        } else {
            val source = ImageDecoder
                .createSource(context.contentResolver, imageUri!!)
            bitmap.value = ImageDecoder.decodeBitmap(source)
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Log.d("Image_debug", imageUri.toString())
        bitmap.value?.let { btm ->
            ImageHolder(
                bitmap = btm.asImageBitmap(),
                modifier = Modifier
                    .size(150.dp)
                    .clickable {
                        launcher.launch("image/*")
                    }
            )
        }
    }
    Spacer(modifier = Modifier.padding(PaddingSmall))
    Text(
        text = "Select image",
        color = DefaultWhite,
        modifier = Modifier.clickable(onClick = {
            launcher.launch("image/*")
        })
    )
}