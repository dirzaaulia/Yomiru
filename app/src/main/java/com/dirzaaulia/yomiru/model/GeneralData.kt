package com.dirzaaulia.yomiru.model

import kotlinx.serialization.Serializable

@Serializable
data class GeneralData<T>(
    val data: T,
)