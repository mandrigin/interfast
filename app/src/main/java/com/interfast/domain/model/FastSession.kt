package com.interfast.domain.model

import java.time.Duration
import java.time.Instant

/**
 * Represents a single fasting session.
 */
data class FastSession(
    val id: Long = 0,
    val protocolId: String,
    val protocolName: String,
    val fastingHours: Int,
    val eatingHours: Int,
    val startedAt: Instant,
    val endedAt: Instant? = null,
    val completedAt: Instant? = null,
    val status: FastStatus = FastStatus.ACTIVE,
    val completionPercentage: Float = 0f
) {
    val targetDuration: Duration
        get() = Duration.ofHours(fastingHours.toLong())

    val targetEndTime: Instant
        get() = startedAt.plus(targetDuration)

    val actualDuration: Duration
        get() {
            val endTime = endedAt ?: completedAt ?: Instant.now()
            return Duration.between(startedAt, endTime)
        }

    val isComplete: Boolean
        get() = status == FastStatus.COMPLETED

    val isActive: Boolean
        get() = status == FastStatus.ACTIVE

    fun calculateProgress(currentTime: Instant = Instant.now()): Float {
        if (!isActive) return completionPercentage

        val elapsed = Duration.between(startedAt, currentTime)
        val target = targetDuration
        return (elapsed.toMillis().toFloat() / target.toMillis().toFloat()).coerceIn(0f, 1f)
    }

    fun remainingDuration(currentTime: Instant = Instant.now()): Duration {
        val elapsed = Duration.between(startedAt, currentTime)
        val remaining = targetDuration.minus(elapsed)
        return if (remaining.isNegative) Duration.ZERO else remaining
    }

    fun elapsedDuration(currentTime: Instant = Instant.now()): Duration {
        return Duration.between(startedAt, currentTime)
    }
}

enum class FastStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED,
    PAUSED
}
