package com.dirzaaulia.yomiru.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MalGenre(
    @SerialName("mal_id")
    val id: Int,
    val name: String,
    val url: String,
    val count: Int
)