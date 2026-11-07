package com.example.bikeracing.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.ui.theme.DarkCard
import com.example.bikeracing.ui.theme.DarkCardAlt
import com.example.bikeracing.ui.theme.DarkDivider
import com.example.bikeracing.ui.theme.FireEnd
import com.example.bikeracing.ui.theme.FireMid
import com.example.bikeracing.ui.theme.FireStart
import com.example.bikeracing.ui.theme.NeonOrange
import com.example.bikeracing.ui.theme.TextPrimary
import com.example.bikeracing.ui.theme.TextSecondary

/**
 * Primary CTA button — fire gradient with chrome text.
 * Secondary variant — outlined ghost button.
 */
@Composable
fun RacingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    enabled: Boolean = true,
    primary: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, tween(80), label = "btn_scale")

    val shape = RoundedCornerShape(cornerRadius)

    val gradient = when {
        primary && enabled  -> Brush.horizontalGradient(listOf(FireStart, FireMid, FireEnd))
        primary && !enabled -> Brush.horizontalGradient(listOf(FireStart.copy(alpha = 0.25f), FireEnd.copy(alpha = 0.25f)))
        else                -> Brush.horizontalGradient(listOf(DarkCard, DarkCardAlt))
    }

    val borderBrush = if (primary && enabled)
        Brush.horizontalGradient(listOf(FireStart.copy(alpha = 0.8f), NeonOrange.copy(alpha = 0.8f)))
    else if (primary)
        Brush.horizontalGradient(listOf(FireStart.copy(alpha = 0.2f), FireEnd.copy(alpha = 0.2f)))
    else
        Brush.horizontalGradient(listOf(NeonOrange.copy(alpha = 0.4f), NeonOrange.copy(alpha = 0.2f)))

    val textColor = when {
        primary && enabled  -> TextPrimary
        primary && !enabled -> TextPrimary.copy(alpha = 0.35f)
        enabled             -> NeonOrange
        else                -> TextSecondary
    }

    Box(
        modifier = modifier
            .scale(scale)
            .then(if (primary && enabled) Modifier.shadow(8.dp, shape, ambientColor = FireMid.copy(alpha = 0.4f), spotColor = FireMid.copy(alpha = 0.4f)) else Modifier)
            .clip(shape)
            .background(gradient)
            .border(1.dp, borderBrush, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 28.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text.uppercase(),
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
            letterSpacing = 2.sp,
        )
    }
}

/**
 * Glass-morphism card with accent glow border.
 */
@Composable
fun GlowingCard(
    modifier: Modifier = Modifier,
    glowColor: Color = FireMid,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .shadow(6.dp, shape, ambientColor = glowColor.copy(alpha = 0.3f), spotColor = glowColor.copy(alpha = 0.3f))
            .clip(shape)
            .background(
                Brush.verticalGradient(listOf(DarkCard, DarkCardAlt))
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(glowColor.copy(alpha = 0.6f), glowColor.copy(alpha = 0.2f))),
                shape,
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/**
 * Section label used throughout the app — coloured small caps.
 */
@Composable
fun SectionLabel(text: String, color: Color = NeonOrange, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        color = color,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 11.sp,
        letterSpacing = 2.sp,
        modifier = modifier,
    )
}
