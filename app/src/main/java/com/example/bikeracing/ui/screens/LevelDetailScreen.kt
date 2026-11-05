package com.example.bikeracing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.data.local.LevelCatalog
import com.example.bikeracing.domain.model.Difficulty
import com.example.bikeracing.ui.components.RacingButton
import com.example.bikeracing.ui.components.StarRating
import com.example.bikeracing.ui.theme.DarkBg
import com.example.bikeracing.ui.theme.DarkCard
import com.example.bikeracing.ui.theme.DarkCardAlt
import com.example.bikeracing.ui.theme.DarkDivider
import com.example.bikeracing.ui.theme.ElectricBlue
import com.example.bikeracing.ui.theme.GoldColor
import com.example.bikeracing.ui.theme.NeonGreen
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.RacingRed
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.ui.theme.TextSecondary
import com.example.bikeracing.viewmodel.GameViewModel

@Composable
fun LevelDetailScreen(
    viewModel: GameViewModel,
    levelId: Int,
    onPlay: () -> Unit,
    onBack: () -> Unit,
) {
    val level = LevelCatalog.getById(levelId) ?: return
    val levelProgress by viewModel.levelProgress.collectAsState()
    val stars = levelProgress[levelId] ?: 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCard)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text(
                    "LEVEL ${level.number}",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Level name + stars
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkCard)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(environmentEmoji(level.environment), fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        level.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                    )
                    Text(
                        level.environment.name.replace('_', ' '),
                        color = TextSecondary,
                        fontSize = 12.sp,
                    )
                    Spacer(Modifier.height(12.dp))
                    StarRating(stars = stars, maxStars = 3)
                }

                // Race info
                RaceInfoCard(level)

                // Objectives
                ObjectivesCard(level)

                // Rewards
                RewardsCard(level)

                Spacer(Modifier.weight(1f))

                // Play button
                RacingButton(
                    text = "START RACE",
                    onClick = onPlay,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                )
            }
        }
    }
}

@Composable
private fun RaceInfoCard(level: com.example.bikeracing.domain.model.Level) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .padding(16.dp),
    ) {
        Text("RACE INFO", color = NeonOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            InfoItem("🛣", "Distance", "${level.trackLength} km")
            InfoItem("⏱", "Time Limit", if (level.timeLimit > 0) "${level.timeLimit}s" else "No Limit")
            InfoItem("🏍", "Opponents", "${level.opponentCount}")
            InfoItem("⚡", "Difficulty", difficultyLabel(level.opponentDifficulty))
        }
    }
}

@Composable
private fun InfoItem(icon: String, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 18.sp)
        Text(value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(label, color = TextSecondary, fontSize = 10.sp)
    }
}

@Composable
private fun ObjectivesCard(level: com.example.bikeracing.domain.model.Level) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .padding(16.dp),
    ) {
        Text("OBJECTIVES", color = NeonOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
        Spacer(Modifier.height(10.dp))
        ObjectiveLine("★☆☆", level.starObjectives.oneStar.description, GoldColor.copy(alpha = 0.5f))
        HorizontalDivider(color = DarkDivider, modifier = Modifier.padding(vertical = 6.dp))
        ObjectiveLine("★★☆", level.starObjectives.twoStar.description, GoldColor.copy(alpha = 0.75f))
        HorizontalDivider(color = DarkDivider, modifier = Modifier.padding(vertical = 6.dp))
        ObjectiveLine("★★★", level.starObjectives.threeStar.description, GoldColor)
    }
}

@Composable
private fun ObjectiveLine(stars: String, description: String, color: androidx.compose.ui.graphics.Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(stars, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(10.dp))
        Text(description, color = TextSecondary, fontSize = 13.sp)
    }
}

@Composable
private fun RewardsCard(level: com.example.bikeracing.domain.model.Level) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCardAlt)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RewardBadge("🪙", "${level.rewardCoins}", "Coins", GoldColor)
        RewardBadge("⭐", "${level.rewardXp}", "XP", ElectricBlue)
        if (level.rewardGems > 0) RewardBadge("💎", "${level.rewardGems}", "Gems", NeonGreen)
    }
}

@Composable
private fun RewardBadge(icon: String, value: String, label: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 22.sp)
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(label, color = TextSecondary, fontSize = 10.sp)
    }
}

private fun difficultyLabel(d: Difficulty) = when (d) {
    Difficulty.BEGINNER -> "Beginner"
    Difficulty.EASY -> "Easy"
    Difficulty.MEDIUM -> "Medium"
    Difficulty.HARD -> "Hard"
    Difficulty.EXPERT -> "Expert"
    Difficulty.MASTER -> "Master"
}

private fun environmentEmoji(env: com.example.bikeracing.domain.model.Environment) = when (env) {
    com.example.bikeracing.domain.model.Environment.CITY -> "🏙"
    com.example.bikeracing.domain.model.Environment.HIGHWAY -> "🛣"
    com.example.bikeracing.domain.model.Environment.DESERT -> "🏜"
    com.example.bikeracing.domain.model.Environment.MOUNTAIN -> "⛰"
    com.example.bikeracing.domain.model.Environment.NIGHT -> "🌙"
    com.example.bikeracing.domain.model.Environment.CHAMPIONSHIP -> "🏆"
}
