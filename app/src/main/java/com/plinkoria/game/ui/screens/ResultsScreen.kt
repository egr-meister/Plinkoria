package com.plinkoria.game.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plinkoria.game.logic.GameLogic
import com.plinkoria.game.ui.Assets
import com.plinkoria.game.ui.LocalAppContainer
import com.plinkoria.game.ui.components.FloatingImage
import com.plinkoria.game.ui.components.IconBadge
import com.plinkoria.game.ui.components.NeonPanel
import com.plinkoria.game.ui.components.NeonProgressBar
import com.plinkoria.game.ui.components.PulsingImage
import com.plinkoria.game.ui.components.ScreenBackground
import com.plinkoria.game.ui.components.StarRating
import com.plinkoria.game.ui.components.TopBar
import com.plinkoria.game.ui.theme.MutedLavender
import com.plinkoria.game.ui.theme.PegGold
import com.plinkoria.game.ui.theme.PlasmaCyan
import com.plinkoria.game.ui.theme.SoftWhite
import com.plinkoria.game.ui.viewmodel.ProgressViewModel

@Composable
fun ResultsScreen(onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: ProgressViewModel = viewModel(factory = ProgressViewModel.Factory(container.repository))
    val state by vm.state.collectAsStateWithLifecycle()
    val totalStars = state.quiz.sumOf { GameLogic.quizStars(it.best, it.total) }

    ScreenBackground {
        Column(Modifier.fillMaxSize()) {
            TopBar(title = "Results", onBack = onBack)
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PulsingImage(Assets.iconTrophy, Modifier.size(84.dp))
                    Column(Modifier.padding(start = 12.dp)) {
                        IconBadge(Assets.iconStar, size = 26.dp) {
                            Text("$totalStars / ${state.quiz.size * 3} stars", style = MaterialTheme.typography.titleLarge, color = PegGold)
                        }
                        IconBadge(Assets.iconPuzzle, size = 26.dp) {
                            Text("${state.levelsCleared} / ${state.levelCount} levels", style = MaterialTheme.typography.titleLarge, color = PlasmaCyan)
                        }
                    }
                }
                NeonPanel(Modifier.fillMaxWidth().widthIn(max = 480.dp)) {
                    IconBadge(Assets.iconTarget, size = 30.dp) {
                        Text("Quiz — best scores", style = MaterialTheme.typography.titleLarge, color = PegGold)
                    }
                    Spacer(Modifier.height(10.dp))
                    state.quiz.forEach { q ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clearAndSetSemantics { contentDescription = "${q.title} quiz: best ${q.best} of ${q.total}" },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FloatingImage(heroForDifficulty(q.difficulty), Modifier.size(48.dp), amplitude = 2.dp)
                            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                                Text(q.title, style = MaterialTheme.typography.titleMedium, color = SoftWhite)
                                Text(q.subtitle, style = MaterialTheme.typography.bodyMedium, color = MutedLavender)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${q.best}/${q.total}", style = MaterialTheme.typography.titleMedium, color = PegGold)
                                StarRating(GameLogic.quizStars(q.best, q.total), size = 18.dp)
                            }
                        }
                        NeonProgressBar(q.best.toFloat() / q.total.coerceAtLeast(1))
                    }
                }
                NeonPanel(Modifier.fillMaxWidth().widthIn(max = 480.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FloatingImage(Assets.iconPuzzle, Modifier.size(60.dp), amplitude = 3.dp)
                        Column(Modifier.weight(1f).padding(start = 12.dp)) {
                            Text("Matching Pairs", style = MaterialTheme.typography.titleLarge, color = PegGold)
                            Text("Levels cleared", style = MaterialTheme.typography.bodyMedium, color = MutedLavender)
                        }
                        Box(Modifier.clearAndSetSemantics { contentDescription = "${state.levelsCleared} of ${state.levelCount} levels cleared" }) {
                            Text("${state.levelsCleared}/${state.levelCount}", style = MaterialTheme.typography.headlineMedium, color = PlasmaCyan)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    NeonProgressBar(state.levelsCleared.toFloat() / state.levelCount.coerceAtLeast(1))
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
