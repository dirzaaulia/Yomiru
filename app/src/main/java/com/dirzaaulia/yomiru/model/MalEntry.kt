package com.dirzaaulia.yomiru.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MalEntry(
    @SerialName("mal_id")
    val id: String? = null,
    val url: String? = null,
    val images: MalImages? = null,
    val name: String? = null,
    val trailer: MalTrailer? = null,
    val video: MalTrailer? = null,
    val approved: Boolean? = null,
    val titles: List<MalTitle>? = emptyList(),
    val title: String? = null,
    @SerialName("title_english")
    val titleEnglish: String? = null,
    @SerialName("title_japanese")
    val titleJapanese: String? = null,
    @SerialName("title_synonims")
    val titleSynonyms: List<String> = emptyList(),
    val type: String? = null,
    val source: String? = null,
    val episodes: Int? = null,
    val status: String? = null,
    val airing: Boolean? = null,
    @SerialName("aired")
    val aired: MalAired? = null,
    val duration: String? = null,
    val rating: String? = null,
    val score: Double? = null,
    @SerialName("scored_by")
    val scoredBy: Int? = null,
    val rank: Int? = null,
    val popularity: Int? = null,
    val members: Int? = null,
    val favorites: Int? = null,
    val synopsis: String? = null,
    val background: String? = null,
    val season: String? = null,
    val year: Int? = null,
    val broadcast: MalBroadcast? = null,
    val producers: List<MalCommonItem>? = emptyList(),
    val licensors: List<MalCommonItem>? = emptyList(),
    val studios: List<MalCommonItem> ?= emptyList(),
    val genres: List<MalCommonItem>? = emptyList(),
    @SerialName("explicit_genres")
    val explicitGenres: List<MalCommonItem>? = emptyList(),
    val themes: List<MalCommonItem>? = emptyList(),
    val demographics: List<MalCommonItem>? = emptyList(),
    val person: MalEntry? = null,
    val language: String? = null,
    val meta: MalMeta? = null,
)

@Serializable
data class MalImages(
    val jpg: MalImage? = null,
    val webp: MalImage? = null
)

@Serializable
data class MalImage(
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
data class MalTrailer(
    @SerialName("youtube_id")
    val youtubeId: String? = null,
    val url: String? = null,
    @SerialName("embed_url")
    val embedUrl: String? = null,
    val images: MalImage? = null
)

@Serializable
data class MalTitle(
    val type: String,
    val title: String
)

@Serializable
data class MalAired(
    val from: String? = null,
    val to: String? = null,
    val prop: MalProp,
    val string: String
)

@Serializable
data class MalProp(
    val from: MalPropItem,
    val to: MalPropItem?
)

@Serializable
data class MalPropItem(
    val day: Int? = null,
    val month: Int? = null,
    val year: Int? = null
)

@Serializable
data class MalBroadcast(
    val day: String? = null,
    val time: String? = null,
    val timezone: String? = null,
    val string: String? = null
)

@Serializable
data class MalCommonItem(
    @SerialName("mal_id")
    val id: Int,
    val type: String,
    val name: String,
    val url: String
)

@Serializable
data class MalMeta(
    val title: String? = null,
    val author: String? = null
)