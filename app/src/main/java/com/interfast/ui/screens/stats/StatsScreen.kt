package com.interfast.ui.screens.stats

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.interfast.domain.model.FastingStats
import com.interfast.ui.components.StatCard
import com.interfast.ui.theme.InterfastColors
import com.interfast.ui.theme.InterfastTheme
import com.interfast.ui.theme.InterfastTypography
import com.interfast.ui.theme.Spacing
import java.time.Duration

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val weeklyData by viewModel.weeklyData.collectAsStateWithLifecycle()

    StatsScreenContent(
        stats = stats,
        weeklyData = weeklyData
    )
}

@Composable
private fun StatsScreenContent(
    stats: FastingStats,
    weeklyData: List<Float>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InterfastColors.VoidBlack)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "STATS",
            style = InterfastTypography.headlineMedium,
            color = InterfastColors.PureWhite,
            modifier = Modifier.padding(Spacing.lg)
        )

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(InterfastColors.Gray15)
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        // This Week
        WeeklyStatsCard(
            weeklyData = weeklyData,
            completionRate = stats.weeklyCompletionRate,
            averageHours = stats.weeklyAverageHours,
            modifier = Modifier.padding(horizontal = Spacing.lg)
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Records
        Text(
            text = "RECORDS",
            style = InterfastTypography.labelMedium,
            color = InterfastColors.Gray60,
            modifier = Modifier.padding(horizontal = Spacing.lg)
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            StatCard(
                value = stats.currentStreak.toString(),
                label = "Current Streak",
                modifier = Modifier.weight(1f),
                valueColor = InterfastColors.PhosphorGreen
            )
            StatCard(
                value = stats.longestStreak.toString(),
                label = "Longest Streak",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            StatCard(
                value = stats.totalHoursFormatted,
                label = "Total Hours",
                modifier = Modifier.weight(1f),
                valueColor = InterfastColors.SignalCyan
            )
            StatCard(
                value = stats.completedFasts.toString(),
                label = "Completed Fasts",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        // Completion rate
        CompletionRateCard(
            rate = stats.completionRate,
            totalFasts = stats.totalFasts,
            completedFasts = stats.completedFasts,
            modifier = Modifier.padding(horizontal = Spacing.lg)
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))
    }
}

@Composable
private fun WeeklyStatsCard(
    weeklyData: List<Float>,
    completionRate: Float,
    averageHours: Float,
    modifier: Modifier = Modifier
) {
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
                    text = "THIS WEEK",
                    style = InterfastTypography.labelMedium,
                    color = InterfastColors.Gray60
                )
                Text(
                    text = "${(completionRate * 100).toInt()}%",
                    style = InterfastTypography.headlineLarge,
                    color = InterfastColors.PhosphorGreen
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Bar chart
            WeeklyBarChart(
                data = weeklyData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%.1f".format(averageHours),
                        style = InterfastTypography.headlineMedium,
                        color = InterfastColors.PureWhite
                    )
                    Text(
                        text = "AVG HOURS",
                        style = InterfastTypography.labelSmall,
                        color = InterfastColors.Gray40
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(InterfastColors.Gray20)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%.1f".format(averageHours * 7),
                        style = InterfastTypography.headlineMedium,
                        color = InterfastColors.PureWhite
                    )
                    Text(
                        text = "TOTAL",
                        style = InterfastTypography.labelSmall,
                        color = InterfastColors.Gray40
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyBarChart(
    data: List<Float>,
    modifier: Modifier = Modifier
) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    val maxValue = data.maxOrNull()?.coerceAtLeast(1f) ?: 1f

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { index, value ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val barHeight = if (value > 0) (value / maxValue) else 0.05f

                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .weight(barHeight.coerceAtLeast(0.05f))
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(
                            if (value >= maxValue * 0.8f) InterfastColors.GlyphRed
                            else if (value > 0) InterfastColors.GlyphRed.copy(alpha = 0.6f)
                            else InterfastColors.Gray15
                        )
                )

                Spacer(modifier = Modifier.height(Spacing.xs))

                Text(
                    text = days.getOrElse(index) { "" },
                    style = InterfastTypography.labelSmall,
                    color = InterfastColors.Gray40
                )
            }
        }
    }
}

@Composable
private fun CompletionRateCard(
    rate: Float,
    totalFasts: Int,
    completedFasts: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(InterfastColors.Gray10)
            .padding(Spacing.md)
    ) {
        Column {
            Text(
                text = "COMPLETION RATE",
                style = InterfastTypography.labelMedium,
                color = InterfastColors.Gray60
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${(rate * 100).toInt()}%",
                    style = InterfastTypography.displaySmall,
                    color = InterfastColors.PureWhite
                )
                Text(
                    text = "$completedFasts / $totalFasts fasts",
                    style = InterfastTypography.dataMedium,
                    color = InterfastColors.Gray60
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(InterfastColors.Gray15)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(rate)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(InterfastColors.SignalCyan)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun StatsScreenPreview() {
    InterfastTheme {
        StatsScreenContent(
            stats = FastingStats(
                currentStreak = 12,
                longestStreak = 27,
                totalFasts = 247,
                completedFasts = 231,
                totalHoursFasted = Duration.ofHours(1842),
                weeklyAverageHours = 16.2f,
                weeklyCompletionRate = 0.98f
            ),
            weeklyData = listOf(16f, 17f, 14f, 18f, 16f, 18f, 0f)
        )
    }
}
