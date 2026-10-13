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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plinkoria.game.content.QuizContent
import com.plinkoria.game.logic.GameLogic
import com.plinkoria.game.ui.Assets
import com.plinkoria.game.ui.LocalAppContainer
import com.plinkoria.game.ui.components.FloatingImage
import com.plinkoria.game.ui.components.IconBadge
import com.plinkoria.game.ui.components.NeonPanel
import com.plinkoria.game.ui.components.PlateButton
import com.plinkoria.game.ui.components.ScreenBackground
import com.plinkoria.game.ui.components.StarRating
import com.plinkoria.game.ui.components.TopBar
import com.plinkoria.game.ui.theme.MutedLavender
import com.plinkoria.game.ui.theme.PegGold
import com.plinkoria.game.ui.theme.SoftWhite
import com.plinkoria.game.ui.viewmodel.ProgressViewModel

@Composable
fun QuizCategoryScreen(onBack: () -> Unit, onCategory: (String) -> Unit) {
    val container = LocalAppContainer.current
    val vm: ProgressViewModel = viewModel(factory = ProgressViewModel.Factory(container.repository))
    val state by vm.state.collectAsStateWithLifecycle()

    ScreenBackground {
        Column(Modifier.fillMaxSize()) {
            TopBar(title = "Quiz", onBack = onBack)
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Circles, balls & spheres — pick a difficulty.\n10 questions • 4 answers • 1 is right.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MutedLavender,
                    textAlign = TextAlign.Center
                )
                QuizContent.categories.forEachIndexed { index, category ->
                    val best = state.quiz.firstOrNull { it.categoryId == category.id }?.best ?: 0
                    val total = category.questions.size
                    NeonPanel(Modifier.fillMaxWidth().widthIn(max = 460.dp), contentPadding = 16.dp) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(84.dp), contentAlignment = Alignment.Center) {
                                FloatingImage(
                                    heroForDifficulty(category.difficulty),
                                    Modifier.size(80.dp),
                                    amplitude = 4.dp,
                                    periodMs = 2600 + index * 300,
                                    phase = index * 0.3f
                                )
                            }
                            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                Text(category.title, style = MaterialTheme.typography.headlineSmall, color = SoftWhite)
                                Text(category.subtitle, style = MaterialTheme.typography.bodyMedium, color = MutedLavender)
                                Spacer(Modifier.height(4.dp))
                                DifficultyMeter(category.difficulty)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconBadge(Assets.iconTrophy, size = 26.dp) {
                                    Text("$best/$total", style = MaterialTheme.typography.titleMedium, color = PegGold)
                                }
                                Spacer(Modifier.height(4.dp))
                                StarRating(GameLogic.quizStars(best, total), size = 20.dp)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        PlateButton(
                            text = "Play ${category.title}",
                            onClick = { onCategory(category.id) },
                            modifier = Modifier.fillMaxWidth(),
                            icon = Assets.iconBall
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
