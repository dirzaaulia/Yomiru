package com.dirzaaulia.yomiru.repository

import com.dirzaaulia.yomiru.model.AniListGraphQLResponse
import com.dirzaaulia.yomiru.model.AniListHomeScreenData
import com.dirzaaulia.yomiru.model.AniListMediaDetailData
import com.dirzaaulia.yomiru.model.AniListMediaListCollectionData
import com.dirzaaulia.yomiru.model.AniListRecommendationPageData
import com.dirzaaulia.yomiru.model.AniListReviewSearchData
import com.dirzaaulia.yomiru.model.AniListSearchData
import com.dirzaaulia.yomiru.model.AniListViewerData
import com.dirzaaulia.yomiru.model.GeneralData
import com.dirzaaulia.yomiru.model.GraphQLRequest
import com.dirzaaulia.yomiru.model.MediaCharacterEntry
import com.dirzaaulia.yomiru.model.MediaEntry
import com.dirzaaulia.yomiru.model.MediaEpisode
import com.dirzaaulia.yomiru.model.MediaGenre
import com.dirzaaulia.yomiru.model.MediaImage
import com.dirzaaulia.yomiru.model.MediaImages
import com.dirzaaulia.yomiru.model.MediaListEntryItem
import com.dirzaaulia.yomiru.model.MediaListGroupItem
import com.dirzaaulia.yomiru.model.MediaReview
import com.dirzaaulia.yomiru.model.MediaReviewItem
import com.dirzaaulia.yomiru.model.MediaStaffItem
import com.dirzaaulia.yomiru.model.MediaUser
import com.dirzaaulia.yomiru.model.request.SearchQuery
import com.dirzaaulia.yomiru.model.response.MediaDetailRecommendation
import com.dirzaaulia.yomiru.model.response.MediaRecommendation
import com.dirzaaulia.yomiru.model.response.MediaVideoResponse
import com.dirzaaulia.yomiru.model.response.Pagination
import com.dirzaaulia.yomiru.model.response.PagingResponse
import com.dirzaaulia.yomiru.model.toMediaEntry
import com.dirzaaulia.yomiru.model.toMediaRecommendation
import com.dirzaaulia.yomiru.network.AniListQueries
import com.dirzaaulia.yomiru.util.ResponseResult
import com.dirzaaulia.yomiru.util.safeFlow
import com.dirzaaulia.yomiru.util.safeIoCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import java.time.LocalDate

class NetworkRepositoryImpl(
    private val client: HttpClient
) : NetworkRepository {

    private fun currentSeason(): String {
        return when (LocalDate.now().monthValue) {
            1, 2, 3 -> "WINTER"
            4, 5, 6 -> "SPRING"
            7, 8, 9 -> "SUMMER"
            else -> "FALL"
        }
    }

    override fun getHomeScreenData(): Flow<ResponseResult<AniListHomeScreenData>> = safeFlow {
        val now = LocalDate.now()
        val variables = mapOf<String, JsonElement>(
            "season" to JsonPrimitive(currentSeason()),
            "seasonYear" to JsonPrimitive(now.year)
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.HOME_SCREEN_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListHomeScreenData>>()

        response.data ?: AniListHomeScreenData()
    }

    override suspend fun searchAnime(
        query: SearchQuery,
        page: Int,
        limit: Int
    ) = safeIoCall<PagingResponse<List<MediaEntry>>> {
        val variables = mutableMapOf<String, JsonElement>(
            "page" to JsonPrimitive(page),
            "perPage" to JsonPrimitive(limit),
            "type" to JsonPrimitive("ANIME")
        )

        query.query?.ifBlank { null }?.let {
            variables["search"] = JsonPrimitive(it)
        }

        val fmt = query.type
        if (!fmt.isNullOrBlank()) {
            variables["format"] = JsonPrimitive(fmt.uppercase().replace(" ", "_"))
        }

        val genre = query.genreName
        if (!genre.isNullOrBlank()) {
            variables["genre"] = JsonPrimitive(genre)
        }

        val sortOrder = query.sort ?: if (!query.query.isNullOrBlank()) "SEARCH_MATCH" else "POPULARITY_DESC"
        variables["sort"] = JsonArray(listOf(JsonPrimitive(sortOrder)))

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.SEARCH_MEDIA_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListSearchData>>()

        val mediaList = response.data?.Page?.media.orEmpty().map { it.toMediaEntry() }
        val hasNext = response.data?.Page?.pageInfo?.hasNextPage ?: false

        PagingResponse(
            pagination = Pagination(hasNextPage = hasNext),
            data = mediaList
        )
    }

    override suspend fun getTopAnime(
        query: SearchQuery,
        page: Int,
        limit: Int
    ) = safeIoCall {
        val variables = mutableMapOf<String, JsonElement>(
            "page" to JsonPrimitive(page),
            "perPage" to JsonPrimitive(limit),
            "type" to JsonPrimitive("ANIME")
        )

        val sortStr = query.sort ?: "SCORE_DESC"
        variables["sort"] = JsonArray(listOf(JsonPrimitive(sortStr)))

        val statusStr = query.status
        if (!statusStr.isNullOrBlank()) {
            variables["status"] = JsonPrimitive(statusStr)
        }

        val fmt = query.type
        if (!fmt.isNullOrBlank()) {
            variables["format"] = JsonPrimitive(fmt)
        }

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.SEARCH_MEDIA_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListSearchData>>()

        val mediaList = response.data?.Page?.media.orEmpty().map { it.toMediaEntry() }
        val hasNext = response.data?.Page?.pageInfo?.hasNextPage ?: false

        PagingResponse(
            pagination = Pagination(hasNextPage = hasNext),
            data = mediaList
        )
    }

    override fun getTopAnime() = safeFlow {
        val variables = mapOf<String, JsonElement>(
            "page" to JsonPrimitive(1),
            "perPage" to JsonPrimitive(12),
            "type" to JsonPrimitive("ANIME")
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.SEARCH_MEDIA_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListSearchData>>()

        val mediaList = response.data?.Page?.media.orEmpty().map { it.toMediaEntry() }
        PagingResponse(data = mediaList)
    }

    override suspend fun searchManga(
        query: SearchQuery,
        page: Int,
        limit: Int
    ) = safeIoCall {
        val variables = mutableMapOf<String, JsonElement>(
            "page" to JsonPrimitive(page),
            "perPage" to JsonPrimitive(limit),
            "type" to JsonPrimitive("MANGA")
        )

        query.query?.ifBlank { null }?.let {
            variables["search"] = JsonPrimitive(it)
        }

        val fmt = query.type
        if (!fmt.isNullOrBlank()) {
            when (fmt.uppercase().replace(" ", "_")) {
                "MANHWA" -> {
                    variables["countryOfOrigin"] = JsonPrimitive("KR")
                    variables["format"] = JsonPrimitive("MANGA")
                }
                "MANHUA" -> {
                    variables["countryOfOrigin"] = JsonPrimitive("CN")
                    variables["format"] = JsonPrimitive("MANGA")
                }
                "LIGHT_NOVEL" -> {
                    variables["format"] = JsonPrimitive("NOVEL")
                }
                else -> {
                    variables["format"] = JsonPrimitive(fmt.uppercase().replace(" ", "_"))
                }
            }
        }

        val country = query.country
        if (!country.isNullOrBlank()) {
            variables["countryOfOrigin"] = JsonPrimitive(country)
        }

        val genre = query.genreName
        if (!genre.isNullOrBlank()) {
            variables["genre"] = JsonPrimitive(genre)
        }

        val sortOrder = query.sort ?: if (!query.query.isNullOrBlank()) "SEARCH_MATCH" else "POPULARITY_DESC"
        variables["sort"] = JsonArray(listOf(JsonPrimitive(sortOrder)))

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.SEARCH_MEDIA_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListSearchData>>()

        val mediaList = response.data?.Page?.media.orEmpty().map { it.toMediaEntry() }
        val hasNext = response.data?.Page?.pageInfo?.hasNextPage ?: false

        PagingResponse(
            pagination = Pagination(hasNextPage = hasNext),
            data = mediaList
        )
    }

    override suspend fun getTopManga(
        query: SearchQuery,
        page: Int,
        limit: Int
    ) = safeIoCall {
        val variables = mutableMapOf<String, JsonElement>(
            "page" to JsonPrimitive(page),
            "perPage" to JsonPrimitive(limit),
            "type" to JsonPrimitive("MANGA")
        )

        val sortStr = query.sort ?: "SCORE_DESC"
        variables["sort"] = JsonArray(listOf(JsonPrimitive(sortStr)))

        val statusStr = query.status
        if (!statusStr.isNullOrBlank()) {
            variables["status"] = JsonPrimitive(statusStr)
        }

        val fmt = query.type
        if (!fmt.isNullOrBlank()) {
            variables["format"] = JsonPrimitive(fmt)
        }

        val country = query.country
        if (!country.isNullOrBlank()) {
            variables["countryOfOrigin"] = JsonPrimitive(country)
        }

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.SEARCH_MEDIA_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListSearchData>>()

        val mediaList = response.data?.Page?.media.orEmpty().map { it.toMediaEntry() }
        val hasNext = response.data?.Page?.pageInfo?.hasNextPage ?: false

        PagingResponse(
            pagination = Pagination(hasNextPage = hasNext),
            data = mediaList
        )
    }

    override fun getTopManga() = safeFlow {
        val variables = mapOf<String, JsonElement>(
            "page" to JsonPrimitive(1),
            "perPage" to JsonPrimitive(12),
            "type" to JsonPrimitive("MANGA")
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.SEARCH_MEDIA_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListSearchData>>()

        val mediaList = response.data?.Page?.media.orEmpty().map { it.toMediaEntry() }
        PagingResponse(data = mediaList)
    }

    override suspend fun searchReview(
        type: String,
        page: Int,
        limit: Int
    ) = safeIoCall {
        val variables = mapOf<String, JsonElement>(
            "page" to JsonPrimitive(page),
            "perPage" to JsonPrimitive(limit)
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.REVIEWS_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListReviewSearchData>>()

        val reviews = response.data?.Page?.reviews.orEmpty().map { rev ->
            val mediaEntry = rev.media?.toMediaEntry()
            MediaReview(
                id = rev.id,
                review = rev.summary.orEmpty(),
                score = (rev.score ?: 80) / 10,
                entry = mediaEntry,
                user = MediaUser(
                    username = rev.user?.name,
                    images = MediaImages(
                        webp = MediaImage(imageUrl = rev.user?.avatar?.large),
                        jpg = MediaImage(imageUrl = rev.user?.avatar?.large)
                    )
                )
            )
        }

        val hasNext = response.data?.Page?.pageInfo?.hasNextPage ?: false

        PagingResponse(
            pagination = Pagination(hasNextPage = hasNext),
            data = reviews
        )
    }

    override fun getTopReview(type: String) = safeFlow {
        val variables = mapOf<String, JsonElement>(
            "page" to JsonPrimitive(1),
            "perPage" to JsonPrimitive(5)
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.REVIEWS_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListReviewSearchData>>()

        val reviews = response.data?.Page?.reviews.orEmpty().map { rev ->
            val mediaEntry = rev.media?.toMediaEntry()
            MediaReview(
                id = rev.id,
                review = rev.summary.orEmpty(),
                score = (rev.score ?: 80) / 10,
                entry = mediaEntry,
                user = MediaUser(
                    username = rev.user?.name,
                    images = MediaImages(
                        webp = MediaImage(imageUrl = rev.user?.avatar?.large),
                        jpg = MediaImage(imageUrl = rev.user?.avatar?.large)
                    )
                )
            )
        }

        PagingResponse(data = reviews)
    }

    override suspend fun searchRecommendations(
        type: String,
        page: Int,
        limit: Int
    ) = safeIoCall<PagingResponse<List<MediaRecommendation>>> {
        val variables = mapOf<String, JsonElement>(
            "page" to JsonPrimitive(page),
            "perPage" to JsonPrimitive(limit)
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.RECOMMENDATIONS_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListRecommendationPageData>>()

        val recommendations = response.data?.Page?.recommendations.orEmpty()
            .map { it.toMediaRecommendation() }
        val hasNext = response.data?.Page?.pageInfo?.hasNextPage ?: false

        PagingResponse(
            pagination = Pagination(hasNextPage = hasNext),
            data = recommendations
        )
    }

    override fun getAnimeRecommendations() = safeFlow<PagingResponse<List<MediaRecommendation>>> {
        val now = LocalDate.now()
        val variables = mapOf<String, JsonElement>(
            "season" to JsonPrimitive(currentSeason()),
            "seasonYear" to JsonPrimitive(now.year)
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.HOME_SCREEN_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListHomeScreenData>>()

        val recommendations = response.data?.recommendations?.recommendations.orEmpty()
            .map { it.toMediaRecommendation() }

        PagingResponse(data = recommendations)
    }

    override fun getMangaRecommendations() = safeFlow<PagingResponse<List<MediaRecommendation>>> {
        val now = LocalDate.now()
        val variables = mapOf<String, JsonElement>(
            "season" to JsonPrimitive(currentSeason()),
            "seasonYear" to JsonPrimitive(now.year)
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.HOME_SCREEN_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListHomeScreenData>>()

        val recommendations = response.data?.recommendations?.recommendations.orEmpty()
            .map { it.toMediaRecommendation() }

        PagingResponse(data = recommendations)
    }

    override fun getAnimeSeasonsNow() = safeFlow {
        val now = LocalDate.now()
        val variables = mapOf<String, JsonElement>(
            "page" to JsonPrimitive(1),
            "perPage" to JsonPrimitive(12),
            "type" to JsonPrimitive("ANIME"),
            "season" to JsonPrimitive(currentSeason()),
            "seasonYear" to JsonPrimitive(now.year)
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.SEARCH_MEDIA_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListSearchData>>()

        val mediaList = response.data?.Page?.media.orEmpty().map { it.toMediaEntry() }
        PagingResponse(data = mediaList)
    }

    override suspend fun getAnimeSeason(
        query: SearchQuery,
        page: Int,
        limit: Int
    ) = safeIoCall {
        val now = LocalDate.now()
        val seasonYear = query.year ?: now.year
        val season = query.season?.uppercase() ?: currentSeason()

        val variables = mutableMapOf<String, JsonElement>(
            "page" to JsonPrimitive(page),
            "perPage" to JsonPrimitive(limit),
            "type" to JsonPrimitive("ANIME"),
            "season" to JsonPrimitive(season),
            "seasonYear" to JsonPrimitive(seasonYear)
        )

        val fmt = query.type
        if (!fmt.isNullOrBlank()) {
            variables["format"] = JsonPrimitive(fmt.uppercase())
        }
        val sortOrder = query.sort
        if (!sortOrder.isNullOrBlank()) {
            variables["sort"] = JsonPrimitive(sortOrder.uppercase())
        }

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.SEARCH_MEDIA_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListSearchData>>()

        val mediaList = response.data?.Page?.media.orEmpty().map { it.toMediaEntry() }
        val hasNext = response.data?.Page?.pageInfo?.hasNextPage ?: false

        PagingResponse(
            pagination = Pagination(hasNextPage = hasNext),
            data = mediaList
        )
    }

    override fun getAnimeDetail(id: String) = safeFlow {
        val mediaId = id.toIntOrNull() ?: 1
        val variables = mapOf<String, JsonElement>(
            "id" to JsonPrimitive(mediaId),
            "type" to JsonPrimitive("ANIME")
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.MEDIA_DETAIL_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListMediaDetailData>>()

        val entry = response.data?.Media?.toMediaEntry() ?: MediaEntry()
        PagingResponse(data = entry)
    }

    override fun getAnimePictures(id: String) = safeFlow<PagingResponse<List<MediaImages>>> {
        val mediaId = id.toIntOrNull() ?: 1
        val variables = mapOf<String, JsonElement>(
            "id" to JsonPrimitive(mediaId),
            "type" to JsonPrimitive("ANIME")
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.MEDIA_DETAIL_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListMediaDetailData>>()

        val media = response.data?.Media
        val images = mutableListOf<MediaImages>()

        media?.coverImage?.extraLarge?.let { url ->
            images.add(
                MediaImages(
                    webp = MediaImage(largeImageUrl = url, imageUrl = url, maximumImageUrl = url),
                    jpg = MediaImage(largeImageUrl = url, imageUrl = url, maximumImageUrl = url)
                )
            )
        }

        media?.bannerImage?.let { url ->
            images.add(
                MediaImages(
                    webp = MediaImage(largeImageUrl = url, imageUrl = url, maximumImageUrl = url),
                    jpg = MediaImage(largeImageUrl = url, imageUrl = url, maximumImageUrl = url)
                )
            )
        }

        PagingResponse(data = images)
    }

    override suspend fun getAnimeEpisodes(
        id: String,
        page: Int
    ) = safeIoCall<PagingResponse<List<MediaEpisode>>> {
        val mediaId = id.toIntOrNull() ?: 1
        val variables = mapOf<String, JsonElement>(
            "id" to JsonPrimitive(mediaId),
            "type" to JsonPrimitive("ANIME")
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.MEDIA_DETAIL_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListMediaDetailData>>()

        val media = response.data?.Media
        val streamingEps = media?.streamingEpisodes.orEmpty()
        val totalEps = (media?.episodes ?: streamingEps.size).coerceAtLeast(streamingEps.size).coerceAtLeast(12)
        val score = ((media?.meanScore ?: media?.averageScore ?: 80) / 10).toDouble()

        val limit = 25
        val startEpIndex = (page - 1) * limit + 1
        val endEpIndex = (page * limit).coerceAtMost(totalEps)

        val pageEpisodes = mutableListOf<MediaEpisode>()
        for (epNum in startEpIndex..endEpIndex) {
            val streamEp = streamingEps.getOrNull(epNum - 1)
            pageEpisodes.add(
                MediaEpisode(
                    id = epNum.toString(),
                    title = streamEp?.title ?: "Episode $epNum",
                    url = streamEp?.url,
                    score = score,
                    aired = streamEp?.site ?: "Aired",
                    filler = false,
                    recap = false
                )
            )
        }

        val hasNext = endEpIndex < totalEps

        PagingResponse(
            pagination = Pagination(hasNextPage = hasNext),
            data = pageEpisodes
        )
    }

    override suspend fun getAnimeCharacters(
        id: String,
        type: String,
        page: Int,
        limit: Int
    ) = safeIoCall<PagingResponse<List<MediaCharacterEntry>>> {
        val mediaId = id.toIntOrNull() ?: 1
        val mediaType = if (type.equals("manga", true)) "MANGA" else "ANIME"
        val variables = mapOf<String, JsonElement>(
            "id" to JsonPrimitive(mediaId),
            "type" to JsonPrimitive(mediaType),
            "page" to JsonPrimitive(page),
            "perPage" to JsonPrimitive(limit)
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.MEDIA_CHARACTERS_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListMediaDetailData>>()

        val characters = response.data?.Media?.characters?.edges.orEmpty().map { edge ->
            val charNode = edge.node
            val charImg = charNode?.image?.large

            val mappedVoiceActors = edge.voiceActors.map { vaNode ->
                val vaImg = vaNode.image?.large
                MediaEntry(
                    id = vaNode.id?.toString(),
                    title = vaNode.name?.full,
                    images = MediaImages(
                        webp = MediaImage(imageUrl = vaImg, largeImageUrl = vaImg),
                        jpg = MediaImage(imageUrl = vaImg, largeImageUrl = vaImg)
                    ),
                    language = vaNode.languageV2 ?: "Japanese"
                )
            }

            MediaCharacterEntry(
                role = edge.role,
                character = MediaEntry(
                    id = charNode?.id?.toString(),
                    title = charNode?.name?.full,
                    images = MediaImages(
                        webp = MediaImage(imageUrl = charImg, largeImageUrl = charImg),
                        jpg = MediaImage(imageUrl = charImg, largeImageUrl = charImg)
                    )
                ),
                voiceActors = mappedVoiceActors
            )
        }

        val hasNext = response.data?.Media?.characters?.pageInfo?.hasNextPage ?: false

        PagingResponse(
            pagination = Pagination(hasNextPage = hasNext),
            data = characters
        )
    }

    override suspend fun getAnimeStaff(
        id: String,
        type: String,
        page: Int,
        limit: Int
    ) = safeIoCall<PagingResponse<List<MediaStaffItem>>> {
        val mediaId = id.toIntOrNull() ?: 1
        val mediaType = if (type.equals("manga", true)) "MANGA" else "ANIME"
        val variables = mapOf<String, JsonElement>(
            "id" to JsonPrimitive(mediaId),
            "type" to JsonPrimitive(mediaType),
            "page" to JsonPrimitive(page),
            "perPage" to JsonPrimitive(limit)
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.MEDIA_STAFF_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListMediaDetailData>>()

        val staff = response.data?.Media?.staff?.edges.orEmpty().map { edge ->
            MediaStaffItem(
                id = edge.node?.id,
                name = edge.node?.name?.full,
                nativeName = edge.node?.name?.native,
                role = edge.role,
                imageUrl = edge.node?.image?.large ?: edge.node?.image?.medium
            )
        }

        val hasNext = response.data?.Media?.staff?.pageInfo?.hasNextPage ?: false

        PagingResponse(
            pagination = Pagination(hasNextPage = hasNext),
            data = staff
        )
    }

    override suspend fun getAnimeReviews(
        id: String,
        type: String,
        page: Int,
        limit: Int
    ) = safeIoCall<PagingResponse<List<MediaReviewItem>>> {
        val mediaId = id.toIntOrNull() ?: 1
        val mediaType = if (type.equals("manga", true)) "MANGA" else "ANIME"
        val variables = mapOf<String, JsonElement>(
            "id" to JsonPrimitive(mediaId),
            "type" to JsonPrimitive(mediaType),
            "page" to JsonPrimitive(page),
            "perPage" to JsonPrimitive(limit)
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.MEDIA_REVIEWS_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListMediaDetailData>>()

        val reviews = response.data?.Media?.reviews?.nodes.orEmpty().map { rev ->
            MediaReviewItem(
                id = rev.id,
                summary = rev.summary,
                body = rev.body,
                rating = rev.rating,
                score = rev.score,
                userName = rev.user?.name,
                userAvatarUrl = rev.user?.avatar?.large
            )
        }

        val hasNext = response.data?.Media?.reviews?.pageInfo?.hasNextPage ?: false

        PagingResponse(
            pagination = Pagination(hasNextPage = hasNext),
            data = reviews
        )
    }

    override suspend fun getAnimeDetailRecommendations(
        id: String,
        type: String,
        page: Int,
        limit: Int
    ) = safeIoCall<PagingResponse<List<MediaDetailRecommendation>>> {
        val mediaId = id.toIntOrNull() ?: 1
        val mediaType = if (type.equals("manga", true)) "MANGA" else "ANIME"
        val variables = mapOf<String, JsonElement>(
            "id" to JsonPrimitive(mediaId),
            "type" to JsonPrimitive(mediaType),
            "page" to JsonPrimitive(page),
            "perPage" to JsonPrimitive(limit)
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.MEDIA_DETAIL_RECOMMENDATIONS_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListMediaDetailData>>()

        val recNodes = response.data?.Media?.recommendations?.nodes.orEmpty()
        val detailRecommendations = recNodes.mapNotNull { node ->
            val recMedia = node.mediaRecommendation
            if (recMedia != null) {
                MediaDetailRecommendation(
                    entry = recMedia.toMediaEntry(),
                    votes = node.rating
                )
            } else {
                null
            }
        }

        val hasNext = response.data?.Media?.recommendations?.pageInfo?.hasNextPage ?: false

        PagingResponse(
            pagination = Pagination(hasNextPage = hasNext),
            data = detailRecommendations
        )
    }

    override fun getAnimeVideos(id: String) = safeFlow<PagingResponse<MediaVideoResponse>> {
        PagingResponse(data = MediaVideoResponse())
    }

    override fun getMangaDetail(id: String) = safeFlow<PagingResponse<MediaEntry>> {
        val mediaId = id.toIntOrNull() ?: 1
        val variables = mapOf<String, JsonElement>(
            "id" to JsonPrimitive(mediaId),
            "type" to JsonPrimitive("MANGA")
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.MEDIA_DETAIL_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListMediaDetailData>>()

        val entry = response.data?.Media?.toMediaEntry() ?: MediaEntry()
        PagingResponse(data = entry)
    }

    override fun getMangaPictures(id: String) = safeFlow<PagingResponse<List<MediaImages>>> {
        val mediaId = id.toIntOrNull() ?: 1
        val variables = mapOf<String, JsonElement>(
            "id" to JsonPrimitive(mediaId),
            "type" to JsonPrimitive("MANGA")
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.MEDIA_DETAIL_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListMediaDetailData>>()

        val media = response.data?.Media
        val images = mutableListOf<MediaImages>()

        media?.coverImage?.extraLarge?.let { url ->
            images.add(
                MediaImages(
                    webp = MediaImage(largeImageUrl = url, imageUrl = url, maximumImageUrl = url),
                    jpg = MediaImage(largeImageUrl = url, imageUrl = url, maximumImageUrl = url)
                )
            )
        }

        PagingResponse(data = images)
    }

    override fun getAnimeGenres() = safeFlow<GeneralData<List<MediaGenre>>> {
        val defaultGenres = listOf(
            MediaGenre(id = 1, name = "Action", count = 0, url = ""),
            MediaGenre(id = 2, name = "Adventure", count = 0, url = ""),
            MediaGenre(id = 4, name = "Comedy", count = 0, url = ""),
            MediaGenre(id = 8, name = "Drama", count = 0, url = ""),
            MediaGenre(id = 10, name = "Fantasy", count = 0, url = ""),
            MediaGenre(id = 14, name = "Horror", count = 0, url = ""),
            MediaGenre(id = 22, name = "Romance", count = 0, url = ""),
            MediaGenre(id = 24, name = "Sci-Fi", count = 0, url = ""),
            MediaGenre(id = 36, name = "Slice of Life", count = 0, url = ""),
            MediaGenre(id = 37, name = "Supernatural", count = 0, url = "")
        )

        GeneralData(data = defaultGenres)
    }

    override suspend fun getUserMediaList(
        type: String
    ) = safeIoCall<List<MediaListGroupItem>> {
        val viewerResponse = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.VIEWER_QUERY, variables = emptyMap()))
        }.body<AniListGraphQLResponse<AniListViewerData>>()

        val userId = viewerResponse.data?.Viewer?.id
        if (userId == null) {
            return@safeIoCall emptyList()
        }

        val mediaType = if (type.equals("manga", true)) "MANGA" else "ANIME"
        val variables = mapOf<String, JsonElement>(
            "userId" to JsonPrimitive(userId),
            "type" to JsonPrimitive(mediaType)
        )

        val response = client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.USER_MEDIA_LIST_QUERY, variables = variables))
        }.body<AniListGraphQLResponse<AniListMediaListCollectionData>>()

        val lists = response.data?.MediaListCollection?.lists.orEmpty()
        lists.map { group ->
            val mappedEntries = group.entries.mapNotNull { node ->
                val entry = node.media?.toMediaEntry()
                entry?.copy(
                    mediaListEntry = MediaListEntryItem(
                        id = node.id,
                        mediaId = node.mediaId,
                        status = node.status,
                        score = node.score,
                        progress = node.progress,
                        progressVolumes = node.progressVolumes
                    )
                )
            }

            MediaListGroupItem(
                name = group.name.orEmpty(),
                status = group.status.orEmpty(),
                entries = mappedEntries
            )
        }
    }

    override suspend fun saveMediaListEntry(
        mediaId: Int,
        status: String,
        score: Double,
        progress: Int
    ) = safeIoCall<Boolean> {
        val variables = mapOf<String, JsonElement>(
            "mediaId" to JsonPrimitive(mediaId),
            "status" to JsonPrimitive(status),
            "score" to JsonPrimitive(score),
            "progress" to JsonPrimitive(progress)
        )

        client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.SAVE_MEDIA_LIST_ENTRY_MUTATION, variables = variables))
        }

        true
    }

    override suspend fun deleteMediaListEntry(
        entryId: Int
    ) = safeIoCall<Boolean> {
        val variables = mapOf<String, JsonElement>(
            "id" to JsonPrimitive(entryId)
        )

        client.post("") {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(query = AniListQueries.DELETE_MEDIA_LIST_ENTRY_MUTATION, variables = variables))
        }

        true
    }
}
