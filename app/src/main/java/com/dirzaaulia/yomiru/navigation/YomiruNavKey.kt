package com.dirzaaulia.yomiru.navigation

import androidx.navigation3.runtime.NavKey
import com.dirzaaulia.yomiru.model.MalEntry
import com.dirzaaulia.yomiru.model.MalReview
import com.dirzaaulia.yomiru.model.response.MalRecommendation
import kotlinx.serialization.Serializable

enum class SearchType {
    SEARCH_ANIME, SEARCH_MANGA, SEASON, TOP, RECOMMENDED, REVIEW
}

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
data class Review(val malReview: MalReview): NavKey

@Serializable
data class Recommendation(val malRecommendation: MalRecommendation): NavKey