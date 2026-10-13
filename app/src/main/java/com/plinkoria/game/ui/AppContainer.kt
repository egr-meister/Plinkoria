package com.plinkoria.game.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.plinkoria.game.audio.SoundManager
import com.plinkoria.game.data.GameRepository

/** What screens need from the Application, handed down without a DI framework. */
class AppContainer(
    val repository: GameRepository,
    val sound: SoundManager
)

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer not provided")
}
