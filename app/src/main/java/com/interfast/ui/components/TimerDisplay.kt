package com.interfast.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.interfast.ui.theme.InterfastColors
import com.interfast.ui.theme.InterfastTheme
import com.interfast.ui.theme.InterfastTypography
import com.interfast.ui.theme.Motion
import com.interfast.ui.theme.Spacing
import java.time.Duration

/**
 * Main timer display component with pulsing colon separator.
 *
 * Features:
 * - Large, bold time display
 * - Pulsing colon animation when active
 * - Optional target time display below
 */
@Composable
fun TimerDisplay(
    hours: Int,
    minutes: Int,
    seconds: Int,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    textStyle: TextStyle = InterfastTypography.timerPrimary,
    color: Color = InterfastColors.PureWhite,
    showPulsingColon: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "timerPulse")
    val colonAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(Motion.DURATION_PULSE),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colonAlpha"
    )

    val effectiveColonAlpha = if (isActive && showPulsingColon) colonAlpha else 1f

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hours
        AnimatedDigits(
            value = hours,
            minDigits = 2,
            style = textStyle,
            color = color
        )

        // Colon
        Text(
            text = ":",
            style = textStyle,
            color = color,
            modifier = Modifier.alpha(effectiveColonAlpha)
        )

        // Minutes
        AnimatedDigits(
            value = minutes,
            minDigits = 2,
            style = textStyle,
            color = color
        )

        // Colon
        Text(
            text = ":",
            style = textStyle,
            color = color,
            modifier = Modifier.alpha(effectiveColonAlpha)
        )

        // Seconds
        AnimatedDigits(
            value = seconds,
            minDigits = 2,
            style = textStyle,
            color = color
        )
    }
}

/**
 * Timer display from Duration.
 */
@Composable
fun TimerDisplay(
    duration: Duration,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    textStyle: TextStyle = InterfastTypography.timerPrimary,
    color: Color = InterfastColors.PureWhite,
    showPulsingColon: Boolean = true
) {
    TimerDisplay(
        hours = duration.toHours().toInt(),
        minutes = duration.toMinutesPart(),
        seconds = duration.toSecondsPart(),
        modifier = modifier,
        isActive = isActive,
        textStyle = textStyle,
        color = color,
        showPulsingColon = showPulsingColon
    )
}

/**
 * Full timer display with elapsed and target times.
 */
@Composable
fun FullTimerDisplay(
    elapsed: Duration,
    target: Duration,
    modifier: Modifier = Modifier,
    isActive: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Elapsed time - large
        TimerDisplay(
            duration = elapsed,
            isActive = isActive,
            textStyle = InterfastTypography.timerPrimary,
            color = InterfastColors.PureWhite
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        // Separator line
        Text(
            text = "─────",
            style = InterfastTypography.bodySmall,
            color = InterfastColors.Gray40
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        // Target time - smaller, secondary color
        TimerDisplay(
            duration = target,
            isActive = false,
            textStyle = InterfastTypography.timerSecondary,
            color = InterfastColors.Gray60,
            showPulsingColon = false
        )
    }
}

/**
 * Animated digit display with smooth transitions.
 */
@Composable
private fun AnimatedDigits(
    value: Int,
    minDigits: Int,
    style: TextStyle,
    color: Color
) {
    val text = value.toString().padStart(minDigits, '0')

    AnimatedContent(
        targetState = text,
        transitionSpec = {
            fadeIn(animationSpec = tween(150)) togetherWith
                    fadeOut(animationSpec = tween(150))
        },
        label = "digitAnimation"
    ) { targetText ->
        Text(
            text = targetText,
            style = style,
            color = color
        )
    }
}

/**
 * Compact timer for widgets and small displays.
 */
@Composable
fun CompactTimerDisplay(
    hours: Int,
    minutes: Int,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = InterfastTypography.headlineLarge,
    color: Color = InterfastColors.PureWhite
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "%02d".format(hours),
            style = textStyle,
            color = color
        )
        Text(
            text = ":",
            style = textStyle,
            color = color
        )
        Text(
            text = "%02d".format(minutes),
            style = textStyle,
            color = color
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun TimerDisplayPreview() {
    InterfastTheme {
        TimerDisplay(
            hours = 16,
            minutes = 42,
            seconds = 8,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun FullTimerDisplayPreview() {
    InterfastTheme {
        FullTimerDisplay(
            elapsed = Duration.ofHours(16).plusMinutes(42).plusSeconds(8),
            target = Duration.ofHours(18),
            modifier = Modifier.padding(24.dp)
        )
    }
}
