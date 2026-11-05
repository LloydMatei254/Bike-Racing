package com.example.bikeracing.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.ui.components.RacingButton
import com.example.bikeracing.ui.theme.DarkBg
import com.example.bikeracing.ui.theme.DarkCard
import com.example.bikeracing.ui.theme.DarkCardAlt
import com.example.bikeracing.ui.theme.DarkDivider
import com.example.bikeracing.ui.theme.GoldColor
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.ui.theme.TextSecondary
import com.example.bikeracing.viewmodel.GameViewModel

@Composable
fun DailyRewardScreen(viewModel: GameViewModel, onBack: () -> Unit) {
    val profile by viewModel.playerProfile.collectAsState()
    val available = viewModel.isDailyRewardAvailable()
    val streak = profile.currentStreak
    val nextReward = 100 + (streak + 1) * 50

    val infiniteTransition = rememberInfiniteTransition(label = "reward")
    val glow by infiniteTransition.animateFloat(
        0.8f, 1.2f,
        infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().background(DarkCard).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text("DAILY REWARD", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp, letterSpacing = 1.sp)
            }
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Spacer(Modifier.height(8.dp))
                // Streak display
                Text(
                    "Day Streak",
                    color = TextSecondary,
                    fontSize = 14.sp,
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(if (available) glow else 1f)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(GoldColor.copy(alpha = 0.4f), DarkCard)
                            )
                        )
                        .border(2.dp, GoldColor.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔥", fontSize = 28.sp)
                        Text("$streak", color = GoldColor, fontWeight = FontWeight.Black, fontSize = 26.sp, lineHeight = 26.sp)
                    }
                }

                // 7-day streak grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    for (day in 1..7) {
                        val isPast = day <= streak
                        val isToday = day == (streak + 1) && available
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isToday -> GoldColor.copy(alpha = 0.25f)
                                        isPast -> NeonOrange.copy(alpha = 0.15f)
                                        else -> DarkCardAlt
                                    }
                                )
                                .border(
                                    1.dp,
                                    when {
                                        isToday -> GoldColor
                                        isPast -> NeonOrange.copy(alpha = 0.4f)
                                        else -> DarkDivider
                                    },
                                    RoundedCornerShape(8.dp),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    if (isPast || isToday) "🪙" else "?",
                                    fontSize = 14.sp,
                                )
                                Text(
                                    "D$day",
                                    color = when {
                                        isToday -> GoldColor
                                        isPast -> NeonOrange
                                        else -> TextSecondary
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }

                // Reward amount
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(DarkCard).padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        if (available) "Your Reward Today" else "Come back tomorrow!",
                        color = TextSecondary,
                        fontSize = 13.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🪙", fontSize = 28.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (available) "+$nextReward" else "Already claimed",
                            color = if (available) GoldColor else TextSecondary,
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp,
                        )
                    }
                    if (available) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Streak bonus: +${streak * 50} coins!",
                            color = NeonOrange,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                if (available) {
                    RacingButton(
                        text = "CLAIM REWARD 🪙$nextReward",
                        onClick = { viewModel.claimDailyReward(); onBack() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                            .background(DarkCard).border(1.dp, DarkDivider, RoundedCornerShape(8.dp)).padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Return tomorrow for your next reward!", color = TextSecondary, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}
