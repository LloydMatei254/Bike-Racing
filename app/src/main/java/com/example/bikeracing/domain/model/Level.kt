package com.example.bikeracing.domain.model

data class Level(
    val id: Int,
    val number: Int,
    val worldId: Int,
    val name: String,
    val description: String,
    val environment: Environment,
    val trackLength: Float,       // km
    val timeLimit: Int,           // seconds, 0 = no limit
    val opponentCount: Int,
    val trafficDensity: TrafficDensity,
    val opponentDifficulty: Difficulty,
    val starObjectives: StarObjectives,
    val rewardCoins: Int,
    val rewardXp: Int,
    val rewardGems: Int = 0,
    val unlockRequires: Int = 0,  // level id that must be completed first
)

data class StarObjectives(
    val oneStar: LevelObjective,
    val twoStar: LevelObjective,
    val threeStar: LevelObjective,
)

data class LevelObjective(
    val description: String,
    val targetPosition: Int = 0,   // finish position
    val targetTimeSeconds: Int = 0,
    val targetScore: Int = 0,
)

enum class Environment {
    CITY, HIGHWAY, DESERT, MOUNTAIN, NIGHT, CHAMPIONSHIP
}

enum class Difficulty {
    BEGINNER, EASY, MEDIUM, HARD, EXPERT, MASTER
}

enum class TrafficDensity {
    NONE, LIGHT, MEDIUM, HEAVY, EXTREME
}

data class LevelResult(
    val levelId: Int,
    val completed: Boolean,
    val starsEarned: Int,        // 0-3
    val bestTimeSeconds: Int,
    val bestPosition: Int,
    val highScore: Int,
    val coinsEarned: Int,
    val xpEarned: Int,
)
