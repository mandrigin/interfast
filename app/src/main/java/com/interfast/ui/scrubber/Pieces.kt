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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
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
        // Never wrap: at large font scales a wrapped rotated line degenerates
        // into scattered single glyphs along the screen edge.
        softWrap = false,
        maxLines = 1,
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
fun BrandHeader(
    active: Boolean,
    edition: String,
    tokens: SurfaceTokens,
    fairphoneTag: String? = null,
    onEditionTap: () -> Unit = {},
) {
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
        // Fairphone units: a single teal square — the Essential Key's color,
        // printed next to the brand like a hardware compatibility mark.
        if (fairphoneTag != null) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(InterfastColors.EssentialTeal),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        // Tapping the edition stamp flips the unit over — the manual is
        // printed on the back, like any decent piece of hardware. clickable
        // precedes the padding so the hit area is comfortably larger than
        // the printed stamp.
        Text(
            text = "$edition ⟲",
            color = tokens.textSecondary,
            style = InterfastTypography.labelSmall.copy(fontFamily = JetBrainsMono),
            modifier = Modifier
                .clip(RoundedCornerShape(3.dp))
                .clickable(onClick = onEditionTap)
                .padding(horizontal = 10.dp, vertical = 14.dp)
                .semantics { contentDescription = "Flip to rear panel, operator's manual" },
        )
    }
}

/* ---------------- hero headline ---------------- */

@Composable
fun HeroTitle(
    active: Boolean,
    hasReached: Boolean,
    startInFuture: Boolean,
    tokens: SurfaceTokens,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 60.sp,
) {
    val (text, sub) = when {
        active && hasReached -> "flowing." to "MILESTONE PASSED — KEEP GOING OR EAT."
        active && startInFuture -> "armed." to "WAITING FOR THE START. LEAVE IT."
        active -> "holding." to "ALARMS SET. FORGET THE PHONE."
        hasReached -> "done." to "ALL TARGETS REACHED. TAPE REWOUND."
        else -> "start a fast." to "DRAG TAPE · PICK TARGETS · ACTIVATE"
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = text,
            color = tokens.textPrimary,
            modifier = Modifier.fillMaxWidth(),
            style = InterfastTypography.displayMedium.copy(
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Black,
                fontSize = fontSize,
                lineHeight = fontSize,
                letterSpacing = (-2).sp,
            ),
        )
        Text(
            text = sub,
            color = tokens.textSecondary,
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            style = InterfastTypography.labelSmall.copy(
                fontFamily = JetBrainsMono,
                letterSpacing = 1.5.sp,
            ),
        )
    }
}

/* ---------------- section header w/ rule ---------------- */

@Composable
fun SectionHeader(
    label: String,
    count: Int,
    tokens: SurfaceTokens,
    hint: String? = null,
) {
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
        if (hint != null) {
            Text(
                text = hint,
                color = InterfastColors.AmberWarning,
                style = InterfastTypography.labelSmall.copy(
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                ),
            )
        }
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
    today: LocalDate,
    checked: Boolean,
    enabled: Boolean,
    isPast: Boolean,
    isReached: Boolean,
    tokens: SurfaceTokens,
    animationDelayMs: Long,
    rowVPad: Dp = 12.dp,
    hourFontSize: TextUnit = 22.sp,
    onToggle: () -> Unit,
) {
    // Cache target text by target + calendar day so labels stay correct
    // across midnight without re-formatting every frame.
    val timeText = remember(targetMillis, today) {
        val zone = ZoneId.systemDefault()
        val targetDt = LocalDateTime.ofInstant(Instant.ofEpochMilli(targetMillis), zone)
        val targetDate = targetDt.toLocalDate()
        val dayLabel = when {
            targetDate == today -> ""
            targetDate == today.plusDays(1) -> " +1D"
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

    // Spell the row out for TalkBack as one unit; the visual fragments
    // ("03", "18H", "→", "14:19") merge into a sentence with toggle state.
    val a11yDescription = buildString {
        append("$hour hour milestone, alarm at $timeText")
        if (isReached) append(", done")
        if (isPast) append(", already past")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                // Past rows dim as a unit — their disabled-ness should be
                // visible before you try to tap them.
                alpha = rowAlpha.value * (if (isPast) 0.45f else 1f)
                translationY = rowOffsetY.value
            }
            .clip(RoundedCornerShape(6.dp))
            .background(tokens.surface)
            .toggleable(
                value = checked,
                enabled = enabled && !isPast,
                role = Role.Checkbox,
                onValueChange = { onToggle() },
            )
            .semantics { contentDescription = a11yDescription }
            .padding(horizontal = 14.dp, vertical = rowVPad),
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
            // Wide enough that "01" survives large font scales unwrapped.
            modifier = Modifier.width(24.dp),
            maxLines = 1,
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
            fontSize = hourFontSize,
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
            textDecoration = if (isPast) TextDecoration.LineThrough else null,
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
