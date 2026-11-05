package com.example.bikeracing.data.repository

import com.example.bikeracing.data.local.AchievementCatalog
import com.example.bikeracing.data.local.LevelCatalog
import com.example.bikeracing.data.local.MotorcycleCatalog
import com.example.bikeracing.data.preferences.GamePreferences
import com.example.bikeracing.domain.model.GameSettings
import com.example.bikeracing.domain.model.Level
import com.example.bikeracing.domain.model.Motorcycle
import com.example.bikeracing.domain.model.PlayerProfile
import com.example.bikeracing.domain.model.RaceResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GameRepository(private val prefs: GamePreferences) {

    val playerProfile: Flow<PlayerProfile> = prefs.playerProfile
    val gameSettings: Flow<GameSettings> = prefs.gameSettings
    val levelProgress: Flow<Map<Int, Int>> = prefs.levelProgress

    val motorcycles: List<Motorcycle> get() = MotorcycleCatalog.all
    val levels: List<Level> get() = LevelCatalog.all
    val worlds: List<LevelCatalog.World> get() = LevelCatalog.worlds

    fun getUnlockedLevelIds(completedLevels: Set<Int>): Set<Int> {
        val unlocked = mutableSetOf(1)
        for (level in LevelCatalog.all) {
            if (level.unlockRequires == 0 || level.unlockRequires in completedLevels) {
                unlocked.add(level.id)
            }
        }
        return unlocked
    }

    suspend fun applyRaceResult(result: RaceResult, currentProfile: PlayerProfile): PlayerProfile {
        var newXp = currentProfile.xp + result.xpEarned
        var newLevel = currentProfile.level
        while (newXp >= (newLevel * 1000)) {
            newXp -= (newLevel * 1000)
            newLevel++
        }
        val updated = currentProfile.copy(
            xp = newXp,
            level = newLevel,
            coins = currentProfile.coins + result.coinsEarned,
            totalRaces = currentProfile.totalRaces + 1,
            totalWins = if (result.finalPosition == 1) currentProfile.totalWins + 1 else currentProfile.totalWins,
            totalDistanceKm = currentProfile.totalDistanceKm + result.distanceKm,
            bestSpeedKmh = maxOf(currentProfile.bestSpeedKmh, result.maxSpeedKmh),
        )
        prefs.savePlayerProfile(updated)
        if (result.starsEarned > 0) {
            prefs.saveLevelStars(result.levelId, result.starsEarned)
        }
        return updated
    }

    suspend fun purchaseMotorcycle(motorcycleId: String, currentProfile: PlayerProfile): PlayerProfile? {
        val bike = MotorcycleCatalog.getById(motorcycleId) ?: return null
        val cost = bike.price
        if (currentProfile.coins < cost) return null
        val updated = currentProfile.copy(
            coins = currentProfile.coins - cost,
            ownedMotorcycleIds = (currentProfile.ownedMotorcycleIds + motorcycleId).distinct(),
        )
        prefs.savePlayerProfile(updated)
        return updated
    }

    suspend fun selectMotorcycle(motorcycleId: String, currentProfile: PlayerProfile): PlayerProfile {
        val updated = currentProfile.copy(selectedMotorcycleId = motorcycleId)
        prefs.savePlayerProfile(updated)
        return updated
    }

    suspend fun saveSettings(settings: GameSettings) = prefs.saveGameSettings(settings)
    suspend fun saveProfile(profile: PlayerProfile) = prefs.savePlayerProfile(profile)
    suspend fun resetProgress() = prefs.resetAllProgress()
}
