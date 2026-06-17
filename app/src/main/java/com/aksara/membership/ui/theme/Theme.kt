package com.aksara.membership.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = OnBrand,
    primaryContainer = IndigoLight,
    onPrimaryContainer = OnBrand,
    secondary = GoldAccent,
    onSecondary = OnBrand,
    background = PaperBg,
    onBackground = TextDark,
    surface = PaperBg,
    onSurface = TextDark,
    surfaceVariant = LavenderCard,
    onSurfaceVariant = TextDark
)

private val DarkColors = darkColorScheme(
    primary = IndigoPrimaryDarkScheme,
    onPrimary = IndigoDark,
    primaryContainer = IndigoDark,
    onPrimaryContainer = TextLight,
    secondary = GoldAccent,
    onSecondary = IndigoDark,
    background = DarkBg,
    onBackground = TextLight,
    surface = DarkSurface,
    onSurface = TextLight,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextLight
)

@Composable
fun AksaraTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
