package com.interfast.domain.model

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Represents the current state of the fasting timer.
 */
sealed class TimerState {
    /**
     * No active fast, ready to start.
     */
    data class Idle(
        val selectedProtocol: FastingProtocol = FastingProtocol.PROTOCOL_16_8
    ) : TimerState()

    /**
     * Actively fasting.
     */
    data class Fasting(
        val session: FastSession,
        val elapsed: Duration,
        val remaining: Duration,
        val progress: Float,
        val currentTime: Instant = Instant.now()
    ) : TimerState() {
        val formattedElapsed: String
            get() = formatDuration(elapsed)

        val formattedRemaining: String
            get() = formatDuration(remaining)

        val formattedTarget: String
            get() = formatDuration(session.targetDuration)

        val percentageText: String
            get() = String.format(Locale.US, "%.1f%%", progress * 100)

        val formattedEndTime: String
            get() {
                val endTime = session.targetEndTime
                    .atZone(ZoneId.systemDefault())
                val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
                return formatter.format(endTime)
            }
    }

    /**
     * Fast is paused (optional feature).
     */
    data class Paused(
        val session: FastSession,
        val elapsedBeforePause: Duration,
        val pausedAt: Instant
    ) : TimerState()

    /**
     * In eating window after completing fast.
     */
    data class EatingWindow(
        val completedSession: FastSession,
        val eatingTimeRemaining: Duration
    ) : TimerState() {
        val formattedRemaining: String
            get() = formatDuration(eatingTimeRemaining)
    }

    companion object {
        fun formatDuration(duration: Duration): String {
            val totalSeconds = duration.toMillis() / 1000
            val hours = totalSeconds / 3600
            val minutes = ((totalSeconds % 3600) / 60).toInt()
            val seconds = (totalSeconds % 60).toInt()
            return "%02d:%02d:%02d".format(hours, minutes, seconds)
        }

        fun formatDurationShort(duration: Duration): String {
            val totalSeconds = duration.toMillis() / 1000
            val hours = totalSeconds / 3600
            val minutes = ((totalSeconds % 3600) / 60).toInt()
            return "%02d:%02d".format(hours, minutes)
        }
    }
}
