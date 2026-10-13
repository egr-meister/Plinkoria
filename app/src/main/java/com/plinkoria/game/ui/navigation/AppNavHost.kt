package com.plinkoria.game.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.plinkoria.game.content.QuizContent
import com.plinkoria.game.logic.GameLogic
import com.plinkoria.game.ui.components.FallbackScreen
import com.plinkoria.game.ui.screens.LevelsScreen
import com.plinkoria.game.ui.screens.MainScreen
import com.plinkoria.game.ui.screens.MenuScreen
import com.plinkoria.game.ui.screens.PairsResultScreen
import com.plinkoria.game.ui.screens.PairsScreen
import com.plinkoria.game.ui.screens.QuizCategoryScreen
import com.plinkoria.game.ui.screens.QuizResultScreen
import com.plinkoria.game.ui.screens.QuizScreen
import com.plinkoria.game.ui.screens.ResultsScreen
import com.plinkoria.game.ui.screens.RulesScreen
import com.plinkoria.game.ui.screens.SettingsScreen

/** Decodes typed args without ever throwing. */
private inline fun <reified T : Any> NavBackStackEntry.safeRoute(): T? =
    try { toRoute<T>() } catch (e: Exception) { null }

/** Pops back if possible; otherwise lands on the Menu (never leaves the user stranded). */
private fun NavHostController.safeBack() {
    if (!popBackStack()) {
        navigate(MenuRoute) { launchSingleTop = true }
    }
}

private fun NavHostController.goToMenu() {
    val popped = popBackStack(MenuRoute, inclusive = false)
    if (!popped) navigate(MenuRoute) { popUpTo(MainRoute) { inclusive = false }; launchSingleTop = true }
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = MainRoute,
        enterTransition = { fadeIn(tween(220)) },
        exitTransition = { fadeOut(tween(180)) },
        popEnterTransition = { fadeIn(tween(220)) },
        popExitTransition = { fadeOut(tween(180)) }
    ) {
        composable<MainRoute> {
            MainScreen(onStart = { navController.navigate(MenuRoute) { launchSingleTop = true } })
        }
        composable<MenuRoute> {
            MenuScreen(
                onBack = { navController.safeBack() },
                onQuiz = { navController.navigate(QuizCategoriesRoute) { launchSingleTop = true } },
                onPairs = { navController.navigate(LevelsRoute) { launchSingleTop = true } },
                onResults = { navController.navigate(ResultsRoute) { launchSingleTop = true } },
                onSettings = { navController.navigate(SettingsRoute) { launchSingleTop = true } },
                onRules = { navController.navigate(RulesRoute) { launchSingleTop = true } }
            )
        }
        composable<QuizCategoriesRoute> {
            QuizCategoryScreen(
                onBack = { navController.safeBack() },
                onCategory = { id -> navController.navigate(QuizRoute(id)) { launchSingleTop = true } }
            )
        }
        composable<QuizRoute> { entry ->
            val route = entry.safeRoute<QuizRoute>()
            val category = QuizContent.category(route?.categoryId)
            if (route == null || category == null) {
                FallbackScreen("That quiz level doesn't exist.") { navController.safeBack() }
            } else {
                QuizScreen(
                    categoryId = category.id,
                    onFinished = { score ->
                        navController.navigate(QuizResultRoute(category.id, score)) {
                            popUpTo<QuizRoute> { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onMenu = { navController.goToMenu() }
                )
            }
        }
        composable<QuizResultRoute> { entry ->
            val route = entry.safeRoute<QuizResultRoute>()
            val category = QuizContent.category(route?.categoryId)
            if (route == null || category == null || route.score !in 0..category.questions.size) {
                FallbackScreen("We couldn't find those quiz results.") { navController.safeBack() }
            } else {
                QuizResultScreen(
                    categoryTitle = category.title,
                    difficulty = category.difficulty,
                    score = route.score,
                    total = category.questions.size,
                    onContinue = { navController.safeBack() },
                    onRestart = {
                        navController.navigate(QuizRoute(category.id)) {
                            popUpTo<QuizResultRoute> { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onMenu = { navController.goToMenu() }
                )
            }
        }
        composable<LevelsRoute> {
            LevelsScreen(
                onBack = { navController.safeBack() },
                onLevel = { level -> navController.navigate(PairsRoute(level)) { launchSingleTop = true } },
                onRules = { navController.navigate(RulesRoute) { launchSingleTop = true } }
            )
        }
        composable<PairsRoute> { entry ->
            val route = entry.safeRoute<PairsRoute>()
            if (route == null || GameLogic.levelConfig(route.level) == null) {
                FallbackScreen("That level doesn't exist.") { navController.safeBack() }
            } else {
                PairsScreen(
                    level = route.level,
                    onFinished = { matched, won ->
                        navController.navigate(PairsResultRoute(route.level, matched, won)) {
                            popUpTo<PairsRoute> { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onMenu = { navController.goToMenu() },
                    onBack = { navController.safeBack() }
                )
            }
        }
        composable<PairsResultRoute> { entry ->
            val route = entry.safeRoute<PairsResultRoute>()
            val config = route?.let { GameLogic.levelConfig(it.level) }
            if (route == null || config == null || route.matched !in 0..config.pairs) {
                FallbackScreen("We couldn't find those level results.") { navController.safeBack() }
            } else {
                val next = GameLogic.nextLevel(route.level)
                PairsResultScreen(
                    level = route.level,
                    matched = route.matched,
                    totalPairs = config.pairs,
                    won = route.won,
                    hasNextLevel = route.won && next != null,
                    onContinue = {
                        if (route.won && next != null) {
                            navController.navigate(PairsRoute(next)) {
                                popUpTo<PairsResultRoute> { inclusive = true }
                                launchSingleTop = true
                            }
                        } else {
                            navController.safeBack()
                        }
                    },
                    onRestart = {
                        navController.navigate(PairsRoute(route.level)) {
                            popUpTo<PairsResultRoute> { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onMenu = { navController.goToMenu() }
                )
            }
        }
        composable<ResultsRoute> { ResultsScreen(onBack = { navController.safeBack() }) }
        composable<SettingsRoute> { SettingsScreen(onBack = { navController.safeBack() }) }
        composable<RulesRoute> { RulesScreen(onBack = { navController.safeBack() }) }
    }
}
