package com.dirzaaulia.yomiru.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

fun getCarouselHomeSize(windowSizeClass: WindowSizeClass): Pair<Dp, Dp> {
    return if (!windowSizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)) {
        Pair(200.dp, 280.dp)
    } else {
        when {
            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                // return for EXPANDED width size class
                Pair(260.dp, 364.dp)
            }

            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                // return for MEDIUM width size class
                Pair(260.dp, 364.dp)
            }

            else -> {
                // return for COMPACT width size class
                Pair(200.dp, 280.dp)
            }
        }
    }
}