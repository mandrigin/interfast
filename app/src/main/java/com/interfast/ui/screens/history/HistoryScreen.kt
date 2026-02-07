package com.interfast.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.interfast.domain.model.DailyFastingSummary
import com.interfast.domain.model.DailyStatus
import com.interfast.domain.model.FastSession
import com.interfast.domain.model.FastStatus
import com.interfast.ui.components.DotProgressBar
import com.interfast.ui.theme.InterfastColors
import com.interfast.ui.theme.InterfastTheme
import com.interfast.ui.theme.InterfastTypography
import com.interfast.ui.theme.Spacing
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val currentMonth by viewModel.currentMonth.collectAsStateWithLifecycle()
    val dailySummaries by viewModel.dailySummaries.collectAsStateWithLifecycle()
    val recentSessions by viewModel.recentSessions.collectAsStateWithLifecycle()

    HistoryScreenContent(
        currentMonth = currentMonth,
        dailySummaries = dailySummaries,
        recentSessions = recentSessions,
        onPreviousMonth = viewModel::previousMonth,
        onNextMonth = viewModel::nextMonth
    )
}

@Composable
private fun HistoryScreenContent(
    currentMonth: YearMonth,
    dailySummaries: List<DailyFastingSummary>,
    recentSessions: List<FastSession>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InterfastColors.VoidBlack)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "HISTORY",
                style = InterfastTypography.headlineMedium,
                color = InterfastColors.PureWhite
            )
            MonthSelector(
                currentMonth = currentMonth,
                onPrevious = onPreviousMonth,
                onNext = onNextMonth
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(InterfastColors.Gray15)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Calendar
            item {
                CalendarGrid(
                    month = currentMonth,
                    summaries = dailySummaries,
                    modifier = Modifier.padding(Spacing.lg)
                )
            }

            // Legend
            item {
                CalendarLegend(
                    modifier = Modifier.padding(horizontal = Spacing.lg)
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.md)
                        .height(1.dp)
                        .background(InterfastColors.Gray15)
                )
            }

            // Recent sessions
            items(recentSessions) { session ->
                SessionCard(
                    session = session,
                    modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm)
                )
            }

            item { Spacer(modifier = Modifier.height(Spacing.xl)) }
        }
    }
}

@Composable
private fun MonthSelector(
    currentMonth: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    val canGoNext = currentMonth.isBefore(YearMonth.now())

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Previous month",
                tint = InterfastColors.Gray60
            )
        }

        Text(
            text = currentMonth.format(monthFormatter).uppercase(),
            style = InterfastTypography.labelMedium,
            color = InterfastColors.Gray60,
            modifier = Modifier.width(100.dp),
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = onNext,
            enabled = canGoNext,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next month",
                tint = if (canGoNext) InterfastColors.Gray60 else InterfastColors.Gray20
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    month: YearMonth,
    summaries: List<DailyFastingSummary>,
    modifier: Modifier = Modifier
) {
    val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
    val firstDayOfMonth = month.atDay(1)
    val dayOfWeekOffset = (firstDayOfMonth.dayOfWeek.value - 1) // Monday = 0
    val summaryMap = summaries.associateBy { it.date }

    Column(modifier = modifier) {
        // Day headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = InterfastTypography.labelSmall,
                    color = InterfastColors.Gray40,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Calendar days
        val totalDays = month.lengthOfMonth()
        val totalCells = dayOfWeekOffset + totalDays
        val weeks = (totalCells + 6) / 7

        for (week in 0 until weeks) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (dayOfWeek in 0..6) {
                    val dayIndex = week * 7 + dayOfWeek - dayOfWeekOffset + 1
                    if (dayIndex in 1..totalDays) {
                        val date = month.atDay(dayIndex)
                        val summary = summaryMap[date]
                        CalendarDay(
                            day = dayIndex,
                            status = summary?.status ?: DailyStatus.FUTURE,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int,
    status: DailyStatus,
    modifier: Modifier = Modifier
) {
    val (dotColor, textColor) = when (status) {
        DailyStatus.COMPLETE -> InterfastColors.PhosphorGreen to InterfastColors.PureWhite
        DailyStatus.PARTIAL -> InterfastColors.AmberWarning to InterfastColors.PureWhite
        DailyStatus.MISSED -> InterfastColors.Gray20 to InterfastColors.Gray40
        DailyStatus.FUTURE -> InterfastColors.Gray15 to InterfastColors.Gray40
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = day.toString(),
            style = InterfastTypography.labelSmall,
            color = textColor
        )
    }
}

@Composable
private fun CalendarLegend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(color = InterfastColors.PhosphorGreen, label = "complete")
        Spacer(modifier = Modifier.width(Spacing.lg))
        LegendItem(color = InterfastColors.AmberWarning, label = "partial")
        Spacer(modifier = Modifier.width(Spacing.lg))
        LegendItem(color = InterfastColors.Gray20, label = "missed")
    }
}

@Composable
private fun LegendItem(color: androidx.compose.ui.graphics.Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = InterfastTypography.labelSmall,
            color = InterfastColors.Gray40
        )
    }
}

@Composable
private fun SessionCard(
    session: FastSession,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val startTime = session.startedAt.atZone(java.time.ZoneId.systemDefault())
    val endTime = (session.completedAt ?: session.endedAt)?.atZone(java.time.ZoneId.systemDefault())

    val statusColor = when (session.status) {
        FastStatus.COMPLETED -> InterfastColors.PhosphorGreen
        FastStatus.CANCELLED -> InterfastColors.AmberWarning
        FastStatus.ACTIVE -> InterfastColors.GlyphRed
        FastStatus.PAUSED -> InterfastColors.Gray60
    }

    val statusLabel = when (session.status) {
        FastStatus.COMPLETED -> "COMPLETED"
        FastStatus.CANCELLED -> "ENDED EARLY"
        FastStatus.ACTIVE -> "IN PROGRESS"
        FastStatus.PAUSED -> "PAUSED"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(InterfastColors.Gray10)
            .padding(Spacing.md)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${startTime.format(dateFormatter)} -> ${endTime?.format(dateFormatter) ?: "..."}",
                    style = InterfastTypography.dataMedium,
                    color = InterfastColors.PureWhite
                )
                Text(
                    text = session.protocolName,
                    style = InterfastTypography.labelMedium,
                    color = InterfastColors.Gray60
                )
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            DotProgressBar(
                progress = session.completionPercentage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                dotCount = 20,
                activeColor = statusColor,
                inactiveColor = InterfastColors.Gray15,
                dotSize = 4.dp,
                dotSpacing = 2.dp
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = statusLabel,
                    style = InterfastTypography.labelSmall,
                    color = statusColor
                )
                if (session.status != FastStatus.ACTIVE) {
                    val duration = session.actualDuration
                    Text(
                        text = "${duration.toHours()}h ${duration.toMinutesPart()}m",
                        style = InterfastTypography.dataSmall,
                        color = InterfastColors.Gray60
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HistoryScreenPreview() {
    InterfastTheme {
        HistoryScreenContent(
            currentMonth = YearMonth.now(),
            dailySummaries = listOf(
                DailyFastingSummary(LocalDate.now().minusDays(1), DailyStatus.COMPLETE, 1f, emptyList()),
                DailyFastingSummary(LocalDate.now().minusDays(2), DailyStatus.COMPLETE, 1f, emptyList()),
                DailyFastingSummary(LocalDate.now().minusDays(3), DailyStatus.PARTIAL, 0.7f, emptyList()),
            ),
            recentSessions = emptyList(),
            onPreviousMonth = {},
            onNextMonth = {}
        )
    }
}
