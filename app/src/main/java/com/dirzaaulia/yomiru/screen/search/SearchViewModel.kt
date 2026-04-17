package com.dirzaaulia.yomiru.screen.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dirzaaulia.yomiru.model.MalEntry
import com.dirzaaulia.yomiru.model.MalReview
import com.dirzaaulia.yomiru.model.request.SearchQuery
import com.dirzaaulia.yomiru.model.request.SearchRequest
import com.dirzaaulia.yomiru.model.response.MalRecommendation
import com.dirzaaulia.yomiru.navigation.SearchType
import com.dirzaaulia.yomiru.pagingsource.MalEntryPaging
import com.dirzaaulia.yomiru.pagingsource.MalEntryPagingSource
import com.dirzaaulia.yomiru.pagingsource.MalRecommendationPagingSource
import com.dirzaaulia.yomiru.pagingsource.MalReviewPagingSource
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
    val search: Flow<PagingData<MalEntry>> = _searchRequest.flatMapLatest { request ->
        Pager(
            config = PagingConfig(pageSize = 25),
            pagingSourceFactory = {
                val pagingType = when (type) {
                    SearchType.SEARCH_ANIME -> MalEntryPaging.SEARCH_ANIME
                    SearchType.SEARCH_MANGA -> MalEntryPaging.SEARCH_MANGA
                    SearchType.SEASON -> MalEntryPaging.SEASONAL
                    SearchType.TOP -> {
                        if (_type.value == "anime") MalEntryPaging.TOP_ANIME
                        else MalEntryPaging.TOP_MANGA
                    }
                    else -> MalEntryPaging.SEARCH_ANIME
                }
                val data = when (request) {
                    is SearchRequest.General -> request.query
                    is SearchRequest.Seasonal -> SearchQuery(
                        year = request.year,
                        season = request.season,
                        type = request.type,
                        sfw = request.sfw
                    )
                    is SearchRequest.Top -> SearchQuery(
                        type = request.type,
                        filter = request.filter,
                        rating = request.rating,
                        sfw = request.sfw == true
                    )
                    else -> SearchQuery()
                }
                MalEntryPagingSource(
                    repository = repository,
                    data = data,
                    type = pagingType
                )
            }
        ).flow
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val review: Flow<PagingData<MalReview>> = _type.flatMapLatest { type ->
        Pager(
            config = PagingConfig(pageSize = 50),
            pagingSourceFactory = {
                MalReviewPagingSource(
                    repository = repository,
                    type = type
                )
            }
        ).flow
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val recommendations: Flow<PagingData<MalRecommendation>> = _type.flatMapLatest { type ->
        Pager(
            config = PagingConfig(pageSize = 50),
            pagingSourceFactory = {
                MalRecommendationPagingSource(
                    repository = repository,
                    type = type
                )
            }
        ).flow
    }.cachedIn(viewModelScope)

    val malGenre = dataStore.genreFlow

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