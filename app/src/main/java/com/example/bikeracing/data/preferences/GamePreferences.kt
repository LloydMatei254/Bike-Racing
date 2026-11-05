package com.example.bikeracing.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.bikeracing.domain.model.ControlType
import com.example.bikeracing.domain.model.GameSettings
import com.example.bikeracing.domain.model.GraphicsQuality
import com.example.bikeracing.domain.model.PlayerProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bike_racing_prefs")

class GamePreferences(private val context: Context) {

    companion object {
        // Player
        val PLAYER_NAME = stringPreferencesKey("player_name")
        val PLAYER_LEVEL = intPreferencesKey("player_level")
        val PLAYER_XP = intPreferencesKey("player_xp")
        val PLAYER_COINS = intPreferencesKey("player_coins")
        val PLAYER_GEMS = intPreferencesKey("player_gems")
        val TOTAL_RACES = intPreferencesKey("total_races")
        val TOTAL_WINS = intPreferencesKey("total_wins")
        val TOTAL_DISTANCE = floatPreferencesKey("total_distance")
        val BEST_SPEED = floatPreferencesKey("best_speed")
        val SELECTED_BIKE = stringPreferencesKey("selected_bike")
        val OWNED_BIKES = stringSetPreferencesKey("owned_bikes")
        val LAST_DAILY_REWARD = longPreferencesKey("last_daily_reward")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        // Level progress — stored as "levelId:stars"
        val LEVEL_PROGRESS = stringPreferencesKey("level_progress")
        // Upgrades stored as JSON-like string "bikeId:e1:a2:b3:h4:n5:s6:c7|..."
        val BIKE_UPGRADES = stringPreferencesKey("bike_upgrades")
        // Achievements — unlocked ids separated by comma
        val ACHIEVEMENTS_UNLOCKED = stringPreferencesKey("achievements_unlocked")
        val ACHIEVEMENT_PROGRESS = stringPreferencesKey("achievement_progress")
        // Settings
        val GRAPHICS_QUALITY = intPreferencesKey("graphics_quality")
        val MUSIC_VOLUME = floatPreferencesKey("music_volume")
        val SFX_VOLUME = floatPreferencesKey("sfx_volume")
        val ENGINE_VOLUME = floatPreferencesKey("engine_volume")
        val CONTROL_TYPE = intPreferencesKey("control_type")
        val STEERING_SENSITIVITY = floatPreferencesKey("steering_sensitivity")
        val VIBRATION = booleanPreferencesKey("vibration")
        val CAMERA_SHAKE = booleanPreferencesKey("camera_shake")
        val SHOW_FPS = booleanPreferencesKey("show_fps")
        val TARGET_FPS = intPreferencesKey("target_fps")
    }

    val playerProfile: Flow<PlayerProfile> = context.dataStore.data.map { prefs ->
        PlayerProfile(
            name = prefs[PLAYER_NAME] ?: "Racer",
            level = prefs[PLAYER_LEVEL] ?: 1,
            xp = prefs[PLAYER_XP] ?: 0,
            coins = prefs[PLAYER_COINS] ?: 500,
            gems = prefs[PLAYER_GEMS] ?: 10,
            totalRaces = prefs[TOTAL_RACES] ?: 0,
            totalWins = prefs[TOTAL_WINS] ?: 0,
            totalDistanceKm = prefs[TOTAL_DISTANCE] ?: 0f,
            bestSpeedKmh = prefs[BEST_SPEED] ?: 0f,
            selectedMotorcycleId = prefs[SELECTED_BIKE] ?: "bike_01",
            ownedMotorcycleIds = (prefs[OWNED_BIKES] ?: setOf("bike_01")).toList(),
            lastDailyRewardDay = prefs[LAST_DAILY_REWARD] ?: 0L,
            currentStreak = prefs[CURRENT_STREAK] ?: 0,
        )
    }

    val gameSettings: Flow<GameSettings> = context.dataStore.data.map { prefs ->
        GameSettings(
            graphicsQuality = GraphicsQuality.values().getOrElse(prefs[GRAPHICS_QUALITY] ?: 1) { GraphicsQuality.MEDIUM },
            musicVolume = prefs[MUSIC_VOLUME] ?: 0.7f,
            sfxVolume = prefs[SFX_VOLUME] ?: 0.9f,
            engineVolume = prefs[ENGINE_VOLUME] ?: 0.8f,
            controlType = ControlType.values().getOrElse(prefs[CONTROL_TYPE] ?: 0) { ControlType.TOUCH },
            steeringSensitivity = prefs[STEERING_SENSITIVITY] ?: 0.5f,
            vibrationEnabled = prefs[VIBRATION] ?: true,
            cameraShake = prefs[CAMERA_SHAKE] ?: true,
            showFps = prefs[SHOW_FPS] ?: false,
            targetFps = prefs[TARGET_FPS] ?: 60,
        )
    }

    // Level progress: stored as "1:3,2:2,3:1,..." (levelId:stars)
    val levelProgress: Flow<Map<Int, Int>> = context.dataStore.data.map { prefs ->
        val raw = prefs[LEVEL_PROGRESS] ?: ""
        if (raw.isEmpty()) emptyMap()
        else raw.split(",").mapNotNull { entry ->
            val parts = entry.split(":")
            if (parts.size == 2) parts[0].toIntOrNull()?.let { id -> parts[1].toIntOrNull()?.let { stars -> id to stars } }
            else null
        }.toMap()
    }

    suspend fun savePlayerProfile(profile: PlayerProfile) {
        context.dataStore.edit { prefs ->
            prefs[PLAYER_NAME] = profile.name
            prefs[PLAYER_LEVEL] = profile.level
            prefs[PLAYER_XP] = profile.xp
            prefs[PLAYER_COINS] = profile.coins
            prefs[PLAYER_GEMS] = profile.gems
            prefs[TOTAL_RACES] = profile.totalRaces
            prefs[TOTAL_WINS] = profile.totalWins
            prefs[TOTAL_DISTANCE] = profile.totalDistanceKm
            prefs[BEST_SPEED] = profile.bestSpeedKmh
            prefs[SELECTED_BIKE] = profile.selectedMotorcycleId
            prefs[OWNED_BIKES] = profile.ownedMotorcycleIds.toSet()
            prefs[LAST_DAILY_REWARD] = profile.lastDailyRewardDay
            prefs[CURRENT_STREAK] = profile.currentStreak
        }
    }

    suspend fun saveGameSettings(settings: GameSettings) {
        context.dataStore.edit { prefs ->
            prefs[GRAPHICS_QUALITY] = settings.graphicsQuality.ordinal
            prefs[MUSIC_VOLUME] = settings.musicVolume
            prefs[SFX_VOLUME] = settings.sfxVolume
            prefs[ENGINE_VOLUME] = settings.engineVolume
            prefs[CONTROL_TYPE] = settings.controlType.ordinal
            prefs[STEERING_SENSITIVITY] = settings.steeringSensitivity
            prefs[VIBRATION] = settings.vibrationEnabled
            prefs[CAMERA_SHAKE] = settings.cameraShake
            prefs[SHOW_FPS] = settings.showFps
            prefs[TARGET_FPS] = settings.targetFps
        }
    }

    suspend fun saveLevelStars(levelId: Int, stars: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[LEVEL_PROGRESS] ?: ""
            val map = parseLevelProgress(current).toMutableMap()
            val existing = map[levelId] ?: 0
            if (stars > existing) {
                map[levelId] = stars
                prefs[LEVEL_PROGRESS] = map.entries.joinToString(",") { "${it.key}:${it.value}" }
            }
        }
    }

    suspend fun resetAllProgress() {
        context.dataStore.edit { it.clear() }
    }

    private fun parseLevelProgress(raw: String): Map<Int, Int> {
        if (raw.isEmpty()) return emptyMap()
        return raw.split(",").mapNotNull { entry ->
            val parts = entry.split(":")
            if (parts.size == 2) parts[0].toIntOrNull()?.let { id -> parts[1].toIntOrNull()?.let { stars -> id to stars } }
            else null
        }.toMap()
    }
}
