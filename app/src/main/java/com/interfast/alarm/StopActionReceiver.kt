package com.interfast.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.interfast.InterfastApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receiver bound to the "STOP" notification action. Cancels every pending
 * scheduler entry and clears the active state in persistence.
 *
 * The notification that triggered this stays in the shade per spec — we do
 * not cancel it here.
 */
class StopActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as InterfastApplication
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                app.alarmScheduler.cancelAll()
                app.scheduleRepository.deactivate()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
