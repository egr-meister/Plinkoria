package com.plinkoria.game.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plinkoria.game.audio.Sfx
import com.plinkoria.game.ui.Assets
import com.plinkoria.game.ui.LocalAppContainer
import com.plinkoria.game.ui.theme.DeepViolet
import com.plinkoria.game.ui.theme.NightVoid
import com.plinkoria.game.ui.theme.RoyalPurple
import com.plinkoria.game.ui.theme.SoftWhite

/** Caps font scaling inside fixed-shape plates so labels stay legible without breaking the art. */
@Composable
fun CappedFontScale(max: Float = 1.3f, content: @Composable () -> Unit) {
    val d = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(d.density, d.fontScale.coerceAtMost(max)),
        content = content
    )
}

private val LabelShadow = Shadow(color = Color(0xCC12002E), blurRadius = 6f)

@Composable
fun PlateLabel(text: String, fontSize: TextUnit = 20.sp, maxLines: Int = 1) {
    Text(
        text = text,
        color = SoftWhite,
        textAlign = TextAlign.Center,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.8.sp,
            shadow = LabelShadow,
            lineHeight = fontSize * 1.15f
        )
    )
}

/** Pressable container that plays the click SFX and scales slightly when pressed. */
@Composable
private fun Modifier.pressable(enabled: Boolean, label: String, sound: Boolean, onClick: () -> Unit): Modifier {
    val container = LocalAppContainer.current
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    return this
        .scale(if (pressed) 0.96f else 1f)
        .semantics { contentDescription = label }
        .clickable(
            interactionSource = interaction,
            indication = null,
            enabled = enabled,
            role = Role.Button
        ) {
            if (sound) container.sound.play(Sfx.Click)
            onClick()
        }
}

/**
 * Simple plate button (btn_plate) — stretched to the requested width, white bold label drawn on top.
 * Height grows with content (min 56dp) so long quiz answers wrap safely.
 */
@Composable
fun PlateButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minHeight: Dp = 60.dp,
    fontSize: TextUnit = 19.sp,
    maxLines: Int = 1,
    playClick: Boolean = true,
    semanticLabel: String = text,
    @DrawableRes icon: Int? = null,
    leading: (@Composable () -> Unit)? = null,
    overlay: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .heightIn(min = minHeight)
            .alpha(if (enabled) 1f else 0.55f)
            .pressable(enabled, semanticLabel, playClick, onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(Assets.btnPlate),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        overlay()
        Row(
            Modifier.padding(start = if (icon != null || leading != null) 12.dp else 28.dp, end = 28.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leading != null) {
                leading()
                Spacer(Modifier.width(10.dp))
            }
            if (icon != null) {
                Image(painterResource(icon), contentDescription = null, modifier = Modifier.size(38.dp))
                Spacer(Modifier.width(8.dp))
            }
            Box(Modifier.weight(1f, fill = false)) {
                CappedFontScale { PlateLabel(text, fontSize, maxLines) }
            }
        }
    }
}

/**
 * Ornate framed plate (btn_menu). The frame centre is transparent, so a Compose gradient
 * is painted behind it. Keeps the art's aspect ratio; width is controlled by the caller (~0.7 screen).
 */
@Composable
fun MenuPlateButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 24.sp,
    enabled: Boolean = true,
    @DrawableRes icon: Int? = null
) {
    Box(
        modifier = modifier
            .aspectRatio(Assets.BTN_MENU_RATIO)
            .alpha(if (enabled) 1f else 0.55f)
            .pressable(enabled, text, true, onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.verticalGradient(listOf(RoyalPurple, DeepViolet, NightVoid)))
        )
        Image(
            painter = painterResource(Assets.btnMenu),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 26.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                FloatingImage(icon, Modifier.size(46.dp), amplitude = 3.dp, sway = 6f)
                Spacer(Modifier.width(8.dp))
            }
            CappedFontScale { PlateLabel(text, fontSize) }
        }
    }
}

/** Round image button (icon_back / icon_pause). ≥48dp touch target. */
@Composable
fun IconImageButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp
) {
    Box(
        modifier = modifier
            .size(size.coerceAtLeast(48.dp))
            .pressable(true, contentDescription, true, onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium.copy(shadow = LabelShadow),
        color = SoftWhite,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}
