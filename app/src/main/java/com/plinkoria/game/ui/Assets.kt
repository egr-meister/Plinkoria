package com.plinkoria.game.ui

import androidx.annotation.DrawableRes
import com.plinkoria.game.R

/**
 * The ONLY place drawables are referenced by the UI.
 * Replace files in res/drawable-nodpi with the same names to re-skin the game — no code change.
 */
object Assets {
    @DrawableRes val bgMain = R.drawable.bg_main          // opaque JPG background
    @DrawableRes val logo = R.drawable.logo

    // Mascots / heroes
    @DrawableRes val mascotA = R.drawable.mascot_a        // robot buddy (hero_1)
    @DrawableRes val mascotB = R.drawable.mascot_b        // neon pilot, standing (hero_4)
    @DrawableRes val mascotC = R.drawable.mascot_c        // neon pilot, presenting pose (hero_2)
    @DrawableRes val mascotD = R.drawable.mascot_d        // crystal orb spirit (hero_5)
    @DrawableRes val orb = R.drawable.orb                 // eyed peg orb (hero_3)

    // Plates & buttons
    @DrawableRes val btnMenu = R.drawable.btn_menu        // ornate framed plate (transparent centre)
    @DrawableRes val btnPlate = R.drawable.btn_plate      // simple plate
    @DrawableRes val plateRound = R.drawable.plate_round  // level bubbles + card backs
    @DrawableRes val iconBack = R.drawable.icon_back
    @DrawableRes val iconPause = R.drawable.icon_pause

    /** Intrinsic width/height of the plates, used with Modifier.aspectRatio. */
    const val BTN_MENU_RATIO = 900f / 354f
    const val BTN_PLATE_RATIO = 900f / 185f

    /** All 19 themed items. Matching Pairs draws its deck from the whole set. */
    @DrawableRes
    val items: List<Int> = listOf(
        R.drawable.item_01, R.drawable.item_02, R.drawable.item_03, R.drawable.item_04,
        R.drawable.item_05, R.drawable.item_06, R.drawable.item_07, R.drawable.item_08,
        R.drawable.item_09, R.drawable.item_10, R.drawable.item_11, R.drawable.item_12,
        R.drawable.item_13, R.drawable.item_14, R.drawable.item_15, R.drawable.item_16,
        R.drawable.item_17, R.drawable.item_18, R.drawable.item_19
    )

    /** Text alternatives for the items (accessibility), same order as [items]. */
    val itemNames: List<String> = listOf(
        "Key", "Lightning", "Shield", "Gift", "Heart", "Bell",
        "Puzzle piece", "Glossy ball", "Star", "Rocket", "Trophy", "Crystal",
        "Golden ring", "Gear", "Hourglass", "Padlock", "Neon ring", "Target", "Magnet"
    )

    // Named UI icons (aliases into the item set)
    @DrawableRes val iconKey = R.drawable.item_01
    @DrawableRes val iconLightning = R.drawable.item_02
    @DrawableRes val iconShield = R.drawable.item_03
    @DrawableRes val iconGift = R.drawable.item_04
    @DrawableRes val iconHeart = R.drawable.item_05
    @DrawableRes val iconBell = R.drawable.item_06
    @DrawableRes val iconPuzzle = R.drawable.item_07
    @DrawableRes val iconBall = R.drawable.item_08
    @DrawableRes val iconStar = R.drawable.item_09
    @DrawableRes val iconRocket = R.drawable.item_10
    @DrawableRes val iconTrophy = R.drawable.item_11
    @DrawableRes val iconCrystal = R.drawable.item_12
    @DrawableRes val iconRingGold = R.drawable.item_13
    @DrawableRes val iconGear = R.drawable.item_14
    @DrawableRes val iconHourglass = R.drawable.item_15
    @DrawableRes val iconLock = R.drawable.item_16
    @DrawableRes val iconRingNeon = R.drawable.item_17
    @DrawableRes val iconTarget = R.drawable.item_18
    @DrawableRes val iconMagnet = R.drawable.item_19

    @DrawableRes
    fun item(index: Int): Int = items[index.mod(items.size)]

    fun itemName(index: Int): String = itemNames[index.mod(itemNames.size)]
}
