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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.ui.theme.DarkBg
import com.example.bikeracing.ui.theme.DarkCard
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

private data class GameModeCard(
    val emoji: String,
    val title: String,
    val description: String,
    val accent: androidx.compose.ui.graphics.Color,
    val onClick: () -> Unit,
    val locked: Boolean = false,
)

@Composable
fun ModeSelectScreen(
    viewModel: GameViewModel,
    onCareer: () -> Unit,
    onBack: () -> Unit,
) {
    val modes = listOf(
        GameModeCard("🏆", "Career Mode", "Progress through 30 levels in 6 worlds", RacingRed, onCareer),
        GameModeCard("⏱", "Time Trial", "Beat the clock on any track", NeonOrange, {}),
        GameModeCard("🚗", "Endless Traffic", "Survive endless traffic at high speed", NeonCyan, {}),
        GameModeCard("🎯", "Championship", "Compete in full championship seasons", GoldColor, {}),
        GameModeCard("🌄", "Free Ride", "Explore tracks without pressure", NeonGreen, {}),
        GameModeCard("⚡", "Challenge Mode", "Special challenges and objectives", NeonPurple, {}),
    )

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
                    "SELECT GAME MODE",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp,
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(modes) { mode ->
                    GameModeCardItem(mode)
                }
            }
        }
    }
}

@Composable
private fun GameModeCardItem(mode: GameModeCard) {
    Box(
        modifier = Modifier
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(mode.accent.copy(alpha = 0.15f), DarkCard)
                )
            )
            .border(1.dp, mode.accent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable(enabled = !mode.locked) { mode.onClick() }
            .padding(16.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(mode.emoji, fontSize = 32.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                mode.title,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 16.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                mode.description,
                color = TextSecondary,
                fontSize = 10.sp,
                lineHeight = 13.sp,
            )
        }
        if (mode.locked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkBg.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center,
            ) {
                Text("🔒", fontSize = 28.sp)
            }
        }
    }
}
