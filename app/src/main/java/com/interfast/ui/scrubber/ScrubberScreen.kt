package com.interfast.ui.scrubber

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
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
import com.interfast.ui.theme.DeviceFlavor
import com.interfast.ui.theme.fitTierFor
import com.interfast.ui.theme.layoutFitFor
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
    onFlipToRear: () -> Unit = {},
    viewModel: ScheduleViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val now by viewModel.now.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haptics = rememberHapticFeedback()
    val tokens = LocalSurfaceTokens.current

    var scrubHintWriteSent by remember { mutableStateOf(false) }

    val heroAlpha = remember { Animatable(0f) }
    val heroOffsetY = remember { Animatable(48f) }
    LaunchedEffect(Unit) {
        launch { heroAlpha.animateTo(1f, tween(700)) }
        heroOffsetY.animateTo(0f, spring(dampingRatio = 0.72f, stiffness = 160f))
    }

    // Keyed on the calendar date so an app left open across midnight doesn't
    // show yesterday's issue number. Fairphone units carry their model tag —
    // the edition is printed for the device it runs on.
    val zone = remember { ZoneId.systemDefault() }
    val today = LocalDateTime.ofInstant(Instant.ofEpochMilli(now), zone).toLocalDate()
    val edition = remember(today) {
        "N° " + today.dayOfYear.toString().padStart(4, '0') +
            (DeviceFlavor.fairphoneTag?.let { " · $it" } ?: "")
    }
    val ghostNumeral = remember(today) {
        // Day-of-year mod 100 as a 2-digit numeral that subtly rotates
        // with the calendar — like an issue number on a printed poster.
        (today.dayOfYear % 100).toString().padStart(2, '0')
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.background),
    ) {
        // The deck must land whole: pick a size regime from the vertical
        // space actually left after system bars, whatever device it is.
        val bars = WindowInsets.systemBars.asPaddingValues()
        val usableHeightDp =
            (maxHeight - bars.calculateTopPadding() - bars.calculateBottomPadding())
                .value.roundToInt()
        val fit = remember(usableHeightDp) { layoutFitFor(fitTierFor(usableHeightDp)) }
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
                .padding(horizontal = 20.dp, vertical = fit.outerPadV),
            verticalArrangement = Arrangement.spacedBy(fit.sectionGap),
        ) {
            BrandHeader(
                active = state.active,
                edition = edition,
                tokens = tokens,
                fairphoneTag = DeviceFlavor.fairphoneTag,
                onEditionTap = onFlipToRear,
            )
            HorizontalDivider(thickness = 1.dp, color = tokens.divider)

            HeroTitle(
                active = state.active,
                hasReached = state.reachedHours.isNotEmpty(),
                startInFuture = state.startEpochMillis > now,
                tokens = tokens,
                fontSize = fit.heroFontSize,
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
                checkedHours = state.checkedHours,
                reachedHours = state.reachedHours,
                showDragHint = !state.active && !state.scrubHintDismissed,
                deckHeight = fit.deckHeight,
                reelSize = fit.reelSize,
                clockFontSize = fit.clockFontSize,
                tokens = tokens,
                onScrubMinutes = { delta ->
                    viewModel.setStartTime(state.startEpochMillis + delta * MS_PER_MINUTE)
                    // Local latch: state.scrubHintDismissed lags the DataStore
                    // round-trip, and the first drag emits dozens of ticks.
                    if (!state.scrubHintDismissed && !scrubHintWriteSent) {
                        scrubHintWriteSent = true
                        viewModel.dismissScrubHint()
                    }
                    haptics.perform(HapticPatterns.TICK)
                },
                onLongPressNow = {
                    viewModel.setStartTime(System.currentTimeMillis())
                    haptics.perform(HapticPatterns.CLICK)
                },
            )

            val hasFutureTargets = state.checkedHours.any { hour ->
                (state.startEpochMillis + hour * MS_PER_HOUR) > now
            }
            SectionHeader(
                label = "TARGETS",
                count = state.checkedHours.size,
                tokens = tokens,
                hint = when {
                    state.active -> null
                    state.checkedHours.isEmpty() -> stringResource(R.string.targets_hint_pick_one)
                    !hasFutureTargets -> stringResource(R.string.targets_hint_all_past)
                    else -> null
                },
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(fit.rowGap),
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
                        today = today,
                        checked = isChecked,
                        enabled = !state.active && (!isPast || isReached),
                        isPast = isPast && !isReached,
                        isReached = isReached,
                        tokens = tokens,
                        rowVPad = fit.rowVPad,
                        hourFontSize = fit.rowHourFont,
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

            val noTargetsHint = stringResource(R.string.targets_hint_pick_one)
            PrimaryActionButton(
                text = stringResource(if (state.active) R.string.disarm else R.string.arm),
                onClick = {
                    if (state.active) viewModel.deactivate() else viewModel.activate()
                    haptics.perform(HapticPatterns.HEAVY_CLICK)
                },
                enabled = state.active || canActivate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(fit.buttonHeight)
                    .semantics {
                        stateDescription = when {
                            state.active -> "Armed"
                            canActivate -> "Ready"
                            else -> noTargetsHint.lowercase()
                        }
                        if (!state.active && !canActivate) disabled()
                    },
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
    checkedHours: Set<Int>,
    reachedHours: Set<Int>,
    showDragHint: Boolean,
    deckHeight: Dp,
    reelSize: Dp,
    clockFontSize: TextUnit,
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

    // The readout row carries only true data: how many alarms are armed, when
    // the next one fires, and how far the start sits from now.
    val alarmsStr = "ALARMS " + checkedHours.size.toString().padStart(2, '0')
    val nextStr = remember(startMillis, nowMillis / 60_000L, checkedHours) {
        val next = checkedHours
            .map { startMillis + it * MS_PER_HOUR }
            .filter { it > nowMillis }
            .minOrNull()
        "NEXT " + if (next != null) {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(next), zone).format(timeFormatter)
        } else {
            "——:——"
        }
    }
    val offsetMinutes = (startMillis - nowMillis) / 60_000L
    val offsetStr = remember(offsetMinutes) {
        val sign = when {
            offsetMinutes > 0 -> "+"
            offsetMinutes < 0 -> "−"
            else -> "·"
        }
        val a = abs(offsetMinutes)
        val body = if (a >= 60) "${a / 60}h${(a % 60).toString().padStart(2, '0')}m" else "${a}m"
        "$sign$body"
    }
    val offsetIsPositive = startMillis > nowMillis

    // Reels rotate 12° per minute of position — visible motion as you scrub —
    // and roll continuously while the tape is LIVE.
    val liveSpin = if (active) {
        val transition = rememberInfiniteTransition(label = "reelSpin")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(60_000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "spin",
        ).value
    } else 0f
    val reelAngle = ((startMillis / 60_000L) * 12f) % 360f + liveSpin

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

    // Real semantics for TalkBack: the scrubber is otherwise gesture-only.
    val scrubberDescription =
        "Fast start time $timeText, Δ $offsetStr. Drag horizontally to adjust."
    val semanticsModifier = Modifier.semantics {
        contentDescription = if (active) {
            "Fast start time $timeText. Locked while active."
        } else {
            scrubberDescription
        }
        if (!active) {
            customActions = listOf(
                CustomAccessibilityAction("Earlier 5 minutes") { onScrubMinutes(-5); true },
                CustomAccessibilityAction("Later 5 minutes") { onScrubMinutes(5); true },
                CustomAccessibilityAction("Earlier 1 hour") { onScrubMinutes(-60); true },
                CustomAccessibilityAction("Later 1 hour") { onScrubMinutes(60); true },
                CustomAccessibilityAction("Set to now") { onLongPressNow(); true },
            )
        }
    }

    val baseModifier = Modifier
        .fillMaxWidth()
        .height(deckHeight)
        .clip(RoundedCornerShape(10.dp))
        .background(tokens.surface)
        .border(
            1.dp,
            if (active) InterfastColors.GlyphRed.copy(alpha = 0.5f) else tokens.divider,
            RoundedCornerShape(10.dp),
        )
        .then(semanticsModifier)

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
        // ── TOP: LED status row — one pip per milestone, lit when armed,
        // green when reached. The pips tell the truth.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            ScheduleRepository.ALL_HOURS.forEach { hour ->
                LedPip(
                    when {
                        reachedHours.contains(hour) -> InterfastColors.PhosphorGreen
                        checkedHours.contains(hour) -> InterfastColors.GlyphRed
                        else -> tokens.divider
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (!active) {
                // clickable comes first so the hit area includes the outer
                // padding — the visual chip stays small, the target doesn't.
                Text(
                    text = stringResource(R.string.label_now),
                    color = tokens.textSecondary,
                    style = readoutStyle.copy(letterSpacing = 1.6.sp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onLongPressNow() }
                        .padding(horizontal = 6.dp, vertical = 9.dp)
                        .border(1.dp, tokens.divider, RoundedCornerShape(3.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                )
                Spacer(modifier = Modifier.size(4.dp))
            }
            Text(
                text = if (active) "TAPE · LIVE" else "TAPE · IDLE",
                color = tokens.textSecondary,
                style = readoutStyle.copy(letterSpacing = 1.6.sp),
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
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
            TapeReel(angleDegrees = reelAngle, diameter = reelSize, color = tokens.textSecondary)
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerX = size.width / 2f
                    // Anchor the wheel low, but never so low that major-tick
                    // labels are amputated when the deck runs short.
                    val labelProbe = textMeasurer.measure(text = "00:00", style = labelStyle)
                    val bottomReserve = 13f + 4f + labelProbe.size.height
                    val wheelY = minOf(size.height * 0.74f, size.height - bottomReserve)
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
                    fontSize = clockFontSize,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 6.dp),
                )
                // One-time affordance hint; dismissed forever after the first
                // real scrub.
                // FQN call: inside Row→Box scope the RowScope extension would
                // otherwise shadow the top-level overload.
                androidx.compose.animation.AnimatedVisibility(
                    visible = showDragHint,
                    enter = fadeIn(tween(400)),
                    exit = fadeOut(tween(400)),
                    modifier = Modifier.align(Alignment.Center).offset(y = 6.dp),
                ) {
                    Text(
                        text = stringResource(R.string.scrub_hint),
                        color = tokens.textSecondary.copy(alpha = 0.8f),
                        style = readoutStyle.copy(letterSpacing = 3.sp),
                    )
                }
            }
            TapeReel(angleDegrees = -reelAngle, diameter = reelSize, color = tokens.textSecondary)
        }

        HorizontalDivider(thickness = 1.dp, color = tokens.divider.copy(alpha = 0.6f))

        // ── BOTTOM: readout — every field is true ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(alarmsStr, color = tokens.textSecondary, style = readoutStyle)
            Text(nextStr, color = tokens.textSecondary, style = readoutStyle)
            Text(
                text = "Δ $offsetStr",
                color = if (offsetIsPositive) InterfastColors.GlyphRed else tokens.textSecondary,
                style = readoutStyle,
            )
        }
    }
}

@Composable
private fun TapeReel(angleDegrees: Float, diameter: Dp, color: Color) {
    Canvas(modifier = Modifier.size(diameter)) {
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
