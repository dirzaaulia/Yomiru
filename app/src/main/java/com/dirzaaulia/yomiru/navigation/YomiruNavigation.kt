package com.dirzaaulia.yomiru.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.dirzaaulia.yomiru.screen.detail.DetailScreen
import com.dirzaaulia.yomiru.screen.developer.DeveloperScreen
import com.dirzaaulia.yomiru.screen.home.HomeScreen
import com.dirzaaulia.yomiru.screen.list.ListScreen
import com.dirzaaulia.yomiru.screen.onboarding.OnboardingScreen
import com.dirzaaulia.yomiru.screen.recommendation.RecommendationScreen
import com.dirzaaulia.yomiru.screen.review.ReviewScreen
import com.dirzaaulia.yomiru.screen.search.SearchScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import id.pgidata.gomamam.repository.DataStoreRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun YomiruNavigation() {
    val dataStore: DataStoreRepository = koinInject()
    val isOnboardingCompleted = runBlocking { dataStore.isOnboardingCompletedFlow.first() }

    val backStack = rememberNavBackStack(if (isOnboardingCompleted) Home else Onboarding)

    NavDisplay(
        backStack = backStack,
        transitionSpec = {
            (slideInHorizontally(animationSpec = tween(300)) { width -> width } + fadeIn(animationSpec = tween(300)))
                .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> -width / 3 } + fadeOut(animationSpec = tween(300)))
        },
        popTransitionSpec = {
            (slideInHorizontally(animationSpec = tween(300)) { width -> -width / 3 } + fadeIn(animationSpec = tween(300)))
                .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> width } + fadeOut(animationSpec = tween(300)))
        },
        predictivePopTransitionSpec = { _ ->
            (slideInHorizontally(animationSpec = tween(300)) { width -> -width / 3 } + fadeIn(animationSpec = tween(300)))
                .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> width } + fadeOut(animationSpec = tween(300)))
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Onboarding> {
                OnboardingScreen(
                    backStack = backStack
                )
            }
            entry<Home> {
                HomeScreen(
                    backStack = backStack
                )
            }
            entry<Search> { search ->
                SearchScreen(
                    viewModel = koinViewModel {
                        parametersOf(search.searchType)
                    },
                    backStack = backStack,
                    searchType = search.searchType,
                    type = search.type
                )
            }
            entry<Detail> { detail ->
                DetailScreen(
                    viewModel = koinViewModel {
                        parametersOf(detail.id, detail.type)
                    },
                    onMediaClick = { id, type ->
                        backStack.add(Detail(id = id, type = type))
                    }
                )
            }
            entry<List> {
                ListScreen(backStack = backStack)
            }
            entry<Review> { review ->
                ReviewScreen(item = review.mediaReview)
            }
            entry<Recommendation> { recommendation ->
                RecommendationScreen(item = recommendation.mediaRecommendation)
            }
            entry<Developer> {
                DeveloperScreen(backStack = backStack)
            }
        }
    )
}
