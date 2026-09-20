package com.dirzaaulia.yomiru.model.response

import com.dirzaaulia.yomiru.model.MediaEntry
import kotlinx.serialization.Serializable

@Serializable
data class MediaRecommendation(
    val entry: List<MediaEntry> = emptyList()
)

@Serializable
data class MediaDetailRecommendation(
    val entry: MediaEntry? = null,
    val votes: Int? = null
)
