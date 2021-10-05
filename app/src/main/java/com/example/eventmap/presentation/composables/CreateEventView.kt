package com.example.eventmap.presentation.composables

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventmap.components.CustomTextField
import com.example.eventmap.data.Event
import com.example.eventmap.presentation.MainActivity.Companion.fusedLocationProviderClient
import com.example.eventmap.presentation.theme.ui.*
import com.example.eventmap.utils.saveEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint

@SuppressLint("MissingPermission")
@Composable
fun CreateEventView(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val title = remember { mutableStateOf("") }
    val desc = remember { mutableStateOf("") }
    val lat = remember { mutableStateOf("") }
    val lng = remember { mutableStateOf("") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(PaddingMedium)
    ) {
        Spacer(modifier = Modifier.padding(PaddingExtra))
        Text(
            text = "Create an event",
            color = DefaultWhite,
            fontSize = 22.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(PaddingMedium))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                //email
                CustomTextField(
                    text = title.value,
                    onValueChange = { title.value = it },
                    hint = "Title",
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                CustomTextField(
                    text = desc.value,
                    onValueChange = { desc.value = it },
                    hint = "Description",
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                CustomTextField(
                    text = lat.value,
                    onValueChange = { lat.value = it },
                    hint = "Latitude",
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                CustomTextField(
                    text = lng.value,
                    onValueChange = { lng.value = it },
                    hint = "Longitude",
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.padding(PaddingSmall))
                Text(
                    text = "Use current location",
                    color = DefaultWhite,
                    modifier = Modifier.clickable(onClick = {
                        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                            lat.value = it.latitude.toString()
                            lng.value = it.longitude.toString()
                        }
                    })
                )
                Spacer(modifier = Modifier.padding(PaddingLarge))
                Button(
                    onClick = {
                        if (title.value.isEmpty() || lat.value.isEmpty() || lng.value.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Fields are empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val event = Event(
                                userId = auth.currentUser?.uid.toString(),
                                title = title.value,
                                description = desc.value,
                                location = GeoPoint(lat.value.toDouble(), lng.value.toDouble())
                            )
                            saveEvent(event)
                            Toast.makeText(
                                context,
                                "Event created",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("Home")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(50.dp)
                        .shadow(elevation = 5.dp)
                ) {
                    Text(text = "Add event", fontSize = 20.sp)
                }
            }
        }
        Spacer(modifier = Modifier.padding(PaddingExtra))
    }
}