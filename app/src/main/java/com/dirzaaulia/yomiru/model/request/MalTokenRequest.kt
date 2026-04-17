package com.dirzaaulia.yomiru.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MalTokenRequest(
    @SerialName("client_id")
    val clientId: String,
    @SerialName("grant_type")
    val grantType: String,
    @SerialName("code")
    val code: String,
    @SerialName("code_verifier")
    val codeVerifier: String
)