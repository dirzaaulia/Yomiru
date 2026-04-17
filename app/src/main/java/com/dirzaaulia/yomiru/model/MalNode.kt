package com.dirzaaulia.yomiru.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MalNode(
    val node: MalNodeEntry? = null
)

@Serializable
data class MalNodeEntry(
    val id: Int? = null,
    val title: String? = null,
    @SerialName("main_picture")
    val mainPicture: MalNodePicture? = null
)

@Serializable
data class MalNodePicture(
    val medium: String? = null,
    val large: String? = null
)