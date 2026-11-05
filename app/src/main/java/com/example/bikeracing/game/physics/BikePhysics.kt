package com.example.bikeracing.game.physics

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

/**
 * Handles motorcycle physics simulation for arcade-style racing.
 * Runs in the game loop at ~60Hz.
 */
class BikePhysics(
    val maxSpeedKmh: Float = 200f,
    val acceleration: Float = 60f,   // rating 0-100
    val braking: Float = 60f,
    val handling: Float = 60f,
    val nitroBoost: Float = 50f,
) {
    // State
    var speedKmh: Float = 0f
        private set
    var speedMs: Float = 0f
        private set
    var lateralPosition: Float = 0f     // -1..1 (left-right on track, -1 = far left)
        private set
    var leanAngle: Float = 0f           // degrees, negative = left
        private set
    var rpm: Float = 0f
        private set
    var gear: Int = 1
        private set
    var distanceM: Float = 0f
        private set
    var isNitroActive: Boolean = false
        private set
    var nitroRemaining: Float = 1f     // 0..1
        private set
    var isCrashed: Boolean = false
        private set
    var crashRecoveryTimer: Float = 0f

    // Track boundaries
    private val trackHalfWidth = 0.9f
    // Gear thresholds in km/h
    private val gearRatios = floatArrayOf(0f, 30f, 70f, 120f, 180f, 240f, 300f)

    private val accelFactor get() = 0.4f + (acceleration / 100f) * 0.8f
    private val brakeFactor get() = 0.6f + (braking / 100f) * 1.0f
    private val handleFactor get() = 0.5f + (handling / 100f) * 1.0f
    private val nitroBonusSpeed get() = maxSpeedKmh * (0.1f + (nitroBoost / 100f) * 0.25f)

    fun update(
        dt: Float,
        throttle: Float,       // 0..1
        brake: Float,          // 0..1
        steer: Float,          // -1..1 (left negative, right positive)
        activateNitro: Boolean,
    ) {
        if (isCrashed) {
            handleCrash(dt)
            return
        }
        updateNitro(dt, activateNitro)
        updateSpeed(dt, throttle, brake)
        updateLateral(dt, steer)
        updateLean(dt, steer)
        updateGear()
        updateRpm()
        distanceM += speedMs * dt
    }

    private fun updateNitro(dt: Float, activate: Boolean) {
        if (activate && nitroRemaining > 0f) {
            isNitroActive = true
            nitroRemaining = max(0f, nitroRemaining - dt * 0.15f)
        } else {
            isNitroActive = false
            // Slowly recharge nitro when not active
            nitroRemaining = min(1f, nitroRemaining + dt * 0.05f)
        }
    }

    private fun updateSpeed(dt: Float, throttle: Float, brake: Float) {
        val effectiveMax = if (isNitroActive) maxSpeedKmh + nitroBonusSpeed else maxSpeedKmh
        val accel = throttle * accelFactor * 80f     // km/h per second
        val decel = brake * brakeFactor * 150f       // km/h per second
        val drag = speedKmh * 0.02f                  // wind resistance

        var newSpeed = speedKmh + (accel - decel - drag) * dt
        if (isNitroActive) newSpeed += 60f * dt

        speedKmh = newSpeed.coerceIn(0f, effectiveMax)
        speedMs = speedKmh / 3.6f
    }

    private fun updateLateral(dt: Float, steer: Float) {
        if (abs(speedKmh) < 1f) return
        val steerRate = handleFactor * 0.8f
        val steerAmount = steer * steerRate * dt
        lateralPosition = (lateralPosition + steerAmount).coerceIn(-trackHalfWidth, trackHalfWidth)
    }

    private fun updateLean(dt: Float, steer: Float) {
        val targetLean = steer * 35f * (speedKmh / maxSpeedKmh)  // max 35 degree lean
        val leanSpeed = 180f * dt
        leanAngle = if (abs(targetLean - leanAngle) < leanSpeed) {
            targetLean
        } else {
            leanAngle + sign(targetLean - leanAngle) * leanSpeed
        }
    }

    private fun updateGear() {
        for (g in gearRatios.indices.reversed()) {
            if (speedKmh >= gearRatios[g]) {
                gear = (g + 1).coerceIn(1, 6)
                return
            }
        }
        gear = 1
    }

    private fun updateRpm() {
        val gearMin = if (gear > 1) gearRatios[gear - 1] else 0f
        val gearMax = gearRatios.getOrElse(gear) { maxSpeedKmh }
        val ratio = if (gearMax > gearMin) (speedKmh - gearMin) / (gearMax - gearMin) else 0f
        rpm = (2000f + ratio * 6000f).coerceIn(800f, 9000f)
    }

    fun triggerCrash() {
        if (isCrashed) return
        isCrashed = true
        crashRecoveryTimer = 2.0f
        speedKmh = speedKmh * 0.3f
        speedMs = speedKmh / 3.6f
    }

    private fun handleCrash(dt: Float) {
        crashRecoveryTimer -= dt
        speedKmh = max(0f, speedKmh - 40f * dt)
        speedMs = speedKmh / 3.6f
        if (crashRecoveryTimer <= 0f) {
            isCrashed = false
            crashRecoveryTimer = 0f
        }
    }

    fun refillNitro() { nitroRemaining = 1f }
    fun reset() {
        speedKmh = 0f; speedMs = 0f; lateralPosition = 0f; leanAngle = 0f
        rpm = 800f; gear = 1; distanceM = 0f; isNitroActive = false
        nitroRemaining = 1f; isCrashed = false; crashRecoveryTimer = 0f
    }
}
