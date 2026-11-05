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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.domain.model.MotorcycleCategory
import com.example.bikeracing.ui.components.RacingButton
import com.example.bikeracing.ui.components.StatBar
import com.example.bikeracing.ui.theme.DarkBg
import com.example.bikeracing.ui.theme.DarkCard
import com.example.bikeracing.ui.theme.DarkDivider
import com.example.bikeracing.ui.theme.ElectricBlue
import com.example.bikeracing.ui.theme.GoldColor
import com.example.bikeracing.ui.theme.NeonGreen
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.RacingRed
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.ui.theme.TextSecondary
import com.example.bikeracing.viewmodel.GameViewModel

@Composable
fun ShopScreen(viewModel: GameViewModel, onBack: () -> Unit) {
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
                Text("MOTORCYCLE SHOP", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp, letterSpacing = 1.sp, modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🪙", fontSize = 14.sp)
                    Spacer(Modifier.width(4.dp))
                    Text("${profile.coins}", color = GoldColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(viewModel.motorcycles.filter { it.id != "bike_01" }) { bike ->
                    val owned = bike.id in profile.ownedMotorcycleIds
                    val canAfford = profile.coins >= bike.price
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkCard)
                            .border(1.dp, if (owned) NeonGreen.copy(alpha = 0.4f) else DarkDivider, RoundedCornerShape(14.dp))
                            .padding(16.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(bikeEmojiShop(bike.category), fontSize = 36.sp)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(bike.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(bike.description, color = TextSecondary, fontSize = 11.sp)
                            }
                            if (owned) Text("✓", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                        StatBar("Top Speed", bike.baseTopSpeed / 3.5f, barColor = RacingRed)
                        Spacer(Modifier.height(4.dp))
                        StatBar("Acceleration", bike.baseAcceleration, barColor = NeonOrange)
                        Spacer(Modifier.height(4.dp))
                        StatBar("Handling", bike.baseHandling, barColor = ElectricBlue)
                        Spacer(Modifier.height(10.dp))
                        if (!owned) {
                            RacingButton(
                                text = if (bike.gemPrice > 0) "BUY 💎${bike.gemPrice}" else "BUY 🪙${bike.price}",
                                onClick = { viewModel.purchaseBike(bike.id) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = canAfford,
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                                    .background(NeonGreen.copy(alpha = 0.1f)).padding(10.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("OWNED", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun bikeEmojiShop(cat: MotorcycleCategory) = when (cat) {
    MotorcycleCategory.STREET -> "🛵"; MotorcycleCategory.SPORT -> "🏍"
    MotorcycleCategory.SUPERBIKE -> "⚡"; MotorcycleCategory.RACING -> "🏁"
    MotorcycleCategory.PREMIUM -> "👑"
}
