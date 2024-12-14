package com.mikeapp.newideatodoapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DesignScreen() {
    val colors = listOf(
        "primary" to MaterialTheme.colorScheme.primary,
        "onPrimary" to MaterialTheme.colorScheme.onPrimary,
        "primaryContainer" to MaterialTheme.colorScheme.primaryContainer,
        "onPrimaryContainer" to MaterialTheme.colorScheme.onPrimaryContainer,
        "secondary" to MaterialTheme.colorScheme.secondary,
        "onSecondary" to MaterialTheme.colorScheme.onSecondary,
        "secondaryContainer" to MaterialTheme.colorScheme.secondaryContainer,
        "onSecondaryContainer" to MaterialTheme.colorScheme.onSecondaryContainer,
        "tertiary" to MaterialTheme.colorScheme.tertiary,
        "onTertiary" to MaterialTheme.colorScheme.onTertiary,
        "tertiaryContainer" to MaterialTheme.colorScheme.tertiaryContainer,
        "onTertiaryContainer" to MaterialTheme.colorScheme.onTertiaryContainer,
        "error" to MaterialTheme.colorScheme.error,
        "onError" to MaterialTheme.colorScheme.onError,
        "errorContainer" to MaterialTheme.colorScheme.errorContainer,
        "onErrorContainer" to MaterialTheme.colorScheme.onErrorContainer,
        "background" to MaterialTheme.colorScheme.background,
        "onBackground" to MaterialTheme.colorScheme.onBackground,
        "surface" to MaterialTheme.colorScheme.surface,
        "onSurface" to MaterialTheme.colorScheme.onSurface,
        "surfaceVariant" to MaterialTheme.colorScheme.surfaceVariant,
        "onSurfaceVariant" to MaterialTheme.colorScheme.onSurfaceVariant,
        "outline" to MaterialTheme.colorScheme.outline,
        "onSurfaceInverse" to MaterialTheme.colorScheme.inverseOnSurface,
        "surfaceInverse" to MaterialTheme.colorScheme.inverseSurface,
    )

    val scrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        colors.forEach { (colorName, color) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .background(color)
                ) {
                    Text(
                        text = colorName,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        color = if (color.isColorLight()) Color.Black else Color.White
                    )
                }
            }
        }
    }
}

private fun Color.isColorLight(): Boolean {
    // Extract RGB components, Compose color values are in the range 0.0 to 1.0
    val r = red * 255
    val g = green * 255
    val b = blue * 255

    // Convert RGB to luminance using the formula
    val luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b

    // Return true if luminance is above the threshold (considered light)
    return luminance > 128 // Threshold for light/dark (0-255 scale)
}