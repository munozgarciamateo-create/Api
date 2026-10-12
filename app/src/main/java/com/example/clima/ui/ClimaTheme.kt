package com.example.clima.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val claro = lightColorScheme(
    primary = Color(0xFF176B87), onPrimary = Color.White,
    primaryContainer = Color(0xFFD7EFF7), onPrimaryContainer = Color(0xFF123543),
    background = Color(0xFFF3F7FA), surface = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE6F1EC)
)
private val oscuro = darkColorScheme(
    primary = Color(0xFF8AD0E6), primaryContainer = Color(0xFF164D62),
    background = Color(0xFF101D25), surface = Color(0xFF172A35)
)
@Composable
fun ClimaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) oscuro else claro, content = content)
}
