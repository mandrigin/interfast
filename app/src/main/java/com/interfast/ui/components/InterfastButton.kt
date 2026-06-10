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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.interfast.ui.theme.InterfastColors
import com.interfast.ui.theme.InterfastTheme
import com.interfast.ui.theme.InterfastTypography
import com.interfast.ui.theme.LocalSurfaceTokens
import com.interfast.ui.theme.Motion
import com.interfast.ui.theme.Spacing

/**
 * Primary action button with icon and label.
 *
 * Design notes:
 * - Minimum touch target 48dp
 * - Scale animation on press
 * - Geometric, flat design
 * - Disabled colors come from surface tokens so the button reads as quiet
 *   (not as a black slab) in the light theme too
 */
@Composable
fun InterfastButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    backgroundColor: Color = InterfastColors.GlyphRed,
    contentColor: Color = InterfastColors.PureWhite,
    height: Dp = 56.dp
) {
    val tokens = LocalSurfaceTokens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(Motion.DURATION_FAST),
        label = "buttonScale"
    )

    val effectiveBackgroundColor = if (enabled) backgroundColor else tokens.surface
    val effectiveContentColor = if (enabled) contentColor else tokens.textSecondary

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
                role = Role.Button,
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
 * Primary CTA button (ACTIVATE / DEACTIVATE).
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
        backgroundColor = InterfastColors.GlyphRed,
        contentColor = InterfastColors.PureWhite
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun PrimaryButtonPreview() {
    InterfastTheme {
        PrimaryActionButton(
            text = "Activate",
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
