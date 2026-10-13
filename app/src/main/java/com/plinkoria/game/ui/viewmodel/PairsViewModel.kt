package com.plinkoria.game.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.plinkoria.game.audio.Sfx
import com.plinkoria.game.audio.SoundManager
import com.plinkoria.game.data.GameRepository
import com.plinkoria.game.logic.Card
import com.plinkoria.game.logic.GameLogic
import com.plinkoria.game.logic.LevelConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PairsOutcome { Won, Lost }

enum class PairsStatus { Loading, Ready, Locked, Invalid }

data class PairsUiState(
    val status: PairsStatus = PairsStatus.Loading,
    val level: Int = 0,
    val pairs: Int = 0,
    val columns: Int = 2,
    val totalSeconds: Int = 0,
    val remainingMs: Long = 0L,
    val cards: List<Card> = emptyList(),
    val matchedPairs: Int = 0,
    val isPaused: Boolean = false,
    val inputLocked: Boolean = false,
    val outcome: PairsOutcome? = null
) {
    val secondsLeft: Int get() = ((remainingMs + 999) / 1000).toInt()
    val timeFraction: Float get() = if (totalSeconds == 0) 0f else remainingMs / (totalSeconds * 1000f)
}

class PairsViewModel(
    private val level: Int,
    private val repository: GameRepository,
    private val sound: SoundManager
) : ViewModel() {

    private val config: LevelConfig? = GameLogic.levelConfig(level)
    private val _state = MutableStateFlow(
        PairsUiState(status = if (config == null) PairsStatus.Invalid else PairsStatus.Loading, level = level)
    )
    val state: StateFlow<PairsUiState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var flipBackJob: Job? = null
    private var finishing = false

    init {
        if (config != null) {
            viewModelScope.launch {
                // Respect level locks even after progress was reset / deep state restore.
                val data = repository.data.first()
                if (GameLogic.isLevelUnlocked(level, data.pairsBest)) {
                    startRound()
                } else {
                    _state.update { it.copy(status = PairsStatus.Locked) }
                }
            }
        }
    }

    fun restart() {
        if (_state.value.status == PairsStatus.Ready && !finishing) startRound()
    }

    private fun startRound() {
        val cfg = config ?: return
        timerJob?.cancel()
        flipBackJob?.cancel()
        finishing = false
        _state.value = PairsUiState(
            status = PairsStatus.Ready,
            level = cfg.level,
            pairs = cfg.pairs,
            columns = cfg.columns,
            totalSeconds = cfg.seconds,
            remainingMs = cfg.seconds * 1000L,
            cards = GameLogic.buildDeck(cfg.pairs)
        )
        timerJob = viewModelScope.launch {
            while (true) {
                delay(TICK_MS)
                val s = _state.value
                if (finishing || s.outcome != null) break
                if (s.isPaused) continue
                val left = (s.remainingMs - TICK_MS).coerceAtLeast(0L)
                _state.update { it.copy(remainingMs = left) }
                if (left == 0L) {
                    finish(PairsOutcome.Lost)
                    break
                }
            }
        }
    }

    fun flip(cardId: Int) {
        val s = _state.value
        if (s.status != PairsStatus.Ready || finishing || s.isPaused || s.inputLocked || s.outcome != null) return
        val card = s.cards.firstOrNull { it.id == cardId } ?: return
        if (card.isFaceUp || card.isMatched) return

        val openUnmatched = s.cards.filter { it.isFaceUp && !it.isMatched }
        val flipped = s.cards.map { if (it.id == cardId) it.copy(isFaceUp = true) else it }

        if (openUnmatched.isEmpty()) {
            sound.play(Sfx.Click)
            _state.update { it.copy(cards = flipped) }
            return
        }
        val other = openUnmatched.first()
        if (other.itemIndex == card.itemIndex) {
            val matched = flipped.map {
                if (it.id == cardId || it.id == other.id) it.copy(isMatched = true, isFaceUp = true) else it
            }
            val pairsDone = GameLogic.matchedPairCount(matched)
            sound.play(Sfx.Match)
            _state.update { it.copy(cards = matched, matchedPairs = pairsDone) }
            if (GameLogic.isDeckCleared(matched)) finish(PairsOutcome.Won)
        } else {
            sound.play(Sfx.Click)
            _state.update { it.copy(cards = flipped, inputLocked = true) }
            flipBackJob = viewModelScope.launch {
                delay(MISMATCH_DELAY_MS)
                _state.update { st ->
                    st.copy(
                        cards = st.cards.map { if (!it.isMatched) it.copy(isFaceUp = false) else it },
                        inputLocked = false
                    )
                }
            }
        }
    }

    /**
     * Ends the round once. Input is locked immediately; the best score is persisted BEFORE
     * [PairsUiState.outcome] is published, because publishing it triggers navigation (which
     * clears this ViewModel and its scope).
     */
    private fun finish(outcome: PairsOutcome) {
        if (finishing) return
        finishing = true
        flipBackJob?.cancel()
        val matched = _state.value.matchedPairs
        _state.update { it.copy(inputLocked = true, isPaused = false) }
        sound.play(if (outcome == PairsOutcome.Won) Sfx.Win else Sfx.Lose)
        viewModelScope.launch {
            repository.savePairsBest(level, matched)
            _state.update { it.copy(outcome = outcome) }
        }
    }

    fun pause() {
        val s = _state.value
        if (s.status == PairsStatus.Ready && !finishing && s.outcome == null) _state.update { it.copy(isPaused = true) }
    }

    fun resume() = _state.update { it.copy(isPaused = false) }

    class Factory(
        private val level: Int,
        private val repository: GameRepository,
        private val sound: SoundManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PairsViewModel(level, repository, sound) as T
    }

    companion object {
        const val TICK_MS = 100L
        const val MISMATCH_DELAY_MS = 700L
    }
}
