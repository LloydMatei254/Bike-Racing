package com.example.bikeracing.game.camera

/**
 * Computes third-person camera positioning for the race scene.
 * Returns plain data classes — no SceneView dependency.
 */
data class CameraState(
    val posX: Float,
    val posY: Float,
    val posZ: Float,
    val lookAtX: Float,
    val lookAtY: Float,
    val lookAtZ: Float,
    val fovDegrees: Float = 75f,
)

class RaceCamera {
    private var currentX = 0f
    private var currentY = 2.5f
    private var currentZ = -5f

    fun computeCamera(
        bikeX: Float,
        speedFactor: Float,   // 0..1
        steerFactor: Float,   // -1..1
    ): CameraState {
        val targetZ = -3.5f - speedFactor * 3.5f
        val targetY = 2.0f + speedFactor * 1.5f
        val targetX = bikeX * 0.4f + steerFactor * 0.3f

        currentX = lerp(currentX, targetX, 0.15f)
        currentY = lerp(currentY, targetY, 0.15f)
        currentZ = lerp(currentZ, targetZ, 0.15f)

        val fov = 70f + speedFactor * 15f

        return CameraState(
            posX = currentX, posY = currentY, posZ = currentZ,
            lookAtX = bikeX * 0.15f, lookAtY = 0.3f, lookAtZ = 8f,
            fovDegrees = fov,
        )
    }

    private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t

    fun reset() {
        currentX = 0f; currentY = 2.5f; currentZ = -5f
    }
}
