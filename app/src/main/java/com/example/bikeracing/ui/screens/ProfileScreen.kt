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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.ui.theme.DarkBg
import com.example.bikeracing.ui.theme.DarkCard
import com.example.bikeracing.ui.theme.DarkCardAlt
import com.example.bikeracing.ui.theme.ElectricBlue
import com.example.bikeracing.ui.theme.GoldColor
import com.example.bikeracing.ui.theme.NeonGreen
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.ui.theme.TextSecondary
import com.example.bikeracing.viewmodel.GameViewModel
import kotlin.math.roundToInt

@Composable
fun ProfileScreen(viewModel: GameViewModel, onBack: () -> Unit) {
    val profile by viewModel.playerProfile.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().background(DarkCard).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text("PROFILE", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp, letterSpacing = 1.sp)
            }
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Avatar
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(DarkCard).padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(NeonOrange, com.example.bikeracing.ui.theme.RacingRed)))
                            .border(3.dp, NeonOrange.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("🏍", fontSize = 36.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(profile.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(Modifier.height(6.dp))
                    // XP bar
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("LVL ${profile.level}", color = NeonOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(Modifier.width(8.dp))
                        LinearProgressIndicator(
                            progress = { profile.progressToNextLevel },
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(4.dp)),
                            color = NeonOrange,
                            trackColor = DarkCardAlt,
                            strokeCap = StrokeCap.Round,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("LVL ${profile.level + 1}", color = TextSecondary, fontSize = 11.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${profile.xp % profile.xpForNextLevel} / ${profile.xpForNextLevel} XP",
                        color = TextSecondary,
                        fontSize = 11.sp,
                    )
                }

                // Stats grid
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(DarkCard).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("STATISTICS", color = NeonOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                    Spacer(Modifier.height(4.dp))
                    StatRow2("Total Races", "${profile.totalRaces}", ElectricBlue)
                    StatRow2("Total Wins", "${profile.totalWins}", GoldColor)
                    StatRow2("Win Rate", if (profile.totalRaces > 0) "${(profile.totalWins * 100f / profile.totalRaces).roundToInt()}%" else "0%", NeonGreen)
                    StatRow2("Distance", "${"%.1f".format(profile.totalDistanceKm)} km", NeonOrange)
                    StatRow2("Best Speed", "${profile.bestSpeedKmh.roundToInt()} km/h", com.example.bikeracing.ui.theme.RacingRed)
                    StatRow2("Motorcycles", "${profile.ownedMotorcycleIds.size}", TextPrimary)
                }

                // Currency
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    CurrencyCard("🪙", "${profile.coins}", "Coins", GoldColor, Modifier.weight(1f))
                    CurrencyCard("💎", "${profile.gems}", "Gems", ElectricBlue, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun StatRow2(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextSecondary, fontSize = 13.sp)
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
private fun CurrencyCard(icon: String, value: String, label: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(DarkCard).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(icon, fontSize = 24.sp)
        Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 22.sp)
        Text(label, color = TextSecondary, fontSize = 11.sp)
    }
}
