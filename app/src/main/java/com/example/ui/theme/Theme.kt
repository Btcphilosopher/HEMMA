package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = IkeaYellow,
    secondary = SoftGray,
    background = ScandiCharcoal,
    surface = CharcoalGray80,
    onPrimary = ScandiCharcoal,
    onBackground = LightCream,
    onSurface = LightCream
  )

private val LightColorScheme =
  lightColorScheme(
    primary = IkeaBlue,
    secondary = IkeaYellow,
    background = LightCream,
    surface = ScandiOak,
    onPrimary = Color.White,
    onSecondary = ScandiCharcoal,
    onBackground = ScandiCharcoal,
    onSurface = ScandiCharcoal
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disabling dynamic colors to force the premium Scandinavian Editorial palette of warm sand, cobalt blue, and golden yellow.
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
