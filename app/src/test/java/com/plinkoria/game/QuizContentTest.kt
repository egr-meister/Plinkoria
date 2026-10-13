package com.plinkoria.game

import com.plinkoria.game.content.QuizContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizContentTest {

    @Test
    fun threeCategoriesOfTenValidQuestions() {
        assertEquals(3, QuizContent.categories.size)
        assertEquals(3, QuizContent.categories.map { it.id }.toSet().size)
        QuizContent.categories.forEach { c ->
            assertEquals(c.id, 10, c.questions.size)
            c.questions.forEach { q ->
                assertEquals(q.text, QuizContent.OPTIONS_PER_QUESTION, q.options.size)
                assertTrue(q.text, q.correctIndex in q.options.indices)
                assertEquals(q.text, q.options.size, q.options.toSet().size)
            }
        }
    }

    @Test
    fun difficultiesAreEasyMediumHardInOrder() {
        assertEquals(listOf(1, 2, 3), QuizContent.categories.map { it.difficulty })
        assertEquals(listOf("easy", "medium", "hard"), QuizContent.categories.map { it.id })
    }

    @Test
    fun correctAnswerPositionsAreVaried() {
        val positions = QuizContent.categories.flatMap { c -> c.questions.map { it.correctIndex } }.toSet()
        assertEquals(setOf(0, 1, 2, 3), positions)
    }

    @Test
    fun unknownCategoryIsNull() {
        assertNull(QuizContent.category(null))
        assertNull(QuizContent.category(""))
        assertNull(QuizContent.category("nope"))
    }
}
