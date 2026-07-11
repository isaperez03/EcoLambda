package com.example.lambdag.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColors = darkColorScheme(
    primary           = Purple80,
    onPrimary         = Color.White,
    background        = Color(0xFF121212),
    onBackground      = Color(0xFFE0E0E0),
    surface           = Color(0xFF1E1E1E),
    onSurface         = Color(0xFFE0E0E0),
    surfaceVariant    = Color(0xFF2A2A2A),
    onSurfaceVariant  = Color(0xFFB0B0B0)
)

private val LightColors = lightColorScheme(
    primary           = Purple40,
    onPrimary         = Color.White,
    background        = Color(0xFFFFFFFF),
    onBackground      = Color(0xFF1C1B1F),  // texto principal bien oscuro
    surface           = Color(0xFFFFFFFF),
    onSurface         = Color(0xFF1C1B1F),
    surfaceVariant    = Color(0xFFE0E0E0),  // bordes de outlined fields
    onSurfaceVariant  = Color(0xFF4A4A4A)   // placeholder y label
)

@Composable
fun LambdaGTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography  = Typography,
        content     = content
    )
}