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
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bikeracing.domain.model.ControlType
import com.example.bikeracing.domain.model.RacePhase
import com.example.bikeracing.game.rendering.GameSceneRenderer
import com.example.bikeracing.ui.gamehud.CountdownOverlay
import com.example.bikeracing.ui.gamehud.GameControls
import com.example.bikeracing.ui.gamehud.RaceHUD
import com.example.bikeracing.ui.theme.DarkBg
import com.example.bikeracing.ui.theme.DarkCard
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.RacingRed
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.ui.theme.TextSecondary
import com.example.bikeracing.viewmodel.GameViewModel
import io.github.sceneview.SceneView

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    levelId: Int,
    onRaceFinished: () -> Unit,
    onQuit: () -> Unit,
) {
    val context = LocalContext.current
    val raceState by viewModel.raceState.collectAsState()
    val settings by viewModel.gameSettings.collectAsState()
    var showPauseMenu by remember { mutableStateOf(false) }
    var showQuitConfirm by remember { mutableStateOf(false) }

    // One renderer instance for this screen lifetime
    val renderer = remember { GameSceneRenderer(context) }

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

    DisposableEffect(Unit) {
        onDispose { renderer.release() }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ---- 3D Scene Layer ----
        AndroidView(
            factory = { ctx ->
                renderer.getOrCreateSceneView(ctx).also { sv ->
                    val level = viewModel.currentLevel.value
                    if (level != null) renderer.loadEnvironment(level.environment)
                }
            },
            update = { _ ->
                // renderer.updateFrame called by game engine updates through state
            },
            modifier = Modifier.fillMaxSize(),
        )

        // ---- Racing Road Overlay (procedural canvas, supplements 3D) ----
        RoadCanvas(
            speedKmh = raceState.speedKmh,
            lateralPos = 0f,
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

/**
 * Procedurally drawn road perspective to supplement / replace 3D scene on load.
 * Uses Compose Canvas to draw road, lane lines, and speed blur.
 */
@Composable
private fun RoadCanvas(
    speedKmh: Float,
    lateralPos: Float,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val horizonY = h * 0.42f

        // Sky gradient
        drawRect(
            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                listOf(
                    Color(0xFF050515),
                    Color(0xFF0A0A20),
                    Color(0xFF050515),
                ),
                startY = 0f,
                endY = horizonY,
            ),
            size = androidx.compose.ui.geometry.Size(w, horizonY),
        )

        // Ground below horizon
        drawRect(
            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                listOf(Color(0xFF1A1A1A), Color(0xFF111111)),
                startY = horizonY,
                endY = h,
            ),
            topLeft = androidx.compose.ui.geometry.Offset(0f, horizonY),
            size = androidx.compose.ui.geometry.Size(w, h - horizonY),
        )

        // Road surface — trapezoidal perspective
        val roadTopWidth = w * 0.28f
        val roadBottomWidth = w * 1.6f
        val roadCenterX = w * 0.5f + lateralPos * w * 0.1f

        val roadPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(roadCenterX - roadTopWidth / 2, horizonY)
            lineTo(roadCenterX + roadTopWidth / 2, horizonY)
            lineTo(roadCenterX + roadBottomWidth / 2, h)
            lineTo(roadCenterX - roadBottomWidth / 2, h)
            close()
        }
        drawPath(
            roadPath,
            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                listOf(Color(0xFF2C2C2C), Color(0xFF1A1A1A)),
                startY = horizonY,
                endY = h,
            ),
        )

        // Road edge lines
        val edgeColor = Color(0xFFFFFFFF).copy(alpha = 0.7f)
        val lineWidth = 4f
        // Left edge
        drawLine(
            color = edgeColor,
            start = androidx.compose.ui.geometry.Offset(roadCenterX - roadTopWidth / 2, horizonY),
            end = androidx.compose.ui.geometry.Offset(roadCenterX - roadBottomWidth / 2, h),
            strokeWidth = lineWidth,
        )
        // Right edge
        drawLine(
            color = edgeColor,
            start = androidx.compose.ui.geometry.Offset(roadCenterX + roadTopWidth / 2, horizonY),
            end = androidx.compose.ui.geometry.Offset(roadCenterX + roadBottomWidth / 2, h),
            strokeWidth = lineWidth,
        )

        // Centre dashed lane lines — animated by speed
        val dashColor = Color(0xFFFFFFFF).copy(alpha = 0.5f)
        val segments = 8
        for (i in 0..segments) {
            val t = i.toFloat() / segments
            val y = horizonY + (h - horizonY) * t
            val xLeft = roadCenterX - (roadTopWidth / 2 + (roadBottomWidth - roadTopWidth) / 2 * t)
            val xRight = roadCenterX + (roadTopWidth / 2 + (roadBottomWidth - roadTopWidth) / 2 * t)
            val midX = (xLeft + xRight) / 2
            val segH = (h - horizonY) / segments
            if (i % 2 == 0) {
                val segStart = y
                val segEnd = (horizonY + (h - horizonY) * ((i + 1f) / segments)).coerceAtMost(h)
                drawLine(
                    color = dashColor,
                    start = androidx.compose.ui.geometry.Offset(midX, segStart),
                    end = androidx.compose.ui.geometry.Offset(midX, segEnd),
                    strokeWidth = 3f,
                )
            }
        }

        // Speed motion blur lines on sides
        if (speedKmh > 80f) {
            val blurAlpha = ((speedKmh - 80f) / 200f).coerceIn(0f, 0.4f)
            val lineCount = 12
            for (i in 0..lineCount) {
                val yFrac = i.toFloat() / lineCount
                val y = horizonY + (h - horizonY) * yFrac
                val lineLen = w * 0.15f * blurAlpha
                drawLine(
                    color = Color.White.copy(alpha = blurAlpha * (1f - yFrac)),
                    start = androidx.compose.ui.geometry.Offset(roadCenterX - roadBottomWidth / 2 * yFrac, y),
                    end = androidx.compose.ui.geometry.Offset(roadCenterX - roadBottomWidth / 2 * yFrac - lineLen, y),
                    strokeWidth = 1.5f,
                )
                drawLine(
                    color = Color.White.copy(alpha = blurAlpha * (1f - yFrac)),
                    start = androidx.compose.ui.geometry.Offset(roadCenterX + roadBottomWidth / 2 * yFrac, y),
                    end = androidx.compose.ui.geometry.Offset(roadCenterX + roadBottomWidth / 2 * yFrac + lineLen, y),
                    strokeWidth = 1.5f,
                )
            }
        }

        // Horizon glow
        drawRect(
            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                listOf(Color(0x00FF6D00), Color(0x18FF6D00), Color(0x00FF6D00)),
                startY = horizonY - 20f,
                endY = horizonY + 20f,
            ),
            topLeft = androidx.compose.ui.geometry.Offset(0f, horizonY - 20f),
            size = androidx.compose.ui.geometry.Size(w, 40f),
        )
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
