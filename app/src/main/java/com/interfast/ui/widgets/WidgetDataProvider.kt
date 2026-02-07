package com.interfast.ui.widgets

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.interfast.data.repository.FastingRepository
import com.interfast.domain.model.FastStatus
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

private val Context.widgetDataStore: DataStore<Preferences> by preferencesDataStore(name = "widget_data")

/**
 * Data class representing the current widget state
 */
data class WidgetData(
    val isFasting: Boolean = false,
    val elapsed: String = "00:00:00",
    val elapsedShort: String = "00:00",
    val target: String = "00:00:00",
    val progress: Float = 0f,
    val streak: Int = 0,
    val weeklyPercent: Int = 0,
    val lastUpdated: Long = 0
)

/**
 * Provides widget data by reading from the fasting repository and caching in DataStore
 */
@Singleton
class WidgetDataProvider @Inject constructor(
    private val repository: FastingRepository
) {
    companion object {
        val KEY_IS_FASTING = booleanPreferencesKey("widget_is_fasting")
        val KEY_ELAPSED = stringPreferencesKey("widget_elapsed")
        val KEY_ELAPSED_SHORT = stringPreferencesKey("widget_elapsed_short")
        val KEY_TARGET = stringPreferencesKey("widget_target")
        val KEY_PROGRESS = floatPreferencesKey("widget_progress")
        val KEY_STREAK = intPreferencesKey("widget_streak")
        val KEY_WEEKLY_PERCENT = intPreferencesKey("widget_weekly_percent")
        val KEY_LAST_UPDATED = longPreferencesKey("widget_last_updated")
    }

    /**
     * Updates the widget data cache with current fasting state
     */
    suspend fun updateWidgetData(context: Context): WidgetData {
        val activeSession = repository.getActiveSession()
        val stats = repository.observeStats().first()

        val widgetData = if (activeSession != null && activeSession.status == FastStatus.ACTIVE) {
            val now = Instant.now()
            val elapsed = Duration.between(activeSession.startedAt, now)
            val target = activeSession.targetDuration
            val progress = activeSession.calculateProgress(now)

            WidgetData(
                isFasting = true,
                elapsed = formatDuration(elapsed),
                elapsedShort = formatDurationShort(elapsed),
                target = formatDuration(target),
                progress = progress,
                streak = stats.currentStreak,
                weeklyPercent = (stats.weeklyCompletionRate * 100).toInt(),
                lastUpdated = System.currentTimeMillis()
            )
        } else {
            WidgetData(
                isFasting = false,
                elapsed = "00:00:00",
                elapsedShort = "00:00",
                target = "00:00:00",
                progress = 0f,
                streak = stats.currentStreak,
                weeklyPercent = (stats.weeklyCompletionRate * 100).toInt(),
                lastUpdated = System.currentTimeMillis()
            )
        }

        // Cache the data in DataStore for widget access
        context.widgetDataStore.updateData { prefs ->
            prefs.toMutablePreferences().apply {
                this[KEY_IS_FASTING] = widgetData.isFasting
                this[KEY_ELAPSED] = widgetData.elapsed
                this[KEY_ELAPSED_SHORT] = widgetData.elapsedShort
                this[KEY_TARGET] = widgetData.target
                this[KEY_PROGRESS] = widgetData.progress
                this[KEY_STREAK] = widgetData.streak
                this[KEY_WEEKLY_PERCENT] = widgetData.weeklyPercent
                this[KEY_LAST_UPDATED] = widgetData.lastUpdated
            }
        }

        return widgetData
    }

    /**
     * Reads cached widget data from DataStore (for use in widgets)
     */
    suspend fun getCachedWidgetData(context: Context): WidgetData {
        val prefs = context.widgetDataStore.data.first()
        return WidgetData(
            isFasting = prefs[KEY_IS_FASTING] ?: false,
            elapsed = prefs[KEY_ELAPSED] ?: "00:00:00",
            elapsedShort = prefs[KEY_ELAPSED_SHORT] ?: "00:00",
            target = prefs[KEY_TARGET] ?: "00:00:00",
            progress = prefs[KEY_PROGRESS] ?: 0f,
            streak = prefs[KEY_STREAK] ?: 0,
            weeklyPercent = prefs[KEY_WEEKLY_PERCENT] ?: 0,
            lastUpdated = prefs[KEY_LAST_UPDATED] ?: 0
        )
    }

    private fun formatDuration(duration: Duration): String {
        val hours = duration.toHours()
        val minutes = duration.toMinutesPart()
        val seconds = duration.toSecondsPart()
        return "%02d:%02d:%02d".format(hours, minutes, seconds)
    }

    private fun formatDurationShort(duration: Duration): String {
        val hours = duration.toHours()
        val minutes = duration.toMinutesPart()
        return "%02d:%02d".format(hours, minutes)
    }
}
