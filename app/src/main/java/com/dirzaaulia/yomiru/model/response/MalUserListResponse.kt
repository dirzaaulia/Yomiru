package com.dirzaaulia.yomiru.model.response

import com.dirzaaulia.yomiru.model.MalNode
import kotlinx.serialization.Serializable

@Serializable
data class MalUserListResponse(
    val data: List<MalNode>
)