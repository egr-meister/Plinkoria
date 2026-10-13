package com.plinkoria.game.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.plinkoria.game.ui.Assets
import com.plinkoria.game.ui.components.FloatingImage
import com.plinkoria.game.ui.components.ItemBurst
import com.plinkoria.game.ui.components.ModalPanel
import com.plinkoria.game.ui.components.SpinningImage
import com.plinkoria.game.ui.components.PanelButtons
import com.plinkoria.game.ui.components.ScreenBackground
import com.plinkoria.game.ui.theme.MutedLavender
import com.plinkoria.game.ui.theme.SoftWhite

@Composable
fun PairsResultScreen(
    level: Int,
    matched: Int,
    totalPairs: Int,
    won: Boolean,
    hasNextLevel: Boolean,
    onContinue: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    BackHandler { onMenu() }
    ScreenBackground {
        ModalPanel(title = if (won) "Well Done" else "Out of Time") {
            Box(Modifier.size(150.dp), contentAlignment = Alignment.Center) {
                SpinningImage(if (won) Assets.iconRingGold else Assets.iconRingNeon, Modifier.size(150.dp), periodMs = 12000, alpha = 0.5f)
                FloatingImage(if (won) Assets.mascotC else Assets.orb, Modifier.size(118.dp), amplitude = 5.dp)
                Image(
                    painterResource(if (won) Assets.iconTrophy else Assets.iconHourglass),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.BottomEnd).size(44.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text("Level $level", style = MaterialTheme.typography.titleMedium, color = MutedLavender)
            Spacer(Modifier.height(4.dp))
            Text(
                "Pairs: $matched/$totalPairs",
                style = MaterialTheme.typography.displaySmall,
                color = SoftWhite,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                when {
                    won && hasNextLevel -> "Level ${level + 1} unlocked!"
                    won -> "Every level cleared — legendary!"
                    else -> "Try again — you've got this."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MutedLavender,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            PanelButtons(
                (if (won && hasNextLevel) "Next Level" else "Continue") to onContinue,
                "Restart" to onRestart,
                "Menu" to onMenu
            )
        }
        if (won) ItemBurst()
    }
}
