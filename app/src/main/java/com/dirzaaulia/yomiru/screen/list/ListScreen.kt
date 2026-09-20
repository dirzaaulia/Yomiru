package com.dirzaaulia.yomiru.screen.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dirzaaulia.yomiru.model.MediaEntry
import com.dirzaaulia.yomiru.navigation.Detail
import com.dirzaaulia.yomiru.navigation.Onboarding
import com.dirzaaulia.yomiru.screen.home.YomiruMenu
import com.dirzaaulia.yomiru.ui.common.ErrorSection
import com.dirzaaulia.yomiru.ui.common.ExpressiveFloatingBar
import com.dirzaaulia.yomiru.ui.common.NetworkImage
import com.dirzaaulia.yomiru.util.ResponseResult
import com.dirzaaulia.yomiru.util.capitalizeWords
import com.dirzaaulia.yomiru.util.getCarouselHomeSize
import com.dirzaaulia.yomiru.util.success
import com.dirzaaulia.yomiru.util.toHumanReadableError
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ListScreen(
    viewModel: ListViewModel = koinViewModel(),
    backStack: NavBackStack<NavKey>
) {
    val selectedMenu by viewModel.selectedMenu.collectAsStateWithLifecycle()
    val token by viewModel.accessToken.collectAsStateWithLifecycle("")
    val listResult by viewModel.currentList.collectAsStateWithLifecycle()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val carouselSize = getCarouselHomeSize(windowSizeClass)

    var selectedStatusFilter by remember { mutableStateOf("ALL") }

    val statusFilters = listOf(
        "ALL" to "All Statuses",
        "CURRENT" to "Watching / Reading",
        "PLANNING" to "Plan to Watch / Read",
        "COMPLETED" to "Completed",
        "PAUSED" to "Paused",
        "DROPPED" to "Dropped",
        "REPEATING" to "Repeating"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedMenu == YomiruMenu.Manga) "My Manga Library" else "My Anime Watchlist",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            ExpressiveFloatingBar(
                selectedMenu = selectedMenu,
                onMenuSelected = { menu ->
                    viewModel.setSelectedMenu(menu)
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 1200.dp)
                    .fillMaxSize()
            ) {
                if (token.isBlank()) {
                    // Non-Logged-In Guest Prompt
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp)),
                            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "ANILIST ACCOUNT REQUIRED",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Sign in with your AniList account to manage your watchlist, update episode progress, and track your library.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { backStack.add(Onboarding) },
                                    shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("SIGN IN WITH ANILIST", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    when (listResult) {
                        ResponseResult.Loading -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingIndicator(
                                modifier = Modifier.size(100.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        is ResponseResult.Success<*> -> {
                            listResult.success { groups ->
                                val allGroups = groups ?: emptyList()
                                val filteredEntries = if (selectedStatusFilter == "ALL") {
                                    allGroups.flatMap { it.entries }
                                } else {
                                    allGroups.filter { it.status.equals(selectedStatusFilter, ignoreCase = true) }
                                        .flatMap { it.entries }
                                }

                                Column(modifier = Modifier.fillMaxSize()) {
                                    // Status Filter Chips
                                    FlowRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        statusFilters.forEach { (code, label) ->
                                            val isSelected = selectedStatusFilter.equals(code, ignoreCase = true)
                                            AssistChip(
                                                onClick = { selectedStatusFilter = code },
                                                label = { Text(label) },
                                                shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                                                colors = if (isSelected) {
                                                    AssistChipDefaults.assistChipColors(
                                                        containerColor = MaterialTheme.colorScheme.primary,
                                                        labelColor = MaterialTheme.colorScheme.onPrimary
                                                    )
                                                } else AssistChipDefaults.assistChipColors()
                                            )
                                        }
                                    }

                                    if (filteredEntries.isEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "No titles in this category",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    } else {
                                        LazyVerticalGrid(
                                            columns = GridCells.Adaptive(minSize = 160.dp),
                                            modifier = Modifier.fillMaxSize(),
                                            contentPadding = PaddingValues(16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            items(filteredEntries) { item ->
                                                UserMediaListCard(
                                                    item = item,
                                                    height = 224.dp,
                                                    onClick = {
                                                        item.id?.let { id ->
                                                            backStack.add(Detail(id = id, type = if (selectedMenu == YomiruMenu.Manga) "manga" else "anime"))
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        is ResponseResult.Error -> Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ErrorSection(
                                errorMessage = (listResult as ResponseResult.Error).throwable.toHumanReadableError(),
                                doRetry = { viewModel.refreshList() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserMediaListCard(
    item: MediaEntry,
    height: Dp = 220.dp,
    onClick: () -> Unit
) {
    val listEntry = item.mediaListEntry

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
            .clickable { onClick() },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
            ) {
                NetworkImage(
                    modifier = Modifier.fillMaxSize(),
                    url = item.images?.webp?.largeImageUrl ?: item.images?.jpg?.imageUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop
                )

                if (listEntry?.status != null) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp),
                        shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Text(
                            text = listEntry.status.replace("_", " ").capitalizeWords(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (listEntry?.score != null && listEntry.score > 0.0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp),
                        shape = CutCornerShape(topEnd = 4.dp, bottomStart = 4.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = String.format(Locale.getDefault(), "%.1f", listEntry.score),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = item.title.orEmpty(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Progress: ${listEntry?.progress ?: 0} / ${item.episodes ?: "?"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
