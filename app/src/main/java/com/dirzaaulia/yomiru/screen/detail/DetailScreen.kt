package com.dirzaaulia.yomiru.screen.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.dirzaaulia.yomiru.model.MediaCharacterEntry
import com.dirzaaulia.yomiru.model.MediaEntry
import com.dirzaaulia.yomiru.model.MediaReviewItem
import com.dirzaaulia.yomiru.model.MediaStaffItem
import com.dirzaaulia.yomiru.screen.detail.anime.DetailEpisodeScreen
import com.dirzaaulia.yomiru.ui.common.ErrorSection
import com.dirzaaulia.yomiru.ui.common.MediaEntryCard
import com.dirzaaulia.yomiru.ui.common.NetworkImage
import com.dirzaaulia.yomiru.util.ResponseResult
import com.dirzaaulia.yomiru.util.capitalizeWords
import com.dirzaaulia.yomiru.util.getCarouselHomeSize
import com.dirzaaulia.yomiru.util.success
import com.dirzaaulia.yomiru.util.toHumanReadableError
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = koinViewModel(),
    onMediaClick: (id: String, type: String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val entryResult by viewModel.mediaEntry.collectAsStateWithLifecycle()
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val windowSizeClass = adaptiveInfo.windowSizeClass
    val isAnime = viewModel.type.equals("anime", ignoreCase = true)

    val isWideWidth = windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isCompactHeight = !windowSizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)

    var showListSheet by remember { mutableStateOf(false) }
    val watchlistDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var notificationMessage by remember { mutableStateOf("") }
    var showNotification by remember { mutableStateOf(false) }

    LaunchedEffect(showNotification) {
        if (showNotification) {
            delay(3000)
            showNotification = false
        }
    }

    val triggerNotification = { msg: String ->
        notificationMessage = msg
        showNotification = true
    }

    LaunchedEffect(showListSheet) {
        if (showListSheet && isWideWidth) {
            watchlistDrawerState.open()
        } else if (!showListSheet && isWideWidth) {
            watchlistDrawerState.close()
        }
    }

    // Single-pane Destinations
    var selectedAnimeDestination by rememberSaveable {
        mutableStateOf(DetailAnimeTabDestination.INFO)
    }
    var selectedMangaDestination by rememberSaveable {
        mutableStateOf(DetailMangaTabDestination.INFO)
    }

    // Dual-pane Destinations for Large Screens
    var leftAnimeDestination by rememberSaveable { mutableStateOf(DetailAnimeTabDestination.INFO) }
    var rightAnimeDestination by rememberSaveable { mutableStateOf(DetailAnimeTabDestination.CHARACTERS) }
    var leftMangaDestination by rememberSaveable { mutableStateOf(DetailMangaTabDestination.INFO) }
    var rightMangaDestination by rememberSaveable { mutableStateOf(DetailMangaTabDestination.CHARACTERS) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (entryResult) {
            ResponseResult.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.padding(32.dp),
                        shape = CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            LoadingIndicator(
                                modifier = Modifier.size(120.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "LOADING MEDIA • 読み込み中",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            is ResponseResult.Success<*> -> {
                entryResult.success { response ->
                    val data = response?.data

                    val scaffoldContent = @Composable {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            containerColor = MaterialTheme.colorScheme.background,
                            floatingActionButtonPosition = FabPosition.End,
                            floatingActionButton = {
                                if (data != null) {
                                    FloatingWatchlistFab(
                                        data = data,
                                        onClick = { showListSheet = true }
                                    )
                                }
                            }
                        ) { paddingValues ->
                            if (isWideWidth && data != null) {
                                // Large Screen Layout: Full Cover Background Image + Top Bar Metadata + Dual-Pane Split with Custom Tab Selectors
                                Box(modifier = Modifier.fillMaxSize()) {
                                    NetworkImage(
                                        modifier = Modifier.fillMaxSize(),
                                        url = data.images?.webp?.maximumImageUrl ?: data.images?.webp?.largeImageUrl,
                                        contentDescription = data.title,
                                        contentScale = ContentScale.Crop
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color.Black.copy(alpha = 0.75f),
                                                        Color.Black.copy(alpha = 0.90f),
                                                        Color.Black.copy(alpha = 0.98f)
                                                    )
                                                )
                                            )
                                    )

                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(paddingValues)
                                    ) {
                                        // Top Bar Metadata Header
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 20.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                if (data.score != null && data.score > 0.0) {
                                                    Surface(
                                                        shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                                                        color = MaterialTheme.colorScheme.primary,
                                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp))
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text(
                                                                text = String.format(Locale.getDefault(), "%.1f", data.score),
                                                                style = MaterialTheme.typography.labelMedium,
                                                                fontWeight = FontWeight.Black
                                                            )
                                                        }
                                                    }
                                                }

                                                if (!data.type.isNullOrBlank()) {
                                                    Surface(
                                                        shape = CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp),
                                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                        contentColor = MaterialTheme.colorScheme.onSurface
                                                    ) {
                                                        Text(
                                                            text = data.type.uppercase(),
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                        )
                                                    }
                                                }

                                                Text(
                                                    text = data.title.orEmpty(),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color.White,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }

                                        // 2-Pane Dual Split Row with Custom User Dropdown Selectors
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            // Left Column Pane
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight()
                                            ) {
                                                PaneTabDropdownSelector(
                                                    paneLabel = "LEFT PANE • 左側",
                                                    isAnime = isAnime,
                                                    selectedAnimeDestination = leftAnimeDestination,
                                                    selectedMangaDestination = leftMangaDestination,
                                                    oppositeAnimeDestination = rightAnimeDestination,
                                                    oppositeMangaDestination = rightMangaDestination,
                                                    onSelectAnime = { newDest ->
                                                        leftAnimeDestination = newDest
                                                        if (rightAnimeDestination == newDest) {
                                                            rightAnimeDestination = DetailAnimeTabDestination.entries.first { it != newDest }
                                                        }
                                                    },
                                                    onSelectManga = { newDest ->
                                                        leftMangaDestination = newDest
                                                        if (rightMangaDestination == newDest) {
                                                            rightMangaDestination = DetailMangaTabDestination.entries.first { it != newDest }
                                                        }
                                                    }
                                                )

                                                DetailTabBody(
                                                    isAnime = isAnime,
                                                    selectedAnimeDestination = leftAnimeDestination,
                                                    selectedMangaDestination = leftMangaDestination,
                                                    data = data,
                                                    viewModel = viewModel,
                                                    windowSizeClass = windowSizeClass,
                                                    onMediaClick = onMediaClick
                                                )
                                            }

                                            // Right Column Pane
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight()
                                            ) {
                                                PaneTabDropdownSelector(
                                                    paneLabel = "RIGHT PANE • 右側",
                                                    isAnime = isAnime,
                                                    selectedAnimeDestination = rightAnimeDestination,
                                                    selectedMangaDestination = rightMangaDestination,
                                                    oppositeAnimeDestination = leftAnimeDestination,
                                                    oppositeMangaDestination = leftMangaDestination,
                                                    onSelectAnime = { newDest ->
                                                        rightAnimeDestination = newDest
                                                        if (leftAnimeDestination == newDest) {
                                                            leftAnimeDestination = DetailAnimeTabDestination.entries.first { it != newDest }
                                                        }
                                                    },
                                                    onSelectManga = { newDest ->
                                                        rightMangaDestination = newDest
                                                        if (leftMangaDestination == newDest) {
                                                            leftMangaDestination = DetailMangaTabDestination.entries.first { it != newDest }
                                                        }
                                                    }
                                                )

                                                DetailTabBody(
                                                    isAnime = isAnime,
                                                    selectedAnimeDestination = rightAnimeDestination,
                                                    selectedMangaDestination = rightMangaDestination,
                                                    data = data,
                                                    viewModel = viewModel,
                                                    windowSizeClass = windowSizeClass,
                                                    onMediaClick = onMediaClick
                                                )
                                            }
                                        }
                                    }
                                }
                            } else if (isCompactHeight && data != null) {
                                // Phone Landscape Layout: Left Column has Full Cover Background Image Header + Metadata, with Tab List placed BELOW it
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(paddingValues)
                                        .statusBarsPadding()
                                ) {
                                    // Left Column: Cover Image Header + Metadata + Tab List below it
                                    Column(
                                        modifier = Modifier
                                            .weight(0.85f)
                                            .fillMaxHeight()
                                            .verticalScroll(rememberScrollState())
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                                .clip(CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp))
                                        ) {
                                            NetworkImage(
                                                modifier = Modifier.fillMaxSize(),
                                                url = data.images?.webp?.maximumImageUrl ?: data.images?.webp?.largeImageUrl,
                                                contentDescription = data.title,
                                                contentScale = ContentScale.Crop
                                            )

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        Brush.verticalGradient(
                                                            colors = listOf(
                                                                Color.Transparent,
                                                                Color.Black.copy(alpha = 0.85f)
                                                            )
                                                        )
                                                    )
                                            )

                                            Column(
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .padding(12.dp)
                                            ) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    if (data.score != null && data.score > 0.0) {
                                                        Surface(
                                                            shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                                                            color = MaterialTheme.colorScheme.primary,
                                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(12.dp))
                                                                Spacer(modifier = Modifier.width(4.dp))
                                                                Text(
                                                                    text = String.format(Locale.getDefault(), "%.1f", data.score),
                                                                    style = MaterialTheme.typography.labelSmall,
                                                                    fontWeight = FontWeight.Black
                                                                )
                                                            }
                                                        }
                                                    }

                                                    if (!data.type.isNullOrBlank()) {
                                                        Surface(
                                                            shape = CutCornerShape(topEnd = 6.dp, bottomStart = 6.dp),
                                                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                            contentColor = MaterialTheme.colorScheme.onSurface
                                                        ) {
                                                            Text(
                                                                text = data.type.uppercase(),
                                                                style = MaterialTheme.typography.labelSmall,
                                                                fontWeight = FontWeight.Bold,
                                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                            )
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(4.dp))

                                                Text(
                                                    text = data.title.orEmpty(),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color.White,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }

                                        // Tab List placed BELOW the image and metadata in the left column!
                                        DetailTabBar(
                                            isAnime = isAnime,
                                            selectedAnimeDestination = selectedAnimeDestination,
                                            selectedMangaDestination = selectedMangaDestination,
                                            onSelectAnime = { selectedAnimeDestination = it },
                                            onSelectManga = { selectedMangaDestination = it }
                                        )
                                    }

                                    // Right Column: Active Tab Body Content
                                    Column(
                                        modifier = Modifier
                                            .weight(1.15f)
                                            .fillMaxHeight()
                                    ) {
                                        DetailTabBody(
                                            isAnime = isAnime,
                                            selectedAnimeDestination = selectedAnimeDestination,
                                            selectedMangaDestination = selectedMangaDestination,
                                            data = data,
                                            viewModel = viewModel,
                                            windowSizeClass = windowSizeClass,
                                            onMediaClick = onMediaClick
                                        )
                                    }
                                }
                            } else {
                                // Standard Vertical Portrait Layout
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = paddingValues.calculateBottomPadding())
                                ) {
                                    if (data != null) {
                                        HeaderCoverSection(data = data)
                                    }

                                    DetailTabBar(
                                        isAnime = isAnime,
                                        selectedAnimeDestination = selectedAnimeDestination,
                                        selectedMangaDestination = selectedMangaDestination,
                                        onSelectAnime = { selectedAnimeDestination = it },
                                        onSelectManga = { selectedMangaDestination = it }
                                    )

                                    if (data != null) {
                                        DetailTabBody(
                                            isAnime = isAnime,
                                            selectedAnimeDestination = selectedAnimeDestination,
                                            selectedMangaDestination = selectedMangaDestination,
                                            data = data,
                                            viewModel = viewModel,
                                            windowSizeClass = windowSizeClass,
                                            onMediaClick = onMediaClick
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (isWideWidth && data != null) {
                        ModalNavigationDrawer(
                            drawerState = watchlistDrawerState,
                            gesturesEnabled = showListSheet,
                            drawerContent = {
                                if (showListSheet) {
                                    ModalDrawerSheet(
                                        modifier = Modifier
                                            .width(380.dp)
                                            .fillMaxHeight(),
                                        drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        drawerContentColor = MaterialTheme.colorScheme.onSurface
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .statusBarsPadding()
                                                .padding(horizontal = 24.dp, vertical = 16.dp)
                                        ) {
                                            SaveMediaListSheet(
                                                entry = data,
                                                onSave = { status, score, progress ->
                                                    viewModel.saveMediaListEntry(status, score, progress) { _, message ->
                                                        triggerNotification(message)
                                                    }
                                                    showListSheet = false
                                                },
                                                onDelete = { listEntryId ->
                                                    viewModel.deleteMediaListEntry(listEntryId) { _, message ->
                                                        triggerNotification(message)
                                                    }
                                                    showListSheet = false
                                                },
                                                onDismiss = { showListSheet = false }
                                            )
                                        }
                                    }
                                }
                            }
                        ) {
                            scaffoldContent()
                        }
                    } else {
                        scaffoldContent()

                        if (showListSheet && data != null) {
                            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                            ModalBottomSheet(
                                onDismissRequest = { showListSheet = false },
                                sheetState = sheetState,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 12.dp)
                                ) {
                                    SaveMediaListSheet(
                                        entry = data,
                                        onSave = { status, score, progress ->
                                            viewModel.saveMediaListEntry(status, score, progress) { _, message ->
                                                triggerNotification(message)
                                            }
                                            showListSheet = false
                                        },
                                        onDelete = { listEntryId ->
                                            viewModel.deleteMediaListEntry(listEntryId) { _, message ->
                                                triggerNotification(message)
                                            }
                                            showListSheet = false
                                        },
                                        onDismiss = { showListSheet = false }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is ResponseResult.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorSection(
                        errorMessage = (entryResult as ResponseResult.Error).throwable.toHumanReadableError(),
                        doRetry = {
                            if (isAnime) {
                                viewModel.getAnimeDetail()
                            } else {
                                viewModel.getMangaDetail()
                            }
                        }
                    )
                }
            }
        }

        // Custom Manga-Punk Top Notification Drop Banner
        TopNotificationBanner(
            visible = showNotification,
            message = notificationMessage,
            onDismiss = { showNotification = false }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopNotificationBanner(
    visible: Boolean,
    message: String,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Surface(
            shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp, topEnd = 4.dp, bottomStart = 4.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clip(CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp, topEnd = 4.dp, bottomStart = 4.dp))
                .clickable { onDismiss() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = "DISMISS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PaneTabDropdownSelector(
    paneLabel: String,
    isAnime: Boolean,
    selectedAnimeDestination: DetailAnimeTabDestination,
    selectedMangaDestination: DetailMangaTabDestination,
    oppositeAnimeDestination: DetailAnimeTabDestination,
    oppositeMangaDestination: DetailMangaTabDestination,
    onSelectAnime: (DetailAnimeTabDestination) -> Unit,
    onSelectManga: (DetailMangaTabDestination) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
            .clickable { expanded = true }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = paneLabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAnime) selectedAnimeDestination.name.capitalizeWords() else selectedMangaDestination.name.capitalizeWords(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "CHANGE TAB",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            if (isAnime) {
                DetailAnimeTabDestination.entries
                    .filter { it != oppositeAnimeDestination && it != selectedAnimeDestination }
                    .forEach { destination ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = destination.name.capitalizeWords(),
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onSelectAnime(destination)
                                expanded = false
                            }
                        )
                    }
            } else {
                DetailMangaTabDestination.entries
                    .filter { it != oppositeMangaDestination && it != selectedMangaDestination }
                    .forEach { destination ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = destination.name.capitalizeWords(),
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onSelectManga(destination)
                                expanded = false
                            }
                        )
                    }
            }
        }
    }
}

@Composable
fun HeaderCoverSection(data: MediaEntry) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        NetworkImage(
            modifier = Modifier.fillMaxSize(),
            url = data.images?.webp?.maximumImageUrl ?: data.images?.webp?.largeImageUrl,
            contentDescription = data.title,
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (data.score != null && data.score > 0.0) {
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
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format(Locale.getDefault(), "%.1f", data.score),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                if (!data.type.isNullOrBlank()) {
                    Surface(
                        shape = CutCornerShape(topEnd = 8.dp, bottomStart = 8.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
                    ) {
                        Text(
                            text = data.type.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = data.title.orEmpty(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun FloatingWatchlistFab(
    data: MediaEntry,
    onClick: () -> Unit
) {
    val listEntry = data.mediaListEntry
    val isManga = data.type.equals("MANGA", ignoreCase = true)

    Surface(
        shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp, topEnd = 4.dp, bottomStart = 4.dp),
        color = if (listEntry != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = if (listEntry != null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface),
        shadowElevation = 8.dp,
        modifier = Modifier
            .clip(CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp, topEnd = 4.dp, bottomStart = 4.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (listEntry != null) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            val progressUnit = if (isManga) "Ch" else "Ep"
            Text(
                text = if (listEntry != null) {
                    "${listEntry.status?.replace("_", " ")?.capitalizeWords()} • $progressUnit ${listEntry.progress ?: 0}"
                } else {
                    if (isManga) "+ ADD TO MANGA LIST" else "+ ADD TO WATCHLIST"
                },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun DetailTabBar(
    isAnime: Boolean,
    selectedAnimeDestination: DetailAnimeTabDestination,
    selectedMangaDestination: DetailMangaTabDestination,
    onSelectAnime: (DetailAnimeTabDestination) -> Unit,
    onSelectManga: (DetailMangaTabDestination) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Tab Scroll Indicator Hint
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TABS • タブ",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "SCROLL TABS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        // Horizontal Scrollable Tab List
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isAnime) {
                DetailAnimeTabDestination.entries.forEach { destination ->
                    val isSelected = selectedAnimeDestination == destination
                    Surface(
                        modifier = Modifier
                            .clip(CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp))
                            .clickable { onSelectAnime(destination) },
                        shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = destination.name.capitalizeWords(),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            } else {
                DetailMangaTabDestination.entries.forEach { destination ->
                    val isSelected = selectedMangaDestination == destination
                    Surface(
                        modifier = Modifier
                            .clip(CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp))
                            .clickable { onSelectManga(destination) },
                        shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = destination.name.capitalizeWords(),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailTabBody(
    isAnime: Boolean,
    selectedAnimeDestination: DetailAnimeTabDestination,
    selectedMangaDestination: DetailMangaTabDestination,
    data: MediaEntry,
    viewModel: DetailViewModel,
    windowSizeClass: WindowSizeClass,
    onMediaClick: (id: String, type: String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (isAnime) {
            Crossfade(
                targetState = selectedAnimeDestination,
                label = "DetailAnimeTabTransition"
            ) { targetState ->
                when (targetState) {
                    DetailAnimeTabDestination.INFO -> {
                        val imageSize = getCarouselHomeSize(windowSizeClass)
                        DetailInfoScreen(
                            entry = data,
                            viewModel = viewModel,
                            imageSize = imageSize,
                            onMediaClick = onMediaClick
                        )
                    }

                    DetailAnimeTabDestination.CHARACTERS -> {
                        DetailCharactersTab(viewModel = viewModel)
                    }

                    DetailAnimeTabDestination.STAFF -> {
                        DetailStaffTab(viewModel = viewModel)
                    }

                    DetailAnimeTabDestination.EPISODES -> {
                        DetailEpisodeScreen(
                            streamingEpisodes = data.streamingEpisodes,
                            totalEpisodes = data.episodes,
                            score = data.score ?: 8.0
                        )
                    }

                    DetailAnimeTabDestination.REVIEWS -> {
                        DetailReviewsTab(viewModel = viewModel)
                    }

                    DetailAnimeTabDestination.STATS -> {
                        DetailStatsTab(entry = data)
                    }

                    DetailAnimeTabDestination.RECOMMENDED -> {
                        DetailRecommendationTab(viewModel = viewModel, windowSizeClass = windowSizeClass, onMediaClick = onMediaClick)
                    }
                }
            }
        } else {
            Crossfade(
                targetState = selectedMangaDestination,
                label = "DetailMangaTabTransition"
            ) { targetState ->
                when (targetState) {
                    DetailMangaTabDestination.INFO -> {
                        val imageSize = getCarouselHomeSize(windowSizeClass)
                        DetailInfoScreen(
                            entry = data,
                            viewModel = viewModel,
                            imageSize = imageSize,
                            onMediaClick = onMediaClick
                        )
                    }

                    DetailMangaTabDestination.CHARACTERS -> {
                        DetailCharactersTab(viewModel = viewModel, isManga = true)
                    }

                    DetailMangaTabDestination.STAFF -> {
                        DetailStaffTab(viewModel = viewModel)
                    }

                    DetailMangaTabDestination.REVIEWS -> {
                        DetailReviewsTab(viewModel = viewModel)
                    }

                    DetailMangaTabDestination.STATS -> {
                        DetailStatsTab(entry = data)
                    }

                    DetailMangaTabDestination.RECOMMENDED -> {
                        DetailRecommendationTab(viewModel = viewModel, windowSizeClass = windowSizeClass, onMediaClick = onMediaClick)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailCharactersTab(
    viewModel: DetailViewModel,
    isManga: Boolean = false
) {
    val items = viewModel.characterPaging.collectAsLazyPagingItems()
    var selectedLanguage by remember { mutableStateOf("All") }

    when {
        items.loadState.refresh is LoadState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LoadingIndicator(
                        modifier = Modifier.size(80.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "LOADING CHARACTERS • 読み込み中",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        items.itemCount == 0 && items.loadState.refresh is LoadState.NotLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No character information available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> {
            Column(modifier = Modifier.fillMaxSize()) {
                if (!isManga) {
                    // Compact Manga-Punk Dropdown Selector for Voice Actor Languages
                    val allLanguages = remember(items.itemSnapshotList) {
                        val langs = items.itemSnapshotList.flatMap { char ->
                            char?.voiceActors.orEmpty().mapNotNull { it.language }
                        }.distinct().sorted()
                        if (langs.isEmpty()) listOf("Japanese") else listOf("All") + langs
                    }
                    var expanded by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "VOICE ACTOR LANGUAGE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Box {
                            Surface(
                                shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                                modifier = Modifier
                                    .clip(CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp))
                                    .clickable { expanded = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (selectedLanguage == "All") "ALL LANGUAGES" else "$selectedLanguage VA".uppercase(),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            ) {
                                allLanguages.forEach { lang ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = if (lang == "All") "All Languages" else "$lang Voice Actors",
                                                fontWeight = if (selectedLanguage.equals(lang, ignoreCase = true)) FontWeight.ExtraBold else FontWeight.Normal,
                                                color = if (selectedLanguage.equals(lang, ignoreCase = true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        onClick = {
                                            selectedLanguage = lang
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                if (isManga) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(items.itemCount) { index ->
                            items[index]?.let { item ->
                                MangaCharacterCard(item = item)
                            }
                        }

                        if (items.loadState.append is LoadState.Loading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    LoadingIndicator(
                                        modifier = Modifier.size(36.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(items.itemCount) { index ->
                            items[index]?.let { item ->
                                val filteredItem = if (selectedLanguage == "All") {
                                    item
                                } else {
                                    item.copy(
                                        voiceActors = item.voiceActors?.filter {
                                            it.language.equals(selectedLanguage, ignoreCase = true)
                                        }
                                    )
                                }
                                CharacterVoiceActorRow(item = filteredItem)
                            }
                        }

                        if (items.loadState.append is LoadState.Loading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    LoadingIndicator(
                                        modifier = Modifier.size(36.dp),
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

@Composable
fun MangaCharacterCard(item: MediaCharacterEntry) {
    val character = item.character

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp)),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NetworkImage(
                modifier = Modifier
                    .width(64.dp)
                    .fillMaxHeight(),
                url = character?.images?.webp?.largeImageUrl ?: character?.images?.jpg?.imageUrl,
                contentDescription = character?.title,
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                if (!item.role.isNullOrBlank()) {
                    Surface(
                        shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Text(
                            text = item.role.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = character?.title.orEmpty(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailStaffTab(
    viewModel: DetailViewModel
) {
    val items = viewModel.staffPaging.collectAsLazyPagingItems()

    when {
        items.loadState.refresh is LoadState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LoadingIndicator(
                        modifier = Modifier.size(80.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "LOADING STAFF • 読み込み中",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        items.itemCount == 0 && items.loadState.refresh is LoadState.NotLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No staff information available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items.itemCount) { index ->
                    items[index]?.let { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                NetworkImage(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape),
                                    url = item.imageUrl,
                                    contentDescription = item.name,
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                if (!item.role.isNullOrBlank()) {
                                    Surface(
                                        shape = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ) {
                                        Text(
                                            text = item.role.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                                Text(
                                    text = item.name.orEmpty(),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                if (!item.nativeName.isNullOrBlank()) {
                                    Text(
                                        text = item.nativeName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                if (items.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingIndicator(
                                modifier = Modifier.size(36.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailReviewsTab(
    viewModel: DetailViewModel
) {
    val items = viewModel.reviewPaging.collectAsLazyPagingItems()

    when {
        items.loadState.refresh is LoadState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LoadingIndicator(
                        modifier = Modifier.size(80.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "LOADING REVIEWS • 読み込み中",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        items.itemCount == 0 && items.loadState.refresh is LoadState.NotLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No community reviews available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items.itemCount) { index ->
                    items[index]?.let { item ->
                        var expanded by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
                                .clickable { expanded = !expanded },
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        NetworkImage(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape),
                                            url = item.userAvatarUrl,
                                            contentDescription = item.userName,
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Text(
                                            text = item.userName ?: "Anonymous",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (item.score != null) {
                                        Surface(
                                            shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${item.score}/100",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                if (!item.summary.isNullOrBlank()) {
                                    Text(
                                        text = item.summary,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                if (!item.body.isNullOrBlank()) {
                                    Text(
                                        text = item.body,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = if (expanded) Int.MAX_VALUE else 3,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        text = if (expanded) "Show Less" else "Read Full Review",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                if (items.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingIndicator(
                                modifier = Modifier.size(36.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailStatsTab(
    entry: MediaEntry?
) {
    if (entry == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No statistical data available",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val stats = entry.stats
    val rankings = entry.rankings
    val tags = entry.tags
    val nextAiring = entry.nextAiringEpisode

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Rankings Badges Section (matching AniList website top badges)
        if (rankings.isNotEmpty()) {
            Column {
                Text(
                    text = "RANKINGS • ランキング",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rankings.forEach { r ->
                        val isRated = r.type.equals("RATED", ignoreCase = true)
                        val icon = if (isRated) Icons.Default.Star else Icons.Default.Favorite
                        val iconColor = if (isRated) Color(0xFFFFB300) else Color(0xFFE91E63)

                        Surface(
                            shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = iconColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = r.context ?: "#${r.rank} ${r.type?.lowercase()?.capitalizeWords()}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Airing & Key Metrics Overview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp)),
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
                    text = "OVERVIEW • 概要",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (nextAiring?.episode != null) {
                    val days = (nextAiring.timeUntilAiring ?: 0) / 86400
                    val hours = ((nextAiring.timeUntilAiring ?: 0) % 86400) / 3600
                    Surface(
                        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Text(
                            text = "AIRING: EPISODE ${nextAiring.episode} IN ${days}D ${hours}H",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("MEAN SCORE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = if (entry.score != null) String.format(Locale.getDefault(), "%.1f / 10", entry.score) else "N/A",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column {
                        Text("POPULARITY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = if (entry.popularity != null) "#${entry.popularity}" else "N/A",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column {
                        Text("FAVOURITES", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = if (entry.members != null) "${entry.members}" else "N/A",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Status Distribution Section (Segmented Multi-Color Progress Bar matching AniList)
        if (stats != null && stats.statusDistribution.isNotEmpty()) {
            val totalStatus = stats.statusDistribution.sumOf { it.amount }.coerceAtLeast(1)

            val statusColors = mapOf(
                "CURRENT" to Color(0xFF28C76F),
                "PLANNING" to Color(0xFF00CFE8),
                "PAUSED" to Color(0xFF7367F0),
                "DROPPED" to Color(0xFFEA5455),
                "COMPLETED" to Color(0xFFED4C67)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp)),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "STATUS DISTRIBUTION • 状態分布",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Multi-Color Segmented Horizontal Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp))
                    ) {
                        stats.statusDistribution.forEach { item ->
                            val weight = (item.amount.toFloat() / totalStatus.toFloat()).coerceAtLeast(0.001f)
                            val color = statusColors[item.status.uppercase()] ?: MaterialTheme.colorScheme.primary
                            Box(
                                modifier = Modifier
                                    .weight(weight)
                                    .fillMaxHeight()
                                    .background(color)
                            )
                        }
                    }

                    // Status Breakdown Chips Grid
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        stats.statusDistribution.forEach { item ->
                            val color = statusColors[item.status.uppercase()] ?: MaterialTheme.colorScheme.primary
                            Surface(
                                shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                                color = color.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, color)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${item.status.lowercase().capitalizeWords()}: ${item.amount}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Score Distribution Vertical Bar Histogram (matching AniList website)
        if (stats != null && stats.scoreDistribution.isNotEmpty()) {
            val maxScoreAmount = stats.scoreDistribution.maxOfOrNull { it.amount }?.coerceAtLeast(1) ?: 1

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp)),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "SCORE DISTRIBUTION • スコア分布",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        stats.scoreDistribution.sortedBy { it.score }.forEach { item ->
                            val heightRatio = (item.amount.toFloat() / maxScoreAmount.toFloat()).coerceAtLeast(0.05f)
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(heightRatio)
                                        .clip(CutCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.primary,
                                                    MaterialTheme.colorScheme.secondary
                                                )
                                            )
                                        )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${item.score}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tags Section (matching sidebar list in AniList website image)
        if (tags.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp)),
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
                        text = "TAGS • タグ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tags.take(20).forEach { tag ->
                            Surface(
                                shape = CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = tag.name.orEmpty(),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (tag.rank != null) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${tag.rank}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Black
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
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailRecommendationTab(
    viewModel: DetailViewModel,
    windowSizeClass: WindowSizeClass,
    onMediaClick: (id: String, type: String) -> Unit = { _, _ -> }
) {
    val items = viewModel.recommendationPaging.collectAsLazyPagingItems()
    val carouselSize = getCarouselHomeSize(windowSizeClass)

    when {
        items.loadState.refresh is LoadState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LoadingIndicator(
                        modifier = Modifier.size(80.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "LOADING RECOMMENDATIONS • 読み込み中",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        items.itemCount == 0 && items.loadState.refresh is LoadState.NotLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No recommendations available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items.itemCount) { index ->
                    items[index]?.entry?.let { item ->
                        MediaEntryCard(
                            item = item,
                            height = 224.dp,
                            onClick = {
                                item.id?.let { id -> onMediaClick(id, item.type ?: viewModel.type) }
                            }
                        )
                    }
                }

                if (items.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingIndicator(
                                modifier = Modifier.size(36.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
