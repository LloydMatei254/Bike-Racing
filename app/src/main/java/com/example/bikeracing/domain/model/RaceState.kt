package com.example.bikeracing.domain.model

data class RaceState(
    val phase: RacePhase = RacePhase.COUNTDOWN,
    val elapsedSeconds: Float = 0f,
    val playerPosition: Int = 1,
    val totalRacers: Int = 4,
    val currentLap: Int = 1,
    val totalLaps: Int = 3,
    val distanceTravelledKm: Float = 0f,
    val trackLengthKm: Float = 2f,
    val speedKmh: Float = 0f,
    val maxSpeedKmh: Float = 0f,
    val rpm: Float = 0f,
    val currentGear: Int = 1,
    val nitroAmount: Float = 1f,     // 0.0 to 1.0
    val isNitroActive: Boolean = false,
    val score: Int = 0,
    val overtakes: Int = 0,
    val countdownValue: Int = 3,
    val isPaused: Boolean = false,
    val coins: Int = 0,
    val xp: Int = 0,
    // Bike pose — needed by renderers
    val leanAngle: Float = 0f,       // degrees, negative = left
    val lateralPosition: Float = 0f, // -1..1 (track left/right)
)

enum class RacePhase {
    COUNTDOWN, RACING, PAUSED, FINISHED, CRASHED
}

data class RaceResult(
    val levelId: Int,
    val finalPosition: Int,
    val totalRacers: Int,
    val raceTimeSeconds: Float,
    val maxSpeedKmh: Float,
    val distanceKm: Float,
    val overtakes: Int,
    val coinsEarned: Int,
    val xpEarned: Int,
    val starsEarned: Int,
    val newUnlocks: List<String> = emptyList(),
    val isPersonalBest: Boolean = false,
)

data class Opponent(
    val id: String,
    val name: String,
    val motorcycleId: String,
    val difficulty: Difficulty,
    val position: Float,     // distance along track 0..1
    val lane: Int,           // -1, 0, 1
    val speedKmh: Float,
    val isActive: Boolean = true,
)
