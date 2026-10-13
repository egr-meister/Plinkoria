package com.plinkoria.game.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** Single local source of truth. Created once in [com.plinkoria.game.PlinkoriaApp]. */
class GameRepository(private val dataStore: DataStore<Preferences>) {

    private val key = stringPreferencesKey(KEY_GAME_DATA)

    val data: Flow<GameData> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> GameSerializer.decode(prefs[key]) }
        .distinctUntilChanged()

    suspend fun saveQuizBest(categoryId: String, score: Int) = update { current ->
        val old = current.quizBest[categoryId] ?: 0
        if (score > old) current.copy(quizBest = current.quizBest + (categoryId to score)) else current
    }

    suspend fun savePairsBest(level: Int, matchedPairs: Int) = update { current ->
        val k = level.toString()
        val old = current.pairsBest[k] ?: 0
        if (matchedPairs > old) current.copy(pairsBest = current.pairsBest + (k to matchedPairs)) else current
    }

    suspend fun setSoundEnabled(enabled: Boolean) = update { current ->
        current.copy(settings = current.settings.copy(soundEnabled = enabled))
    }

    suspend fun toggleSound() = update { current ->
        current.copy(settings = current.settings.copy(soundEnabled = !current.settings.soundEnabled))
    }

    suspend fun resetAll() = update { GameData() }

    /** Read-modify-write inside one DataStore transaction. Write failures are swallowed (best effort). */
    private suspend fun update(transform: (GameData) -> GameData) {
        try {
            dataStore.edit { prefs ->
                val current = GameSerializer.decode(prefs[key])
                prefs[key] = GameSerializer.encode(transform(current))
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
        }
    }

    companion object {
        const val KEY_GAME_DATA = "game_data_json"
        const val STORE_NAME = "plinkoria_store"
    }
}
