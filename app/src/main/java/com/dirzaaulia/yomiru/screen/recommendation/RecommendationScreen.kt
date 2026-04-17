package com.dirzaaulia.yomiru.screen.recommendation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.dirzaaulia.yomiru.model.response.MalRecommendation
import com.dirzaaulia.yomiru.screen.component.ImageWithCaption
import com.dirzaaulia.yomiru.util.getCarouselHomeSize

@Composable
fun RecommendationScreen(
    item: MalRecommendation,
) {

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val carouselSize = getCarouselHomeSize(windowSizeClass)

    val firstEntry = item.entry?.first()
    val secondEntry = item.entry?.last()

    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImageWithCaption(
                    modifier = Modifier
                        .weight(1f)
                        .height(carouselSize.second)
                        .clip(MaterialTheme.shapes.extraLarge),
                    url = firstEntry?.images?.webp?.largeImageUrl,
                    contentDescription = firstEntry?.title,
                    caption = firstEntry?.title
                )
                ImageWithCaption(
                    modifier = Modifier
                        .weight(1f)
                        .height(carouselSize.second)
                        .clip(MaterialTheme.shapes.extraLarge),
                    url = secondEntry?.images?.webp?.largeImageUrl,
                    contentDescription = secondEntry?.title,
                    caption = secondEntry?.title
                )
            }
            Text(
                text = item.user?.username.toString(),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = item.content.toString(),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}