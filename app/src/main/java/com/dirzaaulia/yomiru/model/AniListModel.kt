package com.dirzaaulia.yomiru.model

import com.dirzaaulia.yomiru.model.response.MediaRecommendation
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GraphQLRequest(
    val query: String,
    val variables: Map<String, JsonElement> = emptyMap()
)

@Serializable
data class AniListGraphQLResponse<T>(
    val data: T? = null,
    val errors: List<AniListGraphQLError>? = null
)

@Serializable
data class AniListGraphQLError(
    val message: String? = null,
    val status: Int? = null
)

@Serializable
data class AniListHomeScreenData(
    val seasonal: AniListPage? = null,
    val trending: AniListPage? = null,
    val topAnime: AniListPage? = null,
    val topManga: AniListPage? = null,
    val reviews: AniListReviewPage? = null,
    val recommendations: AniListRecommendationPage? = null
)

@Serializable
data class AniListSearchData(
    val Page: AniListPage? = null
)

@Serializable
data class AniListMediaDetailData(
    val Media: AniListMedia? = null
)

@Serializable
data class AniListRecommendationPageData(
    val Page: AniListRecommendationPage? = null
)

@Serializable
data class AniListMediaListCollectionData(
    val MediaListCollection: AniListMediaListCollection? = null
)

@Serializable
data class AniListViewerData(
    val Viewer: AniListViewer? = null
)

@Serializable
data class AniListViewer(
    val id: Int? = null,
    val name: String? = null
)

@Serializable
data class AniListMediaListCollection(
    val lists: List<AniListMediaListGroup> = emptyList()
)

@Serializable
data class AniListMediaListGroup(
    val name: String? = null,
    val status: String? = null,
    val entries: List<AniListMediaListEntryNode> = emptyList()
)

@Serializable
data class AniListMediaListEntryNode(
    val id: Int? = null,
    val mediaId: Int? = null,
    val status: String? = null,
    val score: Double? = null,
    val progress: Int? = null,
    val progressVolumes: Int? = null,
    val media: AniListMedia? = null
)

@Serializable
data class AniListReviewSearchData(
    val Page: AniListReviewPage? = null
)

@Serializable
data class AniListPage(
    val pageInfo: AniListPagingInfo? = null,
    val media: List<AniListMedia> = emptyList()
)

@Serializable
data class AniListReviewPage(
    val pageInfo: AniListPagingInfo? = null,
    val reviews: List<AniListReview> = emptyList()
)

@Serializable
data class AniListRecommendationPage(
    val pageInfo: AniListPagingInfo? = null,
    val recommendations: List<AniListRecommendation> = emptyList(),
    val nodes: List<AniListRecommendation> = emptyList()
)

@Serializable
data class AniListPagingInfo(
    val total: Int? = null,
    val perPage: Int? = null,
    val currentPage: Int? = null,
    val lastPage: Int? = null,
    val hasNextPage: Boolean? = false
)

@Serializable
data class AniListMedia(
    val id: Int? = null,
    val idMal: Int? = null,
    val title: AniListTitle? = null,
    val type: String? = null,
    val format: String? = null,
    val status: String? = null,
    val description: String? = null,
    val startDate: AniListDate? = null,
    val endDate: AniListDate? = null,
    val season: String? = null,
    val seasonYear: Int? = null,
    val episodes: Int? = null,
    val duration: Int? = null,
    val chapters: Int? = null,
    val volumes: Int? = null,
    val countryOfOrigin: String? = null,
    val isLicensed: Boolean? = null,
    val source: String? = null,
    val hashtag: String? = null,
    val trailer: AniListTrailer? = null,
    val coverImage: AniListCoverImage? = null,
    val bannerImage: String? = null,
    val genres: List<String>? = null,
    val synonyms: List<String>? = null,
    val averageScore: Int? = null,
    val meanScore: Int? = null,
    val popularity: Int? = null,
    val favourites: Int? = null,
    val studios: AniListStudioConnection? = null,
    val mediaListEntry: AniListMediaListEntry? = null,
    val nextAiringEpisode: AniListNextAiringEpisode? = null,
    val rankings: List<AniListRanking> = emptyList(),
    val tags: List<AniListTag> = emptyList(),
    val characters: AniListCharacterConnection? = null,
    val staff: AniListStaffConnection? = null,
    val streamingEpisodes: List<AniListStreamingEpisode>? = null,
    val relations: AniListRelationConnection? = null,
    val reviews: AniListReviewConnection? = null,
    val stats: AniListStats? = null,
    val externalLinks: List<AniListExternalLink>? = null,
    val recommendations: AniListRecommendationPage? = null
)

@Serializable
data class AniListMediaListEntry(
    val id: Int? = null,
    val mediaId: Int? = null,
    val status: String? = null,
    val score: Double? = null,
    val progress: Int? = null,
    val progressVolumes: Int? = null,
    val repeat: Int? = null,
    val notes: String? = null
)

@Serializable
data class AniListNextAiringEpisode(
    val episode: Int? = null,
    val airingAt: Long? = null,
    val timeUntilAiring: Long? = null
)

@Serializable
data class AniListRanking(
    val id: Int? = null,
    val rank: Int? = null,
    val type: String? = null,
    val format: String? = null,
    val year: Int? = null,
    val season: String? = null,
    val allTime: Boolean? = null,
    val context: String? = null
)

@Serializable
data class AniListTag(
    val id: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val rank: Int? = null,
    val isMediaSpoiler: Boolean? = null
)

@Serializable
data class AniListTitle(
    val romaji: String? = null,
    val english: String? = null,
    val native: String? = null,
    val userPreferred: String? = null
)

@Serializable
data class AniListDate(
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null
)

@Serializable
data class AniListCoverImage(
    val extraLarge: String? = null,
    val large: String? = null,
    val medium: String? = null,
    val color: String? = null
)

@Serializable
data class AniListTrailer(
    val id: String? = null,
    val site: String? = null,
    val thumbnail: String? = null
)

@Serializable
data class AniListStudioConnection(
    val nodes: List<AniListStudio> = emptyList()
)

@Serializable
data class AniListStudio(
    val id: Int? = null,
    val name: String? = null
)

@Serializable
data class AniListCharacterConnection(
    val pageInfo: AniListPagingInfo? = null,
    val edges: List<AniListCharacterEdge> = emptyList()
)

@Serializable
data class AniListCharacterEdge(
    val role: String? = null,
    val node: AniListCharacter? = null,
    val voiceActors: List<AniListStaff> = emptyList()
)

@Serializable
data class AniListCharacter(
    val id: Int? = null,
    val name: AniListPersonName? = null,
    val image: AniListCoverImage? = null
)

@Serializable
data class AniListStaffConnection(
    val pageInfo: AniListPagingInfo? = null,
    val edges: List<AniListStaffEdge> = emptyList()
)

@Serializable
data class AniListStaffEdge(
    val role: String? = null,
    val node: AniListStaff? = null
)

@Serializable
data class AniListStaff(
    val id: Int? = null,
    val name: AniListPersonName? = null,
    val image: AniListCoverImage? = null,
    val languageV2: String? = null
)

@Serializable
data class AniListPersonName(
    val full: String? = null,
    val native: String? = null
)

@Serializable
data class AniListStreamingEpisode(
    val title: String? = null,
    val thumbnail: String? = null,
    val url: String? = null,
    val site: String? = null
)

@Serializable
data class AniListRelationConnection(
    val edges: List<AniListRelationEdge> = emptyList()
)

@Serializable
data class AniListRelationEdge(
    val relationType: String? = null,
    val node: AniListMedia? = null
)

@Serializable
data class AniListReviewConnection(
    val pageInfo: AniListPagingInfo? = null,
    val nodes: List<AniListReview> = emptyList()
)

@Serializable
data class AniListReview(
    val id: Int? = null,
    val userId: Int? = null,
    val mediaId: Int? = null,
    val mediaType: String? = null,
    val summary: String? = null,
    val body: String? = null,
    val rating: Int? = null,
    val ratingAmount: Int? = null,
    val score: Int? = null,
    val createdAt: Int? = null,
    val user: AniListUser? = null,
    val media: AniListMedia? = null
)

@Serializable
data class AniListUser(
    val id: Int? = null,
    val name: String? = null,
    val avatar: AniListCoverImage? = null
)

@Serializable
data class AniListStats(
    val statusDistribution: List<AniListStatusDistribution> = emptyList(),
    val scoreDistribution: List<AniListScoreDistribution> = emptyList()
)

@Serializable
data class AniListStatusDistribution(
    val status: String? = null,
    val amount: Int? = null
)

@Serializable
data class AniListScoreDistribution(
    val score: Int? = null,
    val amount: Int? = null
)

@Serializable
data class AniListExternalLink(
    val id: Int? = null,
    val url: String? = null,
    val site: String? = null,
    val color: String? = null
)

@Serializable
data class AniListRecommendation(
    val id: Int? = null,
    val rating: Int? = null,
    val media: AniListMedia? = null,
    val mediaRecommendation: AniListMedia? = null
)

fun AniListRecommendation.toMediaRecommendation(): MediaRecommendation {
    val entries = mutableListOf<MediaEntry>()
    media?.toMediaEntry()?.let { entries.add(it) }
    mediaRecommendation?.toMediaEntry()?.let { entries.add(it) }
    return MediaRecommendation(entry = entries)
}

fun AniListMedia.toMediaEntry(): MediaEntry {
    val imgUrl = coverImage?.extraLarge ?: coverImage?.large ?: coverImage?.medium
    val mediaImg = MediaImages(
        webp = MediaImage(
            largeImageUrl = imgUrl,
            imageUrl = imgUrl,
            smallImageUrl = coverImage?.medium,
            maximumImageUrl = bannerImage ?: imgUrl
        ),
        jpg = MediaImage(
            largeImageUrl = imgUrl,
            imageUrl = imgUrl,
            smallImageUrl = coverImage?.medium,
            maximumImageUrl = bannerImage ?: imgUrl
        )
    )

    val mappedStaff = staff?.edges.orEmpty().map { edge ->
        MediaStaffItem(
            id = edge.node?.id,
            name = edge.node?.name?.full,
            nativeName = edge.node?.name?.native,
            role = edge.role,
            imageUrl = edge.node?.image?.large ?: edge.node?.image?.medium
        )
    }

    val mappedRelations = relations?.edges.orEmpty().map { edge ->
        MediaRelationItem(
            relationType = edge.relationType,
            entry = edge.node?.toMediaEntry()
        )
    }

    val mappedStreamingEpisodes = streamingEpisodes.orEmpty().map { ep ->
        MediaStreamingEpisodeItem(
            title = ep.title,
            thumbnail = ep.thumbnail,
            url = ep.url,
            site = ep.site
        )
    }

    val mappedReviews = reviews?.nodes.orEmpty().map { rev ->
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

    val mappedStats = if (stats != null) {
        MediaStatsItem(
            statusDistribution = stats.statusDistribution.map { StatusAmount(it.status.orEmpty(), it.amount ?: 0) },
            scoreDistribution = stats.scoreDistribution.map { ScoreAmount(it.score ?: 0, it.amount ?: 0) }
        )
    } else null

    val mappedExternalLinks = externalLinks.orEmpty().map { link ->
        MediaExternalLinkItem(
            id = link.id,
            url = link.url,
            site = link.site,
            color = link.color
        )
    }

    val mappedNextAiring = nextAiringEpisode?.let {
        MediaAiringSchedule(
            episode = it.episode,
            airingAt = it.airingAt,
            timeUntilAiring = it.timeUntilAiring
        )
    }

    val mappedRankings = rankings.map { r ->
        MediaRankingItem(
            id = r.id,
            rank = r.rank,
            type = r.type,
            format = r.format,
            year = r.year,
            season = r.season,
            allTime = r.allTime,
            context = r.context
        )
    }

    val mappedTags = tags.map { t ->
        MediaTagItem(
            id = t.id,
            name = t.name,
            description = t.description,
            rank = t.rank,
            isMediaSpoiler = t.isMediaSpoiler
        )
    }

    val mappedMediaListEntry = mediaListEntry?.let {
        MediaListEntryItem(
            id = it.id,
            mediaId = it.mediaId,
            status = it.status,
            score = it.score,
            progress = it.progress,
            progressVolumes = it.progressVolumes,
            repeat = it.repeat,
            notes = it.notes
        )
    }

    return MediaEntry(
        id = id?.toString(),
        title = title?.userPreferred ?: title?.english ?: title?.romaji ?: title?.native,
        titleEnglish = title?.english,
        titleJapanese = title?.native,
        titleSynonyms = synonyms.orEmpty(),
        images = mediaImg,
        synopsis = description?.replace(Regex("<[^>]*>"), ""),
        score = (meanScore ?: averageScore)?.toDouble()?.div(10.0),
        meanScore = meanScore ?: averageScore,
        rank = popularity,
        popularity = popularity,
        members = favourites,
        status = status,
        type = format ?: type,
        episodes = episodes ?: chapters,
        duration = duration,
        chapters = chapters,
        volumes = volumes,
        season = season?.lowercase(),
        year = seasonYear ?: startDate?.year,
        source = source,
        countryOfOrigin = countryOfOrigin,
        isLicensed = isLicensed,
        hashtag = hashtag,
        mediaListEntry = mappedMediaListEntry,
        nextAiringEpisode = mappedNextAiring,
        rankings = mappedRankings,
        tags = mappedTags,
        genres = genres?.map { MediaCommonItem(id = 0, name = it, type = "genre", url = "") }.orEmpty(),
        studios = studios?.nodes?.map { MediaCommonItem(id = it.id ?: 0, name = it.name.orEmpty(), type = "studio", url = "") }.orEmpty(),
        staff = mappedStaff,
        relations = mappedRelations,
        streamingEpisodes = mappedStreamingEpisodes,
        reviews = mappedReviews,
        stats = mappedStats,
        externalLinks = mappedExternalLinks
    )
}
