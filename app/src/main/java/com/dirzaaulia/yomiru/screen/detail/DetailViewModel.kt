package com.dirzaaulia.yomiru.screen.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dirzaaulia.yomiru.model.MalCharacterEntry
import com.dirzaaulia.yomiru.model.MalEntry
import com.dirzaaulia.yomiru.model.MalEpisode
import com.dirzaaulia.yomiru.model.MalImages
import com.dirzaaulia.yomiru.model.response.MalDetailRecommendation
import com.dirzaaulia.yomiru.model.response.MalVideoResponse
import com.dirzaaulia.yomiru.model.response.PagingResponse
import com.dirzaaulia.yomiru.pagingsource.MalEpisodePagingSource
import com.dirzaaulia.yomiru.repository.NetworkRepository
import com.dirzaaulia.yomiru.util.ResponseResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DetailViewModel(
    private val repository: NetworkRepository,
    private val malEntryId: String,
    val type: String,
): ViewModel() {

    val startDestination = if (type == "anime") DetailAnimeTabDestination.INFO else DetailMangaTabDestination.INFO

    private val _malEntry: MutableStateFlow<ResponseResult<PagingResponse<MalEntry>?>>
            = MutableStateFlow(ResponseResult.Success(null))
    val malEntry = _malEntry.asStateFlow()

    private val _malCharacters: MutableStateFlow<ResponseResult<PagingResponse<List<MalCharacterEntry>>?>>
        = MutableStateFlow(ResponseResult.Success(null))
    val malCharacters = _malCharacters.asStateFlow()

    private val _malImages: MutableStateFlow<ResponseResult<PagingResponse<List<MalImages>>?>>
        = MutableStateFlow(ResponseResult.Success(null))
    val malImages = _malImages.asStateFlow()

    private val _malRecommendations: MutableStateFlow<ResponseResult<PagingResponse<List<MalDetailRecommendation>>?>>
        = MutableStateFlow(ResponseResult.Success(null))
    val malRecommendations = _malRecommendations.asStateFlow()

    private val _malVideos: MutableStateFlow<ResponseResult<PagingResponse<MalVideoResponse>?>>
            = MutableStateFlow(ResponseResult.Success(null))
    val malVideos = _malVideos.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val malEpisodes: Flow<PagingData<MalEpisode>> = Pager(
            config = PagingConfig(pageSize = 100),
            pagingSourceFactory = {
                MalEpisodePagingSource(
                    repository = repository,
                    id = malEntryId
                )
            }
        ).flow.cachedIn(viewModelScope)

    init {
        if (type.equals("anime", true)) {
            getAnimeDetail()
            getAnimeImages()
            getAnimeCharacters()
            getAnimeRecommendations()
        }
    }

    fun getAnimeDetail() {
        repository.getAnimeDetail(id = malEntryId).onEach {
            _malEntry.value = it
        }.launchIn(viewModelScope)
    }

    fun getAnimeCharacters() {
        repository.getAnimeCharacters(id = malEntryId).onEach {
            _malCharacters.value = it
        }.launchIn(viewModelScope)
    }

    fun getAnimeImages() {
        repository.getAnimePictures(id = malEntryId).onEach {
            _malImages.value = it
        }.launchIn(viewModelScope)
    }

    fun getAnimeRecommendations() {
        repository.getAnimeDetailRecommendations(id = malEntryId).onEach {
            _malRecommendations.value = it
        }.launchIn(viewModelScope)
    }

    fun getAnimeVideos() {
        repository.getAnimeVideos(id = malEntryId).onEach {
            _malVideos.value = it
        }.launchIn(viewModelScope)
    }
}