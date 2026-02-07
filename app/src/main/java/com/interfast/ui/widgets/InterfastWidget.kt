package com.interfast.ui.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
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
import com.interfast.ui.theme.InterfastColors
import kotlinx.coroutines.flow.first

private val Context.widgetDataStore: DataStore<Preferences> by preferencesDataStore(name = "widget_data")

/**
 * Reads widget data from the shared DataStore
 */
private suspend fun getWidgetData(context: Context): WidgetData {
    return try {
        val prefs = context.widgetDataStore.data.first()
        WidgetData(
            isFasting = prefs[WidgetDataProvider.KEY_IS_FASTING] ?: false,
            elapsed = prefs[WidgetDataProvider.KEY_ELAPSED] ?: "00:00:00",
            elapsedShort = prefs[WidgetDataProvider.KEY_ELAPSED_SHORT] ?: "00:00",
            target = prefs[WidgetDataProvider.KEY_TARGET] ?: "00:00:00",
            progress = prefs[WidgetDataProvider.KEY_PROGRESS] ?: 0f,
            streak = prefs[WidgetDataProvider.KEY_STREAK] ?: 0,
            weeklyPercent = prefs[WidgetDataProvider.KEY_WEEKLY_PERCENT] ?: 0,
            lastUpdated = prefs[WidgetDataProvider.KEY_LAST_UPDATED] ?: 0
        )
    } catch (e: Exception) {
        WidgetData()
    }
}

/**
 * Compact 2x2 Widget - Shows timer and progress ring visualization.
 */
class InterfastCompactWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = getWidgetData(context)

        provideContent {
            GlanceTheme {
                CompactWidgetContent(
                    isFasting = data.isFasting,
                    elapsed = data.elapsedShort,
                    target = data.target.take(5), // "HH:MM"
                    progress = data.progress
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

    override fun onEnabled(context: android.content.Context) {
        super.onEnabled(context)
        com.interfast.worker.WidgetUpdateWorker.schedule(context)
    }
}

/**
 * Banner 4x1 Widget - Horizontal layout with timer and quick actions.
 */
class InterfastBannerWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = getWidgetData(context)

        provideContent {
            GlanceTheme {
                BannerWidgetContent(
                    isFasting = data.isFasting,
                    elapsed = data.elapsed,
                    progress = data.progress,
                    streak = data.streak
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

    override fun onEnabled(context: android.content.Context) {
        super.onEnabled(context)
        com.interfast.worker.WidgetUpdateWorker.schedule(context)
    }
}

/**
 * Dashboard 4x2 Widget - Full featured widget with all information.
 */
class InterfastDashboardWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = getWidgetData(context)

        provideContent {
            GlanceTheme {
                DashboardWidgetContent(
                    isFasting = data.isFasting,
                    elapsed = data.elapsed,
                    target = data.target,
                    progress = data.progress,
                    streak = data.streak,
                    weeklyPercent = data.weeklyPercent
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

    override fun onEnabled(context: android.content.Context) {
        super.onEnabled(context)
        com.interfast.worker.WidgetUpdateWorker.schedule(context)
    }
}
