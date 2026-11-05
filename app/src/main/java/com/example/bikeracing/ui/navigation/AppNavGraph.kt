package com.example.bikeracing.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bikeracing.data.preferences.GamePreferences
import com.example.bikeracing.data.repository.GameRepository
import com.example.bikeracing.ui.screens.CareerMapScreen
import com.example.bikeracing.ui.screens.DailyRewardScreen
import com.example.bikeracing.ui.screens.GameResultScreen
import com.example.bikeracing.ui.screens.GameScreen
import com.example.bikeracing.ui.screens.GarageScreen
import com.example.bikeracing.ui.screens.HomeScreen
import com.example.bikeracing.ui.screens.LeaderboardScreen
import com.example.bikeracing.ui.screens.LevelDetailScreen
import com.example.bikeracing.ui.screens.ModeSelectScreen
import com.example.bikeracing.ui.screens.ProfileScreen
import com.example.bikeracing.ui.screens.SettingsScreen
import com.example.bikeracing.ui.screens.ShopScreen
import com.example.bikeracing.viewmodel.GameViewModel
import com.example.bikeracing.viewmodel.GameViewModelFactory

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val prefs = remember { GamePreferences(context) }
    val repo = remember { GameRepository(prefs) }
    val factory = remember { GameViewModelFactory(repo) }
    val viewModel: GameViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start, tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start, tween(200)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End, tween(300)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(200)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End, tween(200)
            )
        },
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigate = { screen -> navController.navigate(screen.route) },
                onCareerClick = { navController.navigate(Screen.CareerMap.route) },
                onGarageClick = { navController.navigate(Screen.Garage.route) },
                onShopClick = { navController.navigate(Screen.Shop.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onLeaderboardClick = { navController.navigate(Screen.Leaderboard.route) },
                onDailyRewardClick = { navController.navigate(Screen.DailyReward.route) },
                onPlayClick = { navController.navigate(Screen.ModeSelect.route) },
            )
        }
        composable(Screen.ModeSelect.route) {
            ModeSelectScreen(
                viewModel = viewModel,
                onCareer = { navController.navigate(Screen.CareerMap.route) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Screen.CareerMap.route) {
            CareerMapScreen(
                viewModel = viewModel,
                onLevelClick = { levelId ->
                    navController.navigate(Screen.LevelDetail.createRoute(levelId))
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = Screen.LevelDetail.route,
            arguments = listOf(navArgument("levelId") { type = NavType.IntType }),
        ) { backStack ->
            val levelId = backStack.arguments?.getInt("levelId") ?: 1
            LevelDetailScreen(
                viewModel = viewModel,
                levelId = levelId,
                onPlay = { navController.navigate(Screen.Game.createRoute(levelId)) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = Screen.Game.route,
            arguments = listOf(navArgument("levelId") { type = NavType.IntType }),
        ) { backStack ->
            val levelId = backStack.arguments?.getInt("levelId") ?: 1
            GameScreen(
                viewModel = viewModel,
                levelId = levelId,
                onRaceFinished = {
                    navController.navigate(Screen.GameResult.createRoute(levelId)) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                },
                onQuit = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
            )
        }
        composable(
            route = Screen.GameResult.route,
            arguments = listOf(navArgument("levelId") { type = NavType.IntType }),
        ) { backStack ->
            val levelId = backStack.arguments?.getInt("levelId") ?: 1
            GameResultScreen(
                viewModel = viewModel,
                levelId = levelId,
                onRetry = {
                    navController.navigate(Screen.Game.createRoute(levelId)) {
                        popUpTo(Screen.GameResult.route) { inclusive = true }
                    }
                },
                onNextLevel = { nextId ->
                    navController.navigate(Screen.Game.createRoute(nextId)) {
                        popUpTo(Screen.GameResult.route) { inclusive = true }
                    }
                },
                onHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onGarage = { navController.navigate(Screen.Garage.route) },
            )
        }
        composable(Screen.Garage.route) {
            GarageScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable(Screen.Shop.route) {
            ShopScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable(Screen.Profile.route) {
            ProfileScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.DailyReward.route) {
            DailyRewardScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}
