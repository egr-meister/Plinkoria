package com.plinkoria.game.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.plinkoria.game.ui.Assets
import com.plinkoria.game.ui.theme.NeonMagenta
import com.plinkoria.game.ui.theme.PegGold
import com.plinkoria.game.ui.theme.PlasmaCyan
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/** Decorative image that gently bobs and sways (mascots, floating items). */
@Composable
fun FloatingImage(
    @DrawableRes res: Int,
    modifier: Modifier = Modifier,
    amplitude: Dp = 8.dp,
    periodMs: Int = 2600,
    sway: Float = 3f,
    phase: Float = 0f
) {
    val transition = rememberInfiniteTransition(label = "float")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(periodMs, easing = LinearEasing), RepeatMode.Restart),
        label = "floatT"
    )
    val angle = ((t + phase) % 1f) * 2f * PI.toFloat()
    Image(
        painter = painterResource(res),
        contentDescription = null,
        modifier = modifier.graphicsLayer {
            translationY = sin(angle) * amplitude.toPx()
            rotationZ = cos(angle) * sway
        }
    )
}

/** Soft breathing glow + scale, used for logos and hero highlights. */
@Composable
fun PulsingImage(
    @DrawableRes res: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    minScale: Float = 0.97f,
    maxScale: Float = 1.03f,
    periodMs: Int = 1800
) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val s by transition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(tween(periodMs, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseS"
    )
    Image(
        painter = painterResource(res),
        contentDescription = contentDescription,
        modifier = modifier.graphicsLayer {
            scaleX = s
            scaleY = s
        }
    )
}

/** Slowly spinning image (rings, halos). */
@Composable
fun SpinningImage(@DrawableRes res: Int, modifier: Modifier = Modifier, periodMs: Int = 9000, alpha: Float = 1f) {
    val transition = rememberInfiniteTransition(label = "spin")
    val r by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(periodMs, easing = LinearEasing), RepeatMode.Restart),
        label = "spinR"
    )
    Image(
        painter = painterResource(res),
        contentDescription = null,
        modifier = modifier
            .alpha(alpha)
            .graphicsLayer { rotationZ = r }
    )
}

private data class Sparkle(val x: Float, val y: Float, val r: Float, val phase: Float, val color: Color)

/** Twinkling neon sparkles drawn over the background (cheap: one Canvas, ~28 dots). */
@Composable
fun SparkleField(modifier: Modifier = Modifier, count: Int = 28, seed: Int = 7) {
    val sparkles = remember(count, seed) {
        val rnd = Random(seed)
        val colors = listOf(PegGold, PlasmaCyan, NeonMagenta, Color.White)
        List(count) {
            Sparkle(rnd.nextFloat(), rnd.nextFloat(), 1.5f + rnd.nextFloat() * 3.5f, rnd.nextFloat(), colors[rnd.nextInt(colors.size)])
        }
    }
    val transition = rememberInfiniteTransition(label = "sparkle")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3600, easing = LinearEasing), RepeatMode.Restart),
        label = "sparkleT"
    )
    Canvas(modifier.fillMaxSize()) {
        sparkles.forEach { s ->
            val a = 0.5f + 0.5f * sin(((t + s.phase) % 1f) * 2f * PI.toFloat())
            val c = Offset(s.x * size.width, s.y * size.height)
            drawCircle(
                brush = Brush.radialGradient(listOf(s.color.copy(alpha = 0.55f * a), Color.Transparent), center = c, radius = s.r * 4f),
                radius = s.r * 4f,
                center = c
            )
            drawCircle(color = s.color.copy(alpha = 0.9f * a), radius = s.r * 0.6f, center = c)
        }
    }
}

/** Celebration: themed item icons burst outward and fall (results screens). */
@Composable
fun ItemBurst(modifier: Modifier = Modifier, icons: List<Int> = Assets.items.take(12), count: Int = 14) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) { progress.animateTo(1f, tween(1700, easing = FastOutSlowInEasing)) }
    val pieces = remember(count) {
        val rnd = Random(42)
        List(count) { i ->
            Triple(
                (i.toFloat() / count) * 2f * PI.toFloat() + rnd.nextFloat() * 0.4f,
                0.55f + rnd.nextFloat() * 0.45f,
                icons[rnd.nextInt(icons.size)]
            )
        }
    }
    val p = progress.value
    Box(modifier.fillMaxSize().clearAndSetSemantics { }, contentAlignment = Alignment.Center) {
        pieces.forEach { (angle, dist, icon) ->
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .size(34.dp)
                    .graphicsLayer {
                        val reach = size.width * 4.2f * dist * p
                        translationX = cos(angle) * reach
                        translationY = sin(angle) * reach + p * p * size.height * 5f
                        rotationZ = p * 540f * if (angle > PI) 1f else -1f
                        alpha = ((1f - p) * 1.4f).coerceIn(0f, 1f)
                        scaleX = 0.6f + p * 0.6f
                        scaleY = 0.6f + p * 0.6f
                    }
            )
        }
    }
}

/** 1–3 star rating using the star asset; unearned stars are dimmed AND described in text. */
@Composable
fun StarRating(earned: Int, modifier: Modifier = Modifier, max: Int = 3, size: Dp = 34.dp) {
    Row(
        modifier.clearAndSetSemantics { contentDescription = "$earned of $max stars" },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(max) { i ->
            Image(
                painter = painterResource(Assets.iconStar),
                contentDescription = null,
                modifier = Modifier
                    .size(size)
                    .alpha(if (i < earned) 1f else 0.25f)
            )
        }
    }
}

/** Small icon + content row, used for stats (timer, pairs, score). */
@Composable
fun IconBadge(@DrawableRes icon: Int, modifier: Modifier = Modifier, size: Dp = 28.dp, content: @Composable () -> Unit) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Image(painterResource(icon), contentDescription = null, modifier = Modifier.size(size))
        content()
    }
}
