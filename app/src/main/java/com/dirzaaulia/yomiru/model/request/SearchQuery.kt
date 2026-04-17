package com.dirzaaulia.yomiru.model.request

import com.dirzaaulia.yomiru.network.Anime

sealed class SearchRequest {
    object Default : SearchRequest()
    data class General(val query: SearchQuery) : SearchRequest()
    data class Seasonal(
        val year: Int,
        val season: String,
        val type: String?,
        val sfw: Boolean
    ) : SearchRequest()
    data class Top(
        val type: String,
        val filter: String,
        val rating: String? = null,
        val sfw: Boolean? = null
    ) : SearchRequest()
}

data class SearchQuery(
    // Common Fields
    var query: String? = null,
    var type: String? = null,
    var sfw: Boolean = true,
    var page: Int = 1,
    var limit: Int = 25,

    // Normal Search Specific
    var minScore: Int = 0,
    var maxScore: Int = 10,
    var status: String? = null,
    var rating: String? = null,
    var genres: Int? = null,
    var orderBy: String? = null,
    var sort: String? = null,

    // Seasonal Search Specific
    var year: Int? = null,
    var season: String? = null,

    //Top Search Specific
    var filter: String? = null
) {
    companion object {
        fun SearchQuery.transformIntoResources(): Anime {
            return Anime(
                query = query,
                type = type,
                minScore = minScore,
                maxScore = maxScore,
                status = status,
                rating = rating,
                sfw = sfw,
                genres = genres,
                orderBy = orderBy,
                sort = sort,
                page = page,
                limit = limit,
                // Add these to your network model Anime if they aren't there
//                year = year,
//                season = season
            )
        }
    }
}
