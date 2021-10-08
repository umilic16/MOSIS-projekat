package com.example.eventmap.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventmap.presentation.theme.ui.DarkBlue
import com.example.eventmap.presentation.theme.ui.DarkText
import com.example.eventmap.presentation.theme.ui.DefaultWhite
import com.example.eventmap.presentation.theme.ui.HintGray


@Composable
fun CustomTextField(
    text: String = "",
    hint: String = "",
    onValueChange: (String) -> Unit = {},
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(
        textColor = DarkText,
        backgroundColor = DefaultWhite,
        placeholderColor = HintGray
    ),
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    style:TextStyle = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    leadingIcon: @Composable()(() -> Unit)? = {},
    isPasswordVisible: Boolean = false,
    onPasswordToggleClick: (Boolean) -> Unit = {},
    showIcon: Boolean =  keyboardType == KeyboardType.Password
) {
    TextField(
        value = text,
        onValueChange = onValueChange,
        textStyle = style,
        placeholder = {
            Text(text = hint, fontSize = 14.sp)
        },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        visualTransformation = if (!isPasswordVisible && keyboardType == KeyboardType.Password) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = if (showIcon) {
            val icon: @Composable () -> Unit = {
                IconButton(
                    onClick = {
                        onPasswordToggleClick(!isPasswordVisible)
                    },
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) {
                            Icons.Filled.VisibilityOff
                        } else {
                            Icons.Filled.Visibility
                        },
                        tint = HintGray,
                        contentDescription = null
                    )
                }
            }
            icon
        } else null,
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 5.dp)
            .clip(RoundedCornerShape(5.dp))
            .height(50.dp),
        colors = colors,
        readOnly = readOnly,
    )
}