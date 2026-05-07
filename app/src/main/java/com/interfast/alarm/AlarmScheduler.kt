package com.interfast.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.interfast.data.ScheduleRepository

/**
 * Wraps [AlarmManager] for scheduling fast-completion alarms keyed by hour.
 *
 * Each hour (12, 16, 18, 20, 22) gets a unique [PendingIntent] (and therefore
 * a unique alarm slot) via [requestCodeFor]. Scheduling the same hour twice
 * replaces the previous alarm via FLAG_UPDATE_CURRENT.
 */
class AlarmScheduler(private val context: Context) {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleHour(hour: Int, triggerAtMillis: Long) {
        val pi = pendingIntentFor(hour, mutable = false)
        if (canScheduleExact()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pi
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pi
            )
        }
    }

    fun cancelHour(hour: Int) {
        val pi = pendingIntentFor(hour, mutable = false)
        alarmManager.cancel(pi)
    }

    fun cancelAll(hours: Set<Int> = ScheduleRepository.ALL_HOURS.toSet()) {
        hours.forEach { cancelHour(it) }
    }

    fun canScheduleExact(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    private fun pendingIntentFor(hour: Int, mutable: Boolean): PendingIntent {
        val intent = Intent(context, FastNotificationReceiver::class.java).apply {
            putExtra(EXTRA_HOUR, hour)
        }
        var flags = PendingIntent.FLAG_UPDATE_CURRENT
        flags = if (mutable) flags or PendingIntent.FLAG_MUTABLE
        else flags or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(
            context,
            requestCodeFor(hour),
            intent,
            flags
        )
    }

    companion object {
        const val EXTRA_HOUR = "hour"

        // Offset request codes so they don't collide with the stop action.
        fun requestCodeFor(hour: Int): Int = 1000 + hour
    }
}
