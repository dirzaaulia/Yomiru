package com.dirzaaulia.yomiru.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MalReview(
    @SerialName("mal_id")
    val malId: Int? = null,
    val url: String? = null,
    val type: String? = null,
    val reactions: MalReaction? = null,
    val date: String? = null,
    val review: String? = null,
    val score: Int? = null,
    val tags: List<String>? = null,
    @SerialName("is_spoiler")
    val isSpoiler: Boolean? = null,
    @SerialName("is_preliminary")
    val isPreliminary: Boolean? = null,
    @SerialName("episodes_watched")
    val episodesWatched: Int? = null,
    val entry: MalEntry? = null,
    val user: MalUser? = null
)

@Serializable
data class MalReaction(
    val overall: Int? = null,
    val nice: Int? = null,
    @SerialName("love_it")
    val loveIt: Int? = null,
    val funny: Int? = null,
    val confusing: Int? = null,
    val informative: Int? = null,
    @SerialName("well_written")
    val wellWritten: Int? = null,
    val creative: Int? = null
)