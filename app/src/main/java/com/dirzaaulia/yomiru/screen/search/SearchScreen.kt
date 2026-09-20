package com.dirzaaulia.yomiru.screen.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.dirzaaulia.yomiru.model.MediaEntry
import com.dirzaaulia.yomiru.model.MediaReview
import com.dirzaaulia.yomiru.model.request.SearchRequest
import com.dirzaaulia.yomiru.model.response.MediaRecommendation
import com.dirzaaulia.yomiru.navigation.Detail
import com.dirzaaulia.yomiru.navigation.SearchType
import com.dirzaaulia.yomiru.screen.component.ItemRecommendation
import com.dirzaaulia.yomiru.screen.component.ItemReview
import com.dirzaaulia.yomiru.ui.common.MediaEntryCard
import com.dirzaaulia.yomiru.ui.common.VerticalStaggeredGridPaging
import com.dirzaaulia.yomiru.util.getCarouselHomeSize
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    searchType: SearchType,
    type: String,
    backStack: NavBackStack<NavKey>,
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val windowSizeClass = adaptiveInfo.windowSizeClass
    val isWideWidth = windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isCompactHeight = !windowSizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)
    val isTabletop = adaptiveInfo.windowPosture.isTabletop

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()
    val carouselSize = getCarouselHomeSize(windowSizeClass)

    val genre = viewModel.genreFlow.collectAsStateWithLifecycle(emptyList())
    val items = viewModel.search.collectAsLazyPagingItems()
    val reviewItems = viewModel.review.collectAsLazyPagingItems()
    val recommendationsItems = viewModel.recommendations.collectAsLazyPagingItems()

    LaunchedEffect(viewModel) {
        viewModel.setType(type)
    }

    val closeFilterSheet = {
        scope.launch {
            if (isWideWidth) {
                drawerState.close()
            } else {
                bottomSheetScaffoldState.bottomSheetState.hide()
            }
        }
    }

    val toggleFilterSheet = {
        scope.launch {
            if (isWideWidth) {
                if (drawerState.isOpen) drawerState.close() else drawerState.open()
            } else {
                if (bottomSheetScaffoldState.bottomSheetState.isVisible) {
                    bottomSheetScaffoldState.bottomSheetState.hide()
                } else {
                    bottomSheetScaffoldState.bottomSheetState.show()
                }
            }
        }
    }

    val filterSheetContent = @Composable {
        when (searchType) {
            SearchType.SEARCH_ANIME, SearchType.SEARCH_MANGA -> {
                SearchSheet(
                    genre = genre.value,
                    type = searchType,
                    isCompactHeight = isCompactHeight,
                    doSearchWithFilter = { query ->
                        viewModel.setSearchRequest(SearchRequest.General(query))
                        closeFilterSheet()
                    }
                )
            }

            SearchType.SEASON -> {
                SeasonalSearchSheet(
                    genre = genre.value,
                    isCompactHeight = isCompactHeight,
                    onFilterChanged = { year, season, format, sort, genreId, sfw ->
                        viewModel.setSearchRequest(
                            SearchRequest.Seasonal(
                                year = year,
                                season = season.value,
                                type = format,
                                sort = sort,
                                genre = genreId,
                                sfw = sfw
                            )
                        )
                        closeFilterSheet()
                    }
                )
            }

            SearchType.TOP -> {
                if (type == "anime") {
                    SearchSheetTopAnime { format, status, sort, sfw ->
                        viewModel.setSearchRequest(
                            SearchRequest.Top(
                                type = format,
                                filter = status,
                                sort = sort,
                                sfw = sfw
                            )
                        )
                        closeFilterSheet()
                    }
                } else {
                    SearchSheetTopManga { format, sort, country ->
                        viewModel.setSearchRequest(
                            SearchRequest.Top(
                                type = format,
                                filter = null,
                                sort = sort,
                                country = country
                            )
                        )
                        closeFilterSheet()
                    }
                }
            }

            SearchType.RECOMMENDED -> Unit
            SearchType.REVIEW -> Unit
        }
    }

    val topAppBarAction = @Composable {
        TopAppBar(
            title = {
                val title = when (searchType) {
                    SearchType.SEARCH_ANIME -> "Anime Search"
                    SearchType.SEARCH_MANGA -> "Manga Search"
                    SearchType.SEASON -> "Seasonal Anime"
                    SearchType.TOP -> "Top $type"
                    SearchType.RECOMMENDED -> "Recommended $type"
                    SearchType.REVIEW -> "$type Review"
                }
                Text(title)
            },
            actions = {
                val isVisible = when (searchType) {
                    SearchType.SEARCH_ANIME -> true
                    SearchType.SEARCH_MANGA -> true
                    SearchType.SEASON -> true
                    SearchType.TOP -> true
                    SearchType.RECOMMENDED -> false
                    SearchType.REVIEW -> false
                }
                AnimatedVisibility(visible = isVisible) {
                    TooltipBox(
                        positionProvider =
                            TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Above
                            ),
                        tooltip = { PlainTooltip { Text(if (searchType == SearchType.TOP) "Sort/Category" else "Filter") } },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(onClick = { toggleFilterSheet() }) {
                            Icon(
                                imageVector = if (searchType == SearchType.TOP) Icons.AutoMirrored.Filled.Sort else Icons.Filled.FilterAlt,
                                contentDescription = if (searchType == SearchType.TOP) "Sort/Category" else "Filter",
                            )
                        }
                    }
                }
            },
        )
    }

    val mainSearchBodyContent = @Composable {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 1200.dp)
                    .fillMaxSize()
            ) {
                when (searchType) {
                    SearchType.SEARCH_ANIME,
                    SearchType.SEARCH_MANGA,
                    SearchType.SEASON,
                    SearchType.TOP -> NormalSearch(
                        items = items,
                        type = type,
                        windowSizeClass = windowSizeClass,
                        textFieldState = textFieldState,
                        backStack = backStack
                    )

                    SearchType.REVIEW -> ReviewSearch(
                        items = reviewItems,
                        windowSizeClass = windowSizeClass,
                        imageWidth = carouselSize.first,
                        imageHeight = carouselSize.second
                    )

                    SearchType.RECOMMENDED -> RecommendationsSearch(
                        items = recommendationsItems,
                        type = type,
                        windowSizeClass = windowSizeClass,
                        imageWidth = carouselSize.first,
                        imageHeight = carouselSize.second,
                        backStack = backStack
                    )
                }
            }
        }
    }

    if (isWideWidth) {
        // Large Screen / Tablet Side Drawer Sheet
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier
                        .width(380.dp)
                        .fillMaxHeight(),
                    drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    drawerContentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    filterSheetContent()
                }
            }
        ) {
            Scaffold(
                topBar = topAppBarAction
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    mainSearchBodyContent()
                }
            }
        }
    } else {
        // Small Screen / Portrait Bottom Sheet
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            modifier = Modifier.fillMaxSize(),
            sheetContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            sheetContentColor = MaterialTheme.colorScheme.onSurface,
            sheetPeekHeight = if (isTabletop) 220.dp else 0.dp,
            sheetContent = { filterSheetContent() },
            topBar = topAppBarAction
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                mainSearchBodyContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReviewSearch(
    items: LazyPagingItems<MediaReview>,
    windowSizeClass: WindowSizeClass,
    imageWidth: Dp,
    imageHeight: Dp
) {
    val gridColumn = if (!windowSizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)) {
        StaggeredGridCells.Fixed(2)
    } else {
        when {
            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> StaggeredGridCells.Fixed(2)
            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> StaggeredGridCells.Fixed(2)
            else -> StaggeredGridCells.Fixed(1)
        }
    }
    VerticalStaggeredGridPaging(
        list = items,
        columns = gridColumn,
        isHavePlaceholder = true,
        placeholderContent = {
            LoadingIndicator(modifier = Modifier.fillMaxSize())
        }
    ) { item ->
        ItemReview(
            modifier = Modifier.fillMaxWidth(),
            windowSizeClass = windowSizeClass,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            item = item
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RecommendationsSearch(
    items: LazyPagingItems<MediaRecommendation>,
    type: String,
    windowSizeClass: WindowSizeClass,
    imageWidth: Dp,
    imageHeight: Dp,
    backStack: NavBackStack<NavKey>
) {
    val gridColumn = if (!windowSizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)) {
        StaggeredGridCells.Fixed(2)
    } else {
        when {
            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> StaggeredGridCells.Fixed(2)
            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> StaggeredGridCells.Fixed(2)
            else -> StaggeredGridCells.Fixed(1)
        }
    }
    VerticalStaggeredGridPaging(
        list = items,
        columns = gridColumn,
        isHavePlaceholder = true,
        placeholderContent = {
            LoadingIndicator(modifier = Modifier.fillMaxSize())
        }
    ) { item ->
        ItemRecommendation(
            modifier = Modifier.fillMaxWidth(),
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            item = item,
            onMediaClick = { id ->
                backStack.add(Detail(id = id, type = type))
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NormalSearch(
    items: LazyPagingItems<MediaEntry>,
    type: String,
    windowSizeClass: WindowSizeClass,
    textFieldState: TextFieldState,
    backStack: NavBackStack<NavKey>
) {
    val cardHeight = 224.dp
    val gridColumn = StaggeredGridCells.Adaptive(minSize = 160.dp)

    VerticalStaggeredGridPaging(
        list = items,
        columns = gridColumn,
        isHavePlaceholder = textFieldState.text.isEmpty(),
        placeholderContent = {
            LoadingIndicator(modifier = Modifier.fillMaxSize())
        },
    ) { item ->
        MediaEntryCard(
            item = item,
            height = cardHeight,
            onClick = {
                backStack.add(
                    Detail(
                        id = item.id.toString(),
                        type = type
                    )
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteSelect(
    title: String,
    options: List<String>,
    isVisible: Boolean = true,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf("") }

    AnimatedVisibility(visible = isVisible) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            val fillMaxWidth = Modifier.fillMaxWidth()
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { },
                readOnly = true,
                label = { Text(title) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = fillMaxWidth.menuAnchor(
                    ExposedDropdownMenuAnchorType.PrimaryEditable,
                    true
                )
            )

            ExposedDropdownMenu(
                modifier = Modifier.exposedDropdownSize(),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            expanded = false
                            textFieldValue = option
                            onOptionSelected(option)
                        }
                    )
                }
            }
        }
    }
}
