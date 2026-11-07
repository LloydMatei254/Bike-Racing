package com.example.bikeracing.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.ui.theme.DarkDivider
import com.example.bikeracing.ui.theme.GoldColor
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.TextSecondary

@Composable
fun StatBar(
    label: String,
    value: Float,           // 0..100
    modifier: Modifier = Modifier,
    barColor: Color = NeonOrange,
    showValue: Boolean = true,
) {
    var animTarget by remember { mutableFloatStateOf(0f) }
    val animValue by animateFloatAsState(animTarget, tween(900), label = "stat_$label")
    LaunchedEffect(value) { animTarget = value / 100f }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                modifier = Modifier.weight(1f),
            )
            if (showValue) {
                Text(
                    text = "${value.toInt()}",
                    color = barColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(DarkDivider)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animValue)
                    .height(7.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shadow(4.dp, RoundedCornerShape(4.dp), ambientColor = barColor.copy(alpha = 0.6f), spotColor = barColor.copy(alpha = 0.6f))
                    .background(
                        Brush.horizontalGradient(
                            listOf(barColor.copy(alpha = 0.6f), barColor, Color.White.copy(alpha = 0.3f))
                        )
                    )
            )
        }
    }
}

@Composable
fun StarRating(stars: Int, maxStars: Int = 3, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        repeat(maxStars) { i ->
            val filled = i < stars
            Text(
                text = if (filled) "★" else "☆",
                color = if (filled) GoldColor else Color(0xFF2A2A40),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            if (i < maxStars - 1) Spacer(Modifier.width(4.dp))
        }
    }
}
