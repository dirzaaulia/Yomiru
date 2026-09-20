package com.dirzaaulia.yomiru.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dirzaaulia.yomiru.model.MediaCharacterEntry
import com.dirzaaulia.yomiru.model.MediaEntry
import com.dirzaaulia.yomiru.model.MediaEpisode
import com.dirzaaulia.yomiru.model.MediaImages
import com.dirzaaulia.yomiru.model.MediaListEntryItem
import com.dirzaaulia.yomiru.model.MediaReviewItem
import com.dirzaaulia.yomiru.model.MediaStaffItem
import com.dirzaaulia.yomiru.model.response.MediaDetailRecommendation
import com.dirzaaulia.yomiru.model.response.MediaVideoResponse
import com.dirzaaulia.yomiru.model.response.PagingResponse
import com.dirzaaulia.yomiru.pagingsource.MediaCharacterPagingSource
import com.dirzaaulia.yomiru.pagingsource.MediaDetailReviewPagingSource
import com.dirzaaulia.yomiru.pagingsource.MediaEpisodePagingSource
import com.dirzaaulia.yomiru.pagingsource.MediaRecommendationDetailPagingSource
import com.dirzaaulia.yomiru.pagingsource.MediaReviewPagingSource
import com.dirzaaulia.yomiru.pagingsource.MediaStaffPagingSource
import com.dirzaaulia.yomiru.repository.NetworkRepository
import com.dirzaaulia.yomiru.util.ResponseResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: NetworkRepository,
    private val entryId: String,
    val type: String,
): ViewModel() {

    val startDestination: Any = if (type.equals("anime", ignoreCase = true)) DetailAnimeTabDestination.INFO else DetailMangaTabDestination.INFO

    private val _mediaEntry: MutableStateFlow<ResponseResult<PagingResponse<MediaEntry>?>>
            = MutableStateFlow(ResponseResult.Loading)
    val mediaEntry = _mediaEntry.asStateFlow()

    private val _images: MutableStateFlow<ResponseResult<PagingResponse<List<MediaImages>>?>>
        = MutableStateFlow(ResponseResult.Loading)
    val images = _images.asStateFlow()

    private val _videos: MutableStateFlow<ResponseResult<PagingResponse<MediaVideoResponse>?>>
            = MutableStateFlow(ResponseResult.Loading)
    val videos = _videos.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val episodes: Flow<PagingData<MediaEpisode>> = Pager(
        config = PagingConfig(pageSize = 100),
        pagingSourceFactory = {
            MediaEpisodePagingSource(
                repository = repository,
                id = entryId
            )
        }
    ).flow.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val characterPaging: Flow<PagingData<MediaCharacterEntry>> = Pager(
        config = PagingConfig(pageSize = 24),
        pagingSourceFactory = {
            MediaCharacterPagingSource(
                repository = repository,
                id = entryId,
                type = type
            )
        }
    ).flow.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val staffPaging: Flow<PagingData<MediaStaffItem>> = Pager(
        config = PagingConfig(pageSize = 24),
        pagingSourceFactory = {
            MediaStaffPagingSource(
                repository = repository,
                id = entryId,
                type = type
            )
        }
    ).flow.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val reviewPaging: Flow<PagingData<MediaReviewItem>> = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = {
            MediaDetailReviewPagingSource(
                repository = repository,
                id = entryId,
                type = type
            )
        }
    ).flow.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val recommendationPaging: Flow<PagingData<MediaDetailRecommendation>> = Pager(
        config = PagingConfig(pageSize = 12),
        pagingSourceFactory = {
            MediaRecommendationDetailPagingSource(
                repository = repository,
                id = entryId,
                type = type
            )
        }
    ).flow.cachedIn(viewModelScope)

    init {
        if (type.equals("anime", true)) {
            getAnimeDetail()
            getAnimeImages()
            getAnimeVideos()
        } else {
            getMangaDetail()
            getMangaImages()
        }
    }

    fun getAnimeDetail() {
        repository.getAnimeDetail(id = entryId).onEach {
            _mediaEntry.value = it
        }.launchIn(viewModelScope)
    }

    fun getAnimeImages() {
        repository.getAnimePictures(id = entryId).onEach {
            _images.value = it
        }.launchIn(viewModelScope)
    }

    fun getAnimeVideos() {
        repository.getAnimeVideos(id = entryId).onEach {
            _videos.value = it
        }.launchIn(viewModelScope)
    }

    fun getMangaDetail() {
        repository.getMangaDetail(id = entryId).onEach {
            _mediaEntry.value = it
        }.launchIn(viewModelScope)
    }

    fun getMangaImages() {
        repository.getMangaPictures(id = entryId).onEach {
            _images.value = it
        }.launchIn(viewModelScope)
    }

    fun saveMediaListEntry(
        status: String,
        score: Double,
        progress: Int,
        onResult: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            val mediaIdInt = entryId.toIntOrNull()
            if (mediaIdInt == null) {
                onResult(false, "Invalid media ID")
                return@launch
            }
            val result = repository.saveMediaListEntry(mediaIdInt, status, score, progress)
            if (result is ResponseResult.Success) {
                val currentResponse = (_mediaEntry.value as? ResponseResult.Success)?.data
                val currentData = currentResponse?.data
                if (currentData != null) {
                    val updatedEntry = currentData.copy(
                        mediaListEntry = MediaListEntryItem(
                            id = currentData.mediaListEntry?.id ?: mediaIdInt,
                            mediaId = mediaIdInt,
                            status = status,
                            score = score,
                            progress = progress
                        )
                    )
                    _mediaEntry.value = ResponseResult.Success(PagingResponse(data = updatedEntry))
                }
                refreshDetail()
                onResult(true, "Saved to Watchlist • リストを保存しました")
            } else {
                onResult(false, "Failed to save entry. Check login status.")
            }
        }
    }

    fun deleteMediaListEntry(
        listEntryId: Int,
        onResult: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            val result = repository.deleteMediaListEntry(listEntryId)
            if (result is ResponseResult.Success) {
                val currentResponse = (_mediaEntry.value as? ResponseResult.Success)?.data
                val currentData = currentResponse?.data
                if (currentData != null) {
                    val updatedEntry = currentData.copy(mediaListEntry = null)
                    _mediaEntry.value = ResponseResult.Success(PagingResponse(data = updatedEntry))
                }
                refreshDetail()
                onResult(true, "Removed from Watchlist • リストから削除しました")
            } else {
                onResult(false, "Failed to remove entry")
            }
        }
    }

    private fun refreshDetail() {
        if (type.equals("anime", true)) {
            getAnimeDetail()
        } else {
            getMangaDetail()
        }
    }
}
