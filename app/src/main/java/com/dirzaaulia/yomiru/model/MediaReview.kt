package com.dirzaaulia.yomiru.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaReview(
    @SerialName("review_id")
    val id: Int? = null,
    val url: String? = null,
    val type: String? = null,
    val reactions: MediaReaction? = null,
    val date: String? = null,
    val review: String? = null,
    val score: Int? = null,
    val tags: List<String>? = null,
    val entry: MediaEntry? = null,
    val user: MediaUser? = null
)

@Serializable
data class MediaReaction(
    @SerialName("overall")
    val overall: Int? = null,
    @SerialName("nice")
    val nice: Int? = null,
    @SerialName("love_it")
    val loveIt: Int? = null
)
