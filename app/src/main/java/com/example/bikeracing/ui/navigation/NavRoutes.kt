package com.example.bikeracing.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ModeSelect : Screen("mode_select")
    object CareerMap : Screen("career_map")
    object LevelDetail : Screen("level_detail/{levelId}") {
        fun createRoute(levelId: Int) = "level_detail/$levelId"
    }
    object Game : Screen("game/{levelId}") {
        fun createRoute(levelId: Int) = "game/$levelId"
    }
    object GameResult : Screen("game_result/{levelId}") {
        fun createRoute(levelId: Int) = "game_result/$levelId"
    }
    object Garage : Screen("garage")
    object Shop : Screen("shop")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
    object Leaderboard : Screen("leaderboard")
    object DailyReward : Screen("daily_reward")
    object Achievements : Screen("achievements")
}
