package com.dirzaaulia.yomiru.screen.onboarding

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirzaaulia.yomiru.BuildConfig
import id.pgidata.gomamam.repository.DataStoreRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val dataStore: DataStoreRepository
) : ViewModel() {

    val isOnboardingCompleted: StateFlow<Boolean> = dataStore.isOnboardingCompletedFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val accessToken: StateFlow<String> = dataStore.accessTokenFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun completeOnboarding() {
        viewModelScope.launch {
            dataStore.setOnboardingCompleted(true)
        }
    }

    fun buildAniListAuthUrl(): Uri {
        return "https://anilist.co/api/v2/oauth/authorize".toUri().buildUpon()
            .appendQueryParameter("client_id", BuildConfig.ANILIST_CLIENT_ID.replace("\"", ""))
            .appendQueryParameter("response_type", "token")
            .build()
    }
}
