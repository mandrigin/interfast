package com.interfast.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration
import java.time.Instant

class FastSessionTest {

    @Test
    fun `calculateProgress returns 0 at start of fast`() {
        val startTime = Instant.now()
        val session = createSession(startTime)

        val progress = session.calculateProgress(startTime)

        assertEquals(0f, progress, 0.01f)
    }

    @Test
    fun `calculateProgress returns 0_5 at midpoint of fast`() {
        val startTime = Instant.now().minusSeconds(8 * 3600) // 8 hours ago
        val session = createSession(startTime, fastingHours = 16)

        val progress = session.calculateProgress()

        assertEquals(0.5f, progress, 0.05f)
    }

    @Test
    fun `calculateProgress returns 1 when fast is complete`() {
        val startTime = Instant.now().minusSeconds(18 * 3600) // 18 hours ago
        val session = createSession(startTime, fastingHours = 16)

        val progress = session.calculateProgress()

        assertEquals(1f, progress, 0.01f)
    }

    @Test
    fun `calculateProgress is clamped to 1`() {
        val startTime = Instant.now().minusSeconds(24 * 3600) // 24 hours ago
        val session = createSession(startTime, fastingHours = 16)

        val progress = session.calculateProgress()

        assertEquals(1f, progress, 0.01f)
    }

    @Test
    fun `remainingDuration returns correct value`() {
        val now = Instant.now()
        val startTime = now.minusSeconds(4 * 3600) // 4 hours ago
        val session = createSession(startTime, fastingHours = 16)

        val remaining = session.remainingDuration(now)

        assertEquals(12, remaining.toHours())
    }

    @Test
    fun `remainingDuration returns zero when past target`() {
        val startTime = Instant.now().minusSeconds(20 * 3600) // 20 hours ago
        val session = createSession(startTime, fastingHours = 16)

        val remaining = session.remainingDuration()

        assertEquals(Duration.ZERO, remaining)
    }

    @Test
    fun `elapsedDuration returns correct value`() {
        val hoursAgo = 5L
        val startTime = Instant.now().minusSeconds(hoursAgo * 3600)
        val session = createSession(startTime)

        val elapsed = session.elapsedDuration()

        assertEquals(hoursAgo, elapsed.toHours())
    }

    @Test
    fun `targetDuration is calculated from fasting hours`() {
        val session = createSession(fastingHours = 18)

        assertEquals(Duration.ofHours(18), session.targetDuration)
    }

    @Test
    fun `targetEndTime is startedAt plus targetDuration`() {
        val startTime = Instant.parse("2026-02-07T18:00:00Z")
        val session = createSession(startTime = startTime, fastingHours = 16)

        val expectedEnd = Instant.parse("2026-02-08T10:00:00Z")
        assertEquals(expectedEnd, session.targetEndTime)
    }

    @Test
    fun `isActive returns true for active session`() {
        val session = createSession(status = FastStatus.ACTIVE)

        assertTrue(session.isActive)
    }

    @Test
    fun `isActive returns false for completed session`() {
        val session = createSession(status = FastStatus.COMPLETED)

        assertFalse(session.isActive)
    }

    @Test
    fun `isComplete returns true for completed session`() {
        val session = createSession(status = FastStatus.COMPLETED)

        assertTrue(session.isComplete)
    }

    @Test
    fun `calculateProgress uses stored percentage for completed sessions`() {
        val session = createSession(
            status = FastStatus.COMPLETED,
            completionPercentage = 0.85f
        )

        val progress = session.calculateProgress()

        assertEquals(0.85f, progress, 0.01f)
    }

    private fun createSession(
        startTime: Instant = Instant.now(),
        fastingHours: Int = 16,
        eatingHours: Int = 8,
        status: FastStatus = FastStatus.ACTIVE,
        completionPercentage: Float = 0f
    ): FastSession {
        return FastSession(
            id = 1,
            protocolId = "test",
            protocolName = "$fastingHours:$eatingHours",
            fastingHours = fastingHours,
            eatingHours = eatingHours,
            startedAt = startTime,
            status = status,
            completionPercentage = completionPercentage
        )
    }
}
