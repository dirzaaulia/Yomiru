package com.dirzaaulia.yomiru.model.request

sealed class SearchRequest {
    object Default : SearchRequest()
    data class General(val query: SearchQuery) : SearchRequest()
    data class Seasonal(
        val year: Int,
        val season: String,
        val type: String?,
        val sort: String? = null,
        val genre: Int? = null,
        val sfw: Boolean = true
    ) : SearchRequest()
    data class Top(
        val type: String? = null,
        val filter: String? = null,
        val sort: String? = "SCORE_DESC",
        val country: String? = null,
        val rating: String? = null,
        val sfw: Boolean? = null
    ) : SearchRequest()
}

data class SearchQuery(
    // Common Fields
    var query: String? = null,
    var type: String? = null,
    var country: String? = null,
    var sfw: Boolean = true,
    var page: Int = 1,
    var limit: Int = 25,

    // Normal Search Specific
    var minScore: Int = 0,
    var maxScore: Int = 10,
    var status: String? = null,
    var rating: String? = null,
    var genres: Int? = null,
    var genreName: String? = null,
    var orderBy: String? = null,
    var sort: String? = null,

    // Seasonal Search Specific
    var year: Int? = null,
    var season: String? = null,

    //Top Search Specific
    var filter: String? = null
)
