package com.dirzaaulia.yomiru.model

import kotlinx.serialization.Serializable

@Serializable
data class MediaUser(
    val username: String? = null,
    val images: MediaImages? = null
)
