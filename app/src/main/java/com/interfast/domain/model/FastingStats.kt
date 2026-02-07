package com.interfast.domain.model

import java.time.Duration

/**
 * Aggregated fasting statistics.
 */
data class FastingStats(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalFasts: Int = 0,
    val completedFasts: Int = 0,
    val totalHoursFasted: Duration = Duration.ZERO,
    val weeklyAverageHours: Float = 0f,
    val weeklyCompletionRate: Float = 0f,
    val monthlyAverageHours: Float = 0f
) {
    val completionRate: Float
        get() = if (totalFasts > 0) completedFasts.toFloat() / totalFasts else 0f

    val totalHoursFormatted: String
        get() = "${totalHoursFasted.toHours()}"
}

/**
 * Daily fasting summary for calendar display.
 */
data class DailyFastingSummary(
    val date: java.time.LocalDate,
    val status: DailyStatus,
    val completionPercentage: Float,
    val sessions: List<FastSession>
)

enum class DailyStatus {
    COMPLETE,   // 100% completion
    PARTIAL,    // Started but not completed
    MISSED,     // No fasting that day
    FUTURE      // Day hasn't occurred yet
}

/**
 * Weekly summary data.
 */
data class WeeklySummary(
    val weekStart: java.time.LocalDate,
    val dailySummaries: List<DailyFastingSummary>,
    val averageHours: Float,
    val completionRate: Float,
    val totalHours: Float
)
