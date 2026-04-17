package com.dirzaaulia.yomiru.model

import kotlinx.serialization.Serializable

@Serializable
data class MalUser(
    val url: String? = null,
    val username: String? = null,
    val images: MalImages? = null
)