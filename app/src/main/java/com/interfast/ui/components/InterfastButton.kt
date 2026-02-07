package com.interfast.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.interfast.ui.theme.InterfastColors
import com.interfast.ui.theme.InterfastTheme
import com.interfast.ui.theme.InterfastTypography
import com.interfast.ui.theme.Motion
import com.interfast.ui.theme.Spacing

/**
 * Primary action button with icon and label.
 *
 * Design notes:
 * - Minimum touch target 48dp
 * - Scale animation on press
 * - Geometric, flat design
 */
@Composable
fun InterfastButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    backgroundColor: Color = if (isPrimary) InterfastColors.Gray15 else InterfastColors.Gray10,
    contentColor: Color = InterfastColors.PureWhite,
    height: Dp = 56.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(Motion.DURATION_FAST),
        label = "buttonScale"
    )

    val effectiveBackgroundColor = if (enabled) backgroundColor else InterfastColors.Gray15
    val effectiveContentColor = if (enabled) contentColor else InterfastColors.Gray40

    Box(
        modifier = modifier
            .scale(scale)
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(effectiveBackgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = Spacing.md),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = effectiveContentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
            }
            Text(
                text = text.uppercase(),
                style = InterfastTypography.labelLarge,
                color = effectiveContentColor
            )
        }
    }
}

/**
 * Icon-only circular button.
 */
@Composable
fun InterfastIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = InterfastColors.Gray15,
    iconColor: Color = InterfastColors.PureWhite,
    size: Dp = 48.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(Motion.DURATION_FAST),
        label = "iconButtonScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) backgroundColor else InterfastColors.Gray10)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) iconColor else InterfastColors.Gray40,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Primary CTA button (e.g., Start Fast).
 */
@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    InterfastButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        icon = icon,
        enabled = enabled,
        isPrimary = true,
        backgroundColor = InterfastColors.GlyphRed,
        contentColor = InterfastColors.PureWhite
    )
}

/**
 * Secondary action button.
 */
@Composable
fun SecondaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    InterfastButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        icon = icon,
        enabled = enabled,
        isPrimary = false,
        backgroundColor = InterfastColors.Gray15,
        contentColor = InterfastColors.PureWhite
    )
}

/**
 * Timer control buttons row.
 */
@Composable
fun TimerControlButtons(
    onPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    isPaused: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md, Alignment.CenterHorizontally)
    ) {
        InterfastButton(
            text = if (isPaused) "Resume" else "Pause",
            onClick = onPauseClick,
            icon = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
            modifier = Modifier.weight(1f)
        )

        InterfastButton(
            text = "End",
            onClick = onStopClick,
            icon = Icons.Default.Stop,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun InterfastButtonPreview() {
    InterfastTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InterfastButton(
                text = "Pause",
                onClick = {},
                icon = Icons.Default.Pause
            )
            InterfastButton(
                text = "End",
                onClick = {},
                icon = Icons.Default.Stop
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun PrimaryButtonPreview() {
    InterfastTheme {
        PrimaryActionButton(
            text = "Start Fast",
            onClick = {},
            icon = Icons.Default.PlayArrow,
            modifier = Modifier.padding(16.dp)
        )
    }
}
