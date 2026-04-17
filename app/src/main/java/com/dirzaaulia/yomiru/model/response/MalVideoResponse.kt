package com.dirzaaulia.yomiru.model.response

import com.dirzaaulia.yomiru.model.MalEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MalVideoResponse(
    val promo: List<MalEntry>? = null,
    @SerialName("music_videos")
    val musicVideos: List<MalEntry>? = null
)