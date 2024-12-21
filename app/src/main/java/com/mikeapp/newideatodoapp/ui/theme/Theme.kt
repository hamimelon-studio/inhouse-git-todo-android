package com.mikeapp.newideatodoapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = BlueGrotto,
    onPrimary = FancyWhite,
    primaryContainer = BlueGrotto,
    onPrimaryContainer = FancyWhite,
    secondary = BlueGrotto,
    onSecondary = FancyWhite,
    secondaryContainer = BlueGrotto,
    onSecondaryContainer = FancyWhite,
    tertiary = NavyBlue,
    onTertiary = FancyWhite,
    tertiaryContainer = NavyBlue,
    onTertiaryContainer = FancyWhite,
    background = FancyWhite,
    onBackground = FancyBlack,
    surface = LightGray,
    onSurface = FancyBlack,
    surfaceVariant = BlueGreen,
    onSurfaceVariant = FancyBlack,
    surfaceTint = FancyBlack,
    outline = BlueGrotto,
    outlineVariant = UltraLightGray
)

private val DarkColorScheme = darkColorScheme(
    primary = BlueGrotto,
    onPrimary = FancyBlack,
    primaryContainer = BlueGrotto,
    onPrimaryContainer = FancyBlack,
    secondary = BlueGrotto,
    onSecondary = FancyWhite,
    secondaryContainer = BlueGrotto,
    onSecondaryContainer = FancyWhite,
    tertiary = NavyBlue,
    onTertiary = FancyWhite,
    tertiaryContainer = NavyBlue,
    onTertiaryContainer = FancyWhite,
    background = FancyBlack,
    onBackground = FancyWhite,
    surface = NavyBlue,
    onSurface = FancyWhite,
    surfaceVariant = FancyBlack,
    onSurfaceVariant = FancyWhite,
    surfaceTint = FancyWhite,
    outline = BlueGrotto,
    outlineVariant = LightGray
)

@Composable
fun NewIdeaTodoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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