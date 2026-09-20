package com.dirzaaulia.yomiru.navigation

import androidx.navigation3.runtime.NavKey
import com.dirzaaulia.yomiru.model.MediaReview
import com.dirzaaulia.yomiru.model.response.MediaRecommendation
import kotlinx.serialization.Serializable

enum class SearchType {
    SEARCH_ANIME, SEARCH_MANGA, SEASON, TOP, RECOMMENDED, REVIEW
}

@Serializable
object Onboarding : NavKey

@Serializable
object Home : NavKey

@Serializable
data class Search(
    val searchType: SearchType,
    val type: String,
): NavKey

@Serializable
data class Detail(
    val id: String,
    val type: String
) : NavKey

@Serializable
object List: NavKey

@Serializable
data class Review(val mediaReview: MediaReview): NavKey

@Serializable
data class Recommendation(val mediaRecommendation: MediaRecommendation): NavKey

@Serializable
object Developer: NavKey
