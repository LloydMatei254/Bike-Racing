package com.example.bikeracing.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.domain.model.ControlType
import com.example.bikeracing.domain.model.GameSettings
import com.example.bikeracing.domain.model.GraphicsQuality
import com.example.bikeracing.ui.components.RacingButton
import com.example.bikeracing.ui.theme.DarkBg
import com.example.bikeracing.ui.theme.DarkCard
import com.example.bikeracing.ui.theme.DarkDivider
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.RacingRed
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.ui.theme.TextSecondary
import com.example.bikeracing.viewmodel.GameViewModel

@Composable
fun SettingsScreen(viewModel: GameViewModel, onBack: () -> Unit) {
    val settings by viewModel.gameSettings.collectAsState()
    var pendingSettings by remember { mutableStateOf(settings) }
    var showResetConfirm by remember { mutableStateOf(false) }

    // Keep local state in sync with loaded settings
    if (pendingSettings == GameSettings() && settings != GameSettings()) {
        pendingSettings = settings
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().background(DarkCard).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = {
                    viewModel.saveSettings(pendingSettings)
                    onBack()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text("SETTINGS", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp, letterSpacing = 1.sp)
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SettingsSection("AUDIO") {
                    SliderRow("Music Volume", pendingSettings.musicVolume) {
                        pendingSettings = pendingSettings.copy(musicVolume = it)
                    }
                    SliderRow("SFX Volume", pendingSettings.sfxVolume) {
                        pendingSettings = pendingSettings.copy(sfxVolume = it)
                    }
                    SliderRow("Engine Volume", pendingSettings.engineVolume) {
                        pendingSettings = pendingSettings.copy(engineVolume = it)
                    }
                }
                SettingsSection("CONTROLS") {
                    SegmentedRow(
                        label = "Control Type",
                        options = listOf("Touch", "Tilt", "Joystick"),
                        selected = pendingSettings.controlType.ordinal,
                        onSelect = {
                            pendingSettings = pendingSettings.copy(controlType = ControlType.values()[it])
                        },
                    )
                    SliderRow("Steering Sensitivity", pendingSettings.steeringSensitivity) {
                        pendingSettings = pendingSettings.copy(steeringSensitivity = it)
                    }
                    SwitchRow("Vibration", pendingSettings.vibrationEnabled) {
                        pendingSettings = pendingSettings.copy(vibrationEnabled = it)
                    }
                }
                SettingsSection("GRAPHICS") {
                    SegmentedRow(
                        label = "Quality",
                        options = listOf("Low", "Medium", "High"),
                        selected = pendingSettings.graphicsQuality.ordinal,
                        onSelect = {
                            pendingSettings = pendingSettings.copy(graphicsQuality = GraphicsQuality.values()[it])
                        },
                    )
                    SwitchRow("Camera Shake", pendingSettings.cameraShake) {
                        pendingSettings = pendingSettings.copy(cameraShake = it)
                    }
                    SwitchRow("Show FPS", pendingSettings.showFps) {
                        pendingSettings = pendingSettings.copy(showFps = it)
                    }
                    SegmentedRow(
                        label = "Target FPS",
                        options = listOf("30", "60"),
                        selected = if (pendingSettings.targetFps >= 60) 1 else 0,
                        onSelect = {
                            pendingSettings = pendingSettings.copy(targetFps = if (it == 1) 60 else 30)
                        },
                    )
                }
                SettingsSection("ACCOUNT") {
                    Spacer(Modifier.height(4.dp))
                    RacingButton(
                        text = "RESET GAME PROGRESS",
                        onClick = { showResetConfirm = true },
                        modifier = Modifier.fillMaxWidth(),
                        primary = false,
                    )
                }
                Spacer(Modifier.height(8.dp))
                RacingButton(
                    text = "SAVE SETTINGS",
                    onClick = { viewModel.saveSettings(pendingSettings); onBack() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                )
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset All Progress?", color = TextPrimary) },
            text = { Text("This will erase all your career progress, coins, and owned motorcycles. This cannot be undone.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetProgress()
                    showResetConfirm = false
                }) { Text("RESET", color = RacingRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) { Text("CANCEL", color = NeonOrange) }
            },
            containerColor = DarkCard,
        )
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(title, color = NeonOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
        HorizontalDivider(color = DarkDivider)
        content()
    }
}

@Composable
private fun SliderRow(label: String, value: Float, onChange: (Float) -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = TextSecondary, fontSize = 13.sp)
            Text("${(value * 100).toInt()}%", color = NeonOrange, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
        Slider(
            value = value,
            onValueChange = onChange,
            colors = SliderDefaults.colors(
                thumbColor = NeonOrange,
                activeTrackColor = NeonOrange,
                inactiveTrackColor = DarkDivider,
            ),
        )
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = TextSecondary, fontSize = 13.sp)
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(checkedThumbColor = NeonOrange, checkedTrackColor = NeonOrange.copy(alpha = 0.4f)),
        )
    }
}

@Composable
private fun SegmentedRow(label: String, options: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    Column {
        Text(label, color = TextSecondary, fontSize = 13.sp)
        Spacer(Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            options.forEachIndexed { i, opt ->
                val isSelected = i == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) NeonOrange.copy(alpha = 0.25f) else DarkBg)
                        .border(1.dp, if (isSelected) NeonOrange else DarkDivider, RoundedCornerShape(8.dp))
                        .clickable { onSelect(i) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        opt,
                        color = if (isSelected) NeonOrange else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}
