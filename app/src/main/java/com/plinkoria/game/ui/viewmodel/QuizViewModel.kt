package com.plinkoria.game.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.plinkoria.game.audio.Sfx
import com.plinkoria.game.audio.SoundManager
import com.plinkoria.game.content.QuizCategory
import com.plinkoria.game.content.QuizContent
import com.plinkoria.game.content.QuizQuestion
import com.plinkoria.game.data.GameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizUiState(
    val valid: Boolean = true,
    val categoryId: String = "",
    val categoryTitle: String = "",
    val categorySubtitle: String = "",
    val difficulty: Int = 1,
    val index: Int = 0,
    val total: Int = 0,
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctIndex: Int = -1,
    val selectedIndex: Int? = null,
    val score: Int = 0,
    val isPaused: Boolean = false,
    /** Non-null once the round ends; the screen navigates to results exactly once. */
    val finalScore: Int? = null
) {
    val answered: Boolean get() = selectedIndex != null
    val progress: Float get() = if (total == 0) 0f else (index + if (answered) 1 else 0).toFloat() / total
}

class QuizViewModel(
    categoryId: String?,
    private val repository: GameRepository,
    private val sound: SoundManager
) : ViewModel() {

    private val category: QuizCategory? = QuizContent.category(categoryId)
    private var questions: List<QuizQuestion> = emptyList()
    private var advanceJob: Job? = null

    private val _state = MutableStateFlow(QuizUiState(valid = category != null))
    val state: StateFlow<QuizUiState> = _state.asStateFlow()

    init {
        restart()
    }

    fun restart() {
        val c = category ?: return
        advanceJob?.cancel()
        // Shuffle options per round while tracking the correct answer.
        questions = c.questions.map { q ->
            val order = q.options.indices.shuffled()
            QuizQuestion(q.text, order.map { q.options[it] }, order.indexOf(q.correctIndex))
        }
        _state.value = buildState(c, 0, score = 0)
    }

    fun answer(optionIndex: Int) {
        val s = _state.value
        if (!s.valid || s.answered || s.isPaused || s.finalScore != null) return
        if (optionIndex !in s.options.indices) return
        val correct = optionIndex == s.correctIndex
        sound.play(if (correct) Sfx.Correct else Sfx.Wrong)
        _state.update { it.copy(selectedIndex = optionIndex, score = it.score + if (correct) 1 else 0) }
        advanceJob = viewModelScope.launch {
            delay(ADVANCE_DELAY_MS)
            // If the player paused during feedback, wait until they continue.
            _state.first { !it.isPaused }
            advance()
        }
    }

    private suspend fun advance() {
        val c = category ?: return
        val s = _state.value
        val next = s.index + 1
        if (next >= questions.size) {
            repository.saveQuizBest(c.id, s.score)
            _state.update { it.copy(finalScore = s.score) }
        } else {
            _state.value = buildState(c, next, s.score)
        }
    }

    fun pause() {
        if (_state.value.finalScore == null) _state.update { it.copy(isPaused = true) }
    }

    fun resume() = _state.update { it.copy(isPaused = false) }

    private fun buildState(c: QuizCategory, index: Int, score: Int): QuizUiState {
        val q = questions[index]
        return QuizUiState(
            valid = true,
            categoryId = c.id,
            categoryTitle = c.title,
            categorySubtitle = c.subtitle,
            difficulty = c.difficulty,
            index = index,
            total = questions.size,
            question = q.text,
            options = q.options,
            correctIndex = q.correctIndex,
            selectedIndex = null,
            score = score,
            isPaused = false,
            finalScore = null
        )
    }

    class Factory(
        private val categoryId: String?,
        private val repository: GameRepository,
        private val sound: SoundManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            QuizViewModel(categoryId, repository, sound) as T
    }

    companion object {
        const val ADVANCE_DELAY_MS = 750L
    }
}
