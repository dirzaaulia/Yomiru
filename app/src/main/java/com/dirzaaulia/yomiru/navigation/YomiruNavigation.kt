package com.dirzaaulia.yomiru.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.dirzaaulia.yomiru.screen.detail.DetailScreen
import com.dirzaaulia.yomiru.screen.home.HomeScreen
import com.dirzaaulia.yomiru.screen.list.ListScreen
import com.dirzaaulia.yomiru.screen.recommendation.RecommendationScreen
import com.dirzaaulia.yomiru.screen.review.ReviewScreen
import com.dirzaaulia.yomiru.screen.search.SearchScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun YomiruNavigation() {

    val backStack = rememberNavBackStack(Home)
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    NavDisplay(
        backStack = backStack,
        sceneStrategy = listDetailStrategy,
        entryDecorators = listOf(
            // Add the default decorators for managing scenes and saving state
            rememberSaveableStateHolderNavEntryDecorator(),
            // Then add the view model store decorator
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Home> {
                HomeScreen(
                    backStack = backStack,
                )
            }
            entry<Search>(
                metadata = ListDetailSceneStrategy.listPane()
            ) { search ->
                SearchScreen(
                    viewModel = koinViewModel {
                        parametersOf(search.searchType)
                    },
                    backStack = backStack,
                    searchType = search.searchType,
                    type = search.type
                )
            }
            entry<Detail>(
                // --- Conditional Metadata for Detail screen ---
                metadata = ListDetailSceneStrategy.detailPane() // Part of two-pane layout by default
            ) { detail ->
                DetailScreen(viewModel = koinViewModel {
                    parametersOf(detail.id, detail.type)
                })
            }
            entry<List> {
                ListScreen(backStack = backStack)
            }
            entry<Review> { review ->
                ReviewScreen(item = review.malReview)
            }
            entry<Recommendation> { recommendation ->
                RecommendationScreen(item = recommendation.malRecommendation)
            }
        }
    )
}