package com.interfast.ui.scrubber

import com.interfast.data.ScheduleRepository
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pure-logic tests covering the "which hours should we schedule given
 * activatedAt and now" contract used by [ScheduleViewModel.activate].
 *
 * The VM itself drags in [android.app.Application] which we don't want to
 * instantiate in a JVM test, so we mirror its scheduling-decision logic here
 * and lock its behaviour.
 */
class ScheduleViewModelTest {

    private fun futureHoursToSchedule(
        checked: Set<Int>,
        activatedAt: Long,
        now: Long,
    ): Set<Int> = checked.filter { activatedAt + it * 3_600_000L > now }.toSet()

    @Test
    fun `schedules only hours whose target is in the future`() {
        val now = 100_000_000L
        val activatedAt = now - 10 * 3_600_000L  // 10h ago
        val checked = setOf(8, 12, 16, 20)
        val result = futureHoursToSchedule(checked, activatedAt, now)
        assertEquals(setOf(12, 16, 20), result)
    }

    @Test
    fun `nothing scheduled if all targets are past`() {
        val now = 100_000_000L
        val activatedAt = now - 100 * 3_600_000L
        val checked = setOf(12, 16, 22)
        val result = futureHoursToSchedule(checked, activatedAt, now)
        assertEquals(emptySet<Int>(), result)
    }

    @Test
    fun `everything scheduled if activatedAt is now`() {
        val now = 100_000_000L
        val activatedAt = now
        val checked = ScheduleRepository.ALL_HOURS.toSet()
        val result = futureHoursToSchedule(checked, activatedAt, now)
        assertEquals(checked, result)
    }

    /** Mirrors FastNotificationReceiver's auto-disarm decision. */
    private fun isFinalMilestone(hour: Int, checked: Set<Int>): Boolean =
        checked.isNotEmpty() && hour >= checked.max()

    @Test
    fun `last checked hour is final and triggers auto-disarm`() {
        assertEquals(true, isFinalMilestone(16, setOf(12, 16)))
        assertEquals(false, isFinalMilestone(12, setOf(12, 16)))
        assertEquals(true, isFinalMilestone(22, setOf(22)))
        assertEquals(false, isFinalMilestone(16, emptySet()))
    }

    @Test
    fun `activation prunes checked hours that are already past`() {
        val now = 100_000_000L
        val activatedAt = now - 14 * 3_600_000L // 14h ago: 12H milestone already gone
        val checked = setOf(12, 16, 18)
        val pruned = futureHoursToSchedule(checked, activatedAt, now)
        assertEquals(setOf(16, 18), pruned)
    }
}
