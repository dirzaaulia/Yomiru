package com.dirzaaulia.yomiru.screen.home

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.dirzaaulia.yomiru.BuildConfig
import com.dirzaaulia.yomiru.model.MediaEntry
import com.dirzaaulia.yomiru.model.MediaReview
import com.dirzaaulia.yomiru.model.response.MediaRecommendation
import com.dirzaaulia.yomiru.model.response.PagingResponse
import com.dirzaaulia.yomiru.navigation.Detail
import com.dirzaaulia.yomiru.navigation.Developer
import com.dirzaaulia.yomiru.navigation.Review
import com.dirzaaulia.yomiru.navigation.Search
import com.dirzaaulia.yomiru.navigation.SearchType
import com.dirzaaulia.yomiru.screen.component.ItemRecommendation
import com.dirzaaulia.yomiru.screen.component.ItemReview
import com.dirzaaulia.yomiru.ui.common.ErrorSection
import com.dirzaaulia.yomiru.ui.common.ExpressiveFloatingBar
import com.dirzaaulia.yomiru.ui.common.MediaEntryCard
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
    val context = LocalContext.current
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val windowSizeClass = adaptiveInfo.windowSizeClass
    val isTabletop = adaptiveInfo.windowPosture.isTabletop
    val scrollState = rememberScrollState()

    val selectedMenu by viewModel.selectedMenu.collectAsStateWithLifecycle()
    val seasonEntry by viewModel.animeSeason.collectAsStateWithLifecycle()
    val topMediaEntry by viewModel.topMediaEntry.collectAsStateWithLifecycle()
    val topReview by viewModel.topReview.collectAsStateWithLifecycle()
    val recommendations by viewModel.recommendationsEntry.collectAsStateWithLifecycle()

    val isWideWidth = windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isCompactHeight = !windowSizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)
    val isHorizontalLayout = isWideWidth || isCompactHeight || isTabletop

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            val token by viewModel.accessToken.collectAsStateWithLifecycle()
            ExpressiveFloatingBar(
                selectedMenu = selectedMenu,
                isLoggedIn = token.isNotBlank(),
                onMenuSelected = { menu ->
                    viewModel.setSelectedMenu(menu)
                    viewModel.getData()
                },
                onMyListClick = {
                    backStack.add(com.dirzaaulia.yomiru.navigation.List)
                },
                onSyncClick = {
                    val authUrl = "https://anilist.co/api/v2/oauth/authorize".toUri().buildUpon()
                        .appendQueryParameter("client_id", BuildConfig.ANILIST_CLIENT_ID.replace("\"", ""))
                        .appendQueryParameter("response_type", "token")
                        .build()

                    val customTabsIntent = CustomTabsIntent.Builder().build()
                    customTabsIntent.launchUrl(context, authUrl)
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 1200.dp)
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
            ) {
                // Top Search Bar
                TopHeaderSection(
                    onSearchTap = {
                        val searchType = if (selectedMenu.title.equals("anime", true))
                            SearchType.SEARCH_ANIME else SearchType.SEARCH_MANGA
                        backStack.add(
                            Search(
                                searchType = searchType,
                                type = selectedMenu.title
                            )
                        )
                    },
                    onDevClick = {
                        backStack.add(Developer)
                    }
                )

                // Navigation Animation Transition Container
                AnimatedContent(
                    targetState = selectedMenu,
                    transitionSpec = {
                        (slideInHorizontally(
                            initialOffsetX = { width -> if (targetState == YomiruMenu.Manga) width else -width },
                            animationSpec = tween(350, easing = FastOutSlowInEasing)
                        ) + fadeIn(tween(250))).togetherWith(
                            slideOutHorizontally(
                                targetOffsetX = { width -> if (targetState == YomiruMenu.Manga) -width else width },
                                animationSpec = tween(350, easing = FastOutSlowInEasing)
                            ) + fadeOut(tween(250))
                        )
                    },
                    label = "ModeContentTransition"
                ) { currentMenu ->
                    if (isHorizontalLayout) {
                        // Responsive Layout for Phone Landscape & Larger Screens:
                        // Carousels span 1 Full Column/Row width; Reviews & Recommendations render in 2 Columns side-by-side below.
                        Column(modifier = Modifier.fillMaxWidth()) {
                            AnimatedVisibility(
                                visible = currentMenu.index == 0,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                SeasonCarousel(
                                    seasonEntry = seasonEntry,
                                    windowSizeClass = windowSizeClass,
                                    navigateToSearch = { backStack.add(Search(searchType = SearchType.SEASON, type = currentMenu.title)) },
                                    navigateToDetail = { id -> backStack.add(Detail(id = id, type = currentMenu.title)) },
                                    refreshAnimeSeason = { viewModel.getAnimeSeason() }
                                )
                            }

                            TopCarousel(
                                topMediaEntry = topMediaEntry,
                                type = currentMenu.title,
                                windowSizeClass = windowSizeClass,
                                navigateToSearch = { backStack.add(Search(searchType = SearchType.TOP, type = currentMenu.title)) },
                                navigateToDetail = { id -> backStack.add(Detail(id = id, type = currentMenu.title)) },
                                refreshTopCarousel = { viewModel.getTopEntry() }
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    TopReview(
                                        topReview = topReview,
                                        selectedMenu = currentMenu,
                                        windowSizeClass = windowSizeClass,
                                        refreshTopReview = { viewModel.getTopReview() },
                                        navigateToSearchReview = { backStack.add(Search(searchType = SearchType.REVIEW, type = currentMenu.title)) },
                                        navigateToReviewDetail = { review -> backStack.add(Review(review)) }
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    RecommendationHome(
                                        recommendations = recommendations,
                                        selectedMenu = currentMenu,
                                        windowSizeClass = windowSizeClass,
                                        refreshRecommendations = { viewModel.getRecommendations() },
                                        navigateToRecommendationSearch = { backStack.add(Search(searchType = SearchType.RECOMMENDED, type = currentMenu.title)) },
                                        navigateToDetail = { id -> backStack.add(Detail(id = id, type = currentMenu.title)) }
                                    )
                                }
                            }
                        }
                    } else {
                        // Standard Vertical Portrait Layout
                        Column(modifier = Modifier.fillMaxWidth()) {
                            AnimatedVisibility(
                                visible = currentMenu.index == 0,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                SeasonCarousel(
                                    seasonEntry = seasonEntry,
                                    windowSizeClass = windowSizeClass,
                                    navigateToSearch = { backStack.add(Search(searchType = SearchType.SEASON, type = currentMenu.title)) },
                                    navigateToDetail = { id -> backStack.add(Detail(id = id, type = currentMenu.title)) },
                                    refreshAnimeSeason = { viewModel.getAnimeSeason() }
                                )
                            }

                            TopCarousel(
                                topMediaEntry = topMediaEntry,
                                type = currentMenu.title,
                                windowSizeClass = windowSizeClass,
                                navigateToSearch = { backStack.add(Search(searchType = SearchType.TOP, type = currentMenu.title)) },
                                navigateToDetail = { id -> backStack.add(Detail(id = id, type = currentMenu.title)) },
                                refreshTopCarousel = { viewModel.getTopEntry() }
                            )

                            TopReview(
                                topReview = topReview,
                                selectedMenu = currentMenu,
                                windowSizeClass = windowSizeClass,
                                refreshTopReview = { viewModel.getTopReview() },
                                navigateToSearchReview = { backStack.add(Search(searchType = SearchType.REVIEW, type = currentMenu.title)) },
                                navigateToReviewDetail = { review -> backStack.add(Review(review)) }
                            )

                            RecommendationHome(
                                recommendations = recommendations,
                                selectedMenu = currentMenu,
                                windowSizeClass = windowSizeClass,
                                refreshRecommendations = { viewModel.getRecommendations() },
                                navigateToRecommendationSearch = { backStack.add(Search(searchType = SearchType.RECOMMENDED, type = currentMenu.title)) },
                                navigateToDetail = { id -> backStack.add(Detail(id = id, type = currentMenu.title)) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun TopHeaderSection(
    onSearchTap: () -> Unit,
    onDevClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBarHome(
            modifier = Modifier.fillMaxWidth(),
            onSearchTap = onSearchTap,
            onDevClick = onDevClick
        )
    }
}

@Composable
fun SearchBarHome(
    modifier: Modifier = Modifier,
    onSearchTap: () -> Unit,
    onDevClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp, topEnd = 6.dp, bottomStart = 6.dp))
            .clickable { onSearchTap() },
        shape = CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp, topEnd = 6.dp, bottomStart = 6.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onDevClick() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "検索 • Search anime or manga...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SectionHeader(
    title: String,
    katakanaSubtitle: String? = null,
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (!katakanaSubtitle.isNullOrBlank()) {
                Text(
                    text = katakanaSubtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        AssistChip(
            onClick = onSeeAllClick,
            label = {
                Text(
                    text = "See All",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
fun SeasonCarousel(
    seasonEntry: ResponseResult<PagingResponse<List<MediaEntry>>?>,
    windowSizeClass: WindowSizeClass,
    navigateToSearch: () -> Unit,
    navigateToDetail: (String) -> Unit,
    refreshAnimeSeason: () -> Unit
) {
    val carouselSize = getCarouselHomeSize(windowSizeClass)
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(
            title = "This Season",
            katakanaSubtitle = "今期アニメ • SEASONAL AIRINGS",
            onSeeAllClick = navigateToSearch
        )

        when (seasonEntry) {
            ResponseResult.Loading -> LoadingIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(carouselSize.second)
            )

            is ResponseResult.Success<*> -> {
                seasonEntry.success { response ->
                    val list = response?.data.orEmpty()
                    if (list.isEmpty()) return@success

                    val carouselState = rememberCarouselState { list.count() }

                    HorizontalMultiBrowseCarousel(
                        state = carouselState,
                        modifier = Modifier.fillMaxWidth(),
                        preferredItemWidth = carouselSize.first,
                        itemSpacing = 10.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) { i ->
                        val item = list[i]
                        MediaEntryCard(
                            item = item,
                            height = carouselSize.second,
                            badgeText = "SEASONAL",
                            onClick = { navigateToDetail(item.id.toString()) }
                        )
                    }
                }
            }

            is ResponseResult.Error -> {
                seasonEntry.error {
                    ErrorSection(errorMessage = it.toHumanReadableError()) {
                        refreshAnimeSeason()
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
fun TopCarousel(
    topMediaEntry: ResponseResult<PagingResponse<List<MediaEntry>>?>,
    type: String,
    windowSizeClass: WindowSizeClass,
    navigateToSearch: () -> Unit,
    navigateToDetail: (String) -> Unit,
    refreshTopCarousel: () -> Unit = { }
) {
    val carouselSize = getCarouselHomeSize(windowSizeClass)
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(
            title = "Top $type",
            katakanaSubtitle = "人気ランキング • TOP RANKINGS",
            onSeeAllClick = navigateToSearch
        )

        when (topMediaEntry) {
            ResponseResult.Loading -> LoadingIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(carouselSize.second)
            )

            is ResponseResult.Success<*> -> {
                topMediaEntry.success { response ->
                    val list = response?.data.orEmpty()
                    if (list.isEmpty()) return@success

                    val carouselState = rememberCarouselState { list.count() }

                    HorizontalMultiBrowseCarousel(
                        state = carouselState,
                        modifier = Modifier.fillMaxWidth(),
                        preferredItemWidth = carouselSize.first,
                        itemSpacing = 10.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) { i ->
                        val item = list[i]
                        MediaEntryCard(
                            rank = i + 1,
                            item = item,
                            height = carouselSize.second,
                            onClick = { navigateToDetail(item.id.toString()) }
                        )
                    }
                }
            }

            is ResponseResult.Error -> {
                topMediaEntry.error {
                    ErrorSection(errorMessage = it.toHumanReadableError()) {
                        refreshTopCarousel()
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun TopReview(
    topReview: ResponseResult<PagingResponse<List<MediaReview>>?>,
    selectedMenu: YomiruMenu,
    windowSizeClass: WindowSizeClass,
    refreshTopReview: () -> Unit,
    navigateToSearchReview: () -> Unit,
    navigateToReviewDetail: (MediaReview) -> Unit,
) {
    val carouselSize = getCarouselHomeSize(windowSizeClass)
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(
            title = "Top ${selectedMenu.title} Review",
            katakanaSubtitle = "レビュー • REVIEWS",
            onSeeAllClick = navigateToSearchReview
        )

        when (topReview) {
            ResponseResult.Loading -> LoadingIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            is ResponseResult.Success<*> -> {
                topReview.success { response ->
                    val list = response?.data.orEmpty().take(3)
                    if (list.isEmpty()) return@success

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        list.forEach { review ->
                            ItemReview(
                                modifier = Modifier.clickable {
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
            }

            is ResponseResult.Error -> {
                topReview.error {
                    ErrorSection(errorMessage = it.toHumanReadableError()) {
                        refreshTopReview()
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
fun RecommendationHome(
    recommendations: ResponseResult<PagingResponse<List<MediaRecommendation>>?>,
    selectedMenu: YomiruMenu,
    windowSizeClass: WindowSizeClass,
    refreshRecommendations: () -> Unit = { },
    navigateToRecommendationSearch: () -> Unit,
    navigateToDetail: (String) -> Unit
) {
    val carouselSize = getCarouselHomeSize(windowSizeClass)

    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(
            title = "${selectedMenu.title} Recommendations",
            katakanaSubtitle = "おすすめ • PERSONALIZED PICKS",
            onSeeAllClick = navigateToRecommendationSearch
        )

        when (recommendations) {
            ResponseResult.Loading -> LoadingIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(carouselSize.second)
            )

            is ResponseResult.Success<*> -> {
                recommendations.success { response ->
                    val recList = response?.data.orEmpty().take(4)
                    if (recList.isEmpty()) return@success

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        recList.forEach { rec ->
                            ItemRecommendation(
                                modifier = Modifier.fillMaxWidth(),
                                item = rec,
                                imageWidth = carouselSize.first,
                                imageHeight = carouselSize.second
                            )
                        }
                    }
                }
            }

            is ResponseResult.Error -> {
                recommendations.error {
                    ErrorSection(errorMessage = it.toHumanReadableError()) {
                        refreshRecommendations()
                    }
                }
            }
        }
    }
}
