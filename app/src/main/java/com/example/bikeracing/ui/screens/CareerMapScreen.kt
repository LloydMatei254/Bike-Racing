package com.example.bikeracing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.data.local.LevelCatalog
import com.example.bikeracing.domain.model.Environment
import com.example.bikeracing.ui.components.StarRating
import com.example.bikeracing.ui.theme.DarkBg
import com.example.bikeracing.ui.theme.DarkCard
import com.example.bikeracing.ui.theme.DarkCardAlt
import com.example.bikeracing.ui.theme.DarkDivider
import com.example.bikeracing.ui.theme.ElectricBlue
import com.example.bikeracing.ui.theme.GoldColor
import com.example.bikeracing.ui.theme.NeonCyan
import com.example.bikeracing.ui.theme.NeonGreen
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.NeonPurple
import com.example.bikeracing.ui.theme.RacingRed
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.ui.theme.TextSecondary
import com.example.bikeracing.viewmodel.GameViewModel

@Composable
fun CareerMapScreen(
    viewModel: GameViewModel,
    onLevelClick: (Int) -> Unit,
    onBack: () -> Unit,
) {
    val levelProgress by viewModel.levelProgress.collectAsState()
    val unlockedLevels = viewModel.getUnlockedLevels()
    var selectedWorld by remember { mutableIntStateOf(1) }

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
                    "CAREER",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.weight(1f),
                )
                // Total stars
                val totalStars = levelProgress.values.sum()
                val maxStars = LevelCatalog.all.size * 3
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐", fontSize = 14.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "$totalStars/$maxStars",
                        color = GoldColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                }
            }

            // World tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCard)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(LevelCatalog.worlds) { world ->
                    WorldTab(
                        world = world,
                        isSelected = selectedWorld == world.id,
                        onClick = { selectedWorld = world.id },
                        completedCount = LevelCatalog.getByWorld(world.id).count { levelProgress.containsKey(it.id) },
                    )
                }
            }

            // Level grid for selected world
            val worldLevels = LevelCatalog.getByWorld(selectedWorld)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(worldLevels) { level ->
                    val stars = levelProgress[level.id] ?: 0
                    val unlocked = level.id in unlockedLevels
                    LevelCard(
                        level = level,
                        stars = stars,
                        unlocked = unlocked,
                        onClick = { if (unlocked) onLevelClick(level.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun WorldTab(
    world: LevelCatalog.World,
    isSelected: Boolean,
    onClick: () -> Unit,
    completedCount: Int,
) {
    val accent = environmentColor(world.environment)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) Brush.horizontalGradient(listOf(accent.copy(alpha = 0.4f), accent.copy(alpha = 0.2f)))
                else Brush.horizontalGradient(listOf(DarkCardAlt, DarkCardAlt))
            )
            .border(
                1.dp,
                if (isSelected) accent else DarkDivider,
                RoundedCornerShape(20.dp),
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                environmentEmoji(world.environment),
                fontSize = 16.sp,
            )
            Text(
                "W${world.id}",
                color = if (isSelected) accent else TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "$completedCount/5",
                color = TextSecondary,
                fontSize = 10.sp,
            )
        }
    }
}

@Composable
private fun LevelCard(
    level: com.example.bikeracing.domain.model.Level,
    stars: Int,
    unlocked: Boolean,
    onClick: () -> Unit,
) {
    val accent = environmentColor(level.environment)
    val completed = stars > 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (completed)
                    Brush.horizontalGradient(listOf(accent.copy(alpha = 0.12f), DarkCard))
                else
                    Brush.horizontalGradient(listOf(DarkCard, DarkCard))
            )
            .border(
                1.dp,
                if (completed) accent.copy(alpha = 0.5f) else DarkDivider,
                RoundedCornerShape(12.dp),
            )
            .clickable(enabled = unlocked) { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Level number badge
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    if (unlocked) Brush.radialGradient(listOf(accent.copy(alpha = 0.4f), DarkCard))
                    else Brush.radialGradient(listOf(DarkDivider, DarkCard))
                )
                .border(1.dp, if (unlocked) accent else DarkDivider, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                if (!unlocked) "🔒" else "${level.number}",
                color = if (unlocked) TextPrimary else TextSecondary,
                fontWeight = FontWeight.Black,
                fontSize = if (!unlocked) 16.sp else 18.sp,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                level.name,
                color = if (unlocked) TextPrimary else TextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "${level.trackLength} km · ${level.opponentCount} opponents",
                color = TextSecondary,
                fontSize = 11.sp,
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            StarRating(stars = stars)
            if (level.rewardCoins > 0) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🪙", fontSize = 10.sp)
                    Spacer(Modifier.width(2.dp))
                    Text(
                        "${level.rewardCoins}",
                        color = GoldColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

private fun environmentColor(env: Environment) = when (env) {
    Environment.CITY -> NeonOrange
    Environment.HIGHWAY -> ElectricBlue
    Environment.DESERT -> GoldColor
    Environment.MOUNTAIN -> NeonGreen
    Environment.NIGHT -> NeonPurple
    Environment.CHAMPIONSHIP -> RacingRed
}

private fun environmentEmoji(env: Environment) = when (env) {
    Environment.CITY -> "🏙"
    Environment.HIGHWAY -> "🛣"
    Environment.DESERT -> "🏜"
    Environment.MOUNTAIN -> "⛰"
    Environment.NIGHT -> "🌙"
    Environment.CHAMPIONSHIP -> "🏆"
}
