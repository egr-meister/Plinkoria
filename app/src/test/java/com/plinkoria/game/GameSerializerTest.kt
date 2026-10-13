package com.plinkoria.game

import com.plinkoria.game.data.GameData
import com.plinkoria.game.data.GameSerializer
import com.plinkoria.game.data.Settings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameSerializerTest {

    private val defaults = GameData()

    @Test
    fun nullEmptyAndBlankFallBackToDefaults() {
        assertEquals(defaults, GameSerializer.decode(null))
        assertEquals(defaults, GameSerializer.decode(""))
        assertEquals(defaults, GameSerializer.decode("   \n"))
    }

    @Test
    fun corruptedJsonFallsBackToDefaults() {
        listOf(
            "{", "}", "not json", "{\"quizBest\":", "[1,2,3]", "null", "42", "\"text\"",
            "{\"quizBest\":\"oops\"}", "{\"pairsBest\":[1,2]}", "{\"settings\":5}"
        ).forEach { raw ->
            assertEquals("input: $raw", defaults, GameSerializer.decode(raw))
        }
    }

    @Test
    fun emptyObjectGivesDefaults() {
        val d = GameSerializer.decode("{}")
        assertEquals(defaults, d)
        assertTrue(d.settings.soundEnabled)
    }

    @Test
    fun unknownKeysAreIgnored() {
        val raw = """{"quizBest":{"easy":7},"futureField":{"a":1},"settings":{"soundEnabled":false,"vibration":true}}"""
        val d = GameSerializer.decode(raw)
        assertEquals(7, d.quizBest["easy"])
        assertFalse(d.settings.soundEnabled)
    }

    @Test
    fun missingFieldsUseDefaults() {
        val d = GameSerializer.decode("""{"pairsBest":{"1":2}}""")
        assertEquals(emptyMap<String, Int>(), d.quizBest)
        assertEquals(2, d.pairsBest["1"])
        assertEquals(Settings(), d.settings)
    }

    @Test
    fun nullFieldValuesUseDefaults() {
        val d = GameSerializer.decode("""{"quizBest":null,"settings":{"soundEnabled":null}}""")
        assertEquals(emptyMap<String, Int>(), d.quizBest)
        assertTrue(d.settings.soundEnabled)
    }

    @Test
    fun impossibleValuesAreSanitized() {
        val d = GameSerializer.decode("""{"quizBest":{"easy":99,"medium":-4},"pairsBest":{"1":-1,"abc":3,"0":2,"2":3}}""")
        assertEquals(10, d.quizBest["easy"])
        assertEquals(0, d.quizBest["medium"])
        assertEquals(0, d.pairsBest["1"])
        assertFalse(d.pairsBest.containsKey("abc"))
        assertFalse(d.pairsBest.containsKey("0"))
        assertEquals(3, d.pairsBest["2"])
    }

    @Test
    fun roundTripPreservesData() {
        val data = GameData(
            quizBest = mapOf("easy" to 8, "hard" to 10),
            pairsBest = mapOf("1" to 2, "2" to 3),
            settings = Settings(soundEnabled = false)
        )
        assertEquals(data, GameSerializer.decode(GameSerializer.encode(data)))
    }
}
