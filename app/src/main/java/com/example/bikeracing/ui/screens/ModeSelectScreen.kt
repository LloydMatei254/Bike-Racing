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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.ui.theme.DarkBg
import com.example.bikeracing.ui.theme.DarkCard
import com.example.bikeracing.ui.theme.DarkCardMid
import com.example.bikeracing.ui.theme.DarkDivider
import com.example.bikeracing.ui.theme.ElectricBlue
import com.example.bikeracing.ui.theme.FireMid
import com.example.bikeracing.ui.theme.GoldColor
import com.example.bikeracing.ui.theme.NeonCyan
import com.example.bikeracing.ui.theme.NeonGreen
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.NeonPurple
import com.example.bikeracing.ui.theme.RacingRed
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.ui.theme.TextSecondary
import com.example.bikeracing.viewmodel.GameViewModel

private data class GameModeItem(
    val emoji: String,
    val title: String,
    val description: String,
    val accent: Color,
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
        GameModeItem("🏆", "Career",          "30 levels · 6 worlds",          RacingRed,   onCareer),
        GameModeItem("⏱", "Time Trial",       "Beat the clock",                NeonOrange,  {}, locked = true),
        GameModeItem("🚗", "Endless Traffic",  "Survive at high speed",         NeonCyan,    {}, locked = true),
        GameModeItem("🎯", "Championship",     "Full season racing",            GoldColor,   {}, locked = true),
        GameModeItem("🌄", "Free Ride",        "Explore without pressure",      NeonGreen,   {}, locked = true),
        GameModeItem("⚡", "Challenge",        "Special objectives",            NeonPurple,  {}, locked = true),
    )

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {

        // Accent top glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .align(Alignment.TopCenter)
                .blur(80.dp)
                .background(Brush.radialGradient(listOf(FireMid.copy(alpha = 0.2f), Color.Transparent)))
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(DarkCard, DarkCardMid)))
                    .border(width = 1.dp, brush = Brush.horizontalGradient(listOf(RacingRed.copy(alpha = 0.4f), DarkDivider)), shape = RoundedCornerShape(0.dp))
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
                        "SELECT MODE",
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 3.sp,
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(modes) { mode ->
                    ModeCard(mode)
                }
            }
        }
    }
}

@Composable
private fun ModeCard(mode: GameModeItem) {
    Box(
        modifier = Modifier
            .height(150.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(mode.accent.copy(alpha = 0.20f), DarkCard, DarkCardMid)
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(mode.accent.copy(alpha = 0.65f), mode.accent.copy(alpha = 0.1f))),
                RoundedCornerShape(18.dp),
            )
            .clickable(enabled = !mode.locked) { mode.onClick() }
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(mode.emoji, fontSize = 34.sp)
            Column {
                Text(
                    mode.title,
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    lineHeight = 17.sp,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    mode.description,
                    color = TextSecondary,
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                )
            }
        }

        if (mode.locked) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(DarkBg.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔒", fontSize = 26.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("LOCKED", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                }
            }
        }
    }
}
