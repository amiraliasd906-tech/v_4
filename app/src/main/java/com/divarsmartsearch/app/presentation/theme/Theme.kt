package com.divarsmartsearch.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = TerracottaLight,
    onPrimary = Color.White,
    primaryContainer = TerracottaLightContainer,
    onPrimaryContainer = OnTerracottaContainerLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    outline = OutlineLight,
    error = ErrorLight,
)

private val DarkColors = darkColorScheme(
    primary = TerracottaDark,
    onPrimary = Color(0xFF5C1A0C),
    primaryContainer = TerracottaDarkContainer,
    onPrimaryContainer = OnTerracottaContainerDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    outline = OutlineDark,
    error = ErrorDark,
)

/**
 * @param darkTheme when null, follows the system setting; when non-null,
 * this overrides it (used to honor the in-app "حالت شب" toggle in Settings).
 */
@Composable
fun DivarSmartSearchTheme(
    darkTheme: Boolean? = null,
    content: @Composable () -> Unit,
) {
    val useDark = darkTheme ?: isSystemInDarkTheme()
    val colorScheme = if (useDark) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
