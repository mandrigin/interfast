package com.interfast.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.interfast.ui.theme.InterfastColors
import com.interfast.ui.theme.InterfastTheme
import com.interfast.ui.theme.InterfastTypography
import com.interfast.ui.theme.Spacing

/**
 * Stat display card with value and label.
 *
 * Used in stats screen and footer displays.
 */
@Composable
fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = InterfastColors.PureWhite,
    labelColor: Color = InterfastColors.Gray60,
    backgroundColor: Color = InterfastColors.Gray10
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(Spacing.md),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = InterfastTypography.headlineLarge,
                color = valueColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = label.uppercase(),
                style = InterfastTypography.labelSmall,
                color = labelColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Inline stat display for footer bars.
 */
@Composable
fun InlineStat(
    icon: @Composable () -> Unit,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = InterfastColors.PureWhite
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        icon()
        Spacer(modifier = Modifier.width(Spacing.xs))
        Text(
            text = value,
            style = InterfastTypography.dataMedium,
            color = valueColor
        )
    }
}

/**
 * Stats footer row - displays streak, week %, total hours.
 */
@Composable
fun StatsFooter(
    streakDays: Int,
    weeklyPercentage: Int,
    totalHours: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(InterfastColors.Gray10)
            .padding(vertical = Spacing.md, horizontal = Spacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Streak
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusDot(
                    color = InterfastColors.PhosphorGreen,
                    size = 6.dp
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                Text(
                    text = "${streakDays}d",
                    style = InterfastTypography.dataMedium,
                    color = InterfastColors.PureWhite
                )
            }
            Text(
                text = "STREAK",
                style = InterfastTypography.labelSmall,
                color = InterfastColors.Gray40
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(32.dp)
                .background(InterfastColors.Gray20)
        )

        // Weekly %
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$weeklyPercentage%",
                style = InterfastTypography.dataMedium,
                color = InterfastColors.PureWhite
            )
            Text(
                text = "WEEK",
                style = InterfastTypography.labelSmall,
                color = InterfastColors.Gray40
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(32.dp)
                .background(InterfastColors.Gray20)
        )

        // Total hours
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${totalHours}h",
                style = InterfastTypography.dataMedium,
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

/**
 * Mini stat row for compact displays.
 */
@Composable
fun MiniStatsRow(
    streakDays: Int,
    weeklyPercentage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusDot(color = InterfastColors.PhosphorGreen, size = 6.dp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${streakDays}d",
                style = InterfastTypography.dataSmall,
                color = InterfastColors.Gray80
            )
        }

        Text(
            text = "|",
            style = InterfastTypography.dataSmall,
            color = InterfastColors.Gray40
        )

        Text(
            text = "$weeklyPercentage%",
            style = InterfastTypography.dataSmall,
            color = InterfastColors.Gray80
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun StatCardPreview() {
    InterfastTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                value = "12",
                label = "Current Streak",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = "27",
                label = "Longest Streak",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun StatsFooterPreview() {
    InterfastTheme {
        StatsFooter(
            streakDays = 12,
            weeklyPercentage = 98,
            totalHours = 1842
        )
    }
}
