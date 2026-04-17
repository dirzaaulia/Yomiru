package com.dirzaaulia.yomiru

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirzaaulia.yomiru.model.response.MalTokenResponse
import com.dirzaaulia.yomiru.repository.NetworkRepository
import com.dirzaaulia.yomiru.util.ResponseResult
import com.dirzaaulia.yomiru.util.success
import id.pgidata.gomamam.repository.DataStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class MALAuth(
    val codeVerifier: String,
    val codeChallenge: String,
    val state: String
)

class AuthCallbackViewModel(
    private val repository: NetworkRepository,
    private val dataStore: DataStoreRepository
) : ViewModel() {

    private val _malTokenResponse: MutableStateFlow<ResponseResult<MalTokenResponse?>> =
        MutableStateFlow(ResponseResult.Success(null))
    val malTokenResponse = _malTokenResponse.asStateFlow()

    var malAuth: MALAuth? = null

    fun doMalAuth(code: String) {
        repository.getToken(
            clientId = BuildConfig.MAL_CLIENT_ID,
            grantType = "authorization_code",
            code = code,
            codeVerifier = malAuth?.codeVerifier.toString(),
            redirectUri = "com.dirzaaulia.yomiru://oauth-callback"
        ).onEach {
            when (it) {
                is ResponseResult.Success<*> -> {
                    it.success { data ->
                        dataStore.setAccessToken(data.accessToken)
                        dataStore.setRefreshTokenToken(data.refreshToken)
                        dataStore.setExpiresIn(data.expiresIn)
                    }
                }
                else -> Unit
            }
            _malTokenResponse.value = it
        }.launchIn(viewModelScope)
    }

    fun setErrorMalTokenResponse(errorMessage: String) {
        _malTokenResponse.value = ResponseResult.Error(Throwable(errorMessage))
    }
}