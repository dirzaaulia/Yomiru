package com.dirzaaulia.yomiru.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaEpisode(
    @SerialName("media_id")
    val id: String? = null,
    val url: String? = null,
    val title: String? = null,
    @SerialName("title_japanese")
    val titleJapanese: String? = null,
    @SerialName("title_romaji")
    val titleRomaji: String? = null,
    val aired: String? = null,
    val score: Double? = null,
    val filler: Boolean? = null,
    val recap: Boolean? = null
)
