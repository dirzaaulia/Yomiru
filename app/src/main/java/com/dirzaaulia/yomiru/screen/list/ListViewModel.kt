package com.dirzaaulia.yomiru.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirzaaulia.yomiru.model.MediaListGroupItem
import com.dirzaaulia.yomiru.repository.NetworkRepository
import com.dirzaaulia.yomiru.screen.home.YomiruMenu
import com.dirzaaulia.yomiru.util.ResponseResult
import id.pgidata.gomamam.repository.DataStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListViewModel(
    private val repository: NetworkRepository,
    private val dataStore: DataStoreRepository,
): ViewModel() {

    val accessToken = dataStore.accessTokenFlow

    private val _selectedMenu = MutableStateFlow(YomiruMenu.Anime)
    val selectedMenu = _selectedMenu.asStateFlow()

    private val _animeList = MutableStateFlow<ResponseResult<List<MediaListGroupItem>>>(ResponseResult.Loading)
    private val _mangaList = MutableStateFlow<ResponseResult<List<MediaListGroupItem>>>(ResponseResult.Loading)

    val currentList: StateFlow<ResponseResult<List<MediaListGroupItem>>> =
        combine(_selectedMenu, _animeList, _mangaList) { menu, anime, manga ->
            if (menu == YomiruMenu.Manga) manga else anime
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ResponseResult.Loading)

    init {
        getUserAnimeList()
        getUserMangaList()
    }

    fun setSelectedMenu(menu: YomiruMenu) {
        _selectedMenu.value = menu
    }

    fun refreshList() {
        if (_selectedMenu.value == YomiruMenu.Manga) {
            getUserMangaList()
        } else {
            getUserAnimeList()
        }
    }

    fun getUserAnimeList() {
        viewModelScope.launch {
            _animeList.value = ResponseResult.Loading
            _animeList.value = repository.getUserMediaList("anime")
        }
    }

    fun getUserMangaList() {
        viewModelScope.launch {
            _mangaList.value = ResponseResult.Loading
            _mangaList.value = repository.getUserMediaList("manga")
        }
    }
}
