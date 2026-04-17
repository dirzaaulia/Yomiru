package com.dirzaaulia.yomiru.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagingResponse<T>(
    val pagination: Pagination? = null,
    val data: T,
)

@Serializable
data class Pagination(
    @SerialName("last_visible_page")
    val lastVisiblePage: Int? = null,
    @SerialName("has_next_page")
    val hasNextPage: Boolean? = null,
    @SerialName("current_page")
    val currentPage: Int? = null,
    val items: PaginationItem? = null
)

@Serializable
data class PaginationItem(
    val count: Int? = null,
    val total: Int? = null,
    @SerialName("per_page")
    val perPage: Int? = null
)