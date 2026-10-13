package com.plinkoria.game.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.unit.min
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plinkoria.game.ui.Assets
import com.plinkoria.game.ui.LocalAppContainer
import com.plinkoria.game.ui.components.CappedFontScale
import com.plinkoria.game.ui.components.FloatingImage
import com.plinkoria.game.ui.components.IconBadge
import com.plinkoria.game.ui.components.IconImageButton
import com.plinkoria.game.ui.components.NeonPanel
import com.plinkoria.game.ui.components.NeonProgressBar
import com.plinkoria.game.ui.components.PlateButton
import com.plinkoria.game.ui.components.ScreenBackground
import com.plinkoria.game.ui.components.SectionTitle
import com.plinkoria.game.ui.components.StatusGlyph
import com.plinkoria.game.ui.theme.CorrectGreen
import com.plinkoria.game.ui.theme.MutedLavender
import com.plinkoria.game.ui.theme.PegGold
import com.plinkoria.game.ui.theme.SoftWhite
import com.plinkoria.game.ui.theme.WrongRed
import com.plinkoria.game.ui.viewmodel.QuizUiState
import com.plinkoria.game.ui.viewmodel.QuizViewModel

private val Letters = listOf("A", "B", "C", "D", "E", "F")

@Composable
fun QuizScreen(
    categoryId: String,
    onFinished: (Int) -> Unit,
    onMenu: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: QuizViewModel = viewModel(
        factory = QuizViewModel.Factory(categoryId, container.repository, container.sound)
    )
    val state by vm.state.collectAsStateWithLifecycle()
    val finished by rememberUpdatedState(onFinished)

    LaunchedEffect(state.finalScore) {
        state.finalScore?.let { finished(it) }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) { vm.pause() }
    BackHandler { if (state.isPaused) vm.resume() else vm.pause() }

    ScreenBackground {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingImage(heroForDifficulty(state.difficulty), Modifier.size(56.dp), amplitude = 3.dp)
                Column(Modifier.weight(1f).padding(horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    SectionTitle(state.categoryTitle, Modifier.fillMaxWidth())
                    Text(state.categorySubtitle, style = MaterialTheme.typography.labelMedium, color = MutedLavender)
                }
                IconImageButton(Assets.iconPause, "Pause", { vm.pause() })
            }
            // Scrolls only if the content is taller than the screen; otherwise the leftover
            // space below the answers is filled by the character stage.
            BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
                val viewport = maxHeight
                Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = viewport)
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(Modifier.fillMaxWidth().widthIn(max = 480.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.weight(1f)) { NeonProgressBar(state.progress) }
                        Text(
                            "${(state.index + 1).coerceAtMost(state.total)}/${state.total}",
                            style = MaterialTheme.typography.titleMedium,
                            color = PegGold,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth().widthIn(max = 480.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconBadge(Assets.iconStar, size = 26.dp) {
                            Text("Score ${state.score}", style = MaterialTheme.typography.titleMedium, color = SoftWhite)
                        }
                        DifficultyMeter(state.difficulty, size = 20.dp)
                    }
                    Spacer(Modifier.height(12.dp))
                    AnimatedContent(
                        targetState = state,
                        contentKey = { it.index },
                        transitionSpec = {
                            (slideInHorizontally { it / 3 } + fadeIn()) togetherWith (slideOutHorizontally { -it / 3 } + fadeOut())
                        },
                        label = "question"
                    ) { shown ->
                        QuestionBody(state = shown, onAnswer = vm::answer)
                    }
                    Spacer(Modifier.height(8.dp))
                    CharacterStage(
                        difficulty = state.difficulty,
                        mood = when {
                            !state.answered -> Mood.Thinking
                            state.selectedIndex == state.correctIndex -> Mood.Happy
                            else -> Mood.Oops
                        },
                        modifier = Modifier.weight(1f).fillMaxWidth().widthIn(max = 520.dp)
                    )
                }
            }
        }
        if (state.isPaused) {
            PauseOverlay(
                subtitle = "Question ${state.index + 1} of ${state.total} • Score ${state.score}",
                onContinue = { vm.resume() },
                onRestart = { vm.restart() },
                onMenu = onMenu
            )
        }
    }
}

@Composable
private fun QuestionBody(state: QuizUiState, onAnswer: (Int) -> Unit) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        NeonPanel(Modifier.fillMaxWidth().widthIn(max = 480.dp)) {
            Text(
                state.question,
                style = MaterialTheme.typography.titleLarge,
                color = SoftWhite,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(16.dp))
        Column(
            Modifier.fillMaxWidth().widthIn(max = 480.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            state.options.forEachIndexed { i, option ->
                val isCorrect = state.answered && i == state.correctIndex
                val isWrongPick = state.answered && i == state.selectedIndex && i != state.correctIndex
                val letter = Letters.getOrElse(i) { "${i + 1}" }
                val label = when {
                    isCorrect -> "$letter: $option. Correct answer"
                    isWrongPick -> "$letter: $option. Your answer, wrong"
                    else -> "$letter: $option"
                }
                PlateButton(
                    text = option,
                    onClick = { onAnswer(i) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.answered || isCorrect || isWrongPick,
                    minHeight = 66.dp,
                    fontSize = 17.sp,
                    maxLines = 3,
                    playClick = false,
                    semanticLabel = label,
                    leading = { LetterBall(letter) }
                ) {
                    if (isCorrect || isWrongPick) {
                        val tint = if (isCorrect) CorrectGreen else WrongRed
                        Box(
                            Modifier
                                .matchParentSize()
                                .padding(3.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(tint.copy(alpha = 0.55f))
                                .border(3.dp, tint, RoundedCornerShape(14.dp))
                        )
                        Box(
                            Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 12.dp)
                                .size(28.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.White)
                                .padding(4.dp)
                        ) {
                            StatusGlyph(correct = isCorrect, color = tint, modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
        if (state.answered) {
            Spacer(Modifier.height(10.dp))
            val right = state.selectedIndex == state.correctIndex
            IconBadge(if (right) Assets.iconHeart else Assets.iconLightning, size = 26.dp) {
                Text(
                    if (right) "Correct!" else "Not quite!",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (right) CorrectGreen else WrongRed,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            }
        }
    }
}

/** Glossy round plate with the option letter (A–D). */
@Composable
private fun LetterBall(letter: String) {
    Box(Modifier.size(42.dp), contentAlignment = Alignment.Center) {
        Image(painterResource(Assets.plateRound), contentDescription = null, modifier = Modifier.fillMaxSize())
        CappedFontScale(1.2f) {
            Text(
                letter,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SoftWhite,
                    shadow = Shadow(Color(0xCC12002E), blurRadius = 6f)
                )
            )
        }
    }
}

private enum class Mood { Thinking, Happy, Oops }

/**
 * Fills the free space under the answers with the game's characters.
 * Hidden when there is too little room; never forces scrolling (it only takes leftover height).
 */
@Composable
private fun CharacterStage(difficulty: Int, mood: Mood, modifier: Modifier) {
    BoxWithConstraints(modifier, contentAlignment = Alignment.BottomCenter) {
        // Height is capped by width too, so three characters always fit side by side.
        val stageH = min(min(maxHeight, 280.dp), maxWidth * 0.55f)
        if (stageH >= 96.dp) {
            CharacterRow(difficulty, mood, stageH)
        }
    }
}

@Composable
private fun CharacterRow(difficulty: Int, mood: Mood, stageH: Dp) {
    val (left, center, right) = when (difficulty) {
        1 -> Triple(Assets.mascotC, Assets.orb, Assets.mascotA)
        2 -> Triple(Assets.mascotB, Assets.orb, Assets.mascotA)
        else -> Triple(Assets.mascotC, Assets.mascotD, Assets.mascotB)
    }
    val bounce = if (mood == Mood.Happy) 14.dp else 5.dp
    Row(
        Modifier.fillMaxWidth().height(stageH),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        FloatingImage(left, Modifier.height(stageH), amplitude = bounce, periodMs = 3200, sway = 1.5f)
        Box(Modifier.height(stageH * 0.62f), contentAlignment = Alignment.TopCenter) {
            FloatingImage(center, Modifier.size(stageH * 0.42f).align(Alignment.BottomCenter), amplitude = bounce, periodMs = 1800, sway = 6f, phase = 0.4f)
            if (mood != Mood.Thinking) {
                Image(
                    painterResource(if (mood == Mood.Happy) Assets.iconHeart else Assets.iconLightning),
                    contentDescription = null,
                    modifier = Modifier.size(stageH * 0.16f)
                )
            }
        }
        FloatingImage(right, Modifier.height(stageH * 0.72f), amplitude = bounce, periodMs = 2600, sway = 3f, phase = 0.7f)
    }
}
