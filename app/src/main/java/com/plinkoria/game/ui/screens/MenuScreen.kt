package com.plinkoria.game.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plinkoria.game.ui.Assets
import com.plinkoria.game.ui.components.FloatingImage
import com.plinkoria.game.ui.components.MenuPlateButton
import com.plinkoria.game.ui.components.PlateButton
import com.plinkoria.game.ui.components.PulsingImage
import com.plinkoria.game.ui.components.ScreenBackground
import com.plinkoria.game.ui.components.TopBar
import com.plinkoria.game.ui.theme.MutedLavender

@Composable
fun MenuScreen(
    onBack: () -> Unit,
    onQuiz: () -> Unit,
    onPairs: () -> Unit,
    onResults: () -> Unit,
    onSettings: () -> Unit,
    onRules: () -> Unit
) {
    ScreenBackground {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val w = maxWidth
            // Presenting pilot leans in from the bottom-left behind the buttons.
            FloatingImage(
                Assets.mascotC,
                Modifier
                    .align(Alignment.BottomStart)
                    .size(width = w * 0.42f, height = w * 0.77f)
                    .offset(x = -(w * 0.08f)),
                amplitude = 5.dp,
                periodMs = 3600,
                sway = 1.5f
            )
            Column(Modifier.fillMaxSize()) {
                TopBar(title = "Menu", onBack = onBack)
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PulsingImage(Assets.orb, Modifier.size(72.dp))
                        Text(
                            "Pick your chute!",
                            style = MaterialTheme.typography.titleLarge,
                            color = MutedLavender,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                    Box(Modifier.fillMaxWidth(0.76f).widthIn(max = 420.dp)) {
                        MenuPlateButton("QUIZ", onQuiz, Modifier.fillMaxWidth(), icon = Assets.iconTarget)
                    }
                    Box(Modifier.fillMaxWidth(0.76f).widthIn(max = 420.dp)) {
                        MenuPlateButton("PAIRS", onPairs, Modifier.fillMaxWidth(), icon = Assets.iconPuzzle)
                    }
                    Spacer(Modifier.height(2.dp))
                    Column(
                        Modifier
                            .fillMaxWidth(0.72f)
                            .widthIn(max = 400.dp)
                            .align(Alignment.End),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PlateButton("Results", onResults, Modifier.fillMaxWidth(), icon = Assets.iconTrophy, fontSize = 18.sp)
                        PlateButton("Game Rules", onRules, Modifier.fillMaxWidth(), icon = Assets.iconKey, fontSize = 18.sp)
                        PlateButton("Settings", onSettings, Modifier.fillMaxWidth(), icon = Assets.iconGear, fontSize = 18.sp)
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}
