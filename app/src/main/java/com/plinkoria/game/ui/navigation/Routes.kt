package com.plinkoria.game.ui.navigation

import kotlinx.serialization.Serializable

/** Type-safe Navigation Compose routes. Arguments are validated by each destination. */
@Serializable data object MainRoute
@Serializable data object MenuRoute
@Serializable data object QuizCategoriesRoute
@Serializable data class QuizRoute(val categoryId: String = "")
@Serializable data class QuizResultRoute(val categoryId: String = "", val score: Int = -1)
@Serializable data object LevelsRoute
@Serializable data class PairsRoute(val level: Int = -1)
@Serializable data class PairsResultRoute(val level: Int = -1, val matched: Int = -1, val won: Boolean = false)
@Serializable data object ResultsRoute
@Serializable data object SettingsRoute
@Serializable data object RulesRoute
