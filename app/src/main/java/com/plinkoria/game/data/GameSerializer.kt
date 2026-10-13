package com.plinkoria.game.data

import kotlinx.serialization.json.Json

/**
 * Context-free JSON encode/decode for [GameData]. Never throws on read:
 * null, blank, corrupted, wrong-typed or partial JSON all fall back to safe defaults.
 */
object GameSerializer {

    const val MAX_QUIZ_SCORE = 10
    const val MAX_PAIRS = 64

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
        encodeDefaults = true
        isLenient = false
    }

    fun decode(raw: String?): GameData {
        if (raw.isNullOrBlank()) return GameData()
        return try {
            sanitize(json.decodeFromString(GameData.serializer(), raw))
        } catch (e: Exception) {
            GameData()
        }
    }

    fun encode(data: GameData): String = json.encodeToString(GameData.serializer(), sanitize(data))

    /** Drops impossible values so a hand-edited or damaged store can't break the UI. */
    fun sanitize(data: GameData): GameData = data.copy(
        quizBest = data.quizBest
            .filterKeys { it.isNotBlank() }
            .mapValues { (_, v) -> v.coerceIn(0, MAX_QUIZ_SCORE) },
        pairsBest = data.pairsBest
            .filterKeys { key -> key.toIntOrNull()?.let { it > 0 } == true }
            .mapValues { (_, v) -> v.coerceIn(0, MAX_PAIRS) }
    )
}
