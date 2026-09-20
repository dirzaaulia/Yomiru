package com.dirzaaulia.yomiru.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

/**
 * Calculates adaptive media poster card dimensions (Width, Height)
 * based on the active WindowSizeClass matrix.
 */
fun getCarouselHomeSize(windowSizeClass: WindowSizeClass): Pair<Dp, Dp> {
    return if (!windowSizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)) {
        // Phone Landscape (Compact Height < 480dp)
        Pair(170.dp, 238.dp)
    } else {
        when {
            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                // Large Tablets / Desktop / Unfolded Foldable Landscape (> 840dp)
                Pair(220.dp, 308.dp)
            }

            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                // Foldables in Portrait / Small Tablets / Medium Width (600dp - 840dp)
                Pair(200.dp, 280.dp)
            }

            else -> {
                // Standard Phone Portrait (< 600dp)
                Pair(160.dp, 224.dp)
            }
        }
    }
}
