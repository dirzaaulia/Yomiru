package com.dirzaaulia.yomiru

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dirzaaulia.yomiru.navigation.YomiruNavigation
import com.dirzaaulia.yomiru.ui.theme.YomiruTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            YomiruTheme {
                YomiruNavigation()
            }
        }
    }
}