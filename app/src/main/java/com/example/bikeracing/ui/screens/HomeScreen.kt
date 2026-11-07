package com.example.bikeracing.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.ui.theme.*
import com.example.bikeracing.viewmodel.GameViewModel
import kotlin.math.sin

@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    onNavigate: (com.example.bikeracing.ui.navigation.Screen) -> Unit,
    onPlayClick: () -> Unit,
    onCareerClick: () -> Unit,
    onGarageClick: () -> Unit,
    onShopClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onDailyRewardClick: () -> Unit,
) {
    val profile by viewModel.playerProfile.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "home_anim")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Animated background effects
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .align(Alignment.TopCenter)
                .blur(120.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            FireMid.copy(alpha = 0.3f),
                            FireStart.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        radius = 800f
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.BottomCenter)
                .blur(100.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            ElectricBlue.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        radius = 700f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            // Top Status Bar
            TopStatusBar(
                playerName = profile.name,
                playerLevel = profile.level,
                coins = profile.coins,
                gems = profile.gems,
                onProfileClick = onProfileClick
            )

            Spacer(Modifier.height(40.dp))

            // Game Logo Section
            GameLogoSection(pulse, glowAlpha)

            Spacer(Modifier.height(50.dp))

            // Main Action Buttons
            MainActionButtons(
                onPlayClick = onPlayClick,
                onCareerClick = onCareerClick
            )

            Spacer(Modifier.height(32.dp))

            // Menu Grid
            MenuGrid(
                onGarageClick = onGarageClick,
                onShopClick = onShopClick,
                onDailyRewardClick = onDailyRewardClick,
                onLeaderboardClick = onLeaderboardClick,
                onSettingsClick = onSettingsClick,
                dailyAvailable = viewModel.isDailyRewardAvailable()
            )

            Spacer(Modifier.weight(1f))

            // Version Info
            Text(
                "NAIROBI CITY EDITION • v1.0",
                color = TextSecondary.copy(alpha = 0.4f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TopStatusBar(
    playerName: String,
    playerLevel: Int,
    coins: Int,
    gems: Int,
    onProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player Avatar & Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onProfileClick() }
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(FireMid, FireStart, RacingRed)
                            )
                        )
                        .border(2.5.dp, NeonOrange.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        playerName,
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NeonOrange.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, NeonOrange.copy(alpha = 0.4f))
                    ) {
                        Text(
                            "LVL $playerLevel",
                            color = NeonOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Currency
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CurrencyChip("🪙", formatNumber(coins), GoldColor)
                CurrencyChip("💎", formatNumber(gems), NeonCyan)
            }
        }
    }
}

@Composable
private fun CurrencyChip(icon: String, amount: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.5.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 14.sp)
            Spacer(Modifier.width(4.dp))
            Text(
                amount,
                color = color,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun GameLogoSection(pulse: Float, glowAlpha: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Bike Icon with Glow
        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer glow
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(pulse)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                FireMid.copy(alpha = glowAlpha * 0.4f),
                                FireStart.copy(alpha = glowAlpha * 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )
            // Inner circle
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(FireMid, FireStart, RacingRed)
                        )
                    )
                    .border(
                        3.dp,
                        Brush.sweepGradient(
                            listOf(FireStart, NeonOrange, FireMid, FireStart)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🏍", fontSize = 56.sp)
            }
        }

        Spacer(Modifier.height(20.dp))

        // Game Title
        Text(
            "BIKE",
            color = TextPrimary,
            fontWeight = FontWeight.Black,
            fontSize = 52.sp,
            letterSpacing = 12.sp
        )
        Text(
            "RACING",
            color = FireMid,
            fontWeight = FontWeight.Black,
            fontSize = 52.sp,
            letterSpacing = 10.sp
        )

        Spacer(Modifier.height(12.dp))

        // Subtitle with dividers
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, NeonOrange.copy(alpha = 0.6f))
                        )
                    )
            )
            Spacer(Modifier.width(12.dp))
            Text(
                "NAIROBI EDITION",
                color = NeonOrange.copy(alpha = glowAlpha),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp
            )
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(NeonOrange.copy(alpha = 0.6f), Color.Transparent)
                        )
                    )
            )
        }
    }
}

@Composable
private fun MainActionButtons(
    onPlayClick: () -> Unit,
    onCareerClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // PLAY NOW - Primary Button
        PrimaryButton3D(
            text = "▶  PLAY NOW",
            onClick = onPlayClick,
            gradient = Brush.horizontalGradient(
                listOf(FireStart, FireMid, NeonOrange)
            ),
            glowColor = FireMid
        )

        // CAREER MODE - Secondary Button
        SecondaryButton3D(
            text = "CAREER MODE",
            onClick = onCareerClick,
            borderColor = ElectricBlue
        )
    }
}

@Composable
private fun PrimaryButton3D(
    text: String,
    onClick: () -> Unit,
    gradient: Brush,
    glowColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(12.dp, RoundedCornerShape(16.dp))
    ) {
        // Glow effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp)
                .background(glowColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        )
        // Main button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(gradient)
                .border(
                    2.dp,
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    RoundedCornerShape(16.dp)
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text,
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun SecondaryButton3D(
    text: String,
    onClick: () -> Unit,
    borderColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(8.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        borderColor.copy(alpha = 0.2f),
                        DarkCardMid
                    )
                )
            )
            .border(
                2.dp,
                Brush.verticalGradient(
                    listOf(
                        borderColor.copy(alpha = 0.7f),
                        borderColor.copy(alpha = 0.3f)
                    )
                ),
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            letterSpacing = 2.sp
        )
    }
}

@Composable
private fun MenuGrid(
    onGarageClick: () -> Unit,
    onShopClick: () -> Unit,
    onDailyRewardClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onSettingsClick: () -> Unit,
    dailyAvailable: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // First row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MenuButton3D(
                icon = Icons.Default.Garage,
                label = "GARAGE",
                color = NeonOrange,
                onClick = onGarageClick,
                modifier = Modifier.weight(1f)
            )
            MenuButton3D(
                icon = Icons.Default.ShoppingCart,
                label = "SHOP",
                color = ElectricBlue,
                onClick = onShopClick,
                modifier = Modifier.weight(1f)
            )
            MenuButton3D(
                icon = Icons.Default.Star,
                label = "DAILY",
                color = GoldColor,
                onClick = onDailyRewardClick,
                badge = dailyAvailable,
                modifier = Modifier.weight(1f)
            )
        }

        // Second row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MenuButton3D(
                icon = Icons.Default.Leaderboard,
                label = "RANKS",
                color = NeonCyan,
                onClick = onLeaderboardClick,
                modifier = Modifier.weight(1f)
            )
            MenuButton3D(
                icon = Icons.Default.EmojiEvents,
                label = "TROPHIES",
                color = Color(0xFFAA00FF),
                onClick = { },
                modifier = Modifier.weight(1f)
            )
            MenuButton3D(
                icon = Icons.Default.Settings,
                label = "SETTINGS",
                color = TextSecondary,
                onClick = onSettingsClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MenuButton3D(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    badge: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = color.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = BorderStroke(
                1.5.dp,
                Brush.verticalGradient(
                    listOf(
                        color.copy(alpha = 0.6f),
                        color.copy(alpha = 0.2f)
                    )
                )
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = color,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        label,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Badge indicator
        if (badge) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.TopEnd)
                    .offset((-8).dp, 8.dp)
                    .clip(CircleShape)
                    .background(RacingRed)
                    .border(2.dp, DarkBg, CircleShape)
            )
        }
    }
}

private fun formatNumber(n: Int): String = when {
    n >= 1_000_000 -> "${"%.1f".format(n / 1_000_000f)}M"
    n >= 1_000 -> "${"%.1f".format(n / 1_000f)}K"
    else -> "$n"
}
