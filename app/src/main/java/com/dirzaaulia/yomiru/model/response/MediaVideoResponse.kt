package com.dirzaaulia.yomiru.model.response

import kotlinx.serialization.Serializable

@Serializable
data class MediaVideoResponse(
    val url: String? = null,
    val title: String? = null
)
