package com.dirzaaulia.yomiru.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirzaaulia.yomiru.model.MediaEntry
import com.dirzaaulia.yomiru.model.MediaReview
import com.dirzaaulia.yomiru.model.response.MediaRecommendation
import com.dirzaaulia.yomiru.model.response.PagingResponse
import com.dirzaaulia.yomiru.repository.NetworkRepository
import com.dirzaaulia.yomiru.util.ResponseResult
import id.pgidata.gomamam.repository.DataStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

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

    private val _selectedMenu: MutableStateFlow<YomiruMenu> = MutableStateFlow(YomiruMenu.Anime)
    val selectedMenu = _selectedMenu.asStateFlow()

    // Anime States
    private val _topAnimeEntry = MutableStateFlow<ResponseResult<PagingResponse<List<MediaEntry>>?>>(ResponseResult.Loading)
    private val _animeRecommendations = MutableStateFlow<ResponseResult<PagingResponse<List<MediaRecommendation>>?>>(ResponseResult.Loading)
    private val _animeSeason = MutableStateFlow<ResponseResult<PagingResponse<List<MediaEntry>>?>>(ResponseResult.Loading)
    val animeSeason = _animeSeason.asStateFlow()
    private val _animeTopReview = MutableStateFlow<ResponseResult<PagingResponse<List<MediaReview>>?>>(ResponseResult.Loading)

    // Manga States
    private val _topMangaEntry = MutableStateFlow<ResponseResult<PagingResponse<List<MediaEntry>>?>>(ResponseResult.Loading)
    private val _mangaRecommendations = MutableStateFlow<ResponseResult<PagingResponse<List<MediaRecommendation>>?>>(ResponseResult.Loading)
    private val _mangaTopReview = MutableStateFlow<ResponseResult<PagingResponse<List<MediaReview>>?>>(ResponseResult.Loading)

    val topMediaEntry: StateFlow<ResponseResult<PagingResponse<List<MediaEntry>>?>> =
        combine(_selectedMenu, _topAnimeEntry, _topMangaEntry) { menu, anime, manga ->
            if (menu == YomiruMenu.Manga) manga else anime
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ResponseResult.Loading)

    val recommendationsEntry: StateFlow<ResponseResult<PagingResponse<List<MediaRecommendation>>?>> =
        combine(_selectedMenu, _animeRecommendations, _mangaRecommendations) { menu, anime, manga ->
            if (menu == YomiruMenu.Manga) manga else anime
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ResponseResult.Loading)

    val topReview: StateFlow<ResponseResult<PagingResponse<List<MediaReview>>?>> =
        combine(_selectedMenu, _animeTopReview, _mangaTopReview) { menu, anime, manga ->
            if (menu == YomiruMenu.Manga) manga else anime
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ResponseResult.Loading)

    val accessToken: StateFlow<String> = dataStore.accessTokenFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    init {
        getData()
    }

    fun getData() {
        if (_selectedMenu.value == YomiruMenu.Manga) {
            getMangaData()
        } else {
            getAnimeData()
        }
        getAnimeGenres()
    }

    private fun getAnimeData() {
        if (_topAnimeEntry.value !is ResponseResult.Success) {
            getAnimeTop()
        }
        if (_animeSeason.value !is ResponseResult.Success) {
            getAnimeSeason()
        }
        if (_animeTopReview.value !is ResponseResult.Success) {
            getAnimeTopReview()
        }
        if (_animeRecommendations.value !is ResponseResult.Success) {
            getAnimeRecommendations()
        }
    }

    private fun getMangaData() {
        if (_topMangaEntry.value !is ResponseResult.Success) {
            getMangaTop()
        }
        if (_mangaTopReview.value !is ResponseResult.Success) {
            getMangaTopReview()
        }
        if (_mangaRecommendations.value !is ResponseResult.Success) {
            getMangaRecommendations()
        }
    }

    fun setSelectedMenu(menu: YomiruMenu) {
        _selectedMenu.value = menu
    }

    fun getTopEntry() {
        if (_selectedMenu.value == YomiruMenu.Manga) getMangaTop() else getAnimeTop()
    }

    fun getRecommendations() {
        if (_selectedMenu.value == YomiruMenu.Manga) getMangaRecommendations() else getAnimeRecommendations()
    }

    fun getTopReview() {
        if (_selectedMenu.value == YomiruMenu.Manga) getMangaTopReview() else getAnimeTopReview()
    }

    private fun getAnimeTop() {
        repository.getTopAnime().onEach { result ->
            _topAnimeEntry.update { result }
        }.launchIn(viewModelScope)
    }

    private fun getMangaTop() {
        repository.getTopManga().onEach { result ->
            _topMangaEntry.update { result }
        }.launchIn(viewModelScope)
    }

    private fun getAnimeRecommendations() {
        repository.getAnimeRecommendations().onEach { result ->
            _animeRecommendations.update { result }
        }.launchIn(viewModelScope)
    }

    private fun getMangaRecommendations() {
        repository.getMangaRecommendations().onEach { result ->
            _mangaRecommendations.update { result }
        }.launchIn(viewModelScope)
    }

    fun getAnimeSeason() {
        repository.getAnimeSeasonsNow().onEach { result ->
            _animeSeason.update { result }
        }.launchIn(viewModelScope)
    }

    private fun getAnimeTopReview() {
        repository.getTopReview("anime").onEach { result ->
            _animeTopReview.update { result }
        }.launchIn(viewModelScope)
    }

    private fun getMangaTopReview() {
        repository.getTopReview("manga").onEach { result ->
            _mangaTopReview.update { result }
        }.launchIn(viewModelScope)
    }

    private fun getAnimeGenres() {
        repository.getAnimeGenres().onEach { result ->
            if (result is ResponseResult.Success) {
                dataStore.setListGenre(result.data.data)
            }
        }.launchIn(viewModelScope)
    }
}
