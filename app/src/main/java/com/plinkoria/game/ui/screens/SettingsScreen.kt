package com.plinkoria.game.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plinkoria.game.audio.Sfx
import com.plinkoria.game.ui.AppLinks
import com.plinkoria.game.ui.Assets
import com.plinkoria.game.ui.LocalAppContainer
import com.plinkoria.game.ui.components.FloatingImage
import com.plinkoria.game.ui.components.ModalPanel
import com.plinkoria.game.ui.components.NeonPanel
import com.plinkoria.game.ui.components.PanelButtons
import com.plinkoria.game.ui.components.PlateButton
import com.plinkoria.game.ui.components.ScreenBackground
import com.plinkoria.game.ui.components.TopBar
import com.plinkoria.game.ui.theme.MutedLavender
import com.plinkoria.game.ui.theme.NeonMagenta
import com.plinkoria.game.ui.theme.NightVoid
import com.plinkoria.game.ui.theme.PegGold
import com.plinkoria.game.ui.theme.PlasmaCyan
import com.plinkoria.game.ui.theme.SoftWhite
import com.plinkoria.game.ui.viewmodel.ProgressViewModel

private enum class SettingsDialog { None, Privacy, ConfirmReset, ResetDone, LinkFailed }

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val context = LocalContext.current
    val vm: ProgressViewModel = viewModel(factory = ProgressViewModel.Factory(container.repository))
    val state by vm.state.collectAsStateWithLifecycle()
    var dialog by rememberSaveable { mutableStateOf(SettingsDialog.None) }

    ScreenBackground {
        Column(Modifier.fillMaxSize()) {
            TopBar(title = "Settings", onBack = onBack)
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FloatingImage(Assets.iconGear, Modifier.size(84.dp), amplitude = 4.dp, sway = 8f)
                NeonPanel(Modifier.fillMaxWidth().widthIn(max = 480.dp)) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .toggleable(
                                value = state.soundEnabled,
                                role = Role.Switch,
                                onValueChange = { enabled ->
                                    vm.setSound(enabled)
                                    if (enabled) {
                                        container.sound.enabled = true
                                        container.sound.play(Sfx.Click)
                                    }
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painterResource(Assets.iconBell),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp).alpha(if (state.soundEnabled) 1f else 0.4f)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Sound effects", style = MaterialTheme.typography.titleLarge, color = SoftWhite)
                            Text(
                                if (state.soundEnabled) "On" else "Off",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedLavender
                            )
                        }
                        Switch(
                            checked = state.soundEnabled,
                            onCheckedChange = null,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SoftWhite,
                                checkedTrackColor = NeonMagenta,
                                uncheckedThumbColor = MutedLavender,
                                uncheckedTrackColor = NightVoid,
                                uncheckedBorderColor = PlasmaCyan
                            )
                        )
                    }
                }
                Column(
                    Modifier.fillMaxWidth(0.85f).widthIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PlateButton("Privacy Policy", { dialog = SettingsDialog.Privacy }, Modifier.fillMaxWidth(), icon = Assets.iconShield)
                    PlateButton("Rate Plinkoria", {
                        if (!AppLinks.openRateApp(context)) dialog = SettingsDialog.LinkFailed
                    }, Modifier.fillMaxWidth(), icon = Assets.iconHeart)
                    PlateButton("Reset Progress", { dialog = SettingsDialog.ConfirmReset }, Modifier.fillMaxWidth(), icon = Assets.iconHourglass)
                }
                Text(
                    "Plinkoria v1.0 • Fully offline",
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedLavender
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        when (dialog) {
            SettingsDialog.Privacy -> ModalPanel(title = "Privacy") {
                Image(painterResource(Assets.iconShield), contentDescription = null, modifier = Modifier.size(72.dp))
                Spacer(Modifier.height(8.dp))
                Text(
                    "Plinkoria works completely offline. It does not collect, store remotely or share any personal data. " +
                        "There are no accounts, ads, analytics or trackers, and the app has no internet permission.\n\n" +
                        "Your quiz scores, cleared levels and the sound setting are saved only on this device " +
                        "and can be erased any time with Reset Progress or by uninstalling the app.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SoftWhite,
                    textAlign = TextAlign.Start
                )
                Spacer(Modifier.height(18.dp))
                PanelButtons(
                    "Full policy (browser)" to {
                        if (!AppLinks.openPrivacyPolicy(context)) dialog = SettingsDialog.LinkFailed
                    },
                    "Close" to { dialog = SettingsDialog.None }
                )
            }
            SettingsDialog.ConfirmReset -> ModalPanel(title = "Reset progress?") {
                FloatingImage(Assets.mascotD, Modifier.size(96.dp), amplitude = 4.dp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "This erases all quiz best scores and cleared Matching Pairs levels and restores default settings. " +
                        "It can't be undone.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SoftWhite,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(18.dp))
                PanelButtons(
                    "Yes, reset" to {
                        vm.resetAll()
                        dialog = SettingsDialog.ResetDone
                    },
                    "Cancel" to { dialog = SettingsDialog.None }
                )
            }
            SettingsDialog.ResetDone -> ModalPanel(title = "All clear") {
                FloatingImage(Assets.mascotA, Modifier.size(96.dp), amplitude = 4.dp)
                Spacer(Modifier.height(8.dp))
                Text("Progress has been reset.", style = MaterialTheme.typography.bodyLarge, color = PegGold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(18.dp))
                PlateButton("OK", { dialog = SettingsDialog.None }, Modifier.fillMaxWidth())
            }
            SettingsDialog.LinkFailed -> ModalPanel(title = "Can't open link") {
                Text(
                    "No app is available to open this link on your device.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SoftWhite,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(18.dp))
                PlateButton("OK", { dialog = SettingsDialog.None }, Modifier.fillMaxWidth())
            }
            SettingsDialog.None -> Unit
        }
    }
    BackHandler(enabled = dialog != SettingsDialog.None) { dialog = SettingsDialog.None }
}
