package com.dirzaaulia.yomiru.screen.detail

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dirzaaulia.yomiru.screen.component.ImageWithCaption
import com.dirzaaulia.yomiru.screen.detail.anime.DetailEpisodeScreen
import com.dirzaaulia.yomiru.screen.detail.anime.DetailVideoScreen
import com.dirzaaulia.yomiru.util.ResponseResult
import com.dirzaaulia.yomiru.util.capitalizeWords
import com.dirzaaulia.yomiru.util.getCarouselHomeSize
import com.dirzaaulia.yomiru.util.success
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = koinViewModel(),
) {

    val malEntry by viewModel.malEntry.collectAsStateWithLifecycle()
    val malCharacters by viewModel.malCharacters.collectAsStateWithLifecycle()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val carouselSize = getCarouselHomeSize(windowSizeClass)
    var selectedDestination by rememberSaveable {
        mutableStateOf(viewModel.startDestination)
    }

    when (malEntry) {
        ResponseResult.Loading -> Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            LoadingIndicator(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        }

        is ResponseResult.Success<*> -> {
            malEntry.success {
                val data = it?.data
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        LargeFlexibleTopAppBar(
                            title = {
                                Text(
                                    text = data?.title.toString(),
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                scrolledContainerColor = Color.Transparent
                            ),
                            scrollBehavior = scrollBehavior,
                            windowInsets = WindowInsets()
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    ) {
                        PrimaryScrollableTabRow(
                            selectedTabIndex = selectedDestination.ordinal,
                        ) {
                            DetailAnimeTabDestination.entries.forEachIndexed { index, destination ->
                                Tab(
                                    selected = selectedDestination.ordinal == index,
                                    onClick = {
                                        selectedDestination = destination
                                    },
                                    text = {
                                        Text(
                                            text = destination.name.capitalizeWords(),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                )
                            }
                        }
                        Crossfade(
                            selectedDestination,
                        ) { targetState ->
                            if (viewModel.type.equals("anime", true)) {
                                when (targetState) {
                                    DetailAnimeTabDestination.INFO -> {
                                        val imageSize = getCarouselHomeSize(windowSizeClass)
                                        DetailInfoScreen(
                                            viewModel = viewModel,
                                            imageSize = imageSize
                                        )
                                    }

                                    DetailAnimeTabDestination.CHARACTER -> {
                                        when (malCharacters) {
                                            ResponseResult.Loading -> Box(modifier = Modifier.fillMaxSize()) {
                                                LoadingIndicator(modifier = Modifier.fillMaxSize())
                                            }

                                            is ResponseResult.Success<*> -> {
                                                malCharacters.success { response ->
                                                    val list = response?.data.orEmpty()
                                                    LazyColumn {
                                                        items(list) { item ->
                                                            val voiceActorEntry = item.voiceActors?.firstOrNull()
                                                            ElevatedCard {
                                                                Text(item.character?.name.orEmpty())
                                                                Text("Language: ${voiceActorEntry?.language}")
                                                                Text(
                                                                    text = "${item.favorites ?: 0} Favorites",
                                                                    style = MaterialTheme.typography.labelSmall,
                                                                    color = MaterialTheme.colorScheme.outline
                                                                )
                                                                Row(
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .height(carouselSize.second),
                                                                    // Arrangement.spacedBy with a negative value can create the "overlap" look
                                                                    // Or use 0.dp for perfectly side-by-side symmetry
                                                                    horizontalArrangement = Arrangement.spacedBy((-32).dp),
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    ImageWithCaption(
                                                                        modifier = Modifier
                                                                            .weight(1f) // Makes it symmetric
                                                                            .fillMaxHeight()
                                                                            .clip(MaterialTheme.shapes.extraLarge),
                                                                        url = item.character?.images?.webp?.imageUrl,
                                                                        contentDescription = item.character?.name,
                                                                        caption = item.character?.name
                                                                    )
                                                                    ImageWithCaption(
                                                                        modifier = Modifier
                                                                            .weight(1f) // Makes it symmetric
                                                                            .fillMaxHeight()
                                                                            .clip(MaterialTheme.shapes.extraLarge)
                                                                            .zIndex(2f), // Ensures the second one stays "on top" if overlapping
                                                                        url = voiceActorEntry?.person?.images?.jpg?.imageUrl,
                                                                        contentDescription = voiceActorEntry?.person?.name,
                                                                        caption = voiceActorEntry?.person?.name
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            is ResponseResult.Error -> {}
                                        }
                                    }

                                    DetailAnimeTabDestination.EPISODE -> DetailEpisodeScreen(viewModel = viewModel)
                                    DetailAnimeTabDestination.VIDEOS -> DetailVideoScreen(viewModel = viewModel)
                                    DetailAnimeTabDestination.RECOMMENDATION -> TODO()
                                    DetailAnimeTabDestination.RELATION -> TODO()
                                    DetailAnimeTabDestination.REVIEW -> TODO()
                                    DetailAnimeTabDestination.STATISTIC -> TODO()
                                    DetailAnimeTabDestination.STAFF -> TODO()
                                }
                            } else {
                                when (targetState) {
                                    DetailMangaTabDestination.INFO -> TODO()
                                    DetailMangaTabDestination.CHARACTER -> TODO()
                                    DetailMangaTabDestination.RECOMMENDATION -> TODO()
                                    DetailMangaTabDestination.STATISTIC -> TODO()
                                }
                            }

                        }
                    }
                }
            }
        }

        is ResponseResult.Error -> {}
    }
}
