package com.example.bikeracing.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.domain.model.Motorcycle
import com.example.bikeracing.domain.model.MotorcycleCategory
import com.example.bikeracing.ui.components.RacingButton
import com.example.bikeracing.ui.components.SectionLabel
import com.example.bikeracing.ui.components.StatBar
import com.example.bikeracing.ui.theme.DarkBg
import com.example.bikeracing.ui.theme.DarkCard
import com.example.bikeracing.ui.theme.DarkCardAlt
import com.example.bikeracing.ui.theme.DarkCardMid
import com.example.bikeracing.ui.theme.DarkDivider
import com.example.bikeracing.ui.theme.ElectricBlue
import com.example.bikeracing.ui.theme.FireEnd
import com.example.bikeracing.ui.theme.FireMid
import com.example.bikeracing.ui.theme.FireStart
import com.example.bikeracing.ui.theme.GoldColor
import com.example.bikeracing.ui.theme.NeonCyan
import com.example.bikeracing.ui.theme.NeonGreen
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.NeonPurple
import com.example.bikeracing.ui.theme.RacingRed
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.ui.theme.TextSecondary
import com.example.bikeracing.viewmodel.GameViewModel
import com.example.bikeracing.viewmodel.UpgradeStat

@Composable
fun GarageScreen(viewModel: GameViewModel, onBack: () -> Unit) {
    val profile by viewModel.playerProfile.collectAsState()
    val selectedBikeId by viewModel.garageSelectedBikeId.collectAsState()
    val selectedBike = viewModel.motorcycles.find { it.id == selectedBikeId } ?: viewModel.motorcycles.first()
    val upgrades = viewModel.getUpgradesFor(selectedBikeId)
    val isOwned    = selectedBikeId in profile.ownedMotorcycleIds
    val isEquipped = selectedBikeId == profile.selectedMotorcycleId
    val accent = categoryColor(selectedBike.category)

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {

        // Ambient glow behind selected bike
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.TopCenter)
                .blur(120.dp)
                .background(Brush.radialGradient(listOf(accent.copy(alpha = 0.3f), Color.Transparent)))
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(DarkCard, DarkCardAlt))
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(listOf(accent.copy(alpha = 0.4f), DarkDivider)),
                        shape = RoundedCornerShape(0.dp),
                    )
                    .padding(vertical = 4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextPrimary)
                    }
                    Text(
                        "GARAGE",
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 3.sp,
                        modifier = Modifier.weight(1f),
                    )
                    // Coins
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(GoldColor.copy(alpha = 0.12f))
                            .border(1.dp, GoldColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("🪙", fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text("${profile.coins}", color = GoldColor, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                }
            }

            // ── Bike strip ────────────────────────────────────────────────────
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCard)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(viewModel.motorcycles) { bike ->
                    BikeChip(
                        bike       = bike,
                        isSelected = bike.id == selectedBikeId,
                        isOwned    = bike.id in profile.ownedMotorcycleIds,
                        isEquipped = bike.id == profile.selectedMotorcycleId,
                        onClick    = { viewModel.selectGarageBike(bike.id) },
                    )
                }
            }

            // ── Scrollable detail ─────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                BikeHeroCard(selectedBike, upgrades, isEquipped, accent)

                UpgradesPanel(
                    bike     = selectedBike,
                    upgrades = upgrades,
                    coins    = profile.coins,
                    isOwned  = isOwned,
                    onUpgrade = { stat -> viewModel.upgradeBike(selectedBike.id, stat) },
                )

                // Action button
                when {
                    !isOwned -> RacingButton(
                        text    = "BUY  ·  🪙 ${selectedBike.price}",
                        onClick = { viewModel.purchaseBike(selectedBike.id) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = profile.coins >= selectedBike.price,
                    )
                    !isEquipped -> RacingButton(
                        text    = "EQUIP BIKE",
                        onClick = { viewModel.equipBike(selectedBike.id) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                    )
                    else -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonGreen.copy(alpha = 0.12f))
                            .border(
                                1.dp,
                                Brush.horizontalGradient(listOf(NeonGreen.copy(alpha = 0.7f), NeonGreen.copy(alpha = 0.3f))),
                                RoundedCornerShape(12.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "✓  EQUIPPED",
                            color = NeonGreen,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            letterSpacing = 2.sp,
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun BikeChip(
    bike: Motorcycle,
    isSelected: Boolean,
    isOwned: Boolean,
    isEquipped: Boolean,
    onClick: () -> Unit,
) {
    val accent = categoryColor(bike.category)
    val borderColor = when {
        isEquipped -> NeonGreen
        isSelected -> accent
        else       -> DarkDivider
    }
    Column(
        modifier = Modifier
            .width(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) Brush.verticalGradient(listOf(accent.copy(alpha = 0.2f), DarkCard))
                else Brush.verticalGradient(listOf(DarkCard, DarkCard))
            )
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(bikeEmoji(bike.category), fontSize = 24.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            bike.name.split(" ").first(),
            color = if (isSelected) TextPrimary else TextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 11.sp,
        )
        if (!isOwned) Text("🔒", fontSize = 10.sp)
    }
}

@Composable
private fun BikeHeroCard(
    bike: Motorcycle,
    upgrades: com.example.bikeracing.domain.model.MotorcycleUpgrades,
    isEquipped: Boolean,
    accent: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(listOf(accent.copy(alpha = 0.15f), DarkCard, DarkCardMid))
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(accent.copy(alpha = 0.7f), accent.copy(alpha = 0.15f))),
                RoundedCornerShape(20.dp),
            )
            .padding(20.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Large bike emoji
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.radialGradient(listOf(accent.copy(alpha = 0.25f), Color.Transparent)))
                        .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(bikeEmoji(bike.category), fontSize = 44.sp)
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(bike.name, color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 18.sp, lineHeight = 20.sp)
                    Spacer(Modifier.height(3.dp))
                    Text(
                        categoryLabel(bike.category).uppercase(),
                        color = accent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                    )
                    if (isEquipped) {
                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(NeonGreen.copy(alpha = 0.15f))
                                .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("✓", color = NeonGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(3.dp))
                            Text("EQUIPPED", color = NeonGreen, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                        }
                    }
                }
            }
            if (bike.description.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Text(bike.description, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)
            }
            Spacer(Modifier.height(18.dp))

            // Stat bars
            val us = upgrades
            StatBar("TOP SPEED",     upgrades.getUpgradedStat(bike.baseTopSpeed,     us.engineLevel)       / 3.5f, barColor = RacingRed)
            Spacer(Modifier.height(8.dp))
            StatBar("ACCELERATION",  upgrades.getUpgradedStat(bike.baseAcceleration,  us.accelerationLevel), barColor = FireMid)
            Spacer(Modifier.height(8.dp))
            StatBar("HANDLING",      upgrades.getUpgradedStat(bike.baseHandling,       us.handlingLevel),     barColor = ElectricBlue)
            Spacer(Modifier.height(8.dp))
            StatBar("BRAKING",       upgrades.getUpgradedStat(bike.baseBraking,        us.brakesLevel),       barColor = NeonGreen)
            Spacer(Modifier.height(8.dp))
            StatBar("NITRO",         upgrades.getUpgradedStat(bike.baseNitroCapacity,  us.nitroLevel),        barColor = NeonCyan)
        }
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
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard)
            .border(1.dp, DarkDivider, RoundedCornerShape(16.dp))
            .padding(18.dp),
    ) {
        SectionLabel("UPGRADES")
        Spacer(Modifier.height(14.dp))
        UpgradeRow("Engine",       UpgradeStat.ENGINE,       upgrades.engineLevel,       coins, onUpgrade)
        UpgradeRow("Acceleration", UpgradeStat.ACCELERATION, upgrades.accelerationLevel, coins, onUpgrade)
        UpgradeRow("Brakes",       UpgradeStat.BRAKES,       upgrades.brakesLevel,       coins, onUpgrade)
        UpgradeRow("Handling",     UpgradeStat.HANDLING,     upgrades.handlingLevel,     coins, onUpgrade)
        UpgradeRow("Nitro",        UpgradeStat.NITRO,        upgrades.nitroLevel,        coins, onUpgrade)
        UpgradeRow("Suspension",   UpgradeStat.SUSPENSION,   upgrades.suspensionLevel,   coins, onUpgrade)
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
    val maxLevel  = 5
    val cost      = (level + 1) * 500
    val canUpgrade = level < maxLevel && coins >= cost

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(106.dp), letterSpacing = 0.5.sp)
        // Level pips
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(maxLevel) { i ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (i < level)
                                Brush.horizontalGradient(listOf(FireStart.copy(alpha = 0.8f), FireEnd))
                            else
                                Brush.horizontalGradient(listOf(DarkDivider, DarkDivider))
                        )
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        if (level < maxLevel) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (canUpgrade) FireMid.copy(alpha = 0.18f) else DarkDivider.copy(alpha = 0.4f))
                    .border(1.dp, if (canUpgrade) FireMid else DarkDivider, RoundedCornerShape(8.dp))
                    .clickable(enabled = canUpgrade) { onUpgrade(stat) }
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text(
                    "🪙 $cost",
                    color = if (canUpgrade) NeonOrange else TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(NeonGreen.copy(alpha = 0.12f))
                    .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text("MAX", color = NeonGreen, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            }
        }
    }
}

private fun bikeEmoji(cat: MotorcycleCategory) = when (cat) {
    MotorcycleCategory.STREET    -> "🛵"
    MotorcycleCategory.SPORT     -> "🏍"
    MotorcycleCategory.SUPERBIKE -> "⚡"
    MotorcycleCategory.RACING    -> "🏁"
    MotorcycleCategory.PREMIUM   -> "👑"
}
private fun categoryLabel(cat: MotorcycleCategory) = when (cat) {
    MotorcycleCategory.STREET    -> "Street Bike"
    MotorcycleCategory.SPORT     -> "Sport Bike"
    MotorcycleCategory.SUPERBIKE -> "Superbike"
    MotorcycleCategory.RACING    -> "Racing Motorcycle"
    MotorcycleCategory.PREMIUM   -> "Premium"
}
private fun categoryColor(cat: MotorcycleCategory) = when (cat) {
    MotorcycleCategory.STREET    -> NeonOrange
    MotorcycleCategory.SPORT     -> ElectricBlue
    MotorcycleCategory.SUPERBIKE -> RacingRed
    MotorcycleCategory.RACING    -> GoldColor
    MotorcycleCategory.PREMIUM   -> NeonPurple
}
