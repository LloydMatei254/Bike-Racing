package com.example.bikeracing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bikeracing.data.local.LevelCatalog
import com.example.bikeracing.data.local.MotorcycleCatalog
import com.example.bikeracing.data.repository.GameRepository
import com.example.bikeracing.domain.model.ControlType
import com.example.bikeracing.domain.model.GameSettings
import com.example.bikeracing.domain.model.Level
import com.example.bikeracing.domain.model.Motorcycle
import com.example.bikeracing.domain.model.MotorcycleUpgrades
import com.example.bikeracing.domain.model.Opponent
import com.example.bikeracing.domain.model.PlayerProfile
import com.example.bikeracing.domain.model.RaceResult
import com.example.bikeracing.domain.model.RaceState
import com.example.bikeracing.game.controls.InputController
import com.example.bikeracing.game.engine.GameEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(private val repo: GameRepository) : ViewModel() {

    // Player data
    val playerProfile: StateFlow<PlayerProfile> = repo.playerProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerProfile())

    val gameSettings: StateFlow<GameSettings> = repo.gameSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GameSettings())

    val levelProgress: StateFlow<Map<Int, Int>> = repo.levelProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Game engine
    private val gameEngine = GameEngine()
    val raceState: StateFlow<RaceState> = gameEngine.raceState
    val opponents: StateFlow<List<Opponent>> = gameEngine.opponents

    // Input controller (initialized with current settings)
    private var inputController: InputController? = null

    // Latest race result
    private val _lastRaceResult = MutableStateFlow<RaceResult?>(null)
    val lastRaceResult: StateFlow<RaceResult?> = _lastRaceResult.asStateFlow()

    // Current active level
    private val _currentLevel = MutableStateFlow<Level?>(null)
    val currentLevel: StateFlow<Level?> = _currentLevel.asStateFlow()

    // Upgrade state (stub — in full impl stored in DataStore)
    private val upgradeMap = mutableMapOf<String, MotorcycleUpgrades>()

    // Garage selection
    private val _garageSelectedBikeId = MutableStateFlow("bike_01")
    val garageSelectedBikeId: StateFlow<String> = _garageSelectedBikeId.asStateFlow()

    val motorcycles: List<Motorcycle> get() = MotorcycleCatalog.all
    val levels: List<Level> get() = LevelCatalog.all
    val worlds: List<LevelCatalog.World> get() = LevelCatalog.worlds

    // ---- Race lifecycle ----
    fun startRace(levelId: Int) {
        val level = LevelCatalog.getById(levelId) ?: return
        _currentLevel.value = level
        val profile = playerProfile.value
        val bike = MotorcycleCatalog.getById(profile.selectedMotorcycleId)
            ?: MotorcycleCatalog.all.first()
        val upgrades = upgradeMap[bike.id]
        val settings = gameSettings.value

        gameEngine.startRace(level, bike, upgrades)

        inputController = InputController(
            engine = gameEngine,
            controlType = settings.controlType,
            sensitivity = settings.steeringSensitivity,
        )
    }

    fun pauseRace() = gameEngine.pauseRace()
    fun resumeRace() = gameEngine.resumeRace()

    fun finishRace() {
        val result = gameEngine.buildResult()
        _lastRaceResult.value = result
        viewModelScope.launch {
            val profile = playerProfile.value
            repo.applyRaceResult(result, profile)
        }
        gameEngine.stopRace()
    }

    fun getInputController(): InputController? = inputController

    // ---- Input forwarding ----
    fun onThrottle(pressed: Boolean) = inputController?.onThrottle(pressed)
    fun onBrake(pressed: Boolean) = inputController?.onBrake(pressed)
    fun onSteerLeft(pressed: Boolean) = inputController?.onSteerLeft(pressed)
    fun onSteerRight(pressed: Boolean) = inputController?.onSteerRight(pressed)
    fun onNitro(pressed: Boolean) = inputController?.onNitro(pressed)
    fun onJoystick(x: Float) = inputController?.onJoystickMove(x)

    // ---- Garage / Shop ----
    fun selectGarageBike(id: String) { _garageSelectedBikeId.value = id }

    fun purchaseBike(bikeId: String) {
        viewModelScope.launch {
            val profile = playerProfile.value
            repo.purchaseMotorcycle(bikeId, profile)
        }
    }

    fun equipBike(bikeId: String) {
        viewModelScope.launch {
            val profile = playerProfile.value
            repo.selectMotorcycle(bikeId, profile)
        }
    }

    fun upgradeBike(bikeId: String, stat: UpgradeStat) {
        val current = upgradeMap[bikeId] ?: MotorcycleUpgrades(bikeId)
        val upgraded = when (stat) {
            UpgradeStat.ENGINE -> current.copy(engineLevel = (current.engineLevel + 1).coerceAtMost(5))
            UpgradeStat.ACCELERATION -> current.copy(accelerationLevel = (current.accelerationLevel + 1).coerceAtMost(5))
            UpgradeStat.BRAKES -> current.copy(brakesLevel = (current.brakesLevel + 1).coerceAtMost(5))
            UpgradeStat.HANDLING -> current.copy(handlingLevel = (current.handlingLevel + 1).coerceAtMost(5))
            UpgradeStat.NITRO -> current.copy(nitroLevel = (current.nitroLevel + 1).coerceAtMost(5))
            UpgradeStat.SUSPENSION -> current.copy(suspensionLevel = (current.suspensionLevel + 1).coerceAtMost(5))
        }
        upgradeMap[bikeId] = upgraded
    }

    fun getUpgradesFor(bikeId: String): MotorcycleUpgrades =
        upgradeMap[bikeId] ?: MotorcycleUpgrades(bikeId)

    // ---- Settings ----
    fun saveSettings(settings: GameSettings) {
        viewModelScope.launch { repo.saveSettings(settings) }
    }

    // ---- Profile ----
    fun updatePlayerName(name: String) {
        viewModelScope.launch {
            val profile = playerProfile.value.copy(name = name)
            repo.saveProfile(profile)
        }
    }

    // ---- Economy ----
    fun claimDailyReward() {
        viewModelScope.launch {
            val now = System.currentTimeMillis() / (1000L * 60 * 60 * 24)
            val profile = playerProfile.value
            val lastDay = profile.lastDailyRewardDay
            if (now > lastDay) {
                val streak = if (now == lastDay + 1) profile.currentStreak + 1 else 1
                val bonus = 100 + streak * 50
                val updated = profile.copy(
                    coins = profile.coins + bonus,
                    lastDailyRewardDay = now,
                    currentStreak = streak,
                )
                repo.saveProfile(updated)
            }
        }
    }

    fun isDailyRewardAvailable(): Boolean {
        val now = System.currentTimeMillis() / (1000L * 60 * 60 * 24)
        return now > playerProfile.value.lastDailyRewardDay
    }

    fun getUnlockedLevels(): Set<Int> {
        val completed = levelProgress.value.keys
        return repo.getUnlockedLevelIds(completed)
    }

    fun resetProgress() {
        viewModelScope.launch { repo.resetProgress() }
    }

    override fun onCleared() {
        super.onCleared()
        gameEngine.stopRace()
    }
}

enum class UpgradeStat { ENGINE, ACCELERATION, BRAKES, HANDLING, NITRO, SUSPENSION }

class GameViewModelFactory(private val repo: GameRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
