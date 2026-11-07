package com.example.bikeracing.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.data.local.LevelCatalog
import com.example.bikeracing.ui.components.RacingButton
import com.example.bikeracing.ui.components.SectionLabel
import com.example.bikeracing.ui.components.StarRating
import com.example.bikeracing.ui.theme.DarkBg
import com.example.bikeracing.ui.theme.DarkCard
import com.example.bikeracing.ui.theme.DarkCardAlt
import com.example.bikeracing.ui.theme.DarkDivider
import com.example.bikeracing.ui.theme.ElectricBlue
import com.example.bikeracing.ui.theme.FireEnd
import com.example.bikeracing.ui.theme.FireMid
import com.example.bikeracing.ui.theme.FireStart
import com.example.bikeracing.ui.theme.GoldColor
import com.example.bikeracing.ui.theme.NeonGreen
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.ui.theme.TextSecondary
import com.example.bikeracing.viewmodel.GameViewModel
import kotlinx.coroutines.delay
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
    val result    by viewModel.lastRaceResult.collectAsState()
    val nextLevel  = LevelCatalog.getById(levelId + 1)

    var animStars by remember { mutableStateOf(0) }
    val starsTarget = result?.starsEarned ?: 0
    val pos = result?.finalPosition ?: 1
    val posColor = when (pos) { 1 -> GoldColor; 2 -> Color(0xFFCFD8DC); 3 -> Color(0xFFCD7F32); else -> TextSecondary }

    LaunchedEffect(starsTarget) {
        delay(700)
        for (i in 1..starsTarget) { animStars = i; delay(420) }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "result_anim")
    val glowPulse by infiniteTransition.animateFloat(
        0.6f, 1.0f,
        infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow",
    )

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {

        // Background position glow
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.TopCenter)
                .blur(120.dp)
                .scale(glowPulse)
                .background(Brush.radialGradient(listOf(posColor.copy(alpha = 0.25f), Color.Transparent)))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Spacer(Modifier.height(24.dp))

            // ── Position badge ────────────────────────────────────────────────
            PositionBadge(pos, posColor, glowPulse)

            // ── Stars ─────────────────────────────────────────────────────────
            StarRating(stars = animStars, maxStars = 3)

            // ── Title ─────────────────────────────────────────────────────────
            val title = when (pos) { 1 -> "CHAMPION!"; 2 -> "GREAT RACE!"; 3 -> "PODIUM FINISH!"; else -> "RACE COMPLETE" }
            Text(
                title,
                color     = posColor,
                fontWeight = FontWeight.Black,
                fontSize  = 30.sp,
                letterSpacing = 2.sp,
            )

            // ── Stats card ────────────────────────────────────────────────────
            result?.let { r ->
                StatsCard(r, posColor)
                RewardsCard(r.coinsEarned, r.xpEarned)
            }

            Spacer(Modifier.height(4.dp))

            // ── Action buttons ────────────────────────────────────────────────
            if (nextLevel != null) {
                RacingButton(
                    text    = "▶  NEXT LEVEL",
                    onClick = { onNextLevel(nextLevel.id) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                )
                Spacer(Modifier.height(0.dp))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                RacingButton("RETRY",  onRetry,  Modifier.weight(1f).height(50.dp), primary = false)
                RacingButton("GARAGE", onGarage, Modifier.weight(1f).height(50.dp), primary = false)
            }
            RacingButton("HOME", onHome, Modifier.fillMaxWidth().height(50.dp), primary = false)

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PositionBadge(position: Int, color: Color, pulse: Float) {
    val suffix = when (position) { 1 -> "ST"; 2 -> "ND"; 3 -> "RD"; else -> "TH" }
    Box(
        modifier = Modifier
            .size(130.dp)
            .scale(if (position == 1) 0.9f + pulse * 0.1f else 1f)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(listOf(color.copy(alpha = 0.25f), color.copy(alpha = 0.05f), Color.Transparent))
            )
            .border(
                3.dp,
                Brush.sweepGradient(listOf(color, color.copy(alpha = 0.3f), color)),
                CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$position", color = color, fontWeight = FontWeight.Black, fontSize = 52.sp, lineHeight = 52.sp)
            Text(suffix, color = color.copy(alpha = 0.7f), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, letterSpacing = 2.sp)
        }
    }
}

@Composable
private fun StatsCard(result: com.example.bikeracing.domain.model.RaceResult, accent: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(listOf(accent.copy(alpha = 0.08f), DarkCard)))
            .border(
                1.dp,
                Brush.verticalGradient(listOf(accent.copy(alpha = 0.5f), DarkDivider)),
                RoundedCornerShape(18.dp),
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SectionLabel("RACE STATS", color = accent)
        val mins = (result.raceTimeSeconds / 60).toInt()
        val secs = (result.raceTimeSeconds % 60).toInt()
        StatRow("Race Time",  "%02d:%02d".format(mins, secs),                  ElectricBlue)
        StatRow("Top Speed",  "${result.maxSpeedKmh.roundToInt()} km/h",       FireMid)
        StatRow("Distance",   "${"%.2f".format(result.distanceKm)} km",        NeonGreen)
        StatRow("Overtakes",  "${result.overtakes}",                            GoldColor)
        StatRow("Finish",     "${result.finalPosition} / ${result.totalRacers}", accent)
    }
}

@Composable
private fun StatRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = TextSecondary, fontSize = 13.sp)
        Text(value, color = valueColor, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
    }
}

@Composable
private fun RewardsCard(coins: Int, xp: Int) {
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
        RewardItem("🪙", "+$coins", "Coins", GoldColor)
        Box(modifier = Modifier.width(1.dp).height(44.dp).background(DarkDivider))
        RewardItem("⭐", "+$xp",   "XP",    ElectricBlue)
    }
}

@Composable
private fun RewardItem(icon: String, value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon,  fontSize = 24.sp)
        Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 22.sp)
        Text(label, color = TextSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
    }
}
