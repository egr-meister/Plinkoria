package com.plinkoria.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.plinkoria.game.ui.AppContainer
import com.plinkoria.game.ui.LocalAppContainer
import com.plinkoria.game.ui.navigation.AppNavHost
import com.plinkoria.game.ui.theme.PlinkoriaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        val app = application as PlinkoriaApp
        setContent {
            val container = remember { AppContainer(app.repository, app.soundManager) }
            CompositionLocalProvider(LocalAppContainer provides container) {
                PlinkoriaTheme {
                    AppNavHost()
                }
            }
        }
    }
}
