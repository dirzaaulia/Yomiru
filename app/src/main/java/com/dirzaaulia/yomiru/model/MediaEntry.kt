package com.dirzaaulia.yomiru.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaEntry(
    val id: String? = null,
    val title: String? = null,
    @SerialName("title_english")
    val titleEnglish: String? = null,
    @SerialName("title_japanese")
    val titleJapanese: String? = null,
    @SerialName("title_synonyms")
    val titleSynonyms: List<String> = emptyList(),
    val images: MediaImages? = null,
    val synopsis: String? = null,
    val score: Double? = null,
    val meanScore: Int? = null,
    val rank: Int? = null,
    val popularity: Int? = null,
    val members: Int? = null,
    @SerialName("scored_by")
    val scoredBy: Int? = null,
    val status: String? = null,
    val type: String? = null,
    val episodes: Int? = null,
    val duration: Int? = null,
    val chapters: Int? = null,
    val volumes: Int? = null,
    val rating: String? = null,
    val season: String? = null,
    val year: Int? = null,
    val source: String? = null,
    val background: String? = null,
    val countryOfOrigin: String? = null,
    val isLicensed: Boolean? = null,
    val hashtag: String? = null,
    val language: String? = null,
    val mediaListEntry: MediaListEntryItem? = null,
    val nextAiringEpisode: MediaAiringSchedule? = null,
    val rankings: List<MediaRankingItem> = emptyList(),
    val tags: List<MediaTagItem> = emptyList(),
    val genres: List<MediaCommonItem> = emptyList(),
    val studios: List<MediaCommonItem> = emptyList(),
    val staff: List<MediaStaffItem> = emptyList(),
    val relations: List<MediaRelationItem> = emptyList(),
    val streamingEpisodes: List<MediaStreamingEpisodeItem> = emptyList(),
    val reviews: List<MediaReviewItem> = emptyList(),
    val stats: MediaStatsItem? = null,
    val externalLinks: List<MediaExternalLinkItem> = emptyList()
)

@Serializable
data class MediaListEntryItem(
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
data class MediaListGroupItem(
    val name: String,
    val status: String,
    val entries: List<MediaEntry> = emptyList()
)

@Serializable
data class MediaImages(
    val webp: MediaImage? = null,
    val jpg: MediaImage? = null
)

@Serializable
data class MediaImage(
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("small_image_url")
    val smallImageUrl: String? = null,
    @SerialName("large_image_url")
    val largeImageUrl: String? = null,
    @SerialName("maximum_image_url")
    val maximumImageUrl: String? = null
)

@Serializable
data class MediaCommonItem(
    val id: Int,
    val name: String,
    val type: String? = null,
    val url: String? = null
)

@Serializable
data class MediaStaffItem(
    val id: Int? = null,
    val name: String? = null,
    val nativeName: String? = null,
    val role: String? = null,
    val imageUrl: String? = null
)

@Serializable
data class MediaRelationItem(
    val relationType: String? = null,
    val entry: MediaEntry? = null
)

@Serializable
data class MediaStreamingEpisodeItem(
    val title: String? = null,
    val thumbnail: String? = null,
    val url: String? = null,
    val site: String? = null
)

@Serializable
data class MediaReviewItem(
    val id: Int? = null,
    val summary: String? = null,
    val body: String? = null,
    val rating: Int? = null,
    val score: Int? = null,
    val userName: String? = null,
    val userAvatarUrl: String? = null
)

@Serializable
data class MediaStatsItem(
    val statusDistribution: List<StatusAmount> = emptyList(),
    val scoreDistribution: List<ScoreAmount> = emptyList()
)

@Serializable
data class StatusAmount(
    val status: String,
    val amount: Int
)

@Serializable
data class ScoreAmount(
    val score: Int,
    val amount: Int
)

@Serializable
data class MediaExternalLinkItem(
    val id: Int? = null,
    val url: String? = null,
    val site: String? = null,
    val color: String? = null
)

@Serializable
data class MediaAiringSchedule(
    val episode: Int? = null,
    val airingAt: Long? = null,
    val timeUntilAiring: Long? = null
)

@Serializable
data class MediaRankingItem(
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
data class MediaTagItem(
    val id: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val rank: Int? = null,
    val isMediaSpoiler: Boolean? = null
)
