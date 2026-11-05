package com.example.bikeracing.data.local

import com.example.bikeracing.domain.model.Difficulty
import com.example.bikeracing.domain.model.Environment
import com.example.bikeracing.domain.model.Level
import com.example.bikeracing.domain.model.LevelObjective
import com.example.bikeracing.domain.model.StarObjectives
import com.example.bikeracing.domain.model.TrafficDensity

object LevelCatalog {

    val worlds: List<World> = listOf(
        World(1, "City Streets", "Master the urban jungle", Environment.CITY),
        World(2, "Highway Racing", "High-speed highway dominance", Environment.HIGHWAY),
        World(3, "Desert Racing", "Survive the scorching sands", Environment.DESERT),
        World(4, "Mountain Racing", "Conquer treacherous peaks", Environment.MOUNTAIN),
        World(5, "Night Racing", "Race through the neon night", Environment.NIGHT),
        World(6, "Championship", "Prove yourself among legends", Environment.CHAMPIONSHIP),
    )

    val all: List<Level> = buildList {
        // World 1 — City Streets (Levels 1-5)
        add(level(1, 1, "Street Debut", Environment.CITY, 1.5f, 90, 2, TrafficDensity.LIGHT, Difficulty.BEGINNER, 100, 50, 1, 0))
        add(level(2, 1, "Corner Rush", Environment.CITY, 1.8f, 90, 2, TrafficDensity.LIGHT, Difficulty.BEGINNER, 120, 60, 2, 1))
        add(level(3, 1, "City Blitz", Environment.CITY, 2.0f, 95, 3, TrafficDensity.LIGHT, Difficulty.EASY, 150, 75, 3, 2))
        add(level(4, 1, "Downtown Dash", Environment.CITY, 2.2f, 100, 3, TrafficDensity.MEDIUM, Difficulty.EASY, 180, 90, 4, 3))
        add(level(5, 1, "City Champion", Environment.CITY, 2.5f, 100, 3, TrafficDensity.MEDIUM, Difficulty.EASY, 220, 110, 5, 4))
        // World 2 — Highway (Levels 6-10)
        add(level(6, 2, "Open Road", Environment.HIGHWAY, 3.0f, 120, 3, TrafficDensity.MEDIUM, Difficulty.EASY, 250, 120, 8, 5))
        add(level(7, 2, "Fast Lane", Environment.HIGHWAY, 3.5f, 110, 3, TrafficDensity.HEAVY, Difficulty.MEDIUM, 300, 140, 10, 6))
        add(level(8, 2, "Highway Duel", Environment.HIGHWAY, 4.0f, 130, 4, TrafficDensity.HEAVY, Difficulty.MEDIUM, 350, 160, 12, 7))
        add(level(9, 2, "Speed Demon", Environment.HIGHWAY, 4.5f, 120, 4, TrafficDensity.HEAVY, Difficulty.MEDIUM, 400, 180, 15, 8))
        add(level(10, 2, "Highway King", Environment.HIGHWAY, 5.0f, 0, 4, TrafficDensity.EXTREME, Difficulty.HARD, 500, 220, 20, 9))
        // World 3 — Desert (Levels 11-15)
        add(level(11, 3, "Sand Storm", Environment.DESERT, 3.5f, 150, 3, TrafficDensity.LIGHT, Difficulty.MEDIUM, 350, 160, 15, 10))
        add(level(12, 3, "Dune Rider", Environment.DESERT, 4.0f, 140, 3, TrafficDensity.LIGHT, Difficulty.HARD, 400, 185, 18, 11))
        add(level(13, 3, "Desert Heat", Environment.DESERT, 4.5f, 160, 4, TrafficDensity.MEDIUM, Difficulty.HARD, 450, 200, 20, 12))
        add(level(14, 3, "Mirage Run", Environment.DESERT, 5.0f, 150, 4, TrafficDensity.MEDIUM, Difficulty.HARD, 500, 220, 22, 13))
        add(level(15, 3, "Sandstorm King", Environment.DESERT, 5.5f, 180, 5, TrafficDensity.MEDIUM, Difficulty.EXPERT, 600, 260, 25, 14))
        // World 4 — Mountain (Levels 16-20)
        add(level(16, 4, "Cliff Hanger", Environment.MOUNTAIN, 3.0f, 180, 3, TrafficDensity.LIGHT, Difficulty.HARD, 400, 180, 20, 15))
        add(level(17, 4, "Peak Rush", Environment.MOUNTAIN, 3.5f, 200, 3, TrafficDensity.LIGHT, Difficulty.HARD, 450, 200, 22, 16))
        add(level(18, 4, "Summit Sprint", Environment.MOUNTAIN, 4.0f, 190, 4, TrafficDensity.MEDIUM, Difficulty.EXPERT, 550, 240, 25, 17))
        add(level(19, 4, "Avalanche Run", Environment.MOUNTAIN, 4.5f, 210, 4, TrafficDensity.MEDIUM, Difficulty.EXPERT, 620, 270, 28, 18))
        add(level(20, 4, "Mountain Legend", Environment.MOUNTAIN, 5.0f, 200, 5, TrafficDensity.MEDIUM, Difficulty.EXPERT, 700, 300, 30, 19))
        // World 5 — Night (Levels 21-25)
        add(level(21, 5, "Neon Debut", Environment.NIGHT, 4.0f, 200, 4, TrafficDensity.MEDIUM, Difficulty.EXPERT, 600, 260, 28, 20))
        add(level(22, 5, "Rain Rider", Environment.NIGHT, 4.5f, 190, 4, TrafficDensity.HEAVY, Difficulty.EXPERT, 700, 300, 30, 21))
        add(level(23, 5, "Midnight Run", Environment.NIGHT, 5.0f, 210, 5, TrafficDensity.HEAVY, Difficulty.MASTER, 800, 340, 32, 22))
        add(level(24, 5, "Neon Storm", Environment.NIGHT, 5.5f, 200, 5, TrafficDensity.HEAVY, Difficulty.MASTER, 900, 380, 35, 23))
        add(level(25, 5, "Night King", Environment.NIGHT, 6.0f, 0, 5, TrafficDensity.EXTREME, Difficulty.MASTER, 1000, 420, 40, 24))
        // World 6 — Championship (Levels 26-30)
        add(level(26, 6, "Pro League", Environment.CHAMPIONSHIP, 5.0f, 240, 5, TrafficDensity.MEDIUM, Difficulty.MASTER, 900, 380, 40, 25))
        add(level(27, 6, "Grand Prix I", Environment.CHAMPIONSHIP, 6.0f, 220, 5, TrafficDensity.HEAVY, Difficulty.MASTER, 1100, 460, 45, 26))
        add(level(28, 6, "Grand Prix II", Environment.CHAMPIONSHIP, 7.0f, 240, 6, TrafficDensity.HEAVY, Difficulty.MASTER, 1300, 540, 50, 27))
        add(level(29, 6, "World Final", Environment.CHAMPIONSHIP, 8.0f, 260, 6, TrafficDensity.EXTREME, Difficulty.MASTER, 1500, 620, 55, 28))
        add(level(30, 6, "Ultimate Champion", Environment.CHAMPIONSHIP, 10.0f, 0, 7, TrafficDensity.EXTREME, Difficulty.MASTER, 2000, 800, 100, 29))
    }

    private fun level(
        id: Int,
        worldId: Int,
        name: String,
        env: Environment,
        length: Float,
        timeLimit: Int,
        opponents: Int,
        traffic: TrafficDensity,
        difficulty: Difficulty,
        coins: Int,
        xp: Int,
        gems: Int,
        requires: Int,
    ) = Level(
        id = id,
        number = id,
        worldId = worldId,
        name = name,
        description = "Race through ${env.name.lowercase().replace('_', ' ')} terrain.",
        environment = env,
        trackLength = length,
        timeLimit = timeLimit,
        opponentCount = opponents,
        trafficDensity = traffic,
        opponentDifficulty = difficulty,
        starObjectives = StarObjectives(
            oneStar = LevelObjective("Finish the race", targetPosition = opponents + 1),
            twoStar = LevelObjective("Finish in top 3", targetPosition = 3),
            threeStar = LevelObjective("Finish 1st", targetPosition = 1),
        ),
        rewardCoins = coins,
        rewardXp = xp,
        rewardGems = gems,
        unlockRequires = requires,
    )

    fun getById(id: Int) = all.find { it.id == id }
    fun getByWorld(worldId: Int) = all.filter { it.worldId == worldId }

    data class World(
        val id: Int,
        val name: String,
        val description: String,
        val environment: Environment,
    )
}
