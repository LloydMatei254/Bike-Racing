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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.ui.theme.DarkBg
import com.example.bikeracing.ui.theme.DarkCard
import com.example.bikeracing.ui.theme.DarkCardAlt
import com.example.bikeracing.ui.theme.GoldColor
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.SilverColor
import com.example.bikeracing.ui.theme.BronzeColor
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.ui.theme.TextSecondary

private data class LeaderEntry(val rank: Int, val name: String, val score: Int, val level: Int)

@Composable
fun LeaderboardScreen(onBack: () -> Unit) {
    // Simulated leaderboard data — in production would come from server
    val entries = remember {
        listOf(
            LeaderEntry(1, "SpeedKing99", 850000, 30),
            LeaderEntry(2, "MotoLegend", 780000, 28),
            LeaderEntry(3, "RoadWarrior", 720000, 27),
            LeaderEntry(4, "NightRider", 660000, 25),
            LeaderEntry(5, "DustDevil", 610000, 24),
            LeaderEntry(6, "ApexHunter", 580000, 22),
            LeaderEntry(7, "TurboAce", 540000, 21),
            LeaderEntry(8, "GhostRider", 510000, 20),
            LeaderEntry(9, "BlazingTrail", 490000, 19),
            LeaderEntry(10, "You", 45000, 3),
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().background(DarkCard).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text("LEADERBOARD", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp, letterSpacing = 1.sp)
            }
            // Top 3 podium
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
            ) {
                PodiumItem(entries[1], false)
                PodiumItem(entries[0], true)
                PodiumItem(entries[2], false)
            }
            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                itemsIndexed(entries.drop(3)) { i, entry ->
                    LeaderRow(entry, isMe = entry.name == "You")
                }
            }
        }
    }
}

@Composable
private fun PodiumItem(entry: LeaderEntry, isFirst: Boolean) {
    val color = when (entry.rank) { 1 -> GoldColor; 2 -> SilverColor; else -> BronzeColor }
    val size = if (isFirst) 72.dp else 60.dp
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("${entry.rank}", color = color, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Box(
            modifier = Modifier.size(size).clip(CircleShape)
                .background(Brush.radialGradient(listOf(color.copy(alpha = 0.3f), DarkCard))),
            contentAlignment = Alignment.Center,
        ) { Text("🏍", fontSize = if (isFirst) 28.sp else 22.sp) }
        Spacer(Modifier.height(4.dp))
        Text(entry.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Text("${entry.score}", color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
private fun LeaderRow(entry: LeaderEntry, isMe: Boolean) {
    val bg = if (isMe) NeonOrange.copy(alpha = 0.15f) else DarkCardAlt
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(bg).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("#${entry.rank}", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.width(36.dp))
        Text("🏍", fontSize = 18.sp)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(entry.name, color = if (isMe) NeonOrange else TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text("Level ${entry.level}", color = TextSecondary, fontSize = 10.sp)
        }
        Text("${entry.score}", color = GoldColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
