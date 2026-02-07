package com.interfast.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.interfast.ui.theme.InterfastColors
import com.interfast.ui.theme.InterfastTheme
import com.interfast.ui.theme.Motion
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Circular dot progress indicator - signature Interfast component.
 *
 * Inspired by Nothing Phone's Glyph Interface and dot matrix aesthetics.
 * Each dot transitions individually with a staggered animation.
 *
 * @param progress Progress value from 0f to 1f
 * @param dotCount Number of dots in the ring (default 24)
 * @param activeColor Color for active (filled) dots
 * @param inactiveColor Color for inactive dots
 * @param modifier Modifier for sizing
 * @param content Optional content to display in the center
 */
@Composable
fun DotProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    dotCount: Int = 24,
    activeColor: Color = InterfastColors.GlyphRed,
    inactiveColor: Color = InterfastColors.Gray15,
    dotSizeFraction: Float = 0.08f,
    ringRadiusFraction: Float = 0.42f,
    content: @Composable () -> Unit = {}
) {
    val activeDots = (progress * dotCount).toInt()

    Box(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val ringRadius = size.minDimension * ringRadiusFraction
            val dotRadius = size.minDimension * dotSizeFraction / 2

            for (i in 0 until dotCount) {
                // Start from top (12 o'clock position), go clockwise
                val angle = -PI / 2 + (2 * PI * i / dotCount)
                val x = centerX + ringRadius * cos(angle).toFloat()
                val y = centerY + ringRadius * sin(angle).toFloat()

                val isActive = i < activeDots
                val color = if (isActive) activeColor else inactiveColor

                drawCircle(
                    color = color,
                    radius = dotRadius,
                    center = Offset(x, y)
                )
            }
        }

        content()
    }
}

/**
 * Animated version of DotProgressRing with smooth dot transitions.
 */
@Composable
fun AnimatedDotProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    dotCount: Int = 24,
    activeColor: Color = InterfastColors.GlyphRed,
    inactiveColor: Color = InterfastColors.Gray15,
    dotSizeFraction: Float = 0.08f,
    ringRadiusFraction: Float = 0.42f,
    content: @Composable () -> Unit = {}
) {
    val activeDots = (progress * dotCount).toInt()

    Box(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val ringRadius = size.minDimension * ringRadiusFraction
            val dotRadius = size.minDimension * dotSizeFraction / 2

            for (i in 0 until dotCount) {
                val angle = -PI / 2 + (2 * PI * i / dotCount)
                val x = centerX + ringRadius * cos(angle).toFloat()
                val y = centerY + ringRadius * sin(angle).toFloat()

                val isActive = i < activeDots
                // Partial fill for the current dot
                val isCurrent = i == activeDots && progress > 0f
                val partialProgress = if (isCurrent) {
                    (progress * dotCount) - activeDots
                } else 0f

                val color = when {
                    isActive -> activeColor
                    isCurrent -> activeColor.copy(alpha = partialProgress)
                    else -> inactiveColor
                }

                drawCircle(
                    color = color,
                    radius = dotRadius,
                    center = Offset(x, y)
                )
            }
        }

        content()
    }
}

/**
 * Linear dot progress bar - horizontal variant.
 */
@Composable
fun DotProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    dotCount: Int = 20,
    activeColor: Color = InterfastColors.GlyphRed,
    inactiveColor: Color = InterfastColors.Gray15,
    dotSize: Dp = 8.dp,
    dotSpacing: Dp = 4.dp
) {
    val activeDots = (progress * dotCount).toInt()

    Canvas(
        modifier = modifier
    ) {
        val totalWidth = size.width
        val dotRadiusPx = dotSize.toPx() / 2
        val spacingPx = dotSpacing.toPx()
        val totalDotWidth = dotCount * dotSize.toPx() + (dotCount - 1) * spacingPx
        val startX = (totalWidth - totalDotWidth) / 2 + dotRadiusPx

        for (i in 0 until dotCount) {
            val x = startX + i * (dotSize.toPx() + spacingPx)
            val y = size.height / 2

            val isActive = i < activeDots
            val isCurrent = i == activeDots && progress > 0f
            val partialProgress = if (isCurrent) {
                (progress * dotCount) - activeDots
            } else 0f

            val color = when {
                isActive -> activeColor
                isCurrent -> activeColor.copy(alpha = partialProgress)
                else -> inactiveColor
            }

            drawCircle(
                color = color,
                radius = dotRadiusPx,
                center = Offset(x, y)
            )
        }
    }
}

/**
 * Compact dot indicator for status display.
 */
@Composable
fun StatusDot(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 8.dp,
    pulsing: Boolean = false
) {
    Canvas(modifier = modifier.size(size)) {
        drawCircle(
            color = color,
            radius = this.size.minDimension / 2
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun DotProgressRingPreview() {
    InterfastTheme {
        DotProgressRing(
            progress = 0.75f,
            modifier = Modifier.size(200.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun DotProgressBarPreview() {
    InterfastTheme {
        DotProgressBar(
            progress = 0.6f,
            modifier = Modifier.size(width = 300.dp, height = 24.dp)
        )
    }
}
