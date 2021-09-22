package com.example.eventmap.presentation.theme.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = DefaultBlue,
    background = DarkBlue,
    onPrimary = DefaultWhite,
    onBackground = HintGray,
    surface = DefaultWhite,
    onSurface = DefaultWhite

)

@Composable
fun EventMapTheme(content: @Composable() () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}