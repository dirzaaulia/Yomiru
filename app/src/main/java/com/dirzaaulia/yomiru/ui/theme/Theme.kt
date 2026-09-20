package com.dirzaaulia.yomiru.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// Expressive Cut & Asymmetric Manga Shapes
val ExpressiveShapes = Shapes(
    extraSmall = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
    small = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
    medium = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp, topEnd = 4.dp, bottomStart = 4.dp),
    large = CutCornerShape(topStart = 20.dp, bottomEnd = 20.dp, topEnd = 6.dp, bottomStart = 6.dp),
    extraLarge = CutCornerShape(topStart = 26.dp, bottomEnd = 26.dp, topEnd = 8.dp, bottomStart = 8.dp)
)

private val lightScheme = lightColorScheme(
    primary = MinimalistPrimaryLight,
    onPrimary = MinimalistOnPrimaryLight,
    primaryContainer = MinimalistContainerLight,
    onPrimaryContainer = MinimalistOnContainerLight,
    secondary = MinimalistSecondaryLight,
    onSecondary = MinimalistOnSecondaryLight,
    secondaryContainer = MinimalistSecondaryContainerLight,
    onSecondaryContainer = MinimalistOnSecondaryContainerLight,
    tertiary = MinimalistTertiaryLight,
    onTertiary = MinimalistOnTertiaryLight,
    tertiaryContainer = MinimalistTertiaryContainerLight,
    onTertiaryContainer = MinimalistOnTertiaryContainerLight,
    background = MinimalistBackgroundLight,
    onBackground = MinimalistOnBackgroundLight,
    surface = MinimalistSurfaceLight,
    onSurface = MinimalistOnSurfaceLight,
    surfaceContainerHigh = MinimalistSurfaceContainerHighLight,
    outline = MinimalistOutlineLight,
    outlineVariant = MinimalistOutlineLight.copy(alpha = 0.3f)
)

private val darkScheme = darkColorScheme(
    primary = MinimalistPrimaryDark,
    onPrimary = MinimalistOnPrimaryDark,
    primaryContainer = MinimalistContainerDark,
    onPrimaryContainer = MinimalistOnContainerDark,
    secondary = MinimalistSecondaryDark,
    onSecondary = MinimalistOnSecondaryDark,
    secondaryContainer = MinimalistSecondaryContainerDark,
    onSecondaryContainer = MinimalistOnSecondaryContainerDark,
    tertiary = MinimalistTertiaryDark,
    onTertiary = MinimalistOnTertiaryDark,
    tertiaryContainer = MinimalistTertiaryContainerDark,
    onTertiaryContainer = MinimalistOnTertiaryContainerDark,
    background = MinimalistBackgroundDark,
    onBackground = MinimalistOnBackgroundDark,
    surface = MinimalistSurfaceDark,
    onSurface = MinimalistOnSurfaceDark,
    surfaceContainerHigh = MinimalistSurfaceContainerHighDark,
    outline = MinimalistOutlineDark,
    outlineVariant = MinimalistOutlineDark.copy(alpha = 0.3f)
)

@Composable
fun YomiruTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = ExpressiveShapes,
        content = content
    )
}
