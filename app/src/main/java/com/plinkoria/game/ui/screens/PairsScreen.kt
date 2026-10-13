package com.plinkoria.game.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plinkoria.game.logic.Card
import com.plinkoria.game.ui.Assets
import com.plinkoria.game.ui.LocalAppContainer
import com.plinkoria.game.ui.components.FallbackScreen
import com.plinkoria.game.ui.components.IconImageButton
import com.plinkoria.game.ui.components.NeonProgressBar
import com.plinkoria.game.ui.components.ScreenBackground
import com.plinkoria.game.ui.components.SectionTitle
import com.plinkoria.game.ui.components.StatusGlyph
import com.plinkoria.game.ui.theme.CorrectGreen
import com.plinkoria.game.ui.theme.DeepViolet
import com.plinkoria.game.ui.theme.NeonMagenta
import com.plinkoria.game.ui.theme.NightVoid
import com.plinkoria.game.ui.theme.PegGold
import com.plinkoria.game.ui.theme.PlasmaCyan
import com.plinkoria.game.ui.theme.RoyalPurple
import com.plinkoria.game.ui.theme.SoftWhite
import com.plinkoria.game.ui.theme.WrongRed
import com.plinkoria.game.ui.viewmodel.PairsOutcome
import com.plinkoria.game.ui.viewmodel.PairsStatus
import com.plinkoria.game.ui.viewmodel.PairsViewModel

@Composable
fun PairsScreen(
    level: Int,
    onFinished: (matched: Int, won: Boolean) -> Unit,
    onMenu: () -> Unit,
    onBack: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: PairsViewModel = viewModel(
        factory = PairsViewModel.Factory(level, container.repository, container.sound)
    )
    val state by vm.state.collectAsStateWithLifecycle()
    val finished by rememberUpdatedState(onFinished)

    LaunchedEffect(state.outcome) {
        state.outcome?.let { finished(state.matchedPairs, it == PairsOutcome.Won) }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) { vm.pause() }

    when (state.status) {
        PairsStatus.Invalid -> {
            FallbackScreen("That level doesn't exist.", onBack)
            return
        }
        PairsStatus.Locked -> {
            FallbackScreen("Level $level is still locked. Clear the previous level first.", onBack)
            return
        }
        PairsStatus.Loading -> {
            ScreenBackground { }
            return
        }
        PairsStatus.Ready -> Unit
    }

    BackHandler { if (state.isPaused) vm.resume() else vm.pause() }

    ScreenBackground {
        Column(Modifier.fillMaxSize()) {
            // Header: back • Level N • pause (as in the reference layout).
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconImageButton(Assets.iconBack, "Back to levels", onBack)
                Column(Modifier.weight(1f)) {
                    SectionTitle("Level ${state.level}", Modifier.fillMaxWidth())
                }
                IconImageButton(Assets.iconPause, "Pause", { vm.pause() })
            }
            val lowTime = state.secondsLeft <= 10
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatPill(
                    icon = Assets.iconHourglass,
                    text = formatTime(state.secondsLeft),
                    color = if (lowTime) WrongRed else PlasmaCyan,
                    description = "${state.secondsLeft} seconds left"
                )
                StatPill(
                    icon = Assets.iconMagnet,
                    text = "Pairs ${state.matchedPairs}/${state.pairs}",
                    color = PegGold,
                    description = "${state.matchedPairs} of ${state.pairs} pairs found"
                )
            }
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                NeonProgressBar(
                    state.timeFraction,
                    colors = if (lowTime) listOf(WrongRed, NeonMagenta) else listOf(PlasmaCyan, NeonMagenta)
                )
            }
            Spacer(Modifier.height(10.dp))
            CardGrid(
                cards = state.cards,
                columns = state.columns,
                onFlip = vm::flip,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, bottom = 12.dp)
            )
        }
        if (state.isPaused) {
            PauseOverlay(
                subtitle = "Level ${state.level} • ${formatTime(state.secondsLeft)} left • ${state.matchedPairs}/${state.pairs} pairs",
                onContinue = { vm.resume() },
                onRestart = { vm.restart() },
                onMenu = onMenu
            )
        }
    }
}

private fun formatTime(seconds: Int): String = "%02d:%02d".format(seconds / 60, seconds % 60)

/** Rounded pill chip with an asset icon (timer / pairs counter). */
@Composable
private fun StatPill(icon: Int, text: String, color: Color, description: String) {
    Row(
        Modifier
            .clearAndSetSemantics { contentDescription = description }
            .clip(RoundedCornerShape(50))
            .background(Color(0xCC120429))
            .border(2.dp, color, RoundedCornerShape(50))
            .padding(start = 8.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painterResource(icon), contentDescription = null, modifier = Modifier.size(30.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.titleMedium, color = color, maxLines = 1)
    }
}

/**
 * Lays out the whole deck inside the available area — never scrolls.
 * Cards are portrait rectangles that stretch to fill each grid cell; the width/height
 * ratio is kept between 0.62 (tall) and 1.0 (square) so cards never look squashed.
 */
@Composable
private fun CardGrid(cards: List<Card>, columns: Int, onFlip: (Int) -> Unit, modifier: Modifier) {
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val cols = columns.coerceAtLeast(1)
        val rows = ((cards.size + cols - 1) / cols).coerceAtLeast(1)
        val gap = if (rows >= 5) 8.dp else 12.dp
        val gridMaxWidth = min(maxWidth, 560.dp)
        val cellW = ((gridMaxWidth - gap * (cols - 1)) / cols).coerceAtLeast(0.dp)
        val cellH = ((maxHeight - gap * (rows - 1)) / rows).coerceAtLeast(0.dp)
        var cardW = cellW
        var cardH = cellH
        if (cardW > cardH) cardW = cardH                       // cap at square
        if (cardW / cardH < 0.62f) cardH = cardW / 0.62f        // cap at tall
        Column(verticalArrangement = Arrangement.spacedBy(gap), horizontalAlignment = Alignment.CenterHorizontally) {
            cards.chunked(cols).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    row.forEach { card ->
                        FlipCard(
                            card = card,
                            width = cardW,
                            height = cardH,
                            position = card.id + 1,
                            onClick = { onFlip(card.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FlipCard(card: Card, width: Dp, height: Dp, position: Int, onClick: () -> Unit) {
    val showFront = card.isFaceUp || card.isMatched
    val rotation by animateFloatAsState(
        targetValue = if (showFront) 180f else 0f,
        animationSpec = tween(320),
        label = "flip"
    )
    val minSide = min(width, height)
    val shape = RoundedCornerShape((minSide * 0.12f).coerceIn(8.dp, 18.dp))
    val description = when {
        card.isMatched -> "Card $position, ${Assets.itemName(card.itemIndex)}, matched"
        card.isFaceUp -> "Card $position, ${Assets.itemName(card.itemIndex)}"
        else -> "Card $position, hidden"
    }
    Box(
        Modifier
            .size(width = width, height = height)
            .clearAndSetSemantics {
                contentDescription = description
                role = Role.Button
            }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .clip(shape)
            .clickable(enabled = !showFront, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            CardBack(Modifier.fillMaxSize(), shape, minSide)
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
                    .background(Brush.verticalGradient(listOf(Color(0xFF6B2BD6), RoyalPurple, DeepViolet)), shape)
                    .border(
                        width = if (card.isMatched) 3.dp else 2.dp,
                        color = if (card.isMatched) CorrectGreen else PlasmaCyan,
                        shape = shape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Assets.item(card.itemIndex)),
                    contentDescription = null,
                    modifier = Modifier.size(minSide * 0.72f)
                )
                if (card.isMatched) {
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size((minSide * 0.22f).coerceIn(16.dp, 30.dp))
                            .clip(RoundedCornerShape(50))
                            .background(SoftWhite)
                            .padding(3.dp)
                    ) {
                        StatusGlyph(correct = true, color = CorrectGreen, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

/** Card back: deep neon gradient, gold border, faint peg dots and the glossy round plate with the orb. */
@Composable
private fun CardBack(modifier: Modifier, shape: RoundedCornerShape, minSide: Dp) {
    Box(
        modifier
            .background(Brush.verticalGradient(listOf(RoyalPurple, Color(0xFF2A0E6B), NightVoid)), shape)
            .border(2.dp, PegGold, shape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize().padding(6.dp)) {
            val cols = 4
            val step = size.width / cols
            val rowsCount = (size.height / step).toInt().coerceAtLeast(1)
            for (r in 0 until rowsCount) for (c in 0 until cols) {
                if ((r + c) % 2 == 0) {
                    drawCircle(
                        PegGold.copy(alpha = 0.28f),
                        radius = size.minDimension * 0.022f,
                        center = Offset(step * (c + 0.5f), step * (r + 0.5f))
                    )
                }
            }
        }
        Image(
            painter = painterResource(Assets.plateRound),
            contentDescription = null,
            modifier = Modifier.size(minSide * 0.66f)
        )
        Image(
            painter = painterResource(Assets.orb),
            contentDescription = null,
            modifier = Modifier.size(minSide * 0.30f)
        )
    }
}
