package com.interfast.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.interfast.R

/**
 * Owns notification channel definitions for the app.
 * The single channel is high-importance because alarms need to break through silently.
 */
object NotificationChannels {
    const val ALARM_CHANNEL_ID = "interfast_alarms"

    fun create(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as? NotificationManager ?: return

        val channel = NotificationChannel(
            ALARM_CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.notification_channel_description)
            enableVibration(true)
            vibrationPattern = longArrayOf(0L, 250L, 200L, 250L)
        }
        manager.createNotificationChannel(channel)
    }
}
