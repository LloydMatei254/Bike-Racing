package com.example.bikeracing.game.ai

import com.example.bikeracing.domain.model.Difficulty
import com.example.bikeracing.domain.model.Opponent
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import kotlin.random.Random

data class AIBikeState(
    val id: String,
    val name: String,
    val difficulty: Difficulty,
    var distanceM: Float = 0f,
    var lane: Float = 0f,          // -1..1
    var speedKmh: Float = 0f,
    var targetLane: Float = 0f,
    var laneChangeTimer: Float = 0f,
    var isActive: Boolean = true,
    val baseMaxSpeed: Float = 150f,
    val aggression: Float = 0.5f,  // 0..1
)

class OpponentAI(difficulty: Difficulty, count: Int) {

    private val difficultyMultiplier = when (difficulty) {
        Difficulty.BEGINNER -> 0.6f
        Difficulty.EASY -> 0.72f
        Difficulty.MEDIUM -> 0.82f
        Difficulty.HARD -> 0.92f
        Difficulty.EXPERT -> 1.0f
        Difficulty.MASTER -> 1.1f
    }

    val opponents: List<AIBikeState> = List(count) { i ->
        val names = listOf("Storm", "Viper", "Blaze", "Shadow", "Titan", "Nova", "Ghost")
        AIBikeState(
            id = "ai_$i",
            name = names[i % names.size],
            difficulty = difficulty,
            distanceM = -Random.nextFloat() * 50f,  // start slightly behind player
            lane = (i % 3 - 1).toFloat() * 0.6f,
            speedKmh = 40f,
            baseMaxSpeed = (120f + i * 15f) * difficultyMultiplier,
            aggression = 0.3f + Random.nextFloat() * 0.5f,
        )
    }

    fun update(dt: Float, playerDistanceM: Float, playerSpeedKmh: Float) {
        opponents.forEach { ai ->
            if (!ai.isActive) return@forEach
            updateAILane(ai, dt)
            updateAISpeed(ai, dt, playerDistanceM, playerSpeedKmh)
            ai.distanceM += ai.speedKmh / 3.6f * dt
        }
    }

    private fun updateAISpeed(ai: AIBikeState, dt: Float, playerDist: Float, playerSpeed: Float) {
        val gap = playerDist - ai.distanceM
        val targetSpeed = when {
            gap > 200f -> ai.baseMaxSpeed * 1.05f       // player far ahead — push hard
            gap > 50f -> ai.baseMaxSpeed                 // normal
            gap > -50f -> playerSpeed * 0.97f            // close — match speed
            else -> ai.baseMaxSpeed * 0.85f              // player behind — slow slightly
        }
        val accel = if (targetSpeed > ai.speedKmh) 40f else -60f
        ai.speedKmh = (ai.speedKmh + accel * dt).coerceIn(0f, ai.baseMaxSpeed * 1.1f)
    }

    private fun updateAILane(ai: AIBikeState, dt: Float) {
        ai.laneChangeTimer -= dt
        if (ai.laneChangeTimer <= 0f && Random.nextFloat() < 0.1f * ai.aggression) {
            ai.targetLane = (Random.nextFloat() * 2f - 1f) * 0.85f
            ai.laneChangeTimer = 2f + Random.nextFloat() * 3f
        }
        val laneSpeed = 0.8f * dt
        ai.lane = if (abs(ai.targetLane - ai.lane) < laneSpeed) ai.targetLane
                  else ai.lane + sign(ai.targetLane - ai.lane) * laneSpeed
    }

    fun toOpponents(): List<Opponent> = opponents.map { ai ->
        Opponent(
            id = ai.id,
            name = ai.name,
            motorcycleId = "bike_0${(opponents.indexOf(ai) % 3) + 2}",
            difficulty = ai.difficulty,
            position = ai.distanceM,
            lane = ai.lane.toInt(),
            speedKmh = ai.speedKmh,
            isActive = ai.isActive,
        )
    }

    fun getPlayerPosition(playerDistanceM: Float): Int {
        val ahead = opponents.count { it.isActive && it.distanceM > playerDistanceM }
        return ahead + 1
    }
}
