package com.plinkoria.game.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plinkoria.game.audio.Sfx
import com.plinkoria.game.ui.Assets
import com.plinkoria.game.ui.LocalAppContainer
import com.plinkoria.game.ui.components.CappedFontScale
import com.plinkoria.game.ui.components.FloatingImage
import com.plinkoria.game.ui.components.IconBadge
import com.plinkoria.game.ui.components.PlateButton
import com.plinkoria.game.ui.components.ScreenBackground
import com.plinkoria.game.ui.components.TopBar
import com.plinkoria.game.ui.theme.MutedLavender
import com.plinkoria.game.ui.theme.PegGold
import com.plinkoria.game.ui.theme.SoftWhite
import com.plinkoria.game.ui.viewmodel.LevelUi
import com.plinkoria.game.ui.viewmodel.ProgressViewModel

@Composable
fun LevelsScreen(onBack: () -> Unit, onLevel: (Int) -> Unit, onRules: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: ProgressViewModel = viewModel(factory = ProgressViewModel.Factory(container.repository))
    val state by vm.state.collectAsStateWithLifecycle()

    ScreenBackground {
        Column(Modifier.fillMaxSize()) {
            TopBar(title = "Matching Pairs", onBack = onBack)
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FloatingImage(Assets.mascotA, Modifier.size(64.dp), amplitude = 4.dp)
                    Column(Modifier.padding(start = 10.dp)) {
                        IconBadge(Assets.iconTrophy, size = 24.dp) {
                            Text(
                                "Cleared ${state.levelsCleared}/${state.levelCount}",
                                style = MaterialTheme.typography.titleMedium,
                                color = PegGold
                            )
                        }
                        Text(
                            "Clear a level to unlock the next",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedLavender,
                            textAlign = TextAlign.Start
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                // 9 bubbles arranged 3×3, with a cascading offset per row (peg-board feel).
                state.levels.chunked(3).forEachIndexed { rowIndex, row ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .widthIn(max = 460.dp)
                            .offset(x = if (rowIndex % 2 == 1) 18.dp else (-6).dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { level ->
                            LevelBubble(
                                level = level,
                                onClick = {
                                    if (level.unlocked) onLevel(level.level)
                                },
                                modifier = Modifier.weight(1f).padding(6.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                }
                Spacer(Modifier.height(16.dp))
                PlateButton("Game Rules", onRules, Modifier.fillMaxWidth(0.8f).widthIn(max = 420.dp), icon = Assets.iconKey)
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun LevelBubble(level: LevelUi, onClick: () -> Unit, modifier: Modifier) {
    val container = LocalAppContainer.current
    val status = when {
        level.completed -> "cleared"
        level.unlocked -> "unlocked"
        else -> "locked"
    }
    Column(
        modifier
            .clearAndSetSemantics {
                contentDescription = "Level ${level.level}, $status, ${level.pairs} pairs, ${level.seconds} seconds"
                role = Role.Button
            }
            .clickable(enabled = level.unlocked) {
                container.sound.play(Sfx.Click)
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .alpha(if (level.unlocked) 1f else 0.5f),
            contentAlignment = Alignment.Center
        ) {
            Image(painterResource(Assets.plateRound), contentDescription = null, modifier = Modifier.fillMaxSize())
            if (level.unlocked) {
                CappedFontScale {
                    Text(
                        "${level.level}",
                        style = TextStyle(
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SoftWhite,
                            shadow = Shadow(Color(0xCC12002E), blurRadius = 8f)
                        )
                    )
                }
            } else {
                Image(painterResource(Assets.iconLock), contentDescription = null, modifier = Modifier.fillMaxSize(0.5f))
            }
            if (level.completed) {
                Image(
                    painterResource(Assets.iconStar),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .fillMaxSize(0.36f)
                )
            }
        }
        CappedFontScale(1.15f) {
            Text(
                when {
                    level.completed -> "Cleared"
                    level.unlocked -> "${level.bestMatched}/${level.pairs} • ${level.seconds}s"
                    else -> "Locked"
                },
                style = MaterialTheme.typography.labelMedium,
                color = if (level.completed) PegGold else MutedLavender,
                maxLines = 1
            )
        }
    }
}
