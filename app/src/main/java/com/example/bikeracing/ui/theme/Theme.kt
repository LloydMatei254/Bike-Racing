package com.example.bikeracing.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RacingColorScheme = darkColorScheme(
    primary = RacingRed,
    onPrimary = TextPrimary,
    primaryContainer = RacingRedDark,
    onPrimaryContainer = TextPrimary,
    secondary = NeonOrange,
    onSecondary = DarkBg,
    secondaryContainer = Color(0xFF3A1800),
    onSecondaryContainer = NeonOrange,
    tertiary = ElectricBlue,
    onTertiary = TextPrimary,
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextSecondary,
    error = RacingRed,
    onError = TextPrimary,
    outline = DarkDivider,
)

@Composable
fun BikeRacingTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RacingColorScheme,
        typography = Typography,
        content = content,
    )
}

// Helper to avoid importing both Color files
private fun Color(value: Long) = androidx.compose.ui.graphics.Color(value)
