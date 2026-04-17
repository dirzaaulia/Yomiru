package com.dirzaaulia.yomiru.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dirzaaulia.yomiru.model.GeneralData
import com.dirzaaulia.yomiru.model.MalEntry
import com.dirzaaulia.yomiru.model.MalGenre
import com.dirzaaulia.yomiru.model.MalReview
import com.dirzaaulia.yomiru.model.response.MalRecommendation
import com.dirzaaulia.yomiru.model.response.PagingResponse
import com.dirzaaulia.yomiru.repository.NetworkRepository
import com.dirzaaulia.yomiru.util.ResponseResult
import com.dirzaaulia.yomiru.util.success
import id.pgidata.gomamam.repository.DataStoreRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class YomiruMenu(
    val index: Int,
    val title: String
) {
    Default(-1, "Default"),
    Anime(0, "Anime"),
    Manga(1, "Manga")
}

class HomeViewModel(
    private val repository: NetworkRepository,
    private val dataStore: DataStoreRepository
) : ViewModel() {

    private val _topMalEntry: MutableStateFlow<ResponseResult<PagingResponse<List<MalEntry>>?>> =
        MutableStateFlow(ResponseResult.Success(null))
    val topMalEntry = _topMalEntry.asStateFlow()

    private val _recommendationsMalEntry: MutableStateFlow<ResponseResult<PagingResponse<List<MalRecommendation>>?>> =
        MutableStateFlow(ResponseResult.Success(null))
    val recommendationsMalEntry = _recommendationsMalEntry.asStateFlow()

    private val _animeSeason: MutableStateFlow<ResponseResult<PagingResponse<List<MalEntry>>?>> =
        MutableStateFlow(ResponseResult.Success(null))
    val animeSeason = _animeSeason.asStateFlow()

    private val _topReview: MutableStateFlow<ResponseResult<PagingResponse<List<MalReview>>?>> =
        MutableStateFlow(ResponseResult.Success(null))
    val topReview = _topReview.asStateFlow()


    private val _selectedMenu: MutableStateFlow<YomiruMenu> = MutableStateFlow(YomiruMenu.Default)
    val selectedMenu = _selectedMenu.asStateFlow()

    fun getData() {
        getTopEntry()
        getAnimeSeason()
        getTopReview()
        getRecommendations()
        getMalGenre()
    }

    fun getTopEntry() {
        when (_selectedMenu.value.index) {
            0 -> getAnimeTop()
            else -> getMangaTop()
        }
    }

    fun getRecommendations() {
        when (_selectedMenu.value.index) {
            0 -> getAnimeRecommendations()
            else -> getMangaRecommendations()
        }
    }

    fun getTopReview() {
        repository.getTopReview(
            type = _selectedMenu.value.title.lowercase()
        ).onEach { result ->
            _topReview.update { result }
        }.launchIn(viewModelScope)
    }

    fun setSelectedMenu(menu: YomiruMenu) {
        _selectedMenu.value = menu
    }

    private fun getAnimeTop() {
        repository.getTopAnime().onEach { result ->
            _topMalEntry.update { result }
        }.launchIn(viewModelScope)
    }

    private fun getMangaTop() {
        repository.getTopManga().onEach { result ->
            _topMalEntry.update { result }
        }.launchIn(viewModelScope)
    }

    private fun getAnimeRecommendations() {
        repository.getAnimeRecommendations().onEach { result ->
            _recommendationsMalEntry.update { result }
        }.launchIn(viewModelScope)
    }

    private fun getMangaRecommendations() {
        repository.getMangaRecommendations().onEach { result ->
            _recommendationsMalEntry.update { result }
        }.launchIn(viewModelScope)
    }

    fun getAnimeSeason() {
        repository.getAnimeSeasonsNow().onEach { result ->
            _animeSeason.update { result }
        }.launchIn(viewModelScope)
    }

    private fun getMalGenre() {
        repository.getAnimeGenres().onEach { result ->
            when (result) {
                is ResponseResult.Success<*> -> {
                    result.success {
                        val data = it.data
                        viewModelScope.launch {
                            dataStore.setListGenre(data)
                        }
                    }
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }
}