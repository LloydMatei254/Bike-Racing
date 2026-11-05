package com.example.bikeracing.data.local

import com.example.bikeracing.domain.model.Achievement
import com.example.bikeracing.domain.model.AchievementCategory

object AchievementCatalog {
    val all: List<Achievement> = listOf(
        // Speed
        Achievement("speed_100", "Century Rider", "Reach 100 km/h", AchievementCategory.SPEED, 100, rewardCoins = 200),
        Achievement("speed_200", "Speed Demon", "Reach 200 km/h", AchievementCategory.SPEED, 200, rewardCoins = 500),
        Achievement("speed_300", "Lightning Bolt", "Reach 300 km/h", AchievementCategory.SPEED, 300, rewardCoins = 1000),
        Achievement("speed_350", "Sonic Rider", "Reach 350 km/h", AchievementCategory.SPEED, 350, rewardCoins = 2000),
        // Racing
        Achievement("races_1", "First Race", "Complete your first race", AchievementCategory.RACING, 1, rewardCoins = 100),
        Achievement("races_10", "Race Enthusiast", "Complete 10 races", AchievementCategory.RACING, 10, rewardCoins = 300),
        Achievement("races_50", "Race Veteran", "Complete 50 races", AchievementCategory.RACING, 50, rewardCoins = 800),
        Achievement("races_100", "Race Legend", "Complete 100 races", AchievementCategory.RACING, 100, rewardCoins = 2000),
        Achievement("wins_1", "First Win", "Win your first race", AchievementCategory.RACING, 1, rewardCoins = 200),
        Achievement("wins_10", "Dominant Racer", "Win 10 races", AchievementCategory.RACING, 10, rewardCoins = 600),
        Achievement("wins_50", "Champion", "Win 50 races", AchievementCategory.RACING, 50, rewardCoins = 1500),
        Achievement("overtakes_50", "Overtaker", "Overtake 50 opponents", AchievementCategory.RACING, 50, rewardCoins = 400),
        Achievement("overtakes_200", "Traffic Master", "Overtake 200 opponents", AchievementCategory.RACING, 200, rewardCoins = 1000),
        // Career
        Achievement("level_5", "World 1 Clear", "Complete all City levels", AchievementCategory.CAREER, 5, rewardCoins = 500),
        Achievement("level_10", "World 2 Clear", "Complete all Highway levels", AchievementCategory.CAREER, 10, rewardCoins = 700),
        Achievement("level_15", "World 3 Clear", "Complete all Desert levels", AchievementCategory.CAREER, 15, rewardCoins = 900),
        Achievement("level_20", "World 4 Clear", "Complete all Mountain levels", AchievementCategory.CAREER, 20, rewardCoins = 1100),
        Achievement("level_25", "World 5 Clear", "Complete all Night levels", AchievementCategory.CAREER, 25, rewardCoins = 1300),
        Achievement("level_30", "World Champion", "Complete all Championship levels", AchievementCategory.CAREER, 30, rewardCoins = 5000),
        Achievement("distance_100", "Century Traveller", "Travel 100 km total", AchievementCategory.CAREER, 100, rewardCoins = 300),
        Achievement("distance_1000", "Road Warrior", "Travel 1000 km total", AchievementCategory.CAREER, 1000, rewardCoins = 1000),
        // Collection
        Achievement("bikes_3", "Bike Collector", "Own 3 motorcycles", AchievementCategory.COLLECTION, 3, rewardCoins = 300),
        Achievement("bikes_5", "Garage Owner", "Own 5 motorcycles", AchievementCategory.COLLECTION, 5, rewardCoins = 700),
        Achievement("bikes_10", "Full Garage", "Own all 10 motorcycles", AchievementCategory.COLLECTION, 10, rewardCoins = 3000),
        Achievement("stars_30", "Star Collector", "Earn 30 stars", AchievementCategory.COLLECTION, 30, rewardCoins = 800),
        Achievement("stars_90", "Perfect Career", "Earn 3 stars on every level", AchievementCategory.COLLECTION, 90, rewardCoins = 5000),
    )
}
