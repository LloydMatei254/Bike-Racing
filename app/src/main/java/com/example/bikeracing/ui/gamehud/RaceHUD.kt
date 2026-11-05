package com.example.bikeracing.ui.gamehud

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.domain.model.RaceState
import com.example.bikeracing.ui.theme.DarkBg
import com.example.bikeracing.ui.theme.ElectricBlue
import com.example.bikeracing.ui.theme.GoldColor
import com.example.bikeracing.ui.theme.HudAccent
import com.example.bikeracing.ui.theme.HudBackground
import com.example.bikeracing.ui.theme.NeonCyan
import com.example.bikeracing.ui.theme.NeonGreen
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.RacingRed
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun RaceHUD(
    state: RaceState,
    onPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Top bar — timer, position, laps
        TopHudBar(state, onPause, Modifier.align(Alignment.TopCenter).fillMaxWidth())
        // Speed cluster — bottom left
        SpeedCluster(state, Modifier.align(Alignment.BottomStart).padding(8.dp))
        // Nitro meter — bottom right
        NitroMeter(state.nitroAmount, state.isNitroActive, Modifier.align(Alignment.BottomEnd).padding(8.dp))
        // Race position — right center
        PositionIndicator(state, Modifier.align(Alignment.CenterEnd).padding(end = 8.dp))
    }
}

@Composable
private fun TopHudBar(state: RaceState, onPause: () -> Unit, modifier: Modifier) {
    Row(
        modifier = modifier
            .background(HudBackground)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Timer
        val mins = (state.elapsedSeconds / 60).toInt()
        val secs = (state.elapsedSeconds % 60).toInt()
        val ms = ((state.elapsedSeconds % 1f) * 100).toInt()
        Text(
            text = "%02d:%02d.%02d".format(mins, secs, ms),
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
        )
        // Lap counter
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("LAP", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(
                "${state.currentLap}/${state.totalLaps}",
                color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp
            )
        }
        // Score
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("SCORE", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(
                "${state.score}",
                color = GoldColor, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp
            )
        }
        // Pause button
        IconButton(
            onClick = onPause,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(HudBackground),
        ) {
            Icon(Icons.Default.Pause, contentDescription = "Pause", tint = TextPrimary)
        }
    }
}

@Composable
private fun SpeedCluster(state: RaceState, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(HudBackground)
            .border(1.dp, NeonOrange.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "${state.speedKmh.roundToInt()}",
            color = TextPrimary,
            fontWeight = FontWeight.Black,
            fontSize = 36.sp,
            lineHeight = 36.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
        )
        Text("KM/H", color = HudAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        // RPM bar
        RpmBar(state.rpm)
        Spacer(Modifier.height(4.dp))
        Text(
            text = "GEAR ${state.currentGear}",
            color = ElectricBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun RpmBar(rpm: Float) {
    val normalizedRpm = (rpm / 9000f).coerceIn(0f, 1f)
    val animRpm by animateFloatAsState(normalizedRpm, tween(100), label = "rpm")
    val rpmColor = when {
        normalizedRpm > 0.85f -> RacingRed
        normalizedRpm > 0.7f -> NeonOrange
        else -> NeonGreen
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("RPM", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF202040))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animRpm)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Brush.horizontalGradient(listOf(NeonGreen, rpmColor)))
            )
        }
    }
}

@Composable
private fun NitroMeter(nitroAmount: Float, isActive: Boolean, modifier: Modifier) {
    val animNitro by animateFloatAsState(nitroAmount, tween(200), label = "nitro")
    val nitroColor = if (isActive) NeonCyan else NeonCyan.copy(alpha = 0.6f)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(HudBackground)
            .border(1.dp, nitroColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "NITRO",
            color = nitroColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(4.dp))
        // Vertical nitro bar
        Box(
            modifier = Modifier
                .width(20.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF101020))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(20.dp)
                    .fillMaxSize(animNitro)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(nitroColor, nitroColor.copy(alpha = 0.4f))
                        )
                    )
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "${(nitroAmount * 100).roundToInt()}%",
            color = nitroColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun PositionIndicator(state: RaceState, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
            .background(HudBackground)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("POS", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(
            "${state.playerPosition}",
            color = GoldColor,
            fontWeight = FontWeight.Black,
            fontSize = 28.sp,
            lineHeight = 28.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            "of ${state.totalRacers}",
            color = TextSecondary,
            fontSize = 10.sp,
        )
        Spacer(Modifier.height(6.dp))
        Text("OVR", color = TextSecondary, fontSize = 9.sp)
        Text(
            "${state.overtakes}",
            color = NeonOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
    }
}

@Composable
fun CountdownOverlay(value: Int, modifier: Modifier = Modifier) {
    if (value <= 0) return
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(HudBackground, Color.Transparent)
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (value > 0) "$value" else "GO!",
                color = if (value == 1) RacingRed else TextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 72.sp,
                lineHeight = 72.sp,
            )
        }
    }
}
