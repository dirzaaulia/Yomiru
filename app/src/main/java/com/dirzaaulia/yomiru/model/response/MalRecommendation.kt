package com.dirzaaulia.yomiru.model.response

import com.dirzaaulia.yomiru.model.MalEntry
import com.dirzaaulia.yomiru.model.MalUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MalRecommendation(
    @SerialName("mal_id")
    val id: String? = null,
    val entry: List<MalEntry>? = null,
    val content: String? = null,
    val date: String? = null,
    val user: MalUser? = null
)

@Serializable
data class MalDetailRecommendation(
    val entry: MalEntry? = null,
    val url: String? = null,
    val votes: Int? = null
)