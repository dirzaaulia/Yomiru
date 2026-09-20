package com.dirzaaulia.yomiru.screen.review

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.dirzaaulia.yomiru.model.MediaReview
import com.dirzaaulia.yomiru.screen.component.DynamicCircleOutlineText
import com.dirzaaulia.yomiru.screen.component.ImageWithCaption
import com.dirzaaulia.yomiru.ui.common.NetworkImage
import com.dirzaaulia.yomiru.util.getCarouselHomeSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    item: MediaReview
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val carouselSize = getCarouselHomeSize(windowSizeClass)
    val scrollState = rememberScrollState()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 900.dp)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImageWithCaption(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .size(width = carouselSize.first, height = carouselSize.second),
                    url = item.entry?.images?.webp?.largeImageUrl,
                    contentDescription = item.entry?.title,
                    caption = item.entry?.title
                )
                Spacer(modifier = Modifier.weight(1f))
                DynamicCircleOutlineText(
                    text = item.score.toString(),
                    textSyle = MaterialTheme.typography.displayLarge
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val userAvatar = item.user?.images?.webp?.imageUrl
                if (!userAvatar.isNullOrBlank()) {
                    NetworkImage(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        url = userAvatar,
                        contentDescription = item.user?.username,
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Text(
                    modifier = Modifier.weight(1f),
                    text = item.user?.username.orEmpty().ifBlank { "User Review" },
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (item.reactions != null) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if ((item.reactions.overall ?: 0) > 0) {
                        Card(border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)) {
                            Text(modifier = Modifier.padding(6.dp), text = "⭐ ${item.reactions.overall}")
                        }
                    }
                    if ((item.reactions.nice ?: 0) > 0) {
                        Card(border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)) {
                            Text(modifier = Modifier.padding(6.dp), text = "😊 ${item.reactions.nice}")
                        }
                    }
                    if ((item.reactions.loveIt ?: 0) > 0) {
                        Card(border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)) {
                            Text(modifier = Modifier.padding(6.dp), text = "❤️ ${item.reactions.loveIt}")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = item.review.orEmpty(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
}
