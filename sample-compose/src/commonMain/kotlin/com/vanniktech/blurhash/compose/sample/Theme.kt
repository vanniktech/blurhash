package com.vanniktech.blurhash.compose.sample

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable fun Theme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    darkTheme -> DarkColors
    else -> LightColors
  }
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content,
  )
}

private val LightColors = lightColorScheme()

private val DarkColors = darkColorScheme()

private val Typography = Typography()
