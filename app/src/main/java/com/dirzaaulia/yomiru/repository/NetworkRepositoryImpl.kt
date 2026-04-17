package com.dirzaaulia.yomiru.repository

import com.dirzaaulia.yomiru.model.GeneralData
import com.dirzaaulia.yomiru.model.MalCharacterEntry
import com.dirzaaulia.yomiru.model.MalEntry
import com.dirzaaulia.yomiru.model.MalEpisode
import com.dirzaaulia.yomiru.model.MalGenre
import com.dirzaaulia.yomiru.model.MalImages
import com.dirzaaulia.yomiru.model.MalReview
import com.dirzaaulia.yomiru.model.request.SearchQuery
import com.dirzaaulia.yomiru.model.request.SearchQuery.Companion.transformIntoResources
import com.dirzaaulia.yomiru.model.response.MalDetailRecommendation
import com.dirzaaulia.yomiru.model.response.MalRecommendation
import com.dirzaaulia.yomiru.model.response.MalTokenResponse
import com.dirzaaulia.yomiru.model.response.MalUserListResponse
import com.dirzaaulia.yomiru.model.response.MalVideoResponse
import com.dirzaaulia.yomiru.model.response.PagingResponse
import com.dirzaaulia.yomiru.network.Anime
import com.dirzaaulia.yomiru.network.Genres
import com.dirzaaulia.yomiru.network.Manga
import com.dirzaaulia.yomiru.network.Recommendations
import com.dirzaaulia.yomiru.network.Seasons
import com.dirzaaulia.yomiru.network.Token
import com.dirzaaulia.yomiru.network.Top
import com.dirzaaulia.yomiru.network.Users
import com.dirzaaulia.yomiru.util.ResponseResult
import com.dirzaaulia.yomiru.util.executeWithData
import com.dirzaaulia.yomiru.util.safeFlow
import com.dirzaaulia.yomiru.util.safeIoCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.setBody
import io.ktor.http.Parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.time.Year

class NetworkRepositoryImpl(
    private val jikanClient: HttpClient,
    private val malAuthClient: HttpClient,
    private val malClient: HttpClient
) : NetworkRepository {

    /**
     * MyAnimeList
     */
    override fun getToken(
        clientId: String,
        grantType: String,
        code: String,
        codeVerifier: String,
        redirectUri: String
    ) = flow {
        emit(ResponseResult.Loading)
        emit(
            executeWithData<MalTokenResponse> {
                malAuthClient.post(Token) {
                    setBody(FormDataContent(Parameters.build {
                        append("client_id", clientId) // As per docs, client_id is also in the body
                        append("code", code)
                        append("code_verifier", codeVerifier)
                        append("grant_type", grantType)
                    }))
                }.body()
            }
        )
    }.flowOn(Dispatchers.IO)

    override suspend fun getUserAnimeList(
        userName: String,
        offset: Int,
        status: String
    ) = withContext(Dispatchers.IO) {
        executeWithData<MalUserListResponse> {
            malClient.get(
                Users.Username.AnimeList(
                    parent = Users.Username(
                        userName = userName
                    ),
                    offset = offset,
                    status = status
                )
            ).body()
        }
    }

    /**
     * Jikan
     */

    //Top
    override suspend fun getTopAnime(
        query: SearchQuery,
        page: Int
    ) = safeIoCall<PagingResponse<List<MalEntry>>> {
        jikanClient.get(
            Top.Anime(
                type = query.type,
                filter = query.filter,
                rating = query.rating,
                sfw = query.sfw,
                page = page
            )
        ).body()
    }

    override fun getTopAnime() = safeFlow<PagingResponse<List<MalEntry>>> {
        jikanClient.get(Top.Anime(page = 1, limit = 10)).body()
    }

    override suspend fun getTopManga(
        query: SearchQuery,
        page: Int
    ) = safeIoCall<PagingResponse<List<MalEntry>>> {
        jikanClient.get(
            Top.Manga(
                type = query.type,
                filter = query.filter,
                page = page
            )
        ).body()
    }

    override fun getTopManga() = safeFlow<PagingResponse<List<MalEntry>>> {
        jikanClient.get(Top.Manga(page = 1, limit = 10)).body()
    }

    //Review
    override suspend fun getTopReview(
        type: String,
        page: Int
    ) = safeIoCall<PagingResponse<List<MalReview>>> {
        jikanClient.get(
            Top.Reviews(
                type = type,
                page = page,
            )
        ).body()
    }

    override fun getTopReview(type: String) = safeFlow<PagingResponse<List<MalReview>>> {
        jikanClient.get(Top.Reviews(type = type, page = 1)).body()
    }


    //Recomendations
    override suspend fun getAnimeRecommendations(
        page: Int
    ) = safeIoCall<PagingResponse<List<MalRecommendation>>> {
        jikanClient.get(Recommendations.Anime(page = page)).body()
    }

    override suspend fun getMangaRecommendations(
        page: Int
    ) = safeIoCall<PagingResponse<List<MalRecommendation>>> {
        jikanClient.get(Recommendations.Manga(page = page)).body()
    }


    override fun getAnimeRecommendations() = safeFlow<PagingResponse<List<MalRecommendation>>> {
        jikanClient.get(Recommendations.Anime(page = 1)).body()
    }

    override fun getMangaRecommendations() = safeFlow<PagingResponse<List<MalRecommendation>>> {
        jikanClient.get(Recommendations.Manga(page = 1)).body()
    }

    //Search
    override suspend fun animeSearch(
        data: SearchQuery
    ) = safeIoCall<PagingResponse<List<MalEntry>>> {
        jikanClient.get(data.transformIntoResources()).body()
    }

    override suspend fun mangaSearch(
        query: String?,
        page: Int
    ) = safeIoCall<PagingResponse<List<MalEntry>>> {
        val resource = Manga(
            query = query,
            page = page,
            limit = 25
        )
        jikanClient.get(resource).body()
    }

    override fun getAnimeSeasonsNow() = safeFlow<PagingResponse<List<MalEntry>>>{
        jikanClient.get(Seasons.Now(page = 1, limit = 10)).body()
    }

    override suspend fun getAnimeSeason(
        query: SearchQuery,
        page: Int,
        limit: Int
    ) = safeIoCall<PagingResponse<List<MalEntry>>> {
        jikanClient.get(
            Seasons.Year.Season(
                parent = Seasons.Year(
                    year = query.year ?: Year.now().value
                ),
                season = query.season ?: "winter",
                page = page,
                limit = limit,
                sfw = query.sfw,
                type = query.type
            )
        ).body()
    }

    override fun getAnimeDetail(id: String) = safeFlow<PagingResponse<MalEntry>> {
        jikanClient.get(Anime.Id(id = id)).body()
    }

    override fun getAnimeCharacters(id: String) = safeFlow<PagingResponse<List<MalCharacterEntry>>> {
        jikanClient.get(Anime.Id.Characters(Anime.Id(id = id))).body()
    }

    override fun getAnimePictures(id: String) = safeFlow<PagingResponse<List<MalImages>>> {
        jikanClient.get(Anime.Id.Pictures(Anime.Id(id = id))).body()
    }

    override suspend fun getAnimeEpisodes(
        id: String,
        page: Int
    ) = safeIoCall<PagingResponse<List<MalEpisode>>> {
        jikanClient.get(
            Anime.Id.Episodes(
                parent = Anime.Id(id = id),
                page = page
            )
        ).body()
    }

    override fun getAnimeDetailRecommendations(
        id: String
    ) = safeFlow<PagingResponse<List<MalDetailRecommendation>>> {
        jikanClient.get(
            Anime.Id.Recommendations(
                parent = Anime.Id(id = id)
            )
        ).body()
    }

    override fun getAnimeVideos(id: String) = safeFlow<PagingResponse<MalVideoResponse>> {
        jikanClient.get(Anime.Id.Videos(Anime.Id(id = id))).body()
    }

    override fun getAnimeGenres() = safeFlow<GeneralData<List<MalGenre>>> {
        jikanClient.get(Genres.Anime()).body()
    }
}