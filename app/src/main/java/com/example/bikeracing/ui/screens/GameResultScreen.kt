package com.example.bikeracing.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.data.local.LevelCatalog
import kotlinx.coroutines.delay
import com.example.bikeracing.ui.components.RacingButton
import com.example.bikeracing.ui.components.StarRating
import com.example.bikeracing.ui.theme.DarkBg
import com.example.bikeracing.ui.theme.DarkCard
import com.example.bikeracing.ui.theme.DarkCardAlt
import com.example.bikeracing.ui.theme.ElectricBlue
import com.example.bikeracing.ui.theme.GoldColor
import com.example.bikeracing.ui.theme.NeonGreen
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.RacingRed
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.ui.theme.TextSecondary
import com.example.bikeracing.viewmodel.GameViewModel
import kotlin.math.roundToInt

@Composable
fun GameResultScreen(
    viewModel: GameViewModel,
    levelId: Int,
    onRetry: () -> Unit,
    onNextLevel: (Int) -> Unit,
    onHome: () -> Unit,
    onGarage: () -> Unit,
) {
    val result by viewModel.lastRaceResult.collectAsState()
    val nextLevel = LevelCatalog.getById(levelId + 1)

    var animStars by remember { mutableStateOf(0) }
    val starsTarget = result?.starsEarned ?: 0

    LaunchedEffect(starsTarget) {
        delay(600)
        for (i in 1..starsTarget) {
            animStars = i
            delay(400)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(Modifier.height(16.dp))

            // Position badge
            val pos = result?.finalPosition ?: 1
            PositionBadge(pos)

            // Stars
            Spacer(Modifier.height(8.dp))
            StarRating(stars = animStars, maxStars = 3)

            // Title
            val posLabel = when (pos) { 1 -> "CHAMPION!"; 2 -> "GREAT RACE!"; 3 -> "PODIUM!"; else -> "RACE COMPLETE" }
            Text(
                posLabel,
                color = if (pos == 1) GoldColor else TextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                letterSpacing = 2.sp,
            )

            // Stats grid
            result?.let { r ->
                StatsGrid(r)
                RewardsRow(r.coinsEarned, r.xpEarned)
            }

            Spacer(Modifier.height(8.dp))

            // Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RacingButton(
                    text = "RETRY",
                    onClick = onRetry,
                    modifier = Modifier.weight(1f),
                    primary = false,
                )
                if (nextLevel != null) {
                    RacingButton(
                        text = "NEXT",
                        onClick = { onNextLevel(nextLevel.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            RacingButton(
                text = "HOME",
                onClick = onHome,
                modifier = Modifier.fillMaxWidth(),
                primary = false,
            )
            RacingButton(
                text = "GARAGE",
                onClick = onGarage,
                modifier = Modifier.fillMaxWidth(),
                primary = false,
            )
        }
    }
}

@Composable
private fun PositionBadge(position: Int) {
    val color = when (position) { 1 -> GoldColor; 2 -> GoldColor.copy(alpha = 0.7f); 3 -> NeonOrange; else -> TextSecondary }
    val suffix = when (position) { 1 -> "ST"; 2 -> "ND"; 3 -> "RD"; else -> "TH" }

    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(color.copy(alpha = 0.3f), DarkCard)))
            .border(3.dp, color.copy(alpha = 0.8f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$position", color = color, fontWeight = FontWeight.Black, fontSize = 38.sp, lineHeight = 38.sp)
            Text(suffix, color = color.copy(alpha = 0.7f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun StatsGrid(result: com.example.bikeracing.domain.model.RaceResult) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("RACE STATS", color = NeonOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
        Spacer(Modifier.height(4.dp))
        val mins = (result.raceTimeSeconds / 60).toInt()
        val secs = (result.raceTimeSeconds % 60).toInt()
        StatRow("Race Time", "%02d:%02d".format(mins, secs), ElectricBlue)
        StatRow("Max Speed", "${result.maxSpeedKmh.roundToInt()} km/h", NeonOrange)
        StatRow("Distance", "${"%.2f".format(result.distanceKm)} km", NeonGreen)
        StatRow("Overtakes", "${result.overtakes}", GoldColor)
        StatRow("Position", "${result.finalPosition} / ${result.totalRacers}", if (result.finalPosition == 1) GoldColor else TextSecondary)
    }
}

@Composable
private fun StatRow(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = TextSecondary, fontSize = 13.sp)
        Text(value, color = valueColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
private fun RewardsRow(coins: Int, xp: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCardAlt)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RewardItem("🪙", "+$coins", "Coins", GoldColor)
        Spacer(
            Modifier
                .width(1.dp)
                .height(36.dp)
                .background(androidx.compose.ui.graphics.Color(0xFF252540))
        )
        RewardItem("⭐", "+$xp", "XP", ElectricBlue)
    }
}

@Composable
private fun RewardItem(icon: String, value: String, label: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 22.sp)
        Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Text(label, color = TextSecondary, fontSize = 11.sp)
    }
}
