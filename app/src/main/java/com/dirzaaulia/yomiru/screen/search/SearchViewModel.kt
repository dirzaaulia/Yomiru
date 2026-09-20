package com.dirzaaulia.yomiru.screen.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dirzaaulia.yomiru.model.MediaEntry
import com.dirzaaulia.yomiru.model.MediaReview
import com.dirzaaulia.yomiru.model.request.SearchQuery
import com.dirzaaulia.yomiru.model.request.SearchRequest
import com.dirzaaulia.yomiru.model.response.MediaRecommendation
import com.dirzaaulia.yomiru.navigation.SearchType
import com.dirzaaulia.yomiru.pagingsource.MediaEntryPaging
import com.dirzaaulia.yomiru.pagingsource.MediaEntryPagingSource
import com.dirzaaulia.yomiru.pagingsource.MediaRecommendationPagingSource
import com.dirzaaulia.yomiru.pagingsource.MediaReviewPagingSource
import com.dirzaaulia.yomiru.repository.NetworkRepository
import id.pgidata.gomamam.repository.DataStoreRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class SearchViewModel(
    private val repository: NetworkRepository,
    private val dataStore: DataStoreRepository,
    private val type: SearchType,
): ViewModel() {

    private val _searchRequest: MutableStateFlow<SearchRequest> = MutableStateFlow(SearchRequest.Default)
    private val _type: MutableStateFlow<String> = MutableStateFlow("anime")

    @OptIn(ExperimentalCoroutinesApi::class)
    val search: Flow<PagingData<MediaEntry>> = _searchRequest.flatMapLatest { request ->
        Pager(
            config = PagingConfig(pageSize = 25),
            pagingSourceFactory = {
                val pagingType = when (type) {
                    SearchType.SEARCH_ANIME -> MediaEntryPaging.SEARCH_ANIME
                    SearchType.SEARCH_MANGA -> MediaEntryPaging.SEARCH_MANGA
                    SearchType.SEASON -> MediaEntryPaging.SEASONAL
                    SearchType.TOP -> {
                        if (_type.value.equals("anime", ignoreCase = true)) MediaEntryPaging.TOP_ANIME
                        else MediaEntryPaging.TOP_MANGA
                    }
                    else -> MediaEntryPaging.SEARCH_ANIME
                }
                val data = when (request) {
                    is SearchRequest.General -> request.query
                    is SearchRequest.Seasonal -> SearchQuery(
                        year = request.year,
                        season = request.season,
                        type = request.type,
                        sort = request.sort,
                        genres = request.genre,
                        sfw = request.sfw
                    )
                    is SearchRequest.Top -> SearchQuery(
                        type = request.type,
                        status = request.filter,
                        sort = request.sort,
                        country = request.country,
                        rating = request.rating,
                        sfw = request.sfw == true
                    )
                    else -> SearchQuery()
                }
                MediaEntryPagingSource(
                    repository = repository,
                    data = data,
                    type = pagingType
                )
            }
        ).flow
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val review: Flow<PagingData<MediaReview>> = _type.flatMapLatest { type ->
        Pager(
            config = PagingConfig(pageSize = 50),
            pagingSourceFactory = {
                MediaReviewPagingSource(
                    repository = repository,
                    type = type
                )
            }
        ).flow
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val recommendations: Flow<PagingData<MediaRecommendation>> = _type.flatMapLatest { type ->
        Pager(
            config = PagingConfig(pageSize = 50),
            pagingSourceFactory = {
                MediaRecommendationPagingSource(
                    repository = repository,
                    type = type
                )
            }
        ).flow
    }.cachedIn(viewModelScope)

    val genreFlow = dataStore.genreFlow

    fun setType(value: String) {
        _type.value = value
    }

    fun setSearchRequest(request: SearchRequest) {
        _searchRequest.value = request
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TAG_DIRZA", "onCleared")
    }
}
