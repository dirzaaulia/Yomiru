package com.dirzaaulia.yomiru

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import id.pgidata.gomamam.repository.DataStoreRepository
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AuthCallbackActivity : ComponentActivity() {

    private val dataStore: DataStoreRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val uri: Uri? = intent.data
        if (uri != null) {
            val fragment = uri.fragment.orEmpty()
            val accessToken = extractQueryOrFragmentParam(fragment, "access_token")
                ?: uri.getQueryParameter("access_token")
                ?: uri.getQueryParameter("code").orEmpty()

            if (accessToken.isNotBlank()) {
                // Ensure DataStore save completes before finishing the Activity
                lifecycleScope.launch {
                    dataStore.setAccessToken(accessToken)
                    dataStore.setOnboardingCompleted(true)
                    
                    navigateToMain()
                }
                return
            }
        }
        
        navigateToMain()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

    private fun extractQueryOrFragmentParam(source: String, key: String): String? {
        if (source.isBlank()) return null
        val params = source.split("&")
        for (param in params) {
            val keyValue = param.split("=")
            if (keyValue.size == 2 && keyValue[0] == key) {
                return keyValue[1]
            }
        }
        return null
    }
}
