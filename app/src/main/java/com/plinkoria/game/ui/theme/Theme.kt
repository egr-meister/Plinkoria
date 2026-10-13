package com.plinkoria.game.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PlinkoriaColors = darkColorScheme(
    primary = NeonMagenta,
    onPrimary = SoftWhite,
    secondary = PlasmaCyan,
    onSecondary = NightVoid,
    tertiary = PegGold,
    onTertiary = NightVoid,
    background = DeepViolet,
    onBackground = SoftWhite,
    surface = DeepViolet,
    onSurface = SoftWhite,
    surfaceVariant = RoyalPurple,
    onSurfaceVariant = MutedLavender,
    surfaceContainerHigh = RoyalPurple,
    error = WrongRed,
    onError = SoftWhite,
    outline = NeonMagenta
)

@Composable
fun PlinkoriaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PlinkoriaColors,
        typography = PlinkoriaTypography,
        content = content
    )
}
