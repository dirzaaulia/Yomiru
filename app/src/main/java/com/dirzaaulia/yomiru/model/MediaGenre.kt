package com.dirzaaulia.yomiru.model

import kotlinx.serialization.Serializable

@Serializable
data class MediaGenre(
    val id: Int,
    val name: String,
    val count: Int = 0,
    val url: String = ""
)
