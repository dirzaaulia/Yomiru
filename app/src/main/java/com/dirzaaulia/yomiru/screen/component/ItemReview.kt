package com.dirzaaulia.yomiru.screen.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.dirzaaulia.yomiru.model.MediaReview
import com.dirzaaulia.yomiru.ui.common.NetworkImage

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ItemReview(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass,
    imageWidth: Dp,
    imageHeight: Dp,
    item: MediaReview
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(CutCornerShape(topStart = 20.dp, bottomEnd = 20.dp, topEnd = 6.dp, bottomStart = 6.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            val userAvatar = item.user?.images?.webp?.imageUrl ?: item.user?.images?.jpg?.imageUrl
            val username = item.user?.username

            // Header Row: User Info (only if username or avatar exists) & Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!username.isNullOrBlank() || !userAvatar.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!userAvatar.isNullOrBlank()) {
                            NetworkImage(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                url = userAvatar,
                                contentDescription = username,
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        if (!username.isNullOrBlank()) {
                            Column {
                                Text(
                                    text = username,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (!item.date.isNullOrBlank()) {
                                    Text(
                                        text = item.date.take(10),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                } else if (!item.date.isNullOrBlank()) {
                    Text(
                        text = item.date.take(10),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Score Badge
                if (item.score != null) {
                    Surface(
                        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${item.score}/10",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Middle Content Row: Poster Thumbnail + Entry Title & Review Text Excerpt
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                if (item.entry != null) {
                    Card(
                        modifier = Modifier
                            .width(75.dp)
                            .height(108.dp)
                            .clip(CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        NetworkImage(
                            modifier = Modifier.fillMaxSize(),
                            url = item.entry.images?.webp?.largeImageUrl ?: item.entry.images?.jpg?.largeImageUrl,
                            contentDescription = item.entry.title,
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    if (!item.entry?.title.isNullOrBlank()) {
                        Text(
                            text = item.entry?.title.orEmpty(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Text(
                        text = item.review.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Bottom Reactions Row
            if (item.reactions != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if ((item.reactions.loveIt ?: 0) > 0) {
                        ReactionChip(emoji = "❤️", count = item.reactions.loveIt ?: 0)
                    }
                    if ((item.reactions.nice ?: 0) > 0) {
                        ReactionChip(emoji = "😊", count = item.reactions.nice ?: 0)
                    }
                    if ((item.reactions.overall ?: 0) > 0) {
                        ReactionChip(emoji = "⭐", count = item.reactions.overall ?: 0)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReactionChip(
    emoji: String,
    count: Int
) {
    Surface(
        shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
    ) {
        Text(
            text = "$emoji $count",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
