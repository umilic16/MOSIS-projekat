package com.example.eventmap.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CustomTextField(
    text: String = "",
    hint: String = "",
    onValueChange: (String) -> Unit
){
    TextField(
        value= text,
        onValueChange = onValueChange,
        placeholder = {
            Text(text = hint)
        },
    modifier = Modifier.fillMaxWidth()
    )
}