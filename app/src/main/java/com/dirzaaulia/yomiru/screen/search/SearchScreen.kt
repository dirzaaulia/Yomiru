package com.dirzaaulia.yomiru.screen.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.BottomSheetScaffold
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.dirzaaulia.yomiru.model.MalEntry
import com.dirzaaulia.yomiru.model.MalReview
import com.dirzaaulia.yomiru.model.request.SearchRequest
import com.dirzaaulia.yomiru.model.response.MalRecommendation
import com.dirzaaulia.yomiru.navigation.Detail
import com.dirzaaulia.yomiru.navigation.SearchType
import com.dirzaaulia.yomiru.screen.component.ImageWithCaption
import com.dirzaaulia.yomiru.screen.component.ItemRecommendation
import com.dirzaaulia.yomiru.screen.component.ItemReview
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

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val carouselSize = getCarouselHomeSize(windowSizeClass)

    val genre = viewModel.malGenre.collectAsStateWithLifecycle(emptyList())
    val items = viewModel.search.collectAsLazyPagingItems()
    val reviewItems = viewModel.review.collectAsLazyPagingItems()
    val recommendationsItems = viewModel.recommendations.collectAsLazyPagingItems()

    LaunchedEffect(viewModel) {
        viewModel.setType(type)
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        sheetPeekHeight = 0.dp,
        sheetContent = {
            when (searchType) {
                SearchType.SEARCH_ANIME, SearchType.SEARCH_MANGA -> {
                    SearchSheet(
                        genre = genre.value,
                        type = searchType,
                        doSearchWithFilter = { query ->
                            viewModel.setSearchRequest(SearchRequest.General(query))
                        }
                    )
                }

                SearchType.SEASON -> {
                    SeasonalSearchSheet { year, season, type, sfw ->
                        viewModel.setSearchRequest(
                            SearchRequest.Seasonal(
                                year = year,
                                season = season.value,
                                type = type,
                                sfw = sfw
                            )
                        )
                    }
                }

                SearchType.TOP -> {
                    if (type == "anime") {
                        SearchSheetTopAnime { type, filter, rating, sfw ->
                            viewModel.setSearchRequest(
                                SearchRequest.Top(
                                    type = type,
                                    filter = filter,
                                    rating = rating,
                                    sfw = sfw
                                )
                            )
                        }
                    } else {
                        SearchSheetTopManga { type, filter ->
                            viewModel.setSearchRequest(
                                SearchRequest.Top(
                                    type = type,
                                    filter = filter
                                )
                            )
                        }
                    }
                }

                SearchType.RECOMMENDED -> Unit
                SearchType.REVIEW -> Unit
            }
        },
        topBar = {
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
                            tooltip = { PlainTooltip { Text("Filter") } },
                            state = rememberTooltipState(),
                        ) {
                            IconButton(onClick = {
                                scope.launch {
                                    if (bottomSheetScaffoldState.bottomSheetState.isVisible) {
                                        bottomSheetScaffoldState.bottomSheetState.hide()
                                    } else {
                                        bottomSheetScaffoldState.bottomSheetState.show()
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.FilterAlt,
                                    contentDescription = "Filter",
                                )
                            }
                        }
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
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
                    windowSizeClass = windowSizeClass,
                    imageWidth = carouselSize.first,
                    imageHeight = carouselSize.second
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReviewSearch(
    items: LazyPagingItems<MalReview>,
    windowSizeClass: WindowSizeClass,
    imageWidth: Dp,
    imageHeight: Dp
) {
    val gridColumn = if (!windowSizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)) {
        StaggeredGridCells.Fixed(2)
    } else {
        when {
            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                // return for EXPANDED width size class
                StaggeredGridCells.Fixed(2)
            }

            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                // return for MEDIUM width size class
//                StaggeredGridCells.Adaptive(120.dp)
                StaggeredGridCells.Fixed(2)
            }

            else -> {
                // return for COMPACT width size class
                StaggeredGridCells.Fixed(1)
            }
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
    items: LazyPagingItems<MalRecommendation>,
    windowSizeClass: WindowSizeClass,
    imageWidth: Dp,
    imageHeight: Dp
) {
    val gridColumn = if (!windowSizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)) {
        StaggeredGridCells.Fixed(2)
    } else {
        when {
            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                // return for EXPANDED width size class
                StaggeredGridCells.Fixed(2)
            }

            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                // return for MEDIUM width size class
//                StaggeredGridCells.Adaptive(120.dp)
                StaggeredGridCells.Fixed(2)
            }

            else -> {
                // return for COMPACT width size class
                StaggeredGridCells.Fixed(1)
            }
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
            item = item
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NormalSearch(
    items: LazyPagingItems<MalEntry>,
    type : String,
    windowSizeClass: WindowSizeClass,
    textFieldState: TextFieldState,
    backStack: NavBackStack<NavKey>
) {
    // Smartphone Landscape
    val carouselSize = getCarouselHomeSize(windowSizeClass)
    val gridColumn = if (!windowSizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)) {
        StaggeredGridCells.Fixed(3)
    } else {
        when {
            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                // return for EXPANDED width size class
                StaggeredGridCells.Adaptive(carouselSize.second)
            }

            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                // return for MEDIUM width size class
                StaggeredGridCells.Adaptive(carouselSize.second)
            }

            else -> {
                // return for COMPACT width size class
                StaggeredGridCells.Fixed(2)
            }
        }
    }

    VerticalStaggeredGridPaging(
        list = items,
        columns = gridColumn,
        isHavePlaceholder = textFieldState.text.isEmpty(),
        placeholderContent = {
            LoadingIndicator(modifier = Modifier.fillMaxSize())
        },
    ) { item ->
        ImageWithCaption(
            modifier = Modifier
                .height(carouselSize.second)
                .clip(MaterialTheme.shapes.extraLarge) // Apply clipping to the Box
                .clickable {
                    backStack.add(
                        Detail(
                            id = item.id.toString(),
                            type = type
                        )
                    )
                },
            url = item.images?.webp?.largeImageUrl,
            contentDescription = item.title,
            caption = item.title
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
            onExpandedChange = { !expanded }
        ) {
            val fillMaxWidth = Modifier.fillMaxWidth()
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { }, // no typing allowed, only selection
                readOnly = true, // makes it behave like a select field
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
                            onOptionSelected(option)
                        }
                    )
                }
            }
        }
    }
}