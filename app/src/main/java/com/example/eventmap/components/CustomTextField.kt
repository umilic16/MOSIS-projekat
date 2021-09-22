package com.example.eventmap.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.eventmap.presentation.theme.DarkText
import com.example.eventmap.presentation.theme.DefaultWhite
import com.example.eventmap.presentation.theme.HintGray


@Composable
fun CustomTextField(
    text: String = "",
    hint: String = "",
    onValueChange: (String) -> Unit,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(
        textColor = DarkText,
        backgroundColor = DefaultWhite
    )
){
    OutlinedTextField(
        value= text,
        onValueChange = onValueChange,
        textStyle = TextStyle(color = DefaultWhite, fontWeight = FontWeight.Bold),
        placeholder = {
            Text(text = hint, color = HintGray)
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = colors
    )
}