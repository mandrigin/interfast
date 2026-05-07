package com.interfast.ui.scrubber

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.interfast.R
import com.interfast.data.ScheduleRepository
import com.interfast.ui.components.PrimaryActionButton
import com.interfast.ui.theme.InterfastColors
import com.interfast.ui.theme.InterfastTypography
import com.interfast.ui.theme.JetBrainsMono
import com.interfast.ui.theme.LocalSurfaceTokens
import com.interfast.ui.theme.SurfaceTokens
import com.interfast.ui.util.HapticPatterns
import com.interfast.ui.util.rememberHapticFeedback
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private const val MS_PER_MINUTE = 60_000L
private const val MS_PER_HOUR = 3_600_000L

@Composable
fun ScrubberScreen(
    notificationsGranted: Boolean,
    viewModel: ScheduleViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val now by viewModel.now.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haptics = rememberHapticFeedback()
    val tokens = LocalSurfaceTokens.current

    val heroAlpha = remember { Animatable(0f) }
    val heroOffsetY = remember { Animatable(48f) }
    LaunchedEffect(Unit) {
        launch { heroAlpha.animateTo(1f, tween(700)) }
        heroOffsetY.animateTo(0f, spring(dampingRatio = 0.72f, stiffness = 160f))
    }

    val edition = remember {
        "N° " + LocalDate.now().dayOfYear.toString().padStart(4, '0')
    }
    val ghostNumeral = remember {
        // Use day-of-year mod 100 as a 2-digit numeral that subtly rotates
        // with the calendar — like an issue number on a printed poster.
        (LocalDate.now().dayOfYear % 100).toString().padStart(2, '0')
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background),
    ) {
        BackgroundDots(
            modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 0.55f },
            color = tokens.divider,
        )

        // Oversized rotated numeral, behind everything — the "poster" mark.
        GhostedNumeral(
            text = ghostNumeral,
            color = tokens.textPrimary,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = 80.dp),
        )

        // Vertical edition stamp, anchored to the right margin.
        RotatedStamp(
            text = "EDITION · $edition",
            color = tokens.textSecondary,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 18.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            BrandHeader(active = state.active, edition = edition, tokens = tokens)
            HorizontalDivider(thickness = 1.dp, color = tokens.divider)

            HeroTitle(
                active = state.active,
                hasReached = state.reachedHours.isNotEmpty(),
                tokens = tokens,
                modifier = Modifier.graphicsLayer {
                    alpha = heroAlpha.value
                    translationY = heroOffsetY.value
                },
            )

            DiagonalAccent(angle = -3f)

            Scrubber(
                startMillis = state.startEpochMillis,
                nowMillis = now,
                active = state.active,
                tokens = tokens,
                onScrubMinutes = { delta ->
                    viewModel.setStartTime(state.startEpochMillis + delta * MS_PER_MINUTE)
                    haptics.perform(HapticPatterns.TICK)
                },
                onLongPressNow = {
                    viewModel.setStartTime(System.currentTimeMillis())
                    haptics.perform(HapticPatterns.CLICK)
                },
            )

            SectionHeader(label = "TARGETS", count = state.checkedHours.size, tokens = tokens)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ScheduleRepository.ALL_HOURS.forEachIndexed { idx, hour ->
                    val target = state.startEpochMillis + hour * MS_PER_HOUR
                    val isPast = target <= now
                    val isReached = state.reachedHours.contains(hour)
                    val isChecked = state.checkedHours.contains(hour)
                    IndexedHourRow(
                        index = idx + 1,
                        hour = hour,
                        targetMillis = target,
                        checked = isChecked,
                        enabled = !state.active && (!isPast || isReached),
                        isPast = isPast && !isReached,
                        isReached = isReached,
                        tokens = tokens,
                        animationDelayMs = 100L + idx * 60L,
                        onToggle = {
                            viewModel.toggleHour(hour)
                            haptics.perform(HapticPatterns.CLICK)
                        },
                    )
                }
            }

            ProgressStrip(
                longestHour = state.checkedHours.maxOrNull() ?: 0,
                tokens = tokens,
            )

            val hasFutureTargets = state.checkedHours.any { hour ->
                (state.startEpochMillis + hour * MS_PER_HOUR) > now
            }
            val canActivate = state.checkedHours.isNotEmpty() && hasFutureTargets

            val canExact = remember(state.active) { viewModel.canScheduleExact() }
            if (!canExact) {
                Text(
                    text = context.getString(R.string.permission_exact_alarm_required),
                    style = InterfastTypography.labelSmall,
                    color = InterfastColors.AmberWarning,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                context.startActivity(
                                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            }
                        },
                    textAlign = TextAlign.Center,
                )
            }
            if (!notificationsGranted) {
                Text(
                    text = context.getString(R.string.permission_post_notifications_denied),
                    style = InterfastTypography.labelSmall,
                    color = InterfastColors.AmberWarning,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }

            PrimaryActionButton(
                text = if (state.active) "DEACTIVATE" else "ACTIVATE",
                onClick = {
                    if (state.active) viewModel.deactivate() else viewModel.activate()
                    haptics.perform(HapticPatterns.HEAVY_CLICK)
                },
                enabled = state.active || canActivate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
            )

            FooterMark(active = state.active, tokens = tokens)
        }
    }
}

@Composable
private fun Scrubber(
    startMillis: Long,
    nowMillis: Long,
    active: Boolean,
    tokens: SurfaceTokens,
    onScrubMinutes: (deltaMinutes: Int) -> Unit,
    onLongPressNow: () -> Unit,
) {
    val density = LocalDensity.current
    val pxPerMinute = with(density) { 6.dp.toPx() }
    var accumulatedPx by remember { mutableStateOf(0f) }

    val zone = remember { ZoneId.systemDefault() }
    val timeText = remember(startMillis) {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(startMillis), zone)
            .format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()))
    }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()) }

    val minStr = remember(startMillis) {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(startMillis), zone).minute
            .toString().padStart(2, '0')
    }
    val posStr = remember(startMillis) {
        (startMillis / 60_000L % 10000L).toString().padStart(4, '0')
    }
    val offsetStr = remember(startMillis, nowMillis) {
        val mins = (startMillis - nowMillis) / 60_000L
        val sign = when {
            mins > 0 -> "+"
            mins < 0 -> "−"
            else -> "·"
        }
        "$sign${abs(mins)}m"
    }
    val offsetIsPositive = startMillis > nowMillis

    // Reels rotate 12° per minute of position — visible motion as you scrub.
    val reelAngle = ((startMillis / 60_000L) * 12f) % 360f

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(
        color = tokens.textSecondary,
        fontFamily = JetBrainsMono,
        fontSize = 10.sp,
    )

    val scrollState = rememberScrollableState { delta ->
        accumulatedPx -= delta
        val whole = (accumulatedPx / pxPerMinute).roundToInt()
        if (whole != 0) {
            onScrubMinutes(whole)
            accumulatedPx -= whole * pxPerMinute
        }
        delta
    }

    val baseModifier = Modifier
        .fillMaxWidth()
        .height(178.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(tokens.surface)
        .border(
            1.dp,
            if (active) InterfastColors.GlyphRed.copy(alpha = 0.5f) else tokens.divider,
            RoundedCornerShape(10.dp),
        )

    val gestureModifier = if (active) Modifier else Modifier
        .scrollable(
            state = scrollState,
            orientation = Orientation.Horizontal,
            flingBehavior = ScrollableDefaults.flingBehavior(),
        )
        .pointerInput(Unit) {
            detectTapGestures(onLongPress = { onLongPressNow() })
        }

    val majorTickColor = tokens.textSecondary
    val minorTickColor = tokens.textSecondary.copy(alpha = 0.45f)
    val readoutStyle = InterfastTypography.labelSmall.copy(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
    )

    Column(modifier = baseModifier.then(gestureModifier)) {
        // ── TOP: LED status row ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            LedPip(InterfastColors.GlyphRed)
            LedPip(InterfastColors.GlyphRed.copy(alpha = 0.35f))
            LedPip(if (active) InterfastColors.GlyphRed else tokens.divider)
            LedPip(InterfastColors.GlyphRed.copy(alpha = 0.35f))
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (active) "TAPE · LIVE" else "TAPE · IDLE",
                color = tokens.textSecondary,
                style = readoutStyle.copy(letterSpacing = 1.6.sp),
            )
        }

        HorizontalDivider(thickness = 1.dp, color = tokens.divider.copy(alpha = 0.6f))

        // ── MIDDLE: spinning reels flank the wheel ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            TapeReel(angleDegrees = reelAngle, color = tokens.textSecondary)
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerX = size.width / 2f
                    val wheelY = size.height * 0.74f
                    val halfWidthMin = (size.width / 2f / pxPerMinute).toInt() + 2

                    for (m in -halfWidthMin..halfWidthMin) {
                        val absMillis = startMillis + m * MS_PER_MINUTE
                        val clockMin = Math.floorDiv(absMillis, MS_PER_MINUTE)
                        if (clockMin % 5L != 0L) continue
                        val isMajor = clockMin % 30L == 0L
                        val deltaMin = (absMillis - startMillis).toFloat() / MS_PER_MINUTE
                        val x = centerX + deltaMin * pxPerMinute
                        val tickH = if (isMajor) 26f else 10f
                        drawLine(
                            color = if (isMajor) majorTickColor else minorTickColor,
                            start = Offset(x, wheelY - tickH / 2f),
                            end = Offset(x, wheelY + tickH / 2f),
                            strokeWidth = if (isMajor) 1.5f else 1f,
                        )
                        if (isMajor) {
                            val labelDt = LocalDateTime.ofInstant(Instant.ofEpochMilli(absMillis), zone)
                            val labelText = labelDt.format(timeFormatter)
                            val layout = textMeasurer.measure(text = labelText, style = labelStyle)
                            drawText(
                                textLayoutResult = layout,
                                topLeft = Offset(
                                    x - layout.size.width / 2f,
                                    wheelY + tickH / 2f + 4f,
                                ),
                            )
                        }
                    }

                    drawLine(
                        color = InterfastColors.GlyphRed,
                        start = Offset(centerX, wheelY - 30f),
                        end = Offset(centerX, wheelY + 30f),
                        strokeWidth = 2f,
                    )
                }
                Text(
                    text = timeText,
                    color = if (active) InterfastColors.GlyphRed else tokens.textPrimary,
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Black,
                    fontSize = 38.sp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 6.dp),
                )
            }
            TapeReel(angleDegrees = -reelAngle, color = tokens.textSecondary)
        }

        HorizontalDivider(thickness = 1.dp, color = tokens.divider.copy(alpha = 0.6f))

        // ── BOTTOM: OP-1 style readout ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("MIN $minStr", color = tokens.textSecondary, style = readoutStyle)
            Text("POS $posStr", color = tokens.textSecondary, style = readoutStyle)
            Text(
                text = "Δ $offsetStr",
                color = if (offsetIsPositive) InterfastColors.GlyphRed else tokens.textSecondary,
                style = readoutStyle,
            )
        }
    }
}

@Composable
private fun TapeReel(angleDegrees: Float, color: Color) {
    Canvas(modifier = Modifier.size(40.dp)) {
        val r = size.minDimension / 2f - 2f
        val cx = size.width / 2f
        val cy = size.height / 2f
        // Outer ring
        drawCircle(
            color = color.copy(alpha = 0.35f),
            radius = r,
            center = Offset(cx, cy),
            style = Stroke(width = 1.5f),
        )
        // Inner ring
        drawCircle(
            color = color.copy(alpha = 0.25f),
            radius = r * 0.5f,
            center = Offset(cx, cy),
            style = Stroke(width = 1f),
        )
        // Spokes
        val rad = angleDegrees * Math.PI.toFloat() / 180f
        for (i in 0 until 6) {
            val theta = rad + i * (Math.PI.toFloat() / 3f)
            val x1 = cx + (r * 0.30f) * cos(theta)
            val y1 = cy + (r * 0.30f) * sin(theta)
            val x2 = cx + (r * 0.85f) * cos(theta)
            val y2 = cy + (r * 0.85f) * sin(theta)
            drawLine(color, Offset(x1, y1), Offset(x2, y2), 1.5f)
        }
        // Center hub
        drawCircle(color, radius = 3f, center = Offset(cx, cy))
    }
}

@Composable
private fun LedPip(color: Color) {
    Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
}
