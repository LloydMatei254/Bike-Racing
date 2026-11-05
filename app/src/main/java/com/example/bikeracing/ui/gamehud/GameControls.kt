package com.example.bikeracing.ui.gamehud

import androidx.compose.ui.ExperimentalComposeUiApi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.domain.model.ControlType
import com.example.bikeracing.ui.theme.ElectricBlue
import com.example.bikeracing.ui.theme.NeonCyan
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.RacingRed
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.viewmodel.GameViewModel
import kotlin.math.roundToInt

@Composable
fun GameControls(
    viewModel: GameViewModel,
    controlType: ControlType,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (controlType) {
            ControlType.TOUCH -> TouchControls(viewModel)
            ControlType.JOYSTICK -> JoystickControls(viewModel)
            ControlType.TILT -> TiltControls(viewModel)
        }
    }
}

@Composable
private fun TouchControls(viewModel: GameViewModel) {
    // Bottom row: LEFT | THROTTLE | BRAKE | NITRO | RIGHT
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        // Left side — steering
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GameControlButton(
                label = "◄",
                color = ElectricBlue,
                onPress = { viewModel.onSteerLeft(true) },
                onRelease = { viewModel.onSteerLeft(false) },
                size = 70.dp,
            )
        }
        // Right side — throttle, brake, nitro
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GameControlButton(
                label = "BRAKE",
                color = RacingRed,
                onPress = { viewModel.onBrake(true) },
                onRelease = { viewModel.onBrake(false) },
                size = 60.dp,
            )
            GameControlButton(
                label = "NITRO",
                color = NeonCyan,
                onPress = { viewModel.onNitro(true) },
                onRelease = { viewModel.onNitro(false) },
                size = 60.dp,
            )
            GameControlButton(
                label = "GAS",
                color = NeonOrange,
                onPress = { viewModel.onThrottle(true) },
                onRelease = { viewModel.onThrottle(false) },
                size = 70.dp,
            )
            GameControlButton(
                label = "►",
                color = ElectricBlue,
                onPress = { viewModel.onSteerRight(true) },
                onRelease = { viewModel.onSteerRight(false) },
                size = 70.dp,
            )
        }
    }
}

@Composable
private fun JoystickControls(viewModel: GameViewModel) {
    var joystickX by remember { mutableFloatStateOf(0f) }
    var joystickY by remember { mutableFloatStateOf(0f) }
    val stickRadius = 100f

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        // Virtual joystick — left side
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
                .border(2.dp, ElectricBlue.copy(alpha = 0.5f), CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            joystickX = 0f; joystickY = 0f
                            viewModel.onJoystick(0f)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            joystickX = (joystickX + dragAmount.x / stickRadius).coerceIn(-1f, 1f)
                            joystickY = (joystickY + dragAmount.y / stickRadius).coerceIn(-1f, 1f)
                            viewModel.onJoystick(joystickX)
                        }
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .offset {
                        IntOffset(
                            (joystickX * 40f).roundToInt(),
                            (joystickY * 40f).roundToInt()
                        )
                    }
                    .clip(CircleShape)
                    .background(ElectricBlue.copy(alpha = 0.7f))
            )
        }

        // Right side buttons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GameControlButton(
                label = "BRAKE",
                color = RacingRed,
                onPress = { viewModel.onBrake(true) },
                onRelease = { viewModel.onBrake(false) },
                size = 60.dp,
            )
            GameControlButton(
                label = "NITRO",
                color = NeonCyan,
                onPress = { viewModel.onNitro(true) },
                onRelease = { viewModel.onNitro(false) },
                size = 60.dp,
            )
            GameControlButton(
                label = "GAS",
                color = NeonOrange,
                onPress = { viewModel.onThrottle(true) },
                onRelease = { viewModel.onThrottle(false) },
                size = 70.dp,
            )
        }
    }
}

@Composable
private fun TiltControls(viewModel: GameViewModel) {
    // Tilt: show only throttle, brake, nitro buttons — steering comes from sensor
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GameControlButton(
                label = "BRAKE",
                color = RacingRed,
                onPress = { viewModel.onBrake(true) },
                onRelease = { viewModel.onBrake(false) },
                size = 60.dp,
            )
            GameControlButton(
                label = "NITRO",
                color = NeonCyan,
                onPress = { viewModel.onNitro(true) },
                onRelease = { viewModel.onNitro(false) },
                size = 60.dp,
            )
            GameControlButton(
                label = "GAS",
                color = NeonOrange,
                onPress = { viewModel.onThrottle(true) },
                onRelease = { viewModel.onThrottle(false) },
                size = 70.dp,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun GameControlButton(
    label: String,
    color: Color,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    size: androidx.compose.ui.unit.Dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.radialGradient(
                    listOf(color.copy(alpha = 0.35f), color.copy(alpha = 0.15f))
                )
            )
            .border(2.dp, color.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when {
                            event.changes.any { it.pressed } -> onPress()
                            else -> onRelease()
                        }
                        event.changes.forEach { it.consume() }
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = color,
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (label.length > 3) 11.sp else 18.sp,
        )
    }
}
