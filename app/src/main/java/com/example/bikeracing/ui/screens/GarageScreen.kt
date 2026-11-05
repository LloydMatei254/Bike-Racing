package com.example.bikeracing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.domain.model.Motorcycle
import com.example.bikeracing.domain.model.MotorcycleCategory
import com.example.bikeracing.ui.components.RacingButton
import com.example.bikeracing.ui.components.StatBar
import com.example.bikeracing.ui.theme.DarkBg
import com.example.bikeracing.ui.theme.DarkCard
import com.example.bikeracing.ui.theme.DarkCardAlt
import com.example.bikeracing.ui.theme.DarkDivider
import com.example.bikeracing.ui.theme.ElectricBlue
import com.example.bikeracing.ui.theme.GoldColor
import com.example.bikeracing.ui.theme.NeonGreen
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.RacingRed
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.ui.theme.TextSecondary
import com.example.bikeracing.viewmodel.GameViewModel
import com.example.bikeracing.viewmodel.UpgradeStat

@Composable
fun GarageScreen(viewModel: GameViewModel, onBack: () -> Unit) {
    val profile by viewModel.playerProfile.collectAsState()
    val selectedBikeId by viewModel.garageSelectedBikeId.collectAsState()
    val selectedBike = viewModel.motorcycles.find { it.id == selectedBikeId }
        ?: viewModel.motorcycles.first()
    val upgrades = viewModel.getUpgradesFor(selectedBikeId)
    val isOwned = selectedBikeId in profile.ownedMotorcycleIds
    val isEquipped = selectedBikeId == profile.selectedMotorcycleId

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
                    "GARAGE",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.weight(1f),
                )
                // Player coins
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🪙", fontSize = 14.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${profile.coins}",
                        color = GoldColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                }
            }

            // Bike list
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCard)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(viewModel.motorcycles) { bike ->
                    BikeThumb(
                        bike = bike,
                        isSelected = bike.id == selectedBikeId,
                        isOwned = bike.id in profile.ownedMotorcycleIds,
                        isEquipped = bike.id == profile.selectedMotorcycleId,
                        onClick = { viewModel.selectGarageBike(bike.id) },
                    )
                }
            }

            // Detail panel
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BikeDetail(selectedBike, upgrades, isEquipped)

                UpgradesPanel(
                    bike = selectedBike,
                    upgrades = upgrades,
                    coins = profile.coins,
                    isOwned = isOwned,
                    onUpgrade = { stat -> viewModel.upgradeBike(selectedBike.id, stat) },
                )

                if (!isOwned) {
                    RacingButton(
                        text = "BUY  🪙${selectedBike.price}",
                        onClick = { viewModel.purchaseBike(selectedBike.id) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = profile.coins >= selectedBike.price,
                    )
                } else if (!isEquipped) {
                    RacingButton(
                        text = "EQUIP",
                        onClick = { viewModel.equipBike(selectedBike.id) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonGreen.copy(alpha = 0.15f))
                            .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("✓ EQUIPPED", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun BikeThumb(
    bike: Motorcycle,
    isSelected: Boolean,
    isOwned: Boolean,
    isEquipped: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = when {
        isEquipped -> NeonGreen
        isSelected -> NeonOrange
        else -> DarkDivider
    }
    Column(
        modifier = Modifier
            .width(70.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) DarkCardAlt else DarkCard)
            .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(bikeEmoji(bike.category), fontSize = 22.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            bike.name.split(" ").first(),
            color = if (isSelected) TextPrimary else TextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
        )
        if (!isOwned) {
            Text("🔒", fontSize = 10.sp)
        }
    }
}

@Composable
private fun BikeDetail(
    bike: Motorcycle,
    upgrades: com.example.bikeracing.domain.model.MotorcycleUpgrades,
    isEquipped: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard)
            .padding(20.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(bikeEmoji(bike.category), fontSize = 52.sp)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(bike.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    categoryLabel(bike.category),
                    color = categoryColor(bike.category),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                if (isEquipped) {
                    Spacer(Modifier.height(4.dp))
                    Text("EQUIPPED", color = NeonGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }
        if (bike.description.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(bike.description, color = TextSecondary, fontSize = 12.sp)
        }
        Spacer(Modifier.height(16.dp))
        val us = upgrades
        StatBar("TOP SPEED", upgrades.getUpgradedStat(bike.baseTopSpeed, us.engineLevel) / 3.5f, barColor = RacingRed)
        Spacer(Modifier.height(6.dp))
        StatBar("ACCELERATION", upgrades.getUpgradedStat(bike.baseAcceleration, us.accelerationLevel), barColor = NeonOrange)
        Spacer(Modifier.height(6.dp))
        StatBar("HANDLING", upgrades.getUpgradedStat(bike.baseHandling, us.handlingLevel), barColor = ElectricBlue)
        Spacer(Modifier.height(6.dp))
        StatBar("BRAKING", upgrades.getUpgradedStat(bike.baseBraking, us.brakesLevel), barColor = NeonGreen)
        Spacer(Modifier.height(6.dp))
        StatBar("NITRO", upgrades.getUpgradedStat(bike.baseNitroCapacity, us.nitroLevel), barColor = com.example.bikeracing.ui.theme.NeonCyan)
    }
}

@Composable
private fun UpgradesPanel(
    bike: Motorcycle,
    upgrades: com.example.bikeracing.domain.model.MotorcycleUpgrades,
    coins: Int,
    isOwned: Boolean,
    onUpgrade: (UpgradeStat) -> Unit,
) {
    if (!isOwned) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .padding(16.dp),
    ) {
        Text("UPGRADES", color = NeonOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
        Spacer(Modifier.height(10.dp))
        UpgradeRow("Engine", UpgradeStat.ENGINE, upgrades.engineLevel, coins, onUpgrade)
        UpgradeRow("Acceleration", UpgradeStat.ACCELERATION, upgrades.accelerationLevel, coins, onUpgrade)
        UpgradeRow("Brakes", UpgradeStat.BRAKES, upgrades.brakesLevel, coins, onUpgrade)
        UpgradeRow("Handling", UpgradeStat.HANDLING, upgrades.handlingLevel, coins, onUpgrade)
        UpgradeRow("Nitro", UpgradeStat.NITRO, upgrades.nitroLevel, coins, onUpgrade)
        UpgradeRow("Suspension", UpgradeStat.SUSPENSION, upgrades.suspensionLevel, coins, onUpgrade)
    }
}

@Composable
private fun UpgradeRow(
    name: String,
    stat: UpgradeStat,
    level: Int,
    coins: Int,
    onUpgrade: (UpgradeStat) -> Unit,
) {
    val maxLevel = 5
    val cost = (level + 1) * 500
    val canUpgrade = level < maxLevel && coins >= cost

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(100.dp))
        // Level pips
        Row(modifier = Modifier.weight(1f)) {
            for (i in 1..maxLevel) {
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .weight(1f)
                        .padding(horizontal = 1.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (i <= level) NeonOrange else DarkDivider)
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        if (level < maxLevel) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (canUpgrade) NeonOrange.copy(alpha = 0.2f) else DarkDivider.copy(alpha = 0.5f))
                    .border(
                        1.dp,
                        if (canUpgrade) NeonOrange else DarkDivider,
                        RoundedCornerShape(6.dp),
                    )
                    .clickable(enabled = canUpgrade) { onUpgrade(stat) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    "🪙$cost",
                    color = if (canUpgrade) NeonOrange else TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        } else {
            Text("MAX", color = NeonGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private fun bikeEmoji(cat: MotorcycleCategory) = when (cat) {
    MotorcycleCategory.STREET -> "🛵"
    MotorcycleCategory.SPORT -> "🏍"
    MotorcycleCategory.SUPERBIKE -> "⚡"
    MotorcycleCategory.RACING -> "🏁"
    MotorcycleCategory.PREMIUM -> "👑"
}

private fun categoryLabel(cat: MotorcycleCategory) = when (cat) {
    MotorcycleCategory.STREET -> "Street Bike"
    MotorcycleCategory.SPORT -> "Sport Bike"
    MotorcycleCategory.SUPERBIKE -> "Superbike"
    MotorcycleCategory.RACING -> "Racing Motorcycle"
    MotorcycleCategory.PREMIUM -> "Premium"
}

private fun categoryColor(cat: MotorcycleCategory) = when (cat) {
    MotorcycleCategory.STREET -> NeonOrange
    MotorcycleCategory.SPORT -> ElectricBlue
    MotorcycleCategory.SUPERBIKE -> RacingRed
    MotorcycleCategory.RACING -> GoldColor
    MotorcycleCategory.PREMIUM -> com.example.bikeracing.ui.theme.NeonPurple
}
