package com.example.bikeracing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.data.local.LevelCatalog
import com.example.bikeracing.domain.model.Difficulty
import com.example.bikeracing.domain.model.Environment
import com.example.bikeracing.ui.components.RacingButton
import com.example.bikeracing.ui.components.SectionLabel
import com.example.bikeracing.ui.components.StarRating
import com.example.bikeracing.ui.theme.DarkBg
import com.example.bikeracing.ui.theme.DarkCard
import com.example.bikeracing.ui.theme.DarkCardAlt
import com.example.bikeracing.ui.theme.DarkCardMid
import com.example.bikeracing.ui.theme.DarkDivider
import com.example.bikeracing.ui.theme.ElectricBlue
import com.example.bikeracing.ui.theme.FireMid
import com.example.bikeracing.ui.theme.GoldColor
import com.example.bikeracing.ui.theme.NeonGreen
import com.example.bikeracing.ui.theme.NeonOrange
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
    val accent = environmentColor(level.environment)

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {

        // Ambient environment glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.TopCenter)
                .blur(100.dp)
                .background(Brush.radialGradient(listOf(accent.copy(alpha = 0.3f), Color.Transparent)))
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(DarkCard, DarkCardMid)))
                    .border(width = 1.dp, brush = Brush.horizontalGradient(listOf(accent.copy(alpha = 0.5f), DarkDivider)), shape = RoundedCornerShape(0.dp))
                    .padding(vertical = 4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextPrimary)
                    }
                    Text(
                        "LEVEL ${level.number}",
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 3.sp,
                        modifier = Modifier.weight(1f),
                    )
                    StarRating(stars = stars, maxStars = 3)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // ── Hero card ─────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(listOf(accent.copy(alpha = 0.22f), DarkCard, DarkCardMid))
                        )
                        .border(
                            1.dp,
                            Brush.verticalGradient(listOf(accent.copy(alpha = 0.8f), accent.copy(alpha = 0.15f))),
                            RoundedCornerShape(20.dp),
                        )
                        .padding(24.dp),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(environmentEmoji(level.environment), fontSize = 52.sp)
                        Spacer(Modifier.height(10.dp))
                        Text(level.name, color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 24.sp, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            level.environment.name.replace('_', ' '),
                            color = accent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp,
                        )
                    }
                }

                // ── Race info ─────────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkCard)
                        .border(1.dp, DarkDivider, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                ) {
                    SectionLabel("RACE INFO")
                    Spacer(Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        InfoTile("🛣", "${level.trackLength} km",  "Distance",   accent)
                        InfoTile("⏱", if (level.timeLimit > 0) "${level.timeLimit}s" else "∞", "Time Limit", FireMid)
                        InfoTile("🏍", "${level.opponentCount}",    "Opponents",  ElectricBlue)
                        InfoTile("⚡", difficultyLabel(level.opponentDifficulty), "Difficulty", difficultyColor(level.opponentDifficulty))
                    }
                }

                // ── Objectives ────────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkCard)
                        .border(1.dp, DarkDivider, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                ) {
                    SectionLabel("OBJECTIVES")
                    Spacer(Modifier.height(12.dp))
                    ObjectiveLine("★☆☆", level.starObjectives.oneStar.description,   GoldColor.copy(alpha = 0.5f))
                    HorizontalDivider(color = DarkDivider, modifier = Modifier.padding(vertical = 8.dp))
                    ObjectiveLine("★★☆", level.starObjectives.twoStar.description,   GoldColor.copy(alpha = 0.75f))
                    HorizontalDivider(color = DarkDivider, modifier = Modifier.padding(vertical = 8.dp))
                    ObjectiveLine("★★★", level.starObjectives.threeStar.description, GoldColor)
                }

                // ── Rewards ───────────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkCardAlt)
                        .border(1.dp, DarkDivider, RoundedCornerShape(16.dp))
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RewardTile("🪙", "${level.rewardCoins}", "Coins", GoldColor)
                    Box(modifier = Modifier.width(1.dp).height(40.dp).background(DarkDivider))
                    RewardTile("⭐", "${level.rewardXp}", "XP", ElectricBlue)
                    if (level.rewardGems > 0) {
                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(DarkDivider))
                        RewardTile("💎", "${level.rewardGems}", "Gems", NeonGreen)
                    }
                }

                Spacer(Modifier.weight(1f))

                // ── Play button ───────────────────────────────────────────────
                RacingButton(
                    text    = "▶  START RACE",
                    onClick = onPlay,
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun InfoTile(icon: String, value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.15f))
                .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(icon, fontSize = 20.sp)
        }
        Spacer(Modifier.height(6.dp))
        Text(value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
        Text(label, color = TextSecondary, fontSize = 9.sp, letterSpacing = 0.5.sp)
    }
}

@Composable
private fun ObjectiveLine(stars: String, description: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(stars, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(12.dp))
        Text(description, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun RewardTile(icon: String, value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon,  fontSize = 24.sp)
        Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 16.sp)
        Text(label, color = TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
    }
}

private fun difficultyLabel(d: Difficulty) = when (d) {
    Difficulty.BEGINNER -> "Easy"; Difficulty.EASY -> "Easy"
    Difficulty.MEDIUM   -> "Med";  Difficulty.HARD -> "Hard"
    Difficulty.EXPERT   -> "Pro";  Difficulty.MASTER -> "Elite"
}
private fun difficultyColor(d: Difficulty) = when (d) {
    Difficulty.BEGINNER, Difficulty.EASY -> NeonGreen
    Difficulty.MEDIUM                    -> NeonOrange
    Difficulty.HARD                      -> FireMid
    Difficulty.EXPERT, Difficulty.MASTER -> com.example.bikeracing.ui.theme.RacingRed
}
private fun environmentEmoji(env: Environment) = when (env) {
    Environment.CITY           -> "🏙"; Environment.HIGHWAY -> "🛣"
    Environment.DESERT         -> "🏜"; Environment.MOUNTAIN -> "⛰"
    Environment.NIGHT          -> "🌙"; Environment.CHAMPIONSHIP -> "🏆"
}
private fun environmentColor(env: Environment) = when (env) {
    Environment.CITY           -> NeonOrange
    Environment.HIGHWAY        -> ElectricBlue
    Environment.DESERT         -> GoldColor
    Environment.MOUNTAIN       -> NeonGreen
    Environment.NIGHT          -> com.example.bikeracing.ui.theme.NeonPurple
    Environment.CHAMPIONSHIP   -> com.example.bikeracing.ui.theme.RacingRed
}
