package com.dirzaaulia.yomiru.screen.detail

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dirzaaulia.yomiru.model.MediaEntry
import com.dirzaaulia.yomiru.model.MediaImages
import com.dirzaaulia.yomiru.model.response.PagingResponse
import com.dirzaaulia.yomiru.ui.common.NetworkImage
import com.dirzaaulia.yomiru.util.ResponseResult
import com.dirzaaulia.yomiru.util.capitalizeWords
import java.util.Locale

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailInfoScreen(
    entry: MediaEntry?,
    viewModel: DetailViewModel,
    imageSize: Pair<Dp, Dp>,
    onMediaClick: (id: String, type: String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val imagesResult by viewModel.images.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Alternative Titles & Main Metrics
        if (entry != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp, topEnd = 6.dp, bottomStart = 6.dp)),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!entry.titleEnglish.isNullOrBlank()) {
                        Text(
                            text = entry.titleEnglish,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (!entry.titleJapanese.isNullOrBlank()) {
                        Text(
                            text = entry.titleJapanese,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricBadge(label = "RANK", value = if (entry.rank != null) "#${entry.rank}" else "N/A")
                        MetricBadge(label = "POPULARITY", value = if (entry.popularity != null) "#${entry.popularity}" else "N/A")
                        MetricBadge(label = "MEMBERS", value = if (entry.members != null) "${entry.members}" else "N/A")
                    }
                }
            }
        }

        // Synopsis Section
        if (entry != null && !entry.synopsis.isNullOrBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp, topEnd = 6.dp, bottomStart = 6.dp)),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SYNOPSIS • あらすじ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = entry.synopsis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Genres Flow Row
        if (entry?.genres?.isNotEmpty() == true) {
            Column {
                Text(
                    text = "GENRES • ジャンル",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    entry.genres.forEach { genre ->
                        AssistChip(
                            onClick = {},
                            label = { Text(genre.name) },
                            shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        )
                    }
                }
            }
        }

        // Relations Section
        if (entry?.relations?.isNotEmpty() == true) {
            Column {
                Text(
                    text = "RELATIONS • 関連作品",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(entry.relations) { relation ->
                        val relEntry = relation.entry
                        if (relEntry != null) {
                            Card(
                                modifier = Modifier
                                    .width(140.dp)
                                    .clip(CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp))
                                    .clickable {
                                        relEntry.id?.let { id ->
                                            onMediaClick(id, relEntry.type ?: viewModel.type)
                                        }
                                    },
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                )
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                    ) {
                                        NetworkImage(
                                            modifier = Modifier.fillMaxSize(),
                                            url = relEntry.images?.webp?.largeImageUrl ?: relEntry.images?.webp?.imageUrl,
                                            contentScale = ContentScale.Crop
                                        )

                                        if (!relation.relationType.isNullOrBlank()) {
                                            Surface(
                                                modifier = Modifier
                                                    .align(Alignment.TopStart)
                                                    .padding(6.dp),
                                                shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                                                color = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ) {
                                                Text(
                                                    text = relation.relationType.replace("_", " "),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = relEntry.title.orEmpty(),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (!relEntry.type.isNullOrBlank()) {
                                            Text(
                                                text = relEntry.type.uppercase(),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // External Links Section
        if (entry?.externalLinks?.isNotEmpty() == true) {
            Column {
                Text(
                    text = "EXTERNAL LINKS • 外部リンク",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    entry.externalLinks.forEach { link ->
                        if (!link.url.isNullOrBlank()) {
                            AssistChip(
                                onClick = {
                                    try {
                                        val customTabsIntent = CustomTabsIntent.Builder().build()
                                        customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        customTabsIntent.launchUrl(context, Uri.parse(link.url))
                                    } catch (_: Exception) {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
                                        context.startActivity(intent)
                                    }
                                },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(link.site ?: "Link", fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                },
                                shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                )
                            )
                        }
                    }
                }
            }
        }

        // Detailed Information Grid
        if (entry != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp, topEnd = 6.dp, bottomStart = 6.dp)),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "DETAILS • 詳細",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        InfoDetailItem(label = "Format", value = entry.type?.uppercase() ?: "N/A", modifier = Modifier.weight(1f))
                        InfoDetailItem(label = "Episodes / Vols", value = entry.episodes?.toString() ?: "N/A", modifier = Modifier.weight(1f))
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        InfoDetailItem(
                            label = "Duration",
                            value = if (entry.duration != null) "${entry.duration} mins" else "N/A",
                            modifier = Modifier.weight(1f)
                        )
                        InfoDetailItem(
                            label = "Studio",
                            value = entry.studios.firstOrNull()?.name ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        InfoDetailItem(
                            label = "Season",
                            value = if (entry.season != null && entry.year != null) "${entry.season.uppercase()} ${entry.year}" else "N/A",
                            modifier = Modifier.weight(1f)
                        )
                        InfoDetailItem(label = "Status", value = entry.status ?: "N/A", modifier = Modifier.weight(1f))
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        InfoDetailItem(label = "Source", value = entry.source ?: "N/A", modifier = Modifier.weight(1f))
                        InfoDetailItem(label = "Country", value = entry.countryOfOrigin ?: "N/A", modifier = Modifier.weight(1f))
                    }

                    if (!entry.hashtag.isNullOrBlank()) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            InfoDetailItem(label = "Hashtag", value = entry.hashtag, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Image Gallery Carousel (Only displayed if 3 or more gallery images exist)
        val galleryList = (imagesResult as? ResponseResult.Success<*>)?.data?.let {
            @Suppress("UNCHECKED_CAST")
            (it as? PagingResponse<List<MediaImages>>)?.data.orEmpty()
        }.orEmpty()

        if (galleryList.size >= 3) {
            Column {
                Text(
                    text = "GALLERY • ギャラリー",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                val carouselState = rememberCarouselState { galleryList.count() }
                HorizontalMultiBrowseCarousel(
                    state = carouselState,
                    modifier = Modifier.fillMaxWidth(),
                    preferredItemWidth = imageSize.first,
                    itemSpacing = 10.dp
                ) { i ->
                    val item = galleryList[i]
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
                    ) {
                        NetworkImage(
                            modifier = Modifier.fillMaxSize(),
                            url = item.webp?.maximumImageUrl ?: item.webp?.largeImageUrl,
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SaveMediaListSheet(
    entry: MediaEntry,
    onSave: (status: String, score: Double, progress: Int) -> Unit,
    onDelete: (listEntryId: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val listEntry = entry.mediaListEntry
    var selectedStatus by remember { mutableStateOf(listEntry?.status ?: "CURRENT") }
    var progress by remember { mutableIntStateOf(listEntry?.progress ?: 0) }
    var score by remember { mutableFloatStateOf((listEntry?.score ?: 0.0).toFloat()) }

    val isManga = entry.type.equals("MANGA", ignoreCase = true)

    val statusOptions = if (isManga) {
        listOf(
            "CURRENT" to "Reading",
            "PLANNING" to "Plan to Read",
            "COMPLETED" to "Completed",
            "PAUSED" to "Paused",
            "DROPPED" to "Dropped",
            "REPEATING" to "Rereading"
        )
    } else {
        listOf(
            "CURRENT" to "Watching",
            "PLANNING" to "Plan to Watch",
            "COMPLETED" to "Completed",
            "PAUSED" to "Paused",
            "DROPPED" to "Dropped",
            "REPEATING" to "Rewatching"
        )
    }

    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "MANAGE LIST ENTRY • リスト編集",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        // Status Chips
        Text(text = "Status", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            statusOptions.forEach { (code, label) ->
                val isSelected = selectedStatus.equals(code, ignoreCase = true)
                Surface(
                    modifier = Modifier.clickable { selectedStatus = code },
                    shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Progress Stepper with Number TextField & Limit Guard
        val progressLabel = if (isManga) "Chapter Progress" else "Episode Progress"
        val knownLimit = if (isManga) entry.chapters else entry.episodes
        val maxProgress = knownLimit ?: 9999
        val maxDisplay = knownLimit?.toString() ?: "?"

        var progressText by remember(progress) { mutableStateOf(progress.toString()) }

        Text(text = progressLabel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    if (progress > 0) {
                        progress--
                        progressText = progress.toString()
                    }
                },
                enabled = progress > 0,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease Progress",
                    tint = if (progress > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }

            Surface(
                shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        value = progressText,
                        onValueChange = { input ->
                            val cleanDigits = input.filter { it.isDigit() }
                            if (cleanDigits.isEmpty()) {
                                progressText = ""
                                progress = 0
                            } else {
                                val parsed = cleanDigits.toIntOrNull() ?: 0
                                val clamped = parsed.coerceIn(0, maxProgress)
                                progress = clamped
                                progressText = clamped.toString()
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.width(76.dp),
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "/ $maxDisplay",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            IconButton(
                onClick = {
                    if (progress < maxProgress) {
                        progress++
                        progressText = progress.toString()
                    }
                },
                enabled = progress < maxProgress,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase Progress",
                    tint = if (progress < maxProgress) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }

        // Score Slider
        Text(
            text = "Score: ${String.format(Locale.getDefault(), "%.1f", score)} / 10.0",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Slider(
            value = score,
            onValueChange = { score = it },
            valueRange = 0f..10f,
            steps = 20
        )

        // Save CTA Button
        Button(
            onClick = {
                onSave(selectedStatus, score.toDouble(), progress)
                onDismiss()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp)
        ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("SAVE TO ANILIST • 保存", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        // Remove Button (if entry exists)
        if (listEntry?.id != null) {
            OutlinedButton(
                onClick = {
                    onDelete(listEntry.id)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp)
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("REMOVE FROM LIST • 削除", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MetricBadge(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun InfoDetailItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
