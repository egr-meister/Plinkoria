package com.plinkoria.game

import com.plinkoria.game.logic.Card
import com.plinkoria.game.logic.GameLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameLogicTest {

    @Test
    fun levelTableMatchesSpec() {
        val expected = listOf(
            Triple(2, 2, 60), Triple(3, 2, 70), Triple(4, 2, 70),
            Triple(6, 3, 80), Triple(6, 3, 80), Triple(8, 4, 100),
            Triple(8, 4, 100), Triple(10, 4, 110), Triple(12, 4, 120)
        )
        assertEquals(9, GameLogic.levels.size)
        GameLogic.levels.forEachIndexed { i, cfg ->
            assertEquals(i + 1, cfg.level)
            assertEquals(expected[i], Triple(cfg.pairs, cfg.columns, cfg.seconds))
        }
    }

    @Test
    fun deckHasEachItemExactlyTwice() {
        GameLogic.levels.forEach { cfg ->
            repeat(20) { seed ->
                val deck = GameLogic.buildDeck(cfg.pairs, Random(seed))
                assertEquals(cfg.pairs * 2, deck.size)
                val counts = deck.groupingBy { it.itemIndex }.eachCount()
                assertEquals(cfg.pairs, counts.size)
                assertTrue(counts.values.all { it == 2 })
                assertTrue(deck.all { it.itemIndex in 0 until GameLogic.ITEM_COUNT })
                assertEquals(deck.size, deck.map { it.id }.toSet().size)
                assertTrue(deck.none { it.isFaceUp || it.isMatched })
            }
        }
    }

    @Test
    fun deckClampsInvalidPairCounts() {
        assertEquals(2, GameLogic.buildDeck(0).size)
        assertEquals(2, GameLogic.buildDeck(-5).size)
        assertEquals(GameLogic.ITEM_COUNT * 2, GameLogic.buildDeck(99).size)
    }

    @Test
    fun deckIsShuffled() {
        val a = GameLogic.buildDeck(12, Random(1)).map { it.itemIndex }
        val b = GameLogic.buildDeck(12, Random(2)).map { it.itemIndex }
        assertTrue(a != b)
    }

    @Test
    fun levelOneAlwaysUnlocked() {
        assertTrue(GameLogic.isLevelUnlocked(1, emptyMap()))
        assertFalse(GameLogic.isLevelUnlocked(2, emptyMap()))
    }

    @Test
    fun levelUnlocksOnlyAfterPreviousCleared() {
        // Level 1 has 2 pairs: partial progress does not unlock level 2.
        assertFalse(GameLogic.isLevelUnlocked(2, mapOf("1" to 1)))
        assertTrue(GameLogic.isLevelUnlocked(2, mapOf("1" to 2)))
        assertFalse(GameLogic.isLevelUnlocked(3, mapOf("1" to 2)))
        assertTrue(GameLogic.isLevelUnlocked(9, mapOf("8" to 10)))
    }

    @Test
    fun invalidLevelsAreNeverUnlockedOrCompleted() {
        val all = GameLogic.levels.associate { it.level.toString() to it.pairs }
        listOf(0, -1, 10, 100).forEach {
            assertFalse(GameLogic.isLevelUnlocked(it, all))
            assertFalse(GameLogic.isLevelCompleted(it, all))
            assertNull(GameLogic.levelConfig(it))
        }
        assertNotNull(GameLogic.levelConfig(5))
    }

    @Test
    fun completedCountCountsOnlyFullClears() {
        assertEquals(0, GameLogic.completedLevelCount(emptyMap()))
        assertEquals(1, GameLogic.completedLevelCount(mapOf("1" to 2, "2" to 2)))
        assertEquals(2, GameLogic.completedLevelCount(mapOf("1" to 2, "2" to 3, "3" to 3)))
        val all = GameLogic.levels.associate { it.level.toString() to it.pairs }
        assertEquals(9, GameLogic.completedLevelCount(all))
        // Junk keys are ignored.
        assertEquals(0, GameLogic.completedLevelCount(mapOf("x" to 99, "42" to 99)))
    }

    @Test
    fun nextLevelStopsAtNine() {
        assertEquals(2, GameLogic.nextLevel(1))
        assertNull(GameLogic.nextLevel(9))
    }

    @Test
    fun deckClearedAndMatchedPairs() {
        val deck = GameLogic.buildDeck(3, Random(7))
        assertFalse(GameLogic.isDeckCleared(deck))
        assertEquals(0, GameLogic.matchedPairCount(deck))
        val oneItem = deck.first().itemIndex
        val partial = deck.map { if (it.itemIndex == oneItem) it.copy(isMatched = true) else it }
        assertEquals(1, GameLogic.matchedPairCount(partial))
        val done = deck.map { it.copy(isMatched = true) }
        assertTrue(GameLogic.isDeckCleared(done))
        assertFalse(GameLogic.isDeckCleared(emptyList<Card>()))
    }

    @Test
    fun quizStarsThresholds() {
        assertEquals(0, GameLogic.quizStars(0, 10))
        assertEquals(0, GameLogic.quizStars(3, 10))
        assertEquals(1, GameLogic.quizStars(4, 10))
        assertEquals(1, GameLogic.quizStars(6, 10))
        assertEquals(2, GameLogic.quizStars(7, 10))
        assertEquals(2, GameLogic.quizStars(9, 10))
        assertEquals(3, GameLogic.quizStars(10, 10))
        assertEquals(0, GameLogic.quizStars(5, 0))
    }
}
