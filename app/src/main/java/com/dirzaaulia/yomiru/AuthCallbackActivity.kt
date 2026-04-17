package com.dirzaaulia.yomiru

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dirzaaulia.yomiru.navigation.YomiruNavigation
import com.dirzaaulia.yomiru.ui.theme.YomiruTheme
import com.dirzaaulia.yomiru.util.ResponseResult
import com.dirzaaulia.yomiru.util.success
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthCallbackActivity : ComponentActivity() {

    private val viewModel: AuthCallbackViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        subscribeMalTokenResponse()
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val receivedUri: Uri? = intent.data
        Log.d("AuthCallbackActivity", "Handling intent. Received redirect URI: $receivedUri")

        if (receivedUri != null &&
            receivedUri.scheme == "com.dirzaaulia.yomiru" && // Check your scheme
            receivedUri.host == "oauth-callback"
        ) { // Check your host

            val authCode = receivedUri.getQueryParameter("code")
            val error = receivedUri.getQueryParameter("error")
            val state = receivedUri.getQueryParameter("state")

            // TODO: Verify the 'state' parameter

            Log.d("AuthCallbackActivity", "Received URI: $receivedUri")
            Log.d("AuthCallbackActivity", "Auth Code: $authCode, Error: $error, State: $state")
            Log.d("AuthCallbackActivity", "ViewModel instance: ${viewModel.hashCode()}")


            if (authCode != null) {
                Log.i("AuthCallbackActivity", "Authorization code: $authCode, State: $state")
                viewModel.doMalAuth(authCode)
            } else if (error != null) {
                Log.e("AuthCallbackActivity", "Auth error: $error, State: $state")
                viewModel.setErrorMalTokenResponse(error)
            } else {
                Log.w("AuthCallbackActivity", "No code or error in redirect URI.")
                viewModel.setErrorMalTokenResponse("MyAnimeList is busy right now! Try again later.")
            }
        } else {
            Log.w("AuthCallbackActivity", "Unexpected or null redirect URI: $receivedUri")
        }
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    private fun subscribeMalTokenResponse() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.malTokenResponse.collect { response ->
                    setContent {
                        YomiruTheme {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                when (response) {
                                    ResponseResult.Loading -> {
                                        LoadingIndicator(modifier = Modifier.fillMaxSize())
                                    }

                                    else -> {
                                        val mainActivityIntent =
                                            Intent(
                                                this@AuthCallbackActivity,
                                                MainActivity::class.java
                                            ).apply {
                                                flags =
                                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                            }
                                        startActivity(mainActivityIntent)
                                        finish()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}