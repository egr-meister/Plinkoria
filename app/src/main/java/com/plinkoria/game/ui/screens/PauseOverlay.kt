package com.plinkoria.game.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.plinkoria.game.ui.components.ModalPanel
import com.plinkoria.game.ui.components.PanelButtons
import com.plinkoria.game.ui.theme.MutedLavender

@Composable
fun PauseOverlay(
    subtitle: String,
    onContinue: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    ModalPanel(title = "Paused") {
        Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = MutedLavender, textAlign = TextAlign.Center)
        Spacer(Modifier.height(18.dp))
        PanelButtons(
            "Continue" to onContinue,
            "Restart" to onRestart,
            "Menu" to onMenu
        )
    }
}
