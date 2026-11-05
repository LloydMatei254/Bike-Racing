package com.example.bikeracing.domain.model

data class PlayerProfile(
    val name: String = "Racer",
    val level: Int = 1,
    val xp: Int = 0,
    val coins: Int = 500,
    val gems: Int = 10,
    val totalRaces: Int = 0,
    val totalWins: Int = 0,
    val totalDistanceKm: Float = 0f,
    val bestSpeedKmh: Float = 0f,
    val selectedMotorcycleId: String = "bike_01",
    val ownedMotorcycleIds: List<String> = listOf("bike_01"),
    val lastDailyRewardDay: Long = 0L,
    val currentStreak: Int = 0,
) {
    val xpForNextLevel: Int get() = (level * 1000)
    val progressToNextLevel: Float get() = (xp % xpForNextLevel).toFloat() / xpForNextLevel
}

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val category: AchievementCategory,
    val targetValue: Int,
    val iconResName: String = "",
    val rewardCoins: Int = 0,
)

enum class AchievementCategory {
    SPEED, RACING, CAREER, COLLECTION
}

data class AchievementProgress(
    val achievementId: String,
    val currentValue: Int,
    val unlocked: Boolean,
)

data class GameSettings(
    val graphicsQuality: GraphicsQuality = GraphicsQuality.MEDIUM,
    val musicVolume: Float = 0.7f,
    val sfxVolume: Float = 0.9f,
    val engineVolume: Float = 0.8f,
    val controlType: ControlType = ControlType.TOUCH,
    val steeringSensitivity: Float = 0.5f,
    val vibrationEnabled: Boolean = true,
    val cameraShake: Boolean = true,
    val showFps: Boolean = false,
    val targetFps: Int = 60,
)

enum class GraphicsQuality { LOW, MEDIUM, HIGH }
enum class ControlType { TOUCH, TILT, JOYSTICK }

data class GameMode(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
)
