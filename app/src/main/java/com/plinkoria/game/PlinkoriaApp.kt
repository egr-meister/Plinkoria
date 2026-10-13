package com.plinkoria.game

import android.app.Application
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.plinkoria.game.audio.SoundManager
import com.plinkoria.game.data.GameRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/** Manual "container": one repository + one sound manager for the whole process. No DI framework. */
class PlinkoriaApp : Application() {

    val appScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    lateinit var repository: GameRepository
        private set
    lateinit var soundManager: SoundManager
        private set

    override fun onCreate() {
        super.onCreate()
        val dataStore = PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            produceFile = { preferencesDataStoreFile(GameRepository.STORE_NAME) }
        )
        repository = GameRepository(dataStore)
        soundManager = SoundManager(this)

        // Keep SoundManager in sync with the persisted toggle — at startup and live.
        appScope.launch {
            repository.data
                .map { it.settings.soundEnabled }
                .distinctUntilChanged()
                .collect { soundManager.enabled = it }
        }
    }
}
