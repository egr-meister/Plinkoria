package com.plinkoria.game.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.plinkoria.game.content.QuizContent
import com.plinkoria.game.data.GameData
import com.plinkoria.game.data.GameRepository
import com.plinkoria.game.logic.GameLogic
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class QuizBestUi(val categoryId: String, val title: String, val subtitle: String, val difficulty: Int, val best: Int, val total: Int)

data class LevelUi(
    val level: Int,
    val pairs: Int,
    val seconds: Int,
    val unlocked: Boolean,
    val completed: Boolean,
    val bestMatched: Int
)

/** Immutable snapshot for Menu / Quiz categories / Levels / Results / Settings. */
data class ProgressUiState(
    val loaded: Boolean = false,
    val quiz: List<QuizBestUi> = emptyList(),
    val levels: List<LevelUi> = emptyList(),
    val levelsCleared: Int = 0,
    val levelCount: Int = GameLogic.levelCount,
    val soundEnabled: Boolean = true
)

class ProgressViewModel(private val repository: GameRepository) : ViewModel() {

    val state: StateFlow<ProgressUiState> = repository.data
        .map { it.toUi() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgressUiState())

    fun setSound(enabled: Boolean) {
        viewModelScope.launch { repository.setSoundEnabled(enabled) }
    }

    fun resetAll() {
        viewModelScope.launch { repository.resetAll() }
    }

    private fun GameData.toUi(): ProgressUiState = ProgressUiState(
        loaded = true,
        quiz = QuizContent.categories.map { c ->
            QuizBestUi(c.id, c.title, c.subtitle, c.difficulty, quizBest[c.id] ?: 0, c.questions.size)
        },
        levels = GameLogic.levels.map { cfg ->
            LevelUi(
                level = cfg.level,
                pairs = cfg.pairs,
                seconds = cfg.seconds,
                unlocked = GameLogic.isLevelUnlocked(cfg.level, pairsBest),
                completed = GameLogic.isLevelCompleted(cfg.level, pairsBest),
                bestMatched = (pairsBest[cfg.level.toString()] ?: 0).coerceAtMost(cfg.pairs)
            )
        },
        levelsCleared = GameLogic.completedLevelCount(pairsBest),
        soundEnabled = settings.soundEnabled
    )

    class Factory(private val repository: GameRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ProgressViewModel(repository) as T
    }
}
