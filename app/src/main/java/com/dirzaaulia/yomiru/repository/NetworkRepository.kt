package com.dirzaaulia.yomiru.repository

import com.dirzaaulia.yomiru.model.GeneralData
import com.dirzaaulia.yomiru.model.MalCharacterEntry
import com.dirzaaulia.yomiru.model.MalEntry
import com.dirzaaulia.yomiru.model.MalEpisode
import com.dirzaaulia.yomiru.model.MalGenre
import com.dirzaaulia.yomiru.model.MalImages
import com.dirzaaulia.yomiru.model.MalReview
import com.dirzaaulia.yomiru.model.request.SearchQuery
import com.dirzaaulia.yomiru.model.response.MalDetailRecommendation
import com.dirzaaulia.yomiru.model.response.MalRecommendation
import com.dirzaaulia.yomiru.model.response.MalTokenResponse
import com.dirzaaulia.yomiru.model.response.MalUserListResponse
import com.dirzaaulia.yomiru.model.response.MalVideoResponse
import com.dirzaaulia.yomiru.model.response.PagingResponse
import com.dirzaaulia.yomiru.util.ResponseResult
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {

    /**
     * MyAnimeList
     */
    fun getToken(
        clientId: String,
        grantType: String,
        code: String,
        codeVerifier: String,
        redirectUri: String
    ): Flow<ResponseResult<MalTokenResponse>>

    suspend fun getUserAnimeList(
        userName: String,
        offset: Int,
        status: String
    ): ResponseResult<MalUserListResponse>

    /**
     * Jikan
     */

    //Search
    suspend fun animeSearch(data: SearchQuery): ResponseResult<PagingResponse<List<MalEntry>>>
    suspend fun mangaSearch(query: String?, page: Int): ResponseResult<PagingResponse<List<MalEntry>>>

    //Top
    suspend fun getTopAnime(
        query: SearchQuery,
        page: Int
    ): ResponseResult<PagingResponse<List<MalEntry>>>
    fun getTopAnime(): Flow<ResponseResult<PagingResponse<List<MalEntry>>>>
    suspend fun getTopManga(
        query: SearchQuery,
        page: Int
    ): ResponseResult<PagingResponse<List<MalEntry>>>
    fun getTopManga(): Flow<ResponseResult<PagingResponse<List<MalEntry>>>>
    suspend fun getTopReview(
        type: String,
        page: Int
    ): ResponseResult<PagingResponse<List<MalReview>>>
    fun getTopReview(type: String): Flow<ResponseResult<PagingResponse<List<MalReview>>>>

    //Recommendations
    suspend fun getAnimeRecommendations(page: Int): ResponseResult<PagingResponse<List<MalRecommendation>>>
    suspend fun getMangaRecommendations(page: Int): ResponseResult<PagingResponse<List<MalRecommendation>>>
    fun getAnimeRecommendations(): Flow<ResponseResult<PagingResponse<List<MalRecommendation>>>>
    fun getMangaRecommendations(): Flow<ResponseResult<PagingResponse<List<MalRecommendation>>>>

    fun getAnimeSeasonsNow(): Flow<ResponseResult<PagingResponse<List<MalEntry>>>>
    suspend fun getAnimeSeason(
        query: SearchQuery,
        page: Int,
        limit: Int,
    ): ResponseResult<PagingResponse<List<MalEntry>>>

    //Anime Detail
    fun getAnimeDetail(id: String): Flow<ResponseResult<PagingResponse<MalEntry>>>
    fun getAnimeCharacters(id: String): Flow<ResponseResult<PagingResponse<List<MalCharacterEntry>>>>
    fun getAnimePictures(id: String): Flow<ResponseResult<PagingResponse<List<MalImages>>>>
    suspend fun getAnimeEpisodes(id: String, page: Int): ResponseResult<PagingResponse<List<MalEpisode>>>
    fun getAnimeDetailRecommendations(id: String): Flow<ResponseResult<PagingResponse<List<MalDetailRecommendation>>>>
    fun getAnimeVideos(id: String): Flow<ResponseResult<PagingResponse<MalVideoResponse>>>

    //Genres
    fun getAnimeGenres(): Flow<ResponseResult<GeneralData<List<MalGenre>>>>
}