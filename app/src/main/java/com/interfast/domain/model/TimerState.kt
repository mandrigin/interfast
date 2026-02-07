package com.interfast.domain.model

import java.time.Duration
import java.time.Instant
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
            val hours = duration.toHours()
            val minutes = duration.toMinutesPart()
            val seconds = duration.toSecondsPart()
            return "%02d:%02d:%02d".format(hours, minutes, seconds)
        }

        fun formatDurationShort(duration: Duration): String {
            val hours = duration.toHours()
            val minutes = duration.toMinutesPart()
            return "%02d:%02d".format(hours, minutes)
        }
    }
}
