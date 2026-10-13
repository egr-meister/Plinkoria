package com.plinkoria.game.logic

import kotlin.random.Random

/** Difficulty for one Matching Pairs level. */
data class LevelConfig(
    val level: Int,
    val pairs: Int,
    val columns: Int,
    val seconds: Int
)

/** One card in the deck. [itemIndex] is 0-based into the 19 themed item icons. */
data class Card(
    val id: Int,
    val itemIndex: Int,
    val isFaceUp: Boolean = false,
    val isMatched: Boolean = false
)

/** Pure, context-free game rules — fully unit-testable. */
object GameLogic {

    const val ITEM_COUNT = 19
    const val QUIZ_QUESTIONS_PER_CATEGORY = 10

    val levels: List<LevelConfig> = listOf(
        LevelConfig(1, 2, 2, 60),
        LevelConfig(2, 3, 2, 70),
        LevelConfig(3, 4, 2, 70),
        LevelConfig(4, 6, 3, 80),
        LevelConfig(5, 6, 3, 80),
        LevelConfig(6, 8, 4, 100),
        LevelConfig(7, 8, 4, 100),
        LevelConfig(8, 10, 4, 110),
        LevelConfig(9, 12, 4, 120)
    )

    val levelCount: Int get() = levels.size

    fun levelConfig(level: Int): LevelConfig? = levels.firstOrNull { it.level == level }

    /**
     * Builds a shuffled deck: [pairs] distinct icons, each exactly twice.
     * [pairs] is clamped to 1..[ITEM_COUNT].
     */
    fun buildDeck(pairs: Int, random: Random = Random.Default): List<Card> {
        val count = pairs.coerceIn(1, ITEM_COUNT)
        val items = (0 until ITEM_COUNT).shuffled(random).take(count)
        return (items + items)
            .shuffled(random)
            .mapIndexed { index, item -> Card(id = index, itemIndex = item) }
    }

    /** A level is cleared when its best matched pairs equals the level's total pairs. */
    fun isLevelCompleted(level: Int, pairsBest: Map<String, Int>): Boolean {
        val config = levelConfig(level) ?: return false
        return (pairsBest[level.toString()] ?: 0) >= config.pairs
    }

    /** Level 1 is always unlocked; level N unlocks once level N-1 is cleared. */
    fun isLevelUnlocked(level: Int, pairsBest: Map<String, Int>): Boolean {
        if (levelConfig(level) == null) return false
        if (level == 1) return true
        return isLevelCompleted(level - 1, pairsBest)
    }

    fun completedLevelCount(pairsBest: Map<String, Int>): Int =
        levels.count { isLevelCompleted(it.level, pairsBest) }

    fun nextLevel(level: Int): Int? = levelConfig(level + 1)?.level

    /** True when every card in the deck is matched (and the deck is not empty). */
    fun isDeckCleared(cards: List<Card>): Boolean = cards.isNotEmpty() && cards.all { it.isMatched }

    fun matchedPairCount(cards: List<Card>): Int = cards.count { it.isMatched } / 2

    /** Stars for a quiz round: 3 = perfect, 2 = ≥70%, 1 = ≥40%, else 0. */
    fun quizStars(score: Int, total: Int): Int {
        if (total <= 0 || score <= 0) return 0
        val ratio = score.toFloat() / total
        return when {
            score >= total -> 3
            ratio >= 0.7f -> 2
            ratio >= 0.4f -> 1
            else -> 0
        }
    }
}
