package com.interfast.ui.scrubber

import android.content.Intent
import android.os.Build
import android.provider.Settings
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.interfast.R
import com.interfast.data.ScheduleRepository
import com.interfast.ui.components.PrimaryActionButton
import com.interfast.ui.theme.InterfastColors
import com.interfast.ui.theme.InterfastTypography
import com.interfast.ui.theme.JetBrainsMono
import com.interfast.ui.theme.SpaceGrotesk
import com.interfast.ui.theme.Spacing
import com.interfast.ui.util.HapticPatterns
import com.interfast.ui.util.rememberHapticFeedback
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InterfastColors.VoidBlack)
            .systemBarsPadding()
            .padding(horizontal = Spacing.md, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Header(startMillis = state.startEpochMillis)

        Scrubber(
            startMillis = state.startEpochMillis,
            active = state.active,
            onScrubMinutes = { delta ->
                viewModel.setStartTime(state.startEpochMillis + delta * MS_PER_MINUTE)
                haptics.perform(HapticPatterns.TICK)
            },
            onLongPressNow = {
                viewModel.setStartTime(System.currentTimeMillis())
                haptics.perform(HapticPatterns.CLICK)
            },
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            ScheduleRepository.ALL_HOURS.forEach { hour ->
                val target = state.startEpochMillis + hour * MS_PER_HOUR
                val isPast = target <= now
                val isReached = state.reachedHours.contains(hour)
                val isChecked = state.checkedHours.contains(hour)
                HourRow(
                    hour = hour,
                    targetMillis = target,
                    checked = isChecked,
                    enabled = !state.active && (!isPast || isReached),
                    isPast = isPast && !isReached,
                    isReached = isReached,
                    onToggle = {
                        viewModel.toggleHour(hour)
                        haptics.perform(HapticPatterns.CLICK)
                    },
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (state.active) {
            ActiveIndicator()
        }

        // Future targets remaining if active — used to compute button state.
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
                if (state.active) {
                    viewModel.deactivate()
                } else {
                    viewModel.activate()
                }
                haptics.perform(HapticPatterns.HEAVY_CLICK)
            },
            enabled = state.active || canActivate,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
        )
    }
}

@Composable
private fun Header(startMillis: Long) {
    val zone = ZoneId.systemDefault()
    val dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(startMillis), zone)
    val dayLabel = dt.format(DateTimeFormatter.ofPattern("EEE MMM d", Locale.getDefault())).uppercase()

    Text(
        text = "$dayLabel  ·  PROTOCOL",
        style = InterfastTypography.labelMedium,
        color = InterfastColors.Gray60,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun Scrubber(
    startMillis: Long,
    active: Boolean,
    onScrubMinutes: (deltaMinutes: Int) -> Unit,
    onLongPressNow: () -> Unit,
) {
    val density = LocalDensity.current
    // 6 dp ≈ 1 minute. Tunable: smaller values mean faster scrubbing.
    val pxPerMinute = with(density) { 6.dp.toPx() }
    var accumulatedPx by remember { mutableStateOf(0f) }

    val zone = ZoneId.systemDefault()
    val dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(startMillis), zone)
    val timeText = dt.format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()))

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(
        color = InterfastColors.Gray60,
        fontFamily = JetBrainsMono,
        fontSize = 10.sp,
    )

    // Wheel metaphor: drag right pulls earlier times under the needle, drag
    // left advances time — inverted from the raw drag direction. The system
    // scrollable + flingBehavior gives us the same fling physics as LazyRow.
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
        .height(124.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(if (active) InterfastColors.Gray05 else InterfastColors.Gray10)
        .border(
            1.dp,
            if (active) InterfastColors.GlyphRed.copy(alpha = 0.45f) else InterfastColors.Gray20,
            RoundedCornerShape(12.dp)
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

    Box(modifier = baseModifier.then(gestureModifier)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            // Wheel sits in lower portion; big time digit sits in upper.
            val wheelY = size.height * 0.72f
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
                    color = if (isMajor) InterfastColors.Gray60 else InterfastColors.Gray40,
                    start = Offset(x, wheelY - tickH / 2f),
                    end = Offset(x, wheelY + tickH / 2f),
                    strokeWidth = if (isMajor) 1.5f else 1f
                )
                if (isMajor) {
                    val labelDt = LocalDateTime.ofInstant(Instant.ofEpochMilli(absMillis), zone)
                    val labelText = labelDt.format(
                        DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
                    )
                    val layout = textMeasurer.measure(text = labelText, style = labelStyle)
                    drawText(
                        textLayoutResult = layout,
                        topLeft = Offset(
                            x - layout.size.width / 2f,
                            wheelY + tickH / 2f + 4f
                        )
                    )
                }
            }

            // Center needle — fixed, the wheel moves under it.
            drawLine(
                color = InterfastColors.GlyphRed,
                start = Offset(centerX, wheelY - 32f),
                end = Offset(centerX, wheelY + 32f),
                strokeWidth = 2f
            )
        }

        Text(
            text = timeText,
            color = if (active) InterfastColors.GlyphRed else InterfastColors.PureWhite,
            fontFamily = JetBrainsMono,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp),
        )
    }
}

@Composable
private fun HourRow(
    hour: Int,
    targetMillis: Long,
    checked: Boolean,
    enabled: Boolean,
    isPast: Boolean,
    isReached: Boolean,
    onToggle: () -> Unit,
) {
    val zone = ZoneId.systemDefault()
    val targetDt = LocalDateTime.ofInstant(Instant.ofEpochMilli(targetMillis), zone)
    val nowDt = LocalDateTime.now(zone)
    val targetDate = targetDt.toLocalDate()
    val today = LocalDate.now(zone)
    val dayLabel = when {
        targetDate == today -> ""
        targetDate == today.plusDays(1) -> " TOMORROW"
        else -> "  " + targetDate.format(DateTimeFormatter.ofPattern("EEE d", Locale.getDefault())).uppercase()
    }
    val timeText = targetDt.format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())) + dayLabel

    val rowColor = when {
        isReached -> InterfastColors.PhosphorGreen
        isPast -> InterfastColors.Gray40
        checked -> InterfastColors.PureWhite
        else -> InterfastColors.Gray80
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(InterfastColors.Gray05)
            .clickable(enabled = enabled && !isPast, onClick = onToggle)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Checkbox(checked = checked, color = rowColor)
        Text(
            text = "${hour}H",
            color = rowColor,
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.width(56.dp),
        )
        Text(
            text = "→",
            color = InterfastColors.Gray40,
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
            isPast -> StatusBadge("PAST", InterfastColors.Gray40)
            else -> Spacer(modifier = Modifier.size(0.dp))
        }
    }
}

@Composable
private fun Checkbox(checked: Boolean, color: Color) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (checked) color else Color.Transparent)
            .border(
                1.5.dp,
                if (checked) color else InterfastColors.Gray40,
                RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            // Inner dot — minimalist mark instead of a checkmark glyph.
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(InterfastColors.VoidBlack, CircleShape)
            )
        }
    }
}

@Composable
private fun StatusBadge(label: String, color: Color) {
    Text(
        text = label,
        style = InterfastTypography.labelSmall,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, color, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

@Composable
private fun ActiveIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(InterfastColors.GlyphRed, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "ACTIVE",
            color = InterfastColors.GlyphRed,
            style = InterfastTypography.labelMedium,
        )
    }
}
