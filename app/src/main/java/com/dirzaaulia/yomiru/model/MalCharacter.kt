package com.dirzaaulia.yomiru.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MalCharacterEntry(
    val character: MalEntry? = null,
    val role: String? = null,
    val favorites: Int? = null,
    @SerialName("voice_actors")
    val voiceActors: List<MalEntry>? = null
)