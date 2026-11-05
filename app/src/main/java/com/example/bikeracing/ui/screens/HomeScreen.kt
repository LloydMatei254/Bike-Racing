package com.example.bikeracing.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Garage
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.ui.components.RacingButton
import com.example.bikeracing.ui.navigation.Screen
import com.example.bikeracing.ui.theme.DarkBg
import com.example.bikeracing.ui.theme.DarkCard
import com.example.bikeracing.ui.theme.DarkCardAlt
import com.example.bikeracing.ui.theme.ElectricBlue
import com.example.bikeracing.ui.theme.GoldColor
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.RacingRed
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.ui.theme.TextSecondary
import com.example.bikeracing.viewmodel.GameViewModel

@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    onNavigate: (Screen) -> Unit,
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
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse",
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Background gradient rings
        Box(
            modifier = Modifier
                .size(600.dp)
                .align(Alignment.Center)
                .scale(pulse)
                .blur(80.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            RacingRed.copy(alpha = 0.08f),
                            ElectricBlue.copy(alpha = 0.05f),
                            Color.Transparent,
                        )
                    ),
                    CircleShape,
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))

            // Top bar: profile + currency
            TopBar(
                playerName = profile.name,
                playerLevel = profile.level,
                coins = profile.coins,
                gems = profile.gems,
                onProfileClick = onProfileClick,
            )

            Spacer(Modifier.height(20.dp))

            // Game logo
            GameLogo(glowAlpha)

            Spacer(Modifier.height(24.dp))

            // Main play button
            RacingButton(
                text = "PLAY",
                onClick = onPlayClick,
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(56.dp),
            )

            Spacer(Modifier.height(12.dp))

            // Career button
            RacingButton(
                text = "CAREER MODE",
                onClick = onCareerClick,
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(48.dp),
                primary = false,
            )

            Spacer(Modifier.height(24.dp))

            // Menu grid
            MenuGrid(
                onGarage = onGarageClick,
                onShop = onShopClick,
                onDailyReward = onDailyRewardClick,
                onLeaderboard = onLeaderboardClick,
                onSettings = onSettingsClick,
                dailyRewardAvailable = viewModel.isDailyRewardAvailable(),
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TopBar(
    playerName: String,
    playerLevel: Int,
    coins: Int,
    gems: Int,
    onProfileClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Player avatar + name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onProfileClick() }
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(NeonOrange, RacingRed))
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(playerName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("LVL $playerLevel", color = NeonOrange, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        // Currency
        Row(verticalAlignment = Alignment.CenterVertically) {
            CurrencyBadge(icon = "🪙", amount = coins, color = GoldColor)
            Spacer(Modifier.width(8.dp))
            CurrencyBadge(icon = "💎", amount = gems, color = ElectricBlue)
        }
    }
}

@Composable
private fun CurrencyBadge(icon: String, amount: Int, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(icon, fontSize = 13.sp)
        Spacer(Modifier.width(3.dp))
        Text(
            formatNumber(amount),
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun GameLogo(glowAlpha: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Decorative bike icon
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(RacingRed.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
                .border(2.dp, RacingRed.copy(alpha = glowAlpha), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("🏍", fontSize = 44.sp)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = "BIKE",
            color = TextPrimary,
            fontWeight = FontWeight.Black,
            fontSize = 42.sp,
            letterSpacing = 8.sp,
        )
        Text(
            text = "RACING",
            color = RacingRed,
            fontWeight = FontWeight.Black,
            fontSize = 42.sp,
            letterSpacing = 8.sp,
        )
        Text(
            text = "3D",
            color = NeonOrange.copy(alpha = glowAlpha),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            letterSpacing = 4.sp,
        )
    }
}

@Composable
private fun MenuGrid(
    onGarage: () -> Unit,
    onShop: () -> Unit,
    onDailyReward: () -> Unit,
    onLeaderboard: () -> Unit,
    onSettings: () -> Unit,
    dailyRewardAvailable: Boolean,
) {
    val items = listOf(
        MenuItem(Icons.Default.Garage, "Garage", onGarage, NeonOrange),
        MenuItem(Icons.Default.ShoppingCart, "Shop", onShop, ElectricBlue),
        MenuItem(Icons.Default.Star, "Daily\nReward", onDailyReward, GoldColor, badge = dailyRewardAvailable),
        MenuItem(Icons.Default.Leaderboard, "Leaders", onLeaderboard, Color(0xFF9C27B0)),
        MenuItem(Icons.Default.EmojiEvents, "Achieve", {} , Color(0xFF00BCD4)),
        MenuItem(Icons.Default.Settings, "Settings", onSettings, TextSecondary),
    )

    // 2-column grid
    val chunked = items.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        chunked.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { item ->
                    MenuItemCard(item, Modifier.weight(1f))
                }
            }
        }
    }
}

private data class MenuItem(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
    val tint: Color,
    val badge: Boolean = false,
)

@Composable
private fun MenuItemCard(item: MenuItem, modifier: Modifier) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkCardAlt)
                .border(1.dp, item.tint.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                .clickable { item.onClick() }
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(item.icon, contentDescription = item.label, tint = item.tint, modifier = Modifier.size(26.dp))
            Spacer(Modifier.height(6.dp))
            Text(
                item.label,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp,
            )
        }
        if (item.badge) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(RacingRed)
            )
        }
    }
}

private fun formatNumber(n: Int): String = when {
    n >= 1_000_000 -> "${n / 1_000_000}M"
    n >= 1_000 -> "${n / 1_000}K"
    else -> "$n"
}
