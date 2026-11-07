package com.example.bikeracing.game.engine

import com.example.bikeracing.domain.model.Difficulty
import com.example.bikeracing.domain.model.Level
import com.example.bikeracing.domain.model.Motorcycle
import com.example.bikeracing.domain.model.MotorcycleUpgrades
import com.example.bikeracing.domain.model.Opponent
import com.example.bikeracing.domain.model.RacePhase
import com.example.bikeracing.domain.model.RaceResult
import com.example.bikeracing.domain.model.RaceState
import com.example.bikeracing.game.ai.OpponentAI
import com.example.bikeracing.game.physics.BikePhysics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class GameEngine {

    private val engineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var gameLoopJob: Job? = null

    private val _raceState = MutableStateFlow(RaceState())
    val raceState: StateFlow<RaceState> = _raceState.asStateFlow()

    private val _opponents = MutableStateFlow<List<Opponent>>(emptyList())
    val opponents: StateFlow<List<Opponent>> = _opponents.asStateFlow()

    private lateinit var physics: BikePhysics
    private lateinit var aiSystem: OpponentAI
    private lateinit var level: Level

    // Input state (set by controls)
    @Volatile var throttleInput: Float = 0f
    @Volatile var brakeInput: Float = 0f
    @Volatile var steerInput: Float = 0f
    @Volatile var nitroInput: Boolean = false

    private var lastFrameTime = System.nanoTime()
    private var countdownTimer = 3.5f
    private var overtakeCount = 0
    private var lastPosition = 1

    fun startRace(level: Level, motorcycle: Motorcycle, upgrades: MotorcycleUpgrades?) {
        this.level = level
        val eff = upgrades ?: MotorcycleUpgrades(motorcycle.id)

        physics = BikePhysics(
            maxSpeedKmh = eff.getUpgradedStat(motorcycle.baseTopSpeed, eff.engineLevel),
            acceleration = eff.getUpgradedStat(motorcycle.baseAcceleration, eff.accelerationLevel),
            braking = eff.getUpgradedStat(motorcycle.baseBraking, eff.brakesLevel),
            handling = eff.getUpgradedStat(motorcycle.baseHandling, eff.handlingLevel),
            nitroBoost = eff.getUpgradedStat(motorcycle.baseNitroCapacity, eff.nitroLevel),
        )
        aiSystem = OpponentAI(level.opponentDifficulty, level.opponentCount)

        physics.reset()
        countdownTimer = 3.5f
        overtakeCount = 0
        lastPosition = level.opponentCount + 1

        _raceState.value = RaceState(
            phase = RacePhase.COUNTDOWN,
            totalRacers = level.opponentCount + 1,
            totalLaps = if (level.trackLength > 3f) 2 else 3,
            trackLengthKm = level.trackLength,
            countdownValue = 3,
        )

        startGameLoop()
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        lastFrameTime = System.nanoTime()
        gameLoopJob = engineScope.launch {
            while (true) {
                val now = System.nanoTime()
                val dt = ((now - lastFrameTime) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastFrameTime = now
                tick(dt)
                delay(16L) // ~60fps target
            }
        }
    }

    private fun tick(dt: Float) {
        val state = _raceState.value
        when (state.phase) {
            RacePhase.COUNTDOWN -> tickCountdown(dt, state)
            RacePhase.RACING -> tickRacing(dt, state)
            RacePhase.PAUSED -> { /* do nothing */ }
            RacePhase.FINISHED, RacePhase.CRASHED -> { gameLoopJob?.cancel() }
        }
    }

    private fun tickCountdown(dt: Float, state: RaceState) {
        countdownTimer -= dt
        val newCount = countdownTimer.toInt().coerceIn(0, 3)
        if (countdownTimer <= 0f) {
            _raceState.value = state.copy(phase = RacePhase.RACING, countdownValue = 0)
        } else {
            _raceState.value = state.copy(countdownValue = newCount)
        }
    }

    private fun tickRacing(dt: Float, state: RaceState) {
        // Update physics
        physics.update(dt, throttleInput, brakeInput, steerInput, nitroInput)

        // Update AI
        aiSystem.update(dt, physics.distanceM, physics.speedKmh)
        _opponents.value = aiSystem.toOpponents()

        // Track position
        val position = aiSystem.getPlayerPosition(physics.distanceM)
        if (position > lastPosition) overtakeCount++ // moved up in ranks
        lastPosition = position

        // Elapsed time
        val newElapsed = state.elapsedSeconds + dt

        // Distance & laps
        val trackLengthM = level.trackLength * 1000f
        val laps = 1 // single lap for now (extend for multi-lap)
        val distanceKm = physics.distanceM / 1000f

        // Score calculation
        val baseScore = (physics.distanceM * 0.1f + physics.speedKmh * dt * 5f).toInt()
        val newScore = state.score + baseScore

        // Time limit check
        val timeExpired = level.timeLimit > 0 && newElapsed > level.timeLimit

        // Check race completion
        val raceFinished = physics.distanceM >= trackLengthM || timeExpired

        val newState = state.copy(
            elapsedSeconds = newElapsed,
            speedKmh = physics.speedKmh,
            maxSpeedKmh = maxOf(state.maxSpeedKmh, physics.speedKmh),
            rpm = physics.rpm,
            currentGear = physics.gear,
            nitroAmount = physics.nitroRemaining,
            isNitroActive = physics.isNitroActive,
            distanceTravelledKm = distanceKm,
            playerPosition = position,
            score = newScore,
            overtakes = overtakeCount,
            leanAngle = physics.leanAngle,
            lateralPosition = physics.lateralPosition,
            phase = if (raceFinished) RacePhase.FINISHED else RacePhase.RACING,
        )
        _raceState.value = newState
    }

    fun pauseRace() {
        if (_raceState.value.phase == RacePhase.RACING) {
            gameLoopJob?.cancel()
            _raceState.value = _raceState.value.copy(phase = RacePhase.PAUSED, isPaused = true)
        }
    }

    fun resumeRace() {
        if (_raceState.value.phase == RacePhase.PAUSED) {
            _raceState.value = _raceState.value.copy(phase = RacePhase.RACING, isPaused = false)
            lastFrameTime = System.nanoTime()
            startGameLoop()
        }
    }

    fun buildResult(): RaceResult {
        val state = _raceState.value
        val position = state.playerPosition
        val stars = when {
            position == 1 -> 3
            position <= 3 -> 2
            else -> 1
        }
        // Coin calculation: base + position bonus
        val posBonus = when (position) { 1 -> 2.0f; 2 -> 1.5f; 3 -> 1.2f; else -> 1.0f }
        val coinsEarned = (level.rewardCoins * posBonus).roundToInt()
        val xpEarned = (level.rewardXp * posBonus).roundToInt()

        return RaceResult(
            levelId = level.id,
            finalPosition = position,
            totalRacers = state.totalRacers,
            raceTimeSeconds = state.elapsedSeconds,
            maxSpeedKmh = state.maxSpeedKmh,
            distanceKm = state.distanceTravelledKm,
            overtakes = overtakeCount,
            coinsEarned = coinsEarned,
            xpEarned = xpEarned,
            starsEarned = stars,
        )
    }

    fun stopRace() {
        gameLoopJob?.cancel()
        _raceState.value = RaceState()
    }
}
