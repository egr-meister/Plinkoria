package com.plinkoria.game.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.plinkoria.game.ui.Assets

/** Hero artwork per quiz difficulty: Easy = robot, Medium = eyed orb, Hard = crystal orb spirit. */
fun heroForDifficulty(difficulty: Int): Int = when (difficulty) {
    1 -> Assets.mascotA
    2 -> Assets.orb
    else -> Assets.mascotD
}

fun difficultyLabel(difficulty: Int): String = when (difficulty) {
    1 -> "Easy"
    2 -> "Medium"
    else -> "Hard"
}

/** Lightning bolts = difficulty (1–3). Described in text for screen readers. */
@Composable
fun DifficultyMeter(difficulty: Int, modifier: Modifier = Modifier, size: Dp = 22.dp) {
    Row(
        modifier.clearAndSetSemantics { contentDescription = "Difficulty: ${difficultyLabel(difficulty)}" },
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { i ->
            Image(
                painterResource(Assets.iconLightning),
                contentDescription = null,
                modifier = Modifier.size(size).alpha(if (i < difficulty) 1f else 0.22f)
            )
        }
    }
}
