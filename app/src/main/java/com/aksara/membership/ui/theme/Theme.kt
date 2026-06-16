package com.aksara.membership.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
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

@Composable
fun AksaraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}
