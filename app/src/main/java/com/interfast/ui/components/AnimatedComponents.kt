package com.interfast.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.interfast.ui.theme.InterfastColors
import com.interfast.ui.theme.InterfastTypography
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Breathing dot that pulses like it's alive.
 * Fidget-friendly - tap it for a satisfying bounce!
 */
@Composable
fun BreathingDot(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 12.dp,
    breathingSpeed: Int = 2000
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(breathingSpeed, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe_scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(breathingSpeed, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe_alpha"
    )

    var bounceScale by remember { mutableFloatStateOf(1f) }
    val animatedBounce by animateFloatAsState(
        targetValue = bounceScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bounce"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale * animatedBounce)
            .alpha(alpha)
            .clip(CircleShape)
            .background(color)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                bounceScale = 1.5f
            }
    )

    LaunchedEffect(bounceScale) {
        if (bounceScale > 1f) {
            delay(100)
            bounceScale = 1f
        }
    }
}

/**
 * Animated digit that rolls like an odometer.
 * Each digit animates independently for that mechanical feel.
 */
@Composable
fun OdometerDigit(
    digit: Int,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = InterfastTypography.displayLarge,
    color: Color = InterfastColors.PureWhite
) {
    var previousDigit by remember { mutableIntStateOf(digit) }
    var isAnimating by remember { mutableStateOf(false) }

    val offsetY by animateFloatAsState(
        targetValue = if (isAnimating) -50f else 0f,
        animationSpec = tween(150, easing = FastOutSlowInEasing),
        finishedListener = {
            if (isAnimating) {
                previousDigit = digit
                isAnimating = false
            }
        },
        label = "digit_roll"
    )

    LaunchedEffect(digit) {
        if (digit != previousDigit) {
            isAnimating = true
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit.toString(),
            style = textStyle,
            color = color,
            modifier = Modifier
                .offset { IntOffset(0, offsetY.roundToInt()) }
                .alpha(if (isAnimating) 0.5f else 1f)
        )
    }
}

/**
 * Animated timer display with rolling digits.
 */
@Composable
fun AnimatedTimerText(
    hours: Int,
    minutes: Int,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = InterfastTypography.displayLarge,
    color: Color = InterfastColors.PureWhite,
    colonColor: Color = InterfastColors.Gray60
) {
    val h1 = hours / 10
    val h2 = hours % 10
    val m1 = minutes / 10
    val m2 = minutes % 10

    // Pulsing colon
    val infiniteTransition = rememberInfiniteTransition(label = "colon")
    val colonAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colon_pulse"
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OdometerDigit(digit = h1, textStyle = textStyle, color = color)
        OdometerDigit(digit = h2, textStyle = textStyle, color = color)
        Text(
            text = ":",
            style = textStyle,
            color = colonColor,
            modifier = Modifier.alpha(colonAlpha)
        )
        OdometerDigit(digit = m1, textStyle = textStyle, color = color)
        OdometerDigit(digit = m2, textStyle = textStyle, color = color)
    }
}

/**
 * Tappable element that bounces satisfyingly when pressed.
 * Good for fidgeting!
 */
@Composable
fun BouncyBox(
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
    onDoubleTap: () -> Unit = {},
    onLongPress: () -> Unit = {},
    content: @Composable () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bouncy_scale"
    )

    Box(
        modifier = modifier
            .scale(animatedScale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        scale = 0.95f
                        onTap()
                    },
                    onDoubleTap = {
                        scale = 0.9f
                        onDoubleTap()
                    },
                    onLongPress = {
                        scale = 0.85f
                        onLongPress()
                    },
                    onPress = {
                        scale = 0.97f
                        tryAwaitRelease()
                        scale = 1f
                    }
                )
            }
    ) {
        content()
    }

    LaunchedEffect(scale) {
        if (scale < 1f) {
            delay(100)
            scale = 1f
        }
    }
}

/**
 * Celebration particles that burst out on achievement!
 */
@Composable
fun CelebrationBurst(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    particleCount: Int = 12,
    color: Color = InterfastColors.PhosphorGreen
) {
    if (!isActive) return

    val particles = remember {
        List(particleCount) {
            Particle(
                angle = (360f / particleCount) * it,
                speed = Random.nextFloat() * 0.5f + 0.5f
            )
        }
    }

    particles.forEach { particle ->
        val animatedDistance = remember { Animatable(0f) }
        val animatedAlpha = remember { Animatable(1f) }

        LaunchedEffect(isActive) {
            if (isActive) {
                animatedDistance.snapTo(0f)
                animatedAlpha.snapTo(1f)
                animatedDistance.animateTo(
                    targetValue = 100f * particle.speed,
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                )
            }
        }

        LaunchedEffect(isActive) {
            if (isActive) {
                delay(300)
                animatedAlpha.animateTo(0f, tween(300))
            }
        }

        val offsetX = animatedDistance.value * kotlin.math.cos(Math.toRadians(particle.angle.toDouble())).toFloat()
        val offsetY = animatedDistance.value * kotlin.math.sin(Math.toRadians(particle.angle.toDouble())).toFloat()

        Box(
            modifier = modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .size(8.dp)
                .alpha(animatedAlpha.value)
                .clip(CircleShape)
                .background(color)
        )
    }
}

private data class Particle(val angle: Float, val speed: Float)

/**
 * Wiggle animation for playful elements.
 */
@Composable
fun WigglyText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = InterfastTypography.headlineMedium,
    color: Color = InterfastColors.PureWhite,
    wiggleAmount: Float = 3f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wiggle")

    Row(modifier = modifier) {
        text.forEachIndexed { index, char ->
            val rotation by infiniteTransition.animateFloat(
                initialValue = -wiggleAmount,
                targetValue = wiggleAmount,
                animationSpec = infiniteRepeatable(
                    animation = tween(300 + index * 50, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "char_wiggle_$index"
            )

            Text(
                text = char.toString(),
                style = textStyle,
                color = color,
                modifier = Modifier.graphicsLayer { rotationZ = rotation }
            )
        }
    }
}

/**
 * Pulsing glow effect behind elements.
 */
@Composable
fun PulsingGlow(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    pulseSpeed: Int = 1500
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseSpeed, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseSpeed, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .alpha(alpha)
            .clip(CircleShape)
            .background(color)
    )
}
