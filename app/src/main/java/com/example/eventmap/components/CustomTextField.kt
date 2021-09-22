package com.example.eventmap.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.eventmap.presentation.theme.ui.DarkText
import com.example.eventmap.presentation.theme.ui.DefaultWhite
import com.example.eventmap.presentation.theme.ui.HintGray


@Composable
fun CustomTextField(
    text: String = "",
    hint: String = "",
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(
        textColor= DarkText,
        /*disabledTextColor= DefaultWhite,*/
        backgroundColor= DefaultWhite,/*
        cursorColor= DefaultWhite,
        errorCursorColor= DefaultWhite,
        focusedBorderColor= DefaultWhite,
        unfocusedBorderColor= DefaultWhite,
        disabledBorderColor= DefaultWhite,
        errorBorderColor= DefaultWhite,
        leadingIconColor= DefaultWhite,
        disabledLeadingIconColor= DefaultWhite,
        errorLeadingIconColor= DefaultWhite,
        trailingIconColor= DefaultWhite,
        disabledTrailingIconColor= DefaultWhite,
        errorTrailingIconColor= DefaultWhite,
        focusedLabelColor= DefaultWhite,
        unfocusedLabelColor= DefaultWhite,
        disabledLabelColor= DefaultWhite,
        errorLabelColor= DefaultWhite,
        placeholderColor= DefaultWhite,
        disabledPlaceholderColor= DefaultWhite*/
    )
) {
    TextField(
        value = text,
        onValueChange = onValueChange,
        textStyle = TextStyle(//color = DefaultWhite,
            fontWeight = FontWeight.Bold, fontSize = 16.sp
        ),
        placeholder = {
            Text(text = hint, color = HintGray)
        },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = colors
    )
}