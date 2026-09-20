package com.dirzaaulia.yomiru.repository

import com.dirzaaulia.yomiru.model.AniListHomeScreenData
import com.dirzaaulia.yomiru.model.GeneralData
import com.dirzaaulia.yomiru.model.MediaCharacterEntry
import com.dirzaaulia.yomiru.model.MediaEntry
import com.dirzaaulia.yomiru.model.MediaEpisode
import com.dirzaaulia.yomiru.model.MediaGenre
import com.dirzaaulia.yomiru.model.MediaImages
import com.dirzaaulia.yomiru.model.MediaListGroupItem
import com.dirzaaulia.yomiru.model.MediaReview
import com.dirzaaulia.yomiru.model.MediaReviewItem
import com.dirzaaulia.yomiru.model.MediaStaffItem
import com.dirzaaulia.yomiru.model.request.SearchQuery
import com.dirzaaulia.yomiru.model.response.MediaDetailRecommendation
import com.dirzaaulia.yomiru.model.response.MediaRecommendation
import com.dirzaaulia.yomiru.model.response.MediaVideoResponse
import com.dirzaaulia.yomiru.model.response.PagingResponse
import com.dirzaaulia.yomiru.util.ResponseResult
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {

    // AniList Bundled Home Screen Data
    fun getHomeScreenData(): Flow<ResponseResult<AniListHomeScreenData>>

    // Media Search
    suspend fun searchAnime(
        query: SearchQuery,
        page: Int,
        limit: Int
    ): ResponseResult<PagingResponse<List<MediaEntry>>>

    suspend fun getTopAnime(
        query: SearchQuery,
        page: Int,
        limit: Int
    ): ResponseResult<PagingResponse<List<MediaEntry>>>

    fun getTopAnime(): Flow<ResponseResult<PagingResponse<List<MediaEntry>>>>

    suspend fun searchManga(
        query: SearchQuery,
        page: Int,
        limit: Int
    ): ResponseResult<PagingResponse<List<MediaEntry>>>

    suspend fun getTopManga(
        query: SearchQuery,
        page: Int,
        limit: Int
    ): ResponseResult<PagingResponse<List<MediaEntry>>>

    fun getTopManga(): Flow<ResponseResult<PagingResponse<List<MediaEntry>>>>

    // Review Search
    suspend fun searchReview(
        type: String,
        page: Int,
        limit: Int
    ): ResponseResult<PagingResponse<List<MediaReview>>>

    fun getTopReview(type: String): Flow<ResponseResult<PagingResponse<List<MediaReview>>>>

    // Recommendations Search
    suspend fun searchRecommendations(
        type: String,
        page: Int,
        limit: Int
    ): ResponseResult<PagingResponse<List<MediaRecommendation>>>

    fun getAnimeRecommendations(): Flow<ResponseResult<PagingResponse<List<MediaRecommendation>>>>
    fun getMangaRecommendations(): Flow<ResponseResult<PagingResponse<List<MediaRecommendation>>>>

    // Anime Season
    fun getAnimeSeasonsNow(): Flow<ResponseResult<PagingResponse<List<MediaEntry>>>>
    suspend fun getAnimeSeason(
        query: SearchQuery,
        page: Int,
        limit: Int
    ): ResponseResult<PagingResponse<List<MediaEntry>>>

    // Anime / Manga Detail
    fun getAnimeDetail(id: String): Flow<ResponseResult<PagingResponse<MediaEntry>>>
    suspend fun getAnimeCharacters(id: String, type: String, page: Int, limit: Int): ResponseResult<PagingResponse<List<MediaCharacterEntry>>>
    suspend fun getAnimeStaff(id: String, type: String, page: Int, limit: Int): ResponseResult<PagingResponse<List<MediaStaffItem>>>
    suspend fun getAnimeReviews(id: String, type: String, page: Int, limit: Int): ResponseResult<PagingResponse<List<MediaReviewItem>>>
    fun getAnimePictures(id: String): Flow<ResponseResult<PagingResponse<List<MediaImages>>>>
    suspend fun getAnimeEpisodes(id: String, page: Int): ResponseResult<PagingResponse<List<MediaEpisode>>>
    suspend fun getAnimeDetailRecommendations(id: String, type: String, page: Int, limit: Int): ResponseResult<PagingResponse<List<MediaDetailRecommendation>>>
    fun getAnimeVideos(id: String): Flow<ResponseResult<PagingResponse<MediaVideoResponse>>>

    fun getMangaDetail(id: String): Flow<ResponseResult<PagingResponse<MediaEntry>>>
    fun getMangaPictures(id: String): Flow<ResponseResult<PagingResponse<List<MediaImages>>>>

    // Genres
    fun getAnimeGenres(): Flow<ResponseResult<GeneralData<List<MediaGenre>>>>

    // Watchlist / Media List Mutations
    suspend fun getUserMediaList(
        type: String
    ): ResponseResult<List<MediaListGroupItem>>

    suspend fun saveMediaListEntry(
        mediaId: Int,
        status: String,
        score: Double,
        progress: Int
    ): ResponseResult<Boolean>

    suspend fun deleteMediaListEntry(
        entryId: Int
    ): ResponseResult<Boolean>
}
