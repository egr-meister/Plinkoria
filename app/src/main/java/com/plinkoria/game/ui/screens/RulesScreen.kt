package com.plinkoria.game.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.plinkoria.game.ui.Assets
import com.plinkoria.game.ui.components.FloatingImage
import com.plinkoria.game.ui.components.ModalPanel
import com.plinkoria.game.ui.components.PlateButton
import com.plinkoria.game.ui.components.ScreenBackground
import com.plinkoria.game.ui.theme.PlasmaCyan
import com.plinkoria.game.ui.theme.SoftWhite

@Composable
fun RulesScreen(onBack: () -> Unit) {
    ScreenBackground {
        Box {
            ModalPanel(title = "Game Rules") {
                FloatingImage(Assets.mascotC, Modifier.size(width = 90.dp, height = 150.dp), amplitude = 4.dp, sway = 1f)
                Spacer(Modifier.height(10.dp))
                RuleSection(
                    Assets.iconTarget,
                    "Quiz",
                    "Circles, balls and spheres in three levels: Easy, Medium and Hard. Each round has 10 questions " +
                        "with 4 answers — only one is right. The correct answer lights up green with a check, a wrong pick " +
                        "turns red with a cross, then the next question rolls in. Earn up to 3 stars per level."
                )
                RuleSection(
                    Assets.iconPuzzle,
                    "Matching Pairs",
                    "Flip two cards at a time. Matching icons stay open; mismatches flip back. Clear every pair before " +
                        "the hourglass runs out to win and unlock the next level. 9 levels, bigger decks each time — " +
                        "level 1 is always open."
                )
                RuleSection(
                    Assets.iconHourglass,
                    "Pause",
                    "Tap the pause button or press Back during a game. The timer stops while paused. " +
                        "Choose Continue, Restart or Menu."
                )
                RuleSection(
                    Assets.iconShield,
                    "Offline",
                    "Plinkoria works fully offline. Progress is saved only on this device."
                )
                Spacer(Modifier.height(8.dp))
                PlateButton("Back", onBack, Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun RuleSection(icon: Int, title: String, body: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Image(painterResource(icon), contentDescription = null, modifier = Modifier.size(44.dp))
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = PlasmaCyan, modifier = Modifier.semantics { heading() })
            Spacer(Modifier.height(2.dp))
            Text(body, style = MaterialTheme.typography.bodyLarge, color = SoftWhite)
        }
    }
    Spacer(Modifier.height(14.dp))
}
