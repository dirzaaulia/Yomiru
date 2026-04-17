package com.dirzaaulia.yomiru.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.dirzaaulia.yomiru.model.MalEntry
import com.dirzaaulia.yomiru.model.MalReview
import com.dirzaaulia.yomiru.model.response.MalRecommendation
import com.dirzaaulia.yomiru.model.response.PagingResponse
import com.dirzaaulia.yomiru.navigation.Detail
import com.dirzaaulia.yomiru.navigation.Recommendation
import com.dirzaaulia.yomiru.navigation.Review
import com.dirzaaulia.yomiru.navigation.Search
import com.dirzaaulia.yomiru.navigation.SearchType
import com.dirzaaulia.yomiru.screen.component.ImageWithCaption
import com.dirzaaulia.yomiru.screen.component.ItemRecommendation
import com.dirzaaulia.yomiru.screen.component.ItemReview
import com.dirzaaulia.yomiru.ui.common.ErrorSection
import com.dirzaaulia.yomiru.ui.common.NetworkImage
import com.dirzaaulia.yomiru.util.ResponseResult
import com.dirzaaulia.yomiru.util.error
import com.dirzaaulia.yomiru.util.getCarouselHomeSize
import com.dirzaaulia.yomiru.util.success
import com.dirzaaulia.yomiru.util.toHumanReadableError
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    backStack: NavBackStack<NavKey>,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val scrollState = rememberScrollState()

    val selectedMenu by viewModel.selectedMenu.collectAsStateWithLifecycle()
    val seasonMalEntry by viewModel.animeSeason.collectAsStateWithLifecycle()
    val topMalEntry by viewModel.topMalEntry.collectAsStateWithLifecycle()
    val topReview by viewModel.topReview.collectAsStateWithLifecycle()
    val recommendations by viewModel.recommendationsMalEntry.collectAsStateWithLifecycle()

    LaunchedEffect(selectedMenu) {
        if (selectedMenu == YomiruMenu.Default) {
            viewModel.setSelectedMenu(YomiruMenu.Anime)
            viewModel.getData()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            val menu = if (selectedMenu.index == 0) YomiruMenu.Manga else YomiruMenu.Anime
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.setSelectedMenu(menu)
                }
            ) {
                Text(
//                    text = "Switch to ${menu.title}",
                    text = "${windowSizeClass.windowHeightSizeClass} | ${windowSizeClass.windowWidthSizeClass}",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
            ) {
                SearchBarHome {
                    val searchType = if (selectedMenu.title.equals("anime", true))
                        SearchType.SEARCH_ANIME else SearchType.SEARCH_MANGA
                    backStack.add(
                        Search(
                            searchType = searchType,
                            type = selectedMenu.title
                        )
                    )
                }
                AnimatedVisibility(selectedMenu.index == 0) {
                    SeasonCarousel(
                        seasonMalEntry = seasonMalEntry,
                        windowSizeClass = windowSizeClass,
                        navigateToSearch = {
                            backStack.add(
                                Search(
                                    searchType = SearchType.SEASON,
                                    type = selectedMenu.title
                                )
                            )
                        },
                        navigateToDetail = { id ->
                            backStack.add(
                                Detail(
                                    id = id,
                                    type = selectedMenu.title
                                )
                            )
                        },
                        refreshAnimeSeason = { viewModel.getAnimeSeason() }
                    )
                }
                TopCarousel(
                    topMalEntry = topMalEntry,
                    type = selectedMenu.title,
                    windowSizeClass = windowSizeClass,
                    navigateToSearch = {
                        backStack.add(
                            Search(
                                searchType = SearchType.TOP,
                                type = selectedMenu.title
                            )
                        )
                    },
                    navigateToDetail = { id ->
                        backStack.add(
                            Detail(
                                id = id,
                                type = selectedMenu.title
                            )
                        )
                     },
                    refreshTopCarousel = { viewModel.getTopEntry() }
                )
                TopReview(
                    topReview = topReview,
                    selectedMenu = selectedMenu,
                    windowSizeClass = windowSizeClass,
                    refreshTopReview = { viewModel.getTopReview() },
                    navigateToSearchReview = {
                        backStack.add(
                            Search(
                                searchType = SearchType.REVIEW,
                                type = selectedMenu.title
                            )
                        )
                    },
                    navigateToReviewDetail = { review ->
                        backStack.add(Review(review))
                    }
                )
                RecommendationHome(
                    recommendations = recommendations,
                    selectedMenu = selectedMenu,
                    windowSizeClass = windowSizeClass,
                    refreshRecommendations = { viewModel.getRecommendations() },
                    navigateToRecommendationSearch = {
                        backStack.add(
                            Search(
                                searchType = SearchType.RECOMMENDED,
                                type = selectedMenu.title
                            )
                        )
                    },
                    navigateToRecommendationDetail = { item ->
                        backStack.add(Recommendation(item))
                    }
                )
                Spacer(modifier = Modifier.height(84.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarHome(
    onSearchTap: () -> Unit,
) {

    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val focusManager = LocalFocusManager.current

    val inputField = @Composable {
        SearchBarDefaults.InputField(
            modifier = Modifier.onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    onSearchTap.invoke()
                    focusManager.clearFocus()
                }
            },
            searchBarState = searchBarState,
            textFieldState = textFieldState,
            onSearch = {},
            placeholder = { Text("Search...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
        )
    }

    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        state = searchBarState,
        inputField = inputField,
    )
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
fun SeasonCarousel(
    seasonMalEntry: ResponseResult<PagingResponse<List<MalEntry>>?>,
    windowSizeClass: WindowSizeClass,
    navigateToSearch: () -> Unit,
    navigateToDetail: (String) -> Unit,
    refreshAnimeSeason: () -> Unit
) {
    val carouselSize = getCarouselHomeSize(windowSizeClass)
    Column(
        modifier = Modifier
            .fillMaxWidth()
//            .background(color = Color.Red)
    ) {
        when (seasonMalEntry) {
            ResponseResult.Loading -> LoadingIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .height(carouselSize.second)
            )

            is ResponseResult.Success<*> -> {
                seasonMalEntry.success {
                    val list = it?.data.orEmpty()
                    val carouselState = rememberCarouselState { list.count() }

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = "This Season",
                                style = MaterialTheme.typography.headlineLargeEmphasized
                            )
                            Text(
                                modifier = Modifier.clickable {
                                    navigateToSearch.invoke()
                                },
                                text = "See All",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        HorizontalMultiBrowseCarousel(
                            state = carouselState,
                            modifier = Modifier.fillMaxWidth(),
                            preferredItemWidth = carouselSize.first,
                            itemSpacing = 8.dp,
                        ) { i ->
                            val item = list[i]
                            ImageWithCaption(
                                modifier = Modifier
                                    .height(carouselSize.second)
                                    .maskClip(MaterialTheme.shapes.extraLarge) // Apply clipping to the Box
                                    .clickable {
                                        navigateToDetail.invoke(item.id.toString())
                                    },
                                url = item.images?.webp?.largeImageUrl,
                                contentDescription = item.title,
                                caption = item.title
                            )
                        }
                    }

                }
            }

            is ResponseResult.Error -> {
                seasonMalEntry.error {
                    val errorMessage = it.toHumanReadableError()
                    ErrorSection(errorMessage = errorMessage) {
                        refreshAnimeSeason.invoke()
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
fun TopCarousel(
    topMalEntry: ResponseResult<PagingResponse<List<MalEntry>>?>,
    type: String,
    windowSizeClass: WindowSizeClass,
    navigateToSearch: () -> Unit,
    navigateToDetail: (String) -> Unit,
    refreshTopCarousel: () -> Unit = { }
) {
    val carouselSize = getCarouselHomeSize(windowSizeClass)
    Column(
        modifier = Modifier
            .fillMaxWidth()
//            .background(color = Color.Blue)
    ) {
        when (topMalEntry) {
            ResponseResult.Loading -> LoadingIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .height(carouselSize.second)
            )

            is ResponseResult.Success<*> -> {
                topMalEntry.success {
                    val list = it?.data.orEmpty()
                    val carouselState = rememberCarouselState { list.count() }


                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = "Top $type",
                                style = MaterialTheme.typography.headlineLargeEmphasized
                            )
                            Text(
                                modifier = Modifier.clickable {
                                    navigateToSearch.invoke()
                                },
                                text = "See All",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        HorizontalMultiBrowseCarousel(
                            state = carouselState,
                            modifier = Modifier.fillMaxWidth(),
                            preferredItemWidth = carouselSize.first,
                            itemSpacing = 8.dp,
                        ) { i ->
                            val item = list[i]
                            Box( // Wrap image and title
                                modifier = Modifier
                                    .height(carouselSize.second)
                                    .maskClip(MaterialTheme.shapes.extraLarge) // Apply clipping to the Box
                                    .clickable {
                                        navigateToDetail.invoke(item.id.toString())
                                    }
                            ) {
                                NetworkImage(
                                    modifier = Modifier.fillMaxSize(), // Image fills the Box
                                    url = item.images?.webp?.largeImageUrl,
                                    contentDescription = item.title, // Keep for accessibility
                                    contentScale = ContentScale.FillBounds
                                )
                                Box( // Background for the title
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Color.Black.copy(alpha = 0.6f)
                                        ) // Semi-transparent background
                                        .align(Alignment.BottomCenter) // Align to bottom
                                ) {
                                    Text(
                                        text = item.title ?: "No Title",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyLarge, // Or bodySmall, adjust as needed
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                            .fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                }
            }

            is ResponseResult.Error -> {
                topMalEntry.error {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = it.message.toString(),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(
                            onClick = {
                                refreshTopCarousel.invoke()
                            }
                        ) {
                            Text(text = "Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
fun TopReview(
    topReview: ResponseResult<PagingResponse<List<MalReview>>?>,
    selectedMenu: YomiruMenu,
    windowSizeClass: WindowSizeClass,
    refreshTopReview: () -> Unit,
    navigateToSearchReview: () -> Unit,
    navigateToReviewDetail: (MalReview) -> Unit,
) {

    val carouselSize = getCarouselHomeSize(windowSizeClass)
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
//            .background(color = Color.Green)
    ) {
        when (topReview) {
            ResponseResult.Loading -> LoadingIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .height(carouselSize.second)
            )

            is ResponseResult.Success<*> -> {
                topReview.success {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "Top ${selectedMenu.title} Review",
                            style = MaterialTheme.typography.headlineLargeEmphasized
                        )
                        Text(
                            modifier = Modifier.clickable {
                                navigateToSearchReview.invoke()
                            },
                            text = "See All",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val list = it?.data?.subList(0, 20).orEmpty()
                    val state = rememberCarouselState { list.count() }
                    val maxItemWidth = when {
                        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                            // return for EXPANDED width size class
                            520.dp
                        }

                        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                            carouselSize.first
                        }

                        else -> {
                            // return for COMPACT width size class
                            Dp.Unspecified
                        }
                    }
                    HorizontalCenteredHeroCarousel(
                        state = state,
                        modifier = Modifier.fillMaxWidth(),
                        itemSpacing = 8.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        maxItemWidth = maxItemWidth
                    ) { i ->
                        val review = list[i]
                        ItemReview(
                            modifier = Modifier
                                .maskClip(MaterialTheme.shapes.extraLarge)
                                .clickable {
                                    coroutineScope.launch {
                                        navigateToReviewDetail.invoke(review)
                                    }
                                },
                            windowSizeClass = windowSizeClass,
                            imageWidth = carouselSize.first,
                            imageHeight = carouselSize.second,
                            item = review
                        )
                    }
                }
            }

            is ResponseResult.Error -> {
                topReview.error {
                    val errorMessage = it.message
                    ErrorSection(errorMessage = errorMessage.toString()) {
                        refreshTopReview.invoke()
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
fun RecommendationHome(
    recommendations: ResponseResult<PagingResponse<List<MalRecommendation>>?>,
    selectedMenu: YomiruMenu,
    windowSizeClass: WindowSizeClass,
    refreshRecommendations: () -> Unit = { },
    navigateToRecommendationSearch: () -> Unit,
    navigateToRecommendationDetail: (MalRecommendation) -> Unit
) {
    val carouselSize = getCarouselHomeSize(windowSizeClass)

    Column(
        modifier = Modifier
            .fillMaxWidth()
//            .background(color = Color.Yellow)
    ) {

        when (recommendations) {
            ResponseResult.Loading -> LoadingIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .height(carouselSize.second)
            )

            is ResponseResult.Success<*> -> {
                recommendations.success {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "${selectedMenu.title} Recommendations",
                            style = MaterialTheme.typography.headlineLargeEmphasized
                        )
                        Text(
                            modifier = Modifier.clickable {
                                navigateToRecommendationSearch.invoke()
                            },
                            text = "See All",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val list = it?.data?.take(5).orEmpty()
                    val state = rememberCarouselState { list.count() }
                    val maxItemWidth = when {
                        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                            // return for EXPANDED width size class
                            carouselSize.first * 2
                        }

                        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                            carouselSize.first * 2
                        }

                        else -> {
                            // return for COMPACT width size class
                            Dp.Unspecified
                        }
                    }
                    HorizontalCenteredHeroCarousel(
                        state = state,
                        modifier = Modifier.fillMaxWidth(),
                        itemSpacing = 8.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        maxItemWidth = maxItemWidth
                    ) { i ->
                        val item = list[i]
                        ItemRecommendation(
                            modifier = Modifier
                                .maskClip(MaterialTheme.shapes.extraLarge)
                                .clickable {
                                   navigateToRecommendationDetail.invoke(item)
                                },
                            item = item,
                            imageWidth = carouselSize.first,
                            imageHeight = carouselSize.second
                        )
                    }
                }
            }

            is ResponseResult.Error -> {
                recommendations.error {
                    val errorMessage = it.toHumanReadableError()
                    ErrorSection(errorMessage = errorMessage) {
                        refreshRecommendations.invoke()
                    }
                }
            }
        }
    }
}