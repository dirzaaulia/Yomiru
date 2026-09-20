package com.dirzaaulia.yomiru.screen.recommendation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.dirzaaulia.yomiru.model.response.MediaRecommendation
import com.dirzaaulia.yomiru.screen.component.ImageWithCaption
import com.dirzaaulia.yomiru.util.getCarouselHomeSize

@Composable
fun RecommendationScreen(
    item: MediaRecommendation,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val carouselSize = getCarouselHomeSize(windowSizeClass)

    val firstEntry = item.entry.firstOrNull()
    val secondEntry = item.entry.lastOrNull()

    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (firstEntry != null) {
                    ImageWithCaption(
                        modifier = Modifier
                            .weight(1f)
                            .height(carouselSize.second)
                            .clip(MaterialTheme.shapes.extraLarge),
                        url = firstEntry.images?.webp?.largeImageUrl,
                        contentDescription = firstEntry.title,
                        caption = firstEntry.title
                    )
                }
                if (secondEntry != null) {
                    ImageWithCaption(
                        modifier = Modifier
                            .weight(1f)
                            .height(carouselSize.second)
                            .clip(MaterialTheme.shapes.extraLarge),
                        url = secondEntry.images?.webp?.largeImageUrl,
                        contentDescription = secondEntry.title,
                        caption = secondEntry.title
                    )
                }
            }
        }
    }
}
