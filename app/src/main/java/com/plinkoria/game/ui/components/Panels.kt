package com.plinkoria.game.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.plinkoria.game.ui.Assets
import com.plinkoria.game.ui.theme.DeepViolet
import com.plinkoria.game.ui.theme.MutedLavender
import com.plinkoria.game.ui.theme.NeonMagenta
import com.plinkoria.game.ui.theme.NightVoid
import com.plinkoria.game.ui.theme.PegGold
import com.plinkoria.game.ui.theme.PlasmaCyan
import com.plinkoria.game.ui.theme.RoyalPurple
import com.plinkoria.game.ui.theme.SoftWhite

val PanelShape = RoundedCornerShape(28.dp)

/** Full-screen themed background (bg_main) + safe-drawing insets for content. */
@Composable
fun ScreenBackground(
    modifier: Modifier = Modifier,
    applyInsets: Boolean = true,
    sparkles: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(modifier.fillMaxSize().background(DeepViolet)) {
        Image(
            painter = painterResource(Assets.bgMain),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0x33120429), Color(0x00120429), Color(0x80120429))))
        )
        if (sparkles) SparkleField()
        Box(if (applyInsets) Modifier.fillMaxSize().safeDrawingPadding() else Modifier.fillMaxSize()) {
            content()
        }
    }
}

/** Compose-drawn panel: rounded rect, purple gradient fill, magenta→cyan accent border. */
@Composable
fun NeonPanel(
    modifier: Modifier = Modifier,
    contentPadding: Dp = 22.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(PanelShape)
            .background(Brush.verticalGradient(listOf(RoyalPurple, DeepViolet, NightVoid)))
            .border(2.5.dp, Brush.linearGradient(listOf(NeonMagenta, PlasmaCyan, PegGold)), PanelShape)
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

/** Modal overlay: dim scrim that swallows touches + centred NeonPanel (pause / results / rules). */
@Composable
fun ModalPanel(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xCC0A0220))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
            .safeDrawingPadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        NeonPanel(
            Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = PegGold,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() }
            )
            Spacer(Modifier.height(18.dp))
            content()
        }
    }
}

/** Header row: back button + centred title + optional trailing slot. */
@Composable
fun TopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = { Spacer(Modifier.size(52.dp)) }
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconImageButton(Assets.iconBack, "Back", onBack)
        SectionTitle(title, Modifier.weight(1f).padding(horizontal = 8.dp).semantics { heading() })
        trailing()
    }
}

/** Friendly fallback for invalid routes / missing data — never crash, always offer Back. */
@Composable
fun FallbackScreen(message: String, onBack: () -> Unit) {
    ScreenBackground {
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NeonPanel(Modifier.fillMaxWidth().widthIn(max = 440.dp)) {
                FloatingImage(Assets.mascotD, Modifier.size(120.dp))
                Spacer(Modifier.height(12.dp))
                Text("Oops — wrong chute!", style = MaterialTheme.typography.headlineSmall, color = PegGold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                Text(message, style = MaterialTheme.typography.bodyLarge, color = SoftWhite, textAlign = TextAlign.Center)
                Spacer(Modifier.height(20.dp))
                PlateButton("Back", onBack, Modifier.fillMaxWidth())
            }
        }
    }
}

/** Gradient progress bar (quiz progress / pairs timer). Not color-only: callers also show text. */
@Composable
fun NeonProgressBar(progress: Float, modifier: Modifier = Modifier, colors: List<Color> = listOf(NeonMagenta, PlasmaCyan)) {
    val p = progress.coerceIn(0f, 1f)
    Box(
        modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0x66120429))
            .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(50))
    ) {
        Box(
            Modifier
                .fillMaxWidth(p)
                .height(14.dp)
                .clip(RoundedCornerShape(50))
                .background(Brush.horizontalGradient(colors))
        )
    }
}

/** Small drawn check / cross glyphs, so answer state never relies on color alone. */
@Composable
fun StatusGlyph(correct: Boolean, color: Color, modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = w * 0.16f, cap = StrokeCap.Round)
        if (correct) {
            drawLine(color, Offset(w * 0.15f, h * 0.55f), Offset(w * 0.42f, h * 0.8f), stroke.width, StrokeCap.Round)
            drawLine(color, Offset(w * 0.42f, h * 0.8f), Offset(w * 0.86f, h * 0.22f), stroke.width, StrokeCap.Round)
        } else {
            drawLine(color, Offset(w * 0.2f, h * 0.2f), Offset(w * 0.8f, h * 0.8f), stroke.width, StrokeCap.Round)
            drawLine(color, Offset(w * 0.8f, h * 0.2f), Offset(w * 0.2f, h * 0.8f), stroke.width, StrokeCap.Round)
        }
    }
}

/** Drawn padlock glyph for locked levels. */
@Composable
fun LockGlyph(modifier: Modifier = Modifier.size(26.dp), color: Color = MutedLavender) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.25f, h * 0.08f),
            size = androidx.compose.ui.geometry.Size(w * 0.5f, h * 0.6f),
            style = Stroke(width = w * 0.12f)
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.14f, h * 0.42f),
            size = androidx.compose.ui.geometry.Size(w * 0.72f, h * 0.52f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.1f)
        )
    }
}

/** Two-button row helper used by dialogs/panels. */
@Composable
fun PanelButtons(vararg buttons: Pair<String, () -> Unit>) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        buttons.forEach { (label, action) -> PlateButton(label, action, Modifier.fillMaxWidth()) }
    }
}
