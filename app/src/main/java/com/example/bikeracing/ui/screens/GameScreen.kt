package com.example.bikeracing.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.domain.model.ControlType
import com.example.bikeracing.domain.model.RacePhase
import com.example.bikeracing.ui.gamehud.CountdownOverlay
import com.example.bikeracing.ui.gamehud.GameControls
import com.example.bikeracing.ui.gamehud.RaceHUD
import com.example.bikeracing.ui.theme.DarkCard
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.RacingRed
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.ui.theme.TextSecondary
import com.example.bikeracing.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    levelId: Int,
    onRaceFinished: () -> Unit,
    onQuit: () -> Unit,
) {
    val raceState by viewModel.raceState.collectAsState()
    val opponents by viewModel.opponents.collectAsState()
    val settings by viewModel.gameSettings.collectAsState()
    var showPauseMenu by remember { mutableStateOf(false) }
    var showQuitConfirm by remember { mutableStateOf(false) }

    // Start the race when screen appears
    LaunchedEffect(levelId) {
        viewModel.startRace(levelId)
    }

    // Watch for race finish
    LaunchedEffect(raceState.phase) {
        if (raceState.phase == RacePhase.FINISHED) {
            viewModel.finishRace()
            onRaceFinished()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ---- Nairobi City Scene — full-screen procedural Canvas renderer ----
        // Draws: sky, Nairobi skyline, buildings, streetlights, road, traffic, player bike
        NairobiSceneCanvas(
            speedKmh = raceState.speedKmh,
            lateralPosition = raceState.lateralPosition,
            leanAngle = raceState.leanAngle,
            isNitroActive = raceState.isNitroActive,
            distanceTravelledKm = raceState.distanceTravelledKm,
            opponents = opponents,
            modifier = Modifier.fillMaxSize(),
        )

        // ---- HUD Layer ----
        if (raceState.phase == RacePhase.RACING || raceState.phase == RacePhase.PAUSED) {
            RaceHUD(
                state = raceState,
                onPause = {
                    viewModel.pauseRace()
                    showPauseMenu = true
                },
                modifier = Modifier.fillMaxSize(),
            )
        }

        // ---- Countdown overlay ----
        if (raceState.phase == RacePhase.COUNTDOWN) {
            CountdownOverlay(
                value = raceState.countdownValue,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // ---- Controls ----
        if (raceState.phase == RacePhase.RACING) {
            GameControls(
                viewModel = viewModel,
                controlType = settings.controlType,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // ---- Pause Menu ----
        AnimatedVisibility(
            visible = showPauseMenu,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            PauseMenu(
                onResume = {
                    viewModel.resumeRace()
                    showPauseMenu = false
                },
                onRestart = {
                    viewModel.startRace(levelId)
                    showPauseMenu = false
                },
                onQuit = {
                    showPauseMenu = false
                    showQuitConfirm = true
                },
            )
        }

        // ---- Quit confirm dialog ----
        if (showQuitConfirm) {
            AlertDialog(
                onDismissRequest = { showQuitConfirm = false },
                title = { Text("Quit Race?", color = TextPrimary) },
                text = { Text("All progress in this race will be lost.", color = TextSecondary) },
                confirmButton = {
                    TextButton(onClick = {
                        showQuitConfirm = false
                        viewModel.pauseRace()
                        onQuit()
                    }) { Text("QUIT", color = RacingRed, fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { showQuitConfirm = false; viewModel.resumeRace() }) {
                        Text("CONTINUE", color = NeonOrange)
                    }
                },
                containerColor = DarkCard,
            )
        }
    }
}

@Composable
private fun PauseMenu(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onQuit: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC050510)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .width(280.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(DarkCard)
                .border(1.dp, NeonOrange.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("PAUSED", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 24.sp, letterSpacing = 3.sp)
            Spacer(Modifier.height(4.dp))
            PauseMenuButton("▶  RESUME", NeonOrange, onResume)
            PauseMenuButton("↺  RESTART", NeonOrange.copy(alpha = 0.7f), onRestart)
            PauseMenuButton("✕  QUIT RACE", RacingRed.copy(alpha = 0.8f), onQuit)
        }
    }
}

@Composable
private fun PauseMenuButton(text: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = color, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}
