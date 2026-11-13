package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ChemPrimaryDark,
    onPrimary = Color(0xFF00354E),
    primaryContainer = Color(0xFF004D70),
    onPrimaryContainer = Color(0xFFC7E7FF),
    secondary = ChemSecondaryDark,
    onSecondary = Color(0xFF003824),
    secondaryContainer = Color(0xFF005236),
    onSecondaryContainer = Color(0xFF86F8BF),
    tertiary = ChemTertiaryDark,
    onTertiary = Color(0xFF432C00),
    tertiaryContainer = Color(0xFF604100),
    onTertiaryContainer = Color(0xFFFFDF9E),
    background = ChemNavyDark,
    onBackground = Color(0xFFE2E8F0),
    surface = ChemSurfaceDark,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = ChemSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFCBD5E1),
    error = ChemErrorDark,
    onError = Color(0xFF500000)
)

private val LightColorScheme = lightColorScheme(
    primary = ChemPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC7E7FF),
    onPrimaryContainer = Color(0xFF001E2E),
    secondary = ChemSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF86F8BF),
    onSecondaryContainer = Color(0xFF002114),
    tertiary = ChemTertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDF9E),
    onTertiaryContainer = Color(0xFF261800),
    background = ChemBackgroundLight,
    onBackground = Color(0xFF0F172A),
    surface = ChemSurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = ChemSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF475569),
    error = ChemErrorLight,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded sci-fi chemistry palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
