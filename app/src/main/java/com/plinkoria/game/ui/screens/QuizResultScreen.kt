package com.plinkoria.game.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.plinkoria.game.logic.GameLogic
import com.plinkoria.game.ui.Assets
import com.plinkoria.game.ui.components.FloatingImage
import com.plinkoria.game.ui.components.ItemBurst
import com.plinkoria.game.ui.components.ModalPanel
import com.plinkoria.game.ui.components.PanelButtons
import com.plinkoria.game.ui.components.ScreenBackground
import com.plinkoria.game.ui.components.SpinningImage
import com.plinkoria.game.ui.components.StarRating
import com.plinkoria.game.ui.theme.MutedLavender
import com.plinkoria.game.ui.theme.SoftWhite

@Composable
fun QuizResultScreen(
    categoryTitle: String,
    difficulty: Int,
    score: Int,
    total: Int,
    onContinue: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    BackHandler { onContinue() }
    val stars = GameLogic.quizStars(score, total)
    val (cheer, hero) = when (stars) {
        3 -> "Perfect drop!" to Assets.mascotC
        2 -> "Great run!" to Assets.mascotA
        1 -> "Nice bounce!" to Assets.orb
        else -> "Keep rolling!" to Assets.mascotD
    }
    ScreenBackground {
        ModalPanel(title = cheer) {
            Box(Modifier.size(150.dp), contentAlignment = Alignment.Center) {
                SpinningImage(Assets.iconRingGold, Modifier.size(150.dp), periodMs = 12000, alpha = 0.5f)
                FloatingImage(hero, Modifier.size(118.dp), amplitude = 5.dp)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "$categoryTitle quiz",
                style = MaterialTheme.typography.titleMedium,
                color = MutedLavender,
                textAlign = TextAlign.Center
            )
            DifficultyMeter(difficulty, size = 18.dp)
            Spacer(Modifier.height(6.dp))
            Text("Score: $score/$total", style = MaterialTheme.typography.displaySmall, color = SoftWhite, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            StarRating(stars, size = 40.dp)
            Spacer(Modifier.height(20.dp))
            PanelButtons(
                "Continue" to onContinue,
                "Restart" to onRestart,
                "Menu" to onMenu
            )
        }
        if (stars >= 2) ItemBurst()
    }
}
