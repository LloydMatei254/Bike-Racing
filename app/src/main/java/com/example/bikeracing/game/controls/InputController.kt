package com.example.bikeracing.game.controls

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.bikeracing.domain.model.ControlType
import com.example.bikeracing.game.engine.GameEngine
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class InputController(
    private val engine: GameEngine,
    private val controlType: ControlType,
    private val sensitivity: Float = 0.5f,
) : SensorEventListener {

    // Touch/button state
    private var steerLeft = false
    private var steerRight = false
    private var throttlePressed = false
    private var brakePressed = false
    private var nitroPressed = false

    // Joystick state
    private var joystickX = 0f

    // Tilt state
    private var tiltX = 0f

    fun onThrottle(pressed: Boolean) { throttlePressed = pressed; update() }
    fun onBrake(pressed: Boolean) { brakePressed = pressed; update() }
    fun onSteerLeft(pressed: Boolean) { steerLeft = pressed; update() }
    fun onSteerRight(pressed: Boolean) { steerRight = pressed; update() }
    fun onNitro(pressed: Boolean) { nitroPressed = pressed; update() }
    fun onJoystickMove(x: Float) { joystickX = x.coerceIn(-1f, 1f); update() }

    private fun update() {
        when (controlType) {
            ControlType.TOUCH -> {
                val steer = when {
                    steerLeft && !steerRight -> -1f * (0.5f + sensitivity * 0.5f)
                    steerRight && !steerLeft -> 1f * (0.5f + sensitivity * 0.5f)
                    else -> 0f
                }
                engine.steerInput = steer
                engine.throttleInput = if (throttlePressed) 1f else 0.2f // idle creep
                engine.brakeInput = if (brakePressed) 1f else 0f
                engine.nitroInput = nitroPressed
            }
            ControlType.JOYSTICK -> {
                engine.steerInput = joystickX * (0.5f + sensitivity * 0.5f)
                engine.throttleInput = if (throttlePressed) 1f else 0.2f
                engine.brakeInput = if (brakePressed) 1f else 0f
                engine.nitroInput = nitroPressed
            }
            ControlType.TILT -> {
                engine.steerInput = tiltX * (0.5f + sensitivity * 0.5f)
                engine.throttleInput = if (throttlePressed) 1f else 0.2f
                engine.brakeInput = if (brakePressed) 1f else 0f
                engine.nitroInput = nitroPressed
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (controlType != ControlType.TILT) return
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            // X axis: device tilt left/right
            val raw = -event.values[0] / SensorManager.GRAVITY_EARTH
            tiltX = raw.coerceIn(-1f, 1f) * sensitivity
            update()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    fun reset() {
        steerLeft = false; steerRight = false
        throttlePressed = false; brakePressed = false; nitroPressed = false
        joystickX = 0f; tiltX = 0f
        update()
    }
}
