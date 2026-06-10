package com.interfast.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.interfast.InterfastApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Re-registers pending scheduler entries after device reboot or app update.
 *
 * AlarmManager drops an app's alarms both on reboot and on package update, so
 * MY_PACKAGE_REPLACED matters as much as BOOT_COMPLETED — without it every
 * update would silently kill a running fast.
 *
 * Strategy: read the persisted [com.interfast.data.ScheduleState]; if active,
 * re-schedule every checked hour whose target (activatedAt + Nh) is still in
 * the future and which has not already been reached.
 *
 * Deliberately NOT directBootAware — DataStore lives in credential-protected
 * storage and is unreadable before first unlock.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED &&
            action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) return

        val app = context.applicationContext as InterfastApplication
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val state = app.scheduleRepository.snapshot()
                if (!state.active) return@launch
                val activatedAt = state.activatedAtMillis ?: return@launch
                val now = System.currentTimeMillis()
                state.checkedHours.forEach { hour ->
                    if (state.reachedHours.contains(hour)) return@forEach
                    val target = activatedAt + hour * 3_600_000L
                    if (target > now) {
                        app.alarmScheduler.scheduleHour(hour, target)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
