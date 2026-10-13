package com.plinkoria.game.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plinkoria.game.ui.Assets
import com.plinkoria.game.ui.components.FloatingImage
import com.plinkoria.game.ui.components.MenuPlateButton
import com.plinkoria.game.ui.components.PulsingImage
import com.plinkoria.game.ui.components.SpinningImage
import com.plinkoria.game.ui.components.ScreenBackground
import com.plinkoria.game.ui.theme.MutedLavender
import com.plinkoria.game.ui.theme.NeonMagenta
import com.plinkoria.game.ui.theme.PegGold
import com.plinkoria.game.ui.theme.PlasmaCyan
import kotlin.math.abs

/**
 * "Peg Cascade" home: the logo hangs at the top of a triangular peg board; the orb mascot
 * zig-zags down the pegs and drops straight into the Start plate. The pilot (mascot B) stands
 * cropped at the bottom-left edge, the robot (mascot A) peeks from the right — an asymmetric
 * arcade-cabinet composition rather than a centred dashboard stack.
 */
@Composable
fun MainScreen(onStart: () -> Unit) {
    ScreenBackground {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val w = maxWidth
            val h = maxHeight
            val boardTop = h * 0.22f
            val boardHeight = h * 0.40f

            PegBoard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(boardHeight)
                    .offset(y = boardTop)
            )

            FallingOrb(
                boardTop = boardTop,
                boardHeight = boardHeight,
                width = w,
                orbSize = (w * 0.15f).coerceIn(44.dp, 72.dp)
            )

            // Floating themed items around the board edges.
            FloatingImage(Assets.iconStar, Modifier.offset(x = w * 0.04f, y = h * 0.25f).size(w * 0.12f), periodMs = 3000, phase = 0.1f)
            FloatingImage(Assets.iconCrystal, Modifier.offset(x = w * 0.84f, y = h * 0.23f).size(w * 0.11f), periodMs = 3400, phase = 0.5f)
            FloatingImage(Assets.iconHeart, Modifier.offset(x = w * 0.06f, y = h * 0.44f).size(w * 0.10f), periodMs = 2800, phase = 0.3f)
            FloatingImage(Assets.iconLightning, Modifier.offset(x = w * 0.86f, y = h * 0.40f).size(w * 0.09f), periodMs = 3200, phase = 0.8f)
            FloatingImage(Assets.iconGift, Modifier.offset(x = w * 0.42f, y = h * 0.67f).size(w * 0.10f), periodMs = 3600, phase = 0.6f)

            // Mascot B — tall pilot, cropped by the bottom-left edge.
            FloatingImage(
                Assets.mascotB,
                Modifier
                    .align(Alignment.BottomStart)
                    .height(h * 0.46f)
                    .offset(x = -(w * 0.06f), y = h * 0.02f),
                amplitude = 4.dp,
                periodMs = 3800,
                sway = 1f
            )

            // Mascot A — robot buddy waving from the right, tilted.
            FloatingImage(
                Assets.mascotA,
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = w * 0.08f, y = h * 0.50f)
                    .size(w * 0.40f)
                    .rotate(-10f),
                amplitude = 10.dp,
                periodMs = 2400,
                sway = 4f
            )

            // Logo with a slowly spinning neon ring halo behind it.
            SpinningImage(
                Assets.iconRingNeon,
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = -(w * 0.06f))
                    .size(w * 0.46f),
                periodMs = 16000,
                alpha = 0.35f
            )
            PulsingImage(
                Assets.logo,
                contentDescription = "Plinkoria",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
                    .width(w * 0.84f)
                    .rotate(-3f)
            )

            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 28.dp)
                    .width(w * 0.66f)
            ) {
                MenuPlateButton(text = "START", onClick = onStart, modifier = Modifier.fillMaxWidth(), fontSize = 28.sp, icon = Assets.iconRocket)
            }

            Text(
                text = "Drop in • Quiz & Matching Pairs",
                style = MaterialTheme.typography.labelMedium,
                color = MutedLavender,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 8.dp)
            )
        }
    }
}

/** Triangle of glowing pegs, drawn in Compose. */
@Composable
private fun PegBoard(modifier: Modifier) {
    Canvas(modifier) {
        val rows = 7
        val rowGap = size.height / (rows + 1)
        val colGap = size.width / 9f
        for (r in 0 until rows) {
            val count = r + 3
            val y = rowGap * (r + 1)
            val startX = size.width / 2f - colGap * (count - 1) / 2f
            for (c in 0 until count) {
                val x = startX + colGap * c
                val color = if ((r + c) % 3 == 0) PlasmaCyan else PegGold
                drawCircle(
                    brush = Brush.radialGradient(listOf(color.copy(alpha = 0.45f), Color.Transparent), center = Offset(x, y), radius = 18f),
                    radius = 18f,
                    center = Offset(x, y)
                )
                drawCircle(color = color, radius = 6.5f, center = Offset(x, y))
                drawCircle(color = Color.White.copy(alpha = 0.7f), radius = 2.2f, center = Offset(x - 2f, y - 2f))
            }
        }
        // Guide rails toward the Start slot.
        drawLine(
            brush = Brush.verticalGradient(listOf(Color.Transparent, NeonMagenta.copy(alpha = 0.6f))),
            start = Offset(size.width * 0.56f, size.height * 0.9f),
            end = Offset(size.width * 0.62f, size.height * 1.05f),
            strokeWidth = 4f
        )
    }
}

/** The orb bounces left/right from peg to peg, then drops into the Start plate. Loops. */
@Composable
private fun FallingOrb(boardTop: Dp, boardHeight: Dp, width: Dp, orbSize: Dp) {
    val transition = rememberInfiniteTransition(label = "orb")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4200, easing = LinearEasing), RepeatMode.Restart),
        label = "orbT"
    )
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4200, easing = LinearEasing), RepeatMode.Restart),
        label = "orbSpin"
    )
    // Zig-zag: x swings between pegs while y progresses downward with small hops.
    val steps = 7
    val phase = t * steps
    val hop = 1f - abs((phase % 1f) * 2f - 1f) // 0→1→0 within each step
    val dir = if (phase.toInt() % 2 == 0) 1f else -1f
    val drift = 0.62f * t                      // trend toward the Start slot (right side)
    val xFrac = (0.5f + drift * 0.25f + dir * 0.06f * hop).coerceIn(0.1f, 0.85f)
    val yPos = boardTop - orbSize + (boardHeight + orbSize * 1.6f) * t - orbSize * 0.25f * hop
    val alpha = if (t > 0.94f) (1f - t) / 0.06f else if (t < 0.04f) t / 0.04f else 1f

    Image(
        painter = painterResource(Assets.iconRingGold),
        contentDescription = null,
        modifier = Modifier
            .offset(x = width * xFrac - orbSize * 0.7f, y = yPos - orbSize * 0.2f)
            .size(orbSize * 1.4f)
            .graphicsLayer {
                rotationZ = -spin
                this.alpha = alpha * 0.55f
            }
    )
    Image(
        painter = painterResource(Assets.orb),
        contentDescription = null,
        modifier = Modifier
            .offset(x = width * xFrac - orbSize / 2, y = yPos)
            .size(orbSize)
            .graphicsLayer {
                rotationZ = spin
                this.alpha = alpha
            }
    )
}
