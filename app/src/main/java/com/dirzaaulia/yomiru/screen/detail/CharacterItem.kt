package com.dirzaaulia.yomiru.screen.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dirzaaulia.yomiru.model.MalCharacterEntry

@Composable
fun CharacterVoiceActorRow(
    item: MalCharacterEntry,
    modifier: Modifier = Modifier
) {
    val character = item.character
    // Taking the first voice actor from the list as shown in the UI
    val voiceActorEntry = item.voiceActors?.firstOrNull()

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min), // Keeps height consistent
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side: Character Image
            AsyncImage(
                model = character?.images?.webp?.imageUrl,
                contentDescription = character?.name,
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )

            // Character Info
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = character?.name.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.role.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${item.favorites ?: 0} Favorites",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Right Side: Voice Actor Info
            if (voiceActorEntry != null) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = voiceActorEntry.person?.name.orEmpty(),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = voiceActorEntry.language.orEmpty(), // Usually Japanese in MAL
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Voice Actor Image
                AsyncImage(
                    model = voiceActorEntry.person?.images?.jpg?.imageUrl,
                    contentDescription = voiceActorEntry.name,
                    modifier = Modifier
                        .width(60.dp)
                        .fillMaxHeight(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

// A unique, non-boring character header inspired by MAL
// Concept: "Character Card with Aura"
// - Floating avatar with glow
// - Gradient + noise background
// - Anime-style typography hierarchy
// - Expandable stats chip row

@Composable
fun CharacterHeader(
    item: MalCharacterEntry,
    modifier: Modifier = Modifier
) {
    val character = item.character
    // Taking the first voice actor from the list as shown in the UI
    val voiceActorEntry = item.voiceActors?.firstOrNull()
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1F1B2E),
            Color(0xFF2C244A),
            Color(0xFF14121F)
        )
    )

    Box(
        modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(gradient)
    ) {
        // subtle animated glow layer
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x33B388FF),
                            Color.Transparent
                        ),
                        radius = 600f
                    )
                )
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Avatar with aura
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .shadow(24.dp, CircleShape)
                    .background(Color(0xFFB388FF), CircleShape)
                    .padding(3.dp)
            ) {
                AsyncImage(
                    model = character?.images?.webp?.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column {
                Text(
                    text = character?.name.orEmpty(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )

                Text(
                    text = item.role.orEmpty().uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFB8B5FF)
                    )
                )

                Spacer(Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatChip("❤ ${item.favorites}")
                }
            }
        }

        // Voice actor capsule (top right)
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
            shape = RoundedCornerShape(50),
            color = Color(0xFF000000).copy(alpha = 0.45f)
        ) {
            Column(Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                Text(
                    text = voiceActorEntry?.person?.name.orEmpty(),
                    style = MaterialTheme.typography.labelMedium.copy(color = Color.White)
                )
                Text(
                    text = voiceActorEntry?.language.orEmpty(),
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFB388FF))
                )
            }
        }
    }
}

@Composable
private fun StatChip(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color(0xFFB388FF).copy(alpha = 0.18f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
        )
    }
}
