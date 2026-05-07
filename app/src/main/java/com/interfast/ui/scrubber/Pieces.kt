package com.interfast.ui.scrubber

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.interfast.ui.theme.InterfastColors
import com.interfast.ui.theme.InterfastTypography
import com.interfast.ui.theme.JetBrainsMono
import com.interfast.ui.theme.SpaceGrotesk
import com.interfast.ui.theme.SurfaceTokens
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/* ---------------- background ---------------- */

@Composable
fun BackgroundDots(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val spacingPx = 22.dp.toPx()
        // Major dots every 4 cells get a 2x radius — gives the grid a halftone
        // rhythm rather than a flat field.
        var y = spacingPx / 2f
        var row = 0
        while (y < size.height) {
            var x = spacingPx / 2f
            var col = 0
            while (x < size.width) {
                val major = (row % 4 == 0) && (col % 4 == 0)
                drawCircle(
                    color = color,
                    radius = if (major) 2.4f else 1.1f,
                    center = Offset(x, y),
                )
                x += spacingPx
                col += 1
            }
            y += spacingPx
            row += 1
        }
    }
}

/* ---------------- rotated mono stamp ---------------- */

@Composable
fun RotatedStamp(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    angle: Float = -90f,
) {
    Text(
        text = text,
        color = color,
        modifier = modifier.graphicsLayer { rotationZ = angle },
        style = InterfastTypography.labelSmall.copy(
            fontFamily = JetBrainsMono,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
        ),
    )
}

/* ---------------- diagonal accent rule ---------------- */

@Composable
fun DiagonalAccent(modifier: Modifier = Modifier, angle: Float = -4f) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .graphicsLayer { rotationZ = angle },
    ) {
        drawLine(
            color = InterfastColors.GlyphRed,
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = 2f,
        )
    }
}

/* ---------------- ghosted oversized numeral ---------------- */

@Composable
fun GhostedNumeral(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    angle: Float = 6f,
) {
    Text(
        text = text,
        color = color.copy(alpha = 0.06f),
        modifier = modifier.graphicsLayer { rotationZ = angle },
        style = InterfastTypography.displayLarge.copy(
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Black,
            fontSize = 240.sp,
            letterSpacing = (-12).sp,
        ),
    )
}

/* ---------------- breathing red square ---------------- */

@Composable
fun BreathingMark(active: Boolean, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "breath")
    val pulseAlpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alpha",
    )
    val a = if (active) pulseAlpha else 1f
    Box(modifier = modifier.background(InterfastColors.GlyphRed.copy(alpha = a)))
}

/* ---------------- brand row ---------------- */

@Composable
fun BrandHeader(active: Boolean, edition: String, tokens: SurfaceTokens) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BreathingMark(active = active, modifier = Modifier.size(10.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "INTERFAST",
            color = tokens.textPrimary,
            style = InterfastTypography.labelMedium.copy(
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp,
            ),
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = edition,
            color = tokens.textSecondary,
            style = InterfastTypography.labelSmall.copy(fontFamily = JetBrainsMono),
        )
    }
}

/* ---------------- hero headline ---------------- */

@Composable
fun HeroTitle(
    active: Boolean,
    hasReached: Boolean,
    tokens: SurfaceTokens,
    modifier: Modifier = Modifier,
) {
    val text = when {
        hasReached -> "flowing."
        active -> "holding."
        else -> "start your IF."
    }
    Text(
        text = text,
        color = tokens.textPrimary,
        modifier = modifier.fillMaxWidth(),
        style = InterfastTypography.displayMedium.copy(
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Black,
            fontSize = 60.sp,
            lineHeight = 60.sp,
            letterSpacing = (-2).sp,
        ),
    )
}

/* ---------------- section header w/ rule ---------------- */

@Composable
fun SectionHeader(label: String, count: Int, tokens: SurfaceTokens) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "$label // ${count.toString().padStart(2, '0')}",
            color = tokens.textSecondary,
            style = InterfastTypography.labelSmall.copy(
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
            ),
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = tokens.divider,
        )
    }
}

/* ---------------- 24-segment progress strip ---------------- */

@Composable
fun ProgressStrip(longestHour: Int, tokens: SurfaceTokens) {
    val segments = 24
    Row(
        modifier = Modifier.fillMaxWidth().height(6.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        for (i in 0 until segments) {
            val lit = i < longestHour
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (lit) InterfastColors.GlyphRed else tokens.divider),
            )
        }
    }
}

/* ---------------- footer ---------------- */

@Composable
fun FooterMark(active: Boolean, tokens: SurfaceTokens) {
    val context = LocalContext.current
    var taps by remember { mutableStateOf(0) }
    var lastTapMs by remember { mutableStateOf(0L) }
    val tapInteraction = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        StatusDot(InterfastColors.GlyphRed)
        StatusDot(if (active) InterfastColors.GlyphRed else tokens.divider)
        StatusDot(InterfastColors.GlyphRed)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "TX-1 // MANDRIGIN · 2026",
            color = tokens.textSecondary.copy(alpha = 0.7f),
            style = InterfastTypography.labelSmall.copy(
                fontFamily = JetBrainsMono,
                letterSpacing = 1.2.sp,
            ),
            modifier = Modifier.clickable(
                interactionSource = tapInteraction,
                indication = null,
            ) {
                val now = System.currentTimeMillis()
                if (now - lastTapMs > 1500L) taps = 0
                taps += 1
                lastTapMs = now
                if (taps >= 5) {
                    taps = 0
                    com.interfast.alarm.EasterEgg.fire(context)
                }
            },
        )
    }
}

@Composable
private fun StatusDot(color: Color) {
    Box(modifier = Modifier.size(4.dp).background(color, CircleShape))
}

/* ---------------- toggle row ---------------- */

@Composable
fun IndexedHourRow(
    index: Int,
    hour: Int,
    targetMillis: Long,
    checked: Boolean,
    enabled: Boolean,
    isPast: Boolean,
    isReached: Boolean,
    tokens: SurfaceTokens,
    animationDelayMs: Long,
    onToggle: () -> Unit,
) {
    // Cache target text by targetMillis to avoid per-frame allocation.
    val timeText = remember(targetMillis) {
        val zone = ZoneId.systemDefault()
        val targetDt = LocalDateTime.ofInstant(Instant.ofEpochMilli(targetMillis), zone)
        val targetDate = targetDt.toLocalDate()
        val today = LocalDate.now(zone)
        val dayLabel = when {
            targetDate == today -> ""
            targetDate == today.plusDays(1) -> " TMR"
            else -> "  " + targetDate.format(
                DateTimeFormatter.ofPattern("EEE d", Locale.getDefault())
            ).uppercase()
        }
        targetDt.format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())) + dayLabel
    }

    val rowColor = when {
        isReached -> InterfastColors.PhosphorGreen
        isPast -> tokens.textSecondary.copy(alpha = 0.6f)
        checked -> tokens.textPrimary
        else -> tokens.textSecondary
    }

    val rowAlpha = remember { Animatable(0f) }
    val rowOffsetY = remember { Animatable(28f) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(animationDelayMs)
        kotlinx.coroutines.coroutineScope {
            launch { rowAlpha.animateTo(1f, tween(420)) }
            rowOffsetY.animateTo(0f, spring(dampingRatio = 0.7f, stiffness = 220f))
        }
    }

    // Toggle bump on `checked` change after first composition.
    val markScale = remember { Animatable(1f) }
    var firstFrame by remember { mutableStateOf(true) }
    LaunchedEffect(checked) {
        if (firstFrame) {
            firstFrame = false
            return@LaunchedEffect
        }
        markScale.snapTo(1.6f)
        markScale.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = 600f))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = rowAlpha.value
                translationY = rowOffsetY.value
            }
            .clip(RoundedCornerShape(6.dp))
            .background(tokens.surface)
            .clickable(enabled = enabled && !isPast, onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = index.toString().padStart(2, '0'),
            color = tokens.textSecondary,
            style = InterfastTypography.labelSmall.copy(
                fontFamily = JetBrainsMono,
                letterSpacing = 1.sp,
            ),
            modifier = Modifier.width(20.dp),
        )
        CheckMark(
            checked = checked,
            color = rowColor,
            tokens = tokens,
            modifier = Modifier.scale(markScale.value),
        )
        Text(
            text = "${hour}H",
            color = rowColor,
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            modifier = Modifier.width(56.dp),
        )
        Text(
            text = "→",
            color = tokens.textSecondary,
            style = InterfastTypography.bodyMedium,
        )
        Text(
            text = timeText,
            color = rowColor,
            fontFamily = JetBrainsMono,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
        )
        when {
            isReached -> StatusBadge("DONE", InterfastColors.PhosphorGreen)
            isPast -> StatusBadge("PAST", tokens.textSecondary.copy(alpha = 0.6f))
            else -> {}
        }
    }
}

@Composable
private fun CheckMark(checked: Boolean, color: Color, tokens: SurfaceTokens, modifier: Modifier) {
    Box(
        modifier = modifier
            .size(18.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(if (checked) color else Color.Transparent)
            .border(
                1.5.dp,
                if (checked) color else tokens.textSecondary.copy(alpha = 0.6f),
                RoundedCornerShape(3.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Box(modifier = Modifier.size(7.dp).background(tokens.background, CircleShape))
        }
    }
}

@Composable
private fun StatusBadge(label: String, color: Color) {
    Text(
        text = label,
        color = color,
        style = InterfastTypography.labelSmall.copy(
            fontFamily = JetBrainsMono,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .border(1.dp, color, RoundedCornerShape(3.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}
