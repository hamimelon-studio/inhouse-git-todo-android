package com.mikeapp.newideatodoapp.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Suppress("DEPRECATION")
@Composable
fun SetSystemBarsColor() {
    val view = LocalView.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val isDarkColor = calculateIsDarkColor(primaryColor)

    LaunchedEffect(view, primaryColor) {
        val window = (view.context as Activity).window
        window.statusBarColor = primaryColor.toArgb()

        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !isDarkColor
        }
    }
}

private fun calculateIsDarkColor(color: Color): Boolean {
    val luminance = 0.299 * color.red + 0.587 * color.green + 0.114 * color.blue
    return luminance < 0.5
}