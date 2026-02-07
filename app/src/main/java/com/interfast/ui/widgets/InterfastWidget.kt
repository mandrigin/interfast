package com.interfast.ui.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.interfast.MainActivity
import com.interfast.R
import com.interfast.ui.theme.InterfastColors

/**
 * Compact 2x2 Widget - Shows timer and progress ring visualization.
 */
class InterfastCompactWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // In a real implementation, this would read from DataStore/Room
        val isFasting = true
        val elapsed = "16:42"
        val target = "18:00"
        val progress = 0.92f

        provideContent {
            GlanceTheme {
                CompactWidgetContent(
                    isFasting = isFasting,
                    elapsed = elapsed,
                    target = target,
                    progress = progress
                )
            }
        }
    }
}

@Composable
private fun CompactWidgetContent(
    isFasting: Boolean,
    elapsed: String,
    target: String,
    progress: Float
) {
    val backgroundColor = ColorProvider(InterfastColors.Gray10)
    val textColor = ColorProvider(InterfastColors.PureWhite)
    val accentColor = if (isFasting) {
        ColorProvider(InterfastColors.GlyphRed)
    } else {
        ColorProvider(InterfastColors.SignalCyan)
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable(actionStartActivity<MainActivity>())
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status indicator
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = GlanceModifier
                        .size(8.dp)
                        .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {}
                Spacer(modifier = GlanceModifier.width(6.dp))
                Text(
                    text = if (isFasting) "FASTING" else "EATING",
                    style = TextStyle(
                        color = ColorProvider(InterfastColors.Gray60),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Time display
            Text(
                text = elapsed,
                style = TextStyle(
                    color = textColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = target,
                style = TextStyle(
                    color = ColorProvider(InterfastColors.Gray40),
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Progress percentage
            Text(
                text = "${(progress * 100).toInt()}%",
                style = TextStyle(
                    color = accentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

class InterfastCompactWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = InterfastCompactWidget()
}

/**
 * Banner 4x1 Widget - Horizontal layout with timer and quick actions.
 */
class InterfastBannerWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val isFasting = true
        val elapsed = "16:42:08"
        val progress = 0.92f
        val streak = 12

        provideContent {
            GlanceTheme {
                BannerWidgetContent(
                    isFasting = isFasting,
                    elapsed = elapsed,
                    progress = progress,
                    streak = streak
                )
            }
        }
    }
}

@Composable
private fun BannerWidgetContent(
    isFasting: Boolean,
    elapsed: String,
    progress: Float,
    streak: Int
) {
    val backgroundColor = ColorProvider(InterfastColors.Gray10)
    val textColor = ColorProvider(InterfastColors.PureWhite)
    val accentColor = if (isFasting) {
        ColorProvider(InterfastColors.GlyphRed)
    } else {
        ColorProvider(InterfastColors.SignalCyan)
    }

    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable(actionStartActivity<MainActivity>())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status dot
        Box(
            modifier = GlanceModifier
                .size(10.dp)
                .background(accentColor),
            contentAlignment = Alignment.Center
        ) {}

        Spacer(modifier = GlanceModifier.width(12.dp))

        // Status label
        Text(
            text = if (isFasting) "FASTING" else "EATING",
            style = TextStyle(
                color = ColorProvider(InterfastColors.Gray60),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(modifier = GlanceModifier.width(16.dp))

        // Time
        Text(
            text = elapsed,
            style = TextStyle(
                color = textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = GlanceModifier.defaultWeight())

        // Progress
        Text(
            text = "${(progress * 100).toInt()}%",
            style = TextStyle(
                color = accentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = GlanceModifier.width(12.dp))

        // Streak
        Text(
            text = "${streak}d",
            style = TextStyle(
                color = ColorProvider(InterfastColors.PhosphorGreen),
                fontSize = 14.sp
            )
        )
    }
}

class InterfastBannerWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = InterfastBannerWidget()
}

/**
 * Dashboard 4x2 Widget - Full featured widget with all information.
 */
class InterfastDashboardWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val isFasting = true
        val elapsed = "16:42:08"
        val target = "18:00:00"
        val progress = 0.92f
        val streak = 12
        val weeklyPercent = 98

        provideContent {
            GlanceTheme {
                DashboardWidgetContent(
                    isFasting = isFasting,
                    elapsed = elapsed,
                    target = target,
                    progress = progress,
                    streak = streak,
                    weeklyPercent = weeklyPercent
                )
            }
        }
    }
}

@Composable
private fun DashboardWidgetContent(
    isFasting: Boolean,
    elapsed: String,
    target: String,
    progress: Float,
    streak: Int,
    weeklyPercent: Int
) {
    val backgroundColor = ColorProvider(InterfastColors.Gray10)
    val textColor = ColorProvider(InterfastColors.PureWhite)
    val accentColor = if (isFasting) {
        ColorProvider(InterfastColors.GlyphRed)
    } else {
        ColorProvider(InterfastColors.SignalCyan)
    }

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable(actionStartActivity<MainActivity>())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "INTERFAST",
                style = TextStyle(
                    color = ColorProvider(InterfastColors.Gray60),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = GlanceModifier.defaultWeight())

            Box(
                modifier = GlanceModifier
                    .size(8.dp)
                    .background(accentColor),
                contentAlignment = Alignment.Center
            ) {}
            Spacer(modifier = GlanceModifier.width(6.dp))
            Text(
                text = if (isFasting) "FASTING" else "EATING",
                style = TextStyle(
                    color = accentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        // Divider
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ColorProvider(InterfastColors.Gray15)),
            contentAlignment = Alignment.Center
        ) {}

        Spacer(modifier = GlanceModifier.height(12.dp))

        // Timer
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = elapsed,
                style = TextStyle(
                    color = textColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "  /  ",
                style = TextStyle(
                    color = ColorProvider(InterfastColors.Gray40),
                    fontSize = 16.sp
                )
            )
            Text(
                text = target,
                style = TextStyle(
                    color = ColorProvider(InterfastColors.Gray60),
                    fontSize = 18.sp
                )
            )
        }

        Spacer(modifier = GlanceModifier.defaultWeight())

        // Progress bar (simplified - dots aren't easily doable in Glance)
        // Using Row with weighted layout for progress
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(6.dp)
        ) {
            if (progress > 0.01f) {
                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .height(6.dp)
                        .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {}
            }
            if (progress < 0.99f) {
                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .height(6.dp)
                        .background(ColorProvider(InterfastColors.Gray15)),
                    contentAlignment = Alignment.Center
                ) {}
            }
        }

        Spacer(modifier = GlanceModifier.height(12.dp))

        // Divider
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ColorProvider(InterfastColors.Gray15)),
            contentAlignment = Alignment.Center
        ) {}

        Spacer(modifier = GlanceModifier.height(8.dp))

        // Footer stats
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${streak}d streak",
                style = TextStyle(
                    color = ColorProvider(InterfastColors.PhosphorGreen),
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = GlanceModifier.defaultWeight())

            Text(
                text = "${weeklyPercent}% week",
                style = TextStyle(
                    color = ColorProvider(InterfastColors.Gray60),
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = GlanceModifier.defaultWeight())

            Text(
                text = "${(progress * 100).toInt()}%",
                style = TextStyle(
                    color = accentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

class InterfastDashboardWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = InterfastDashboardWidget()
}
