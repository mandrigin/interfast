package com.interfast.alarm

import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.interfast.R

/**
 * Hidden notification triggered by tapping the MANDRIGIN footer mark five times.
 * Uses the same dithered low-res banner aesthetic as milestone alerts.
 */
object EasterEgg {

    private const val EGG_ID = 99999

    fun fire(context: Context) {
        val banner = NotificationArt.renderHelloBanner()
        val bigView = RemoteViews(context.packageName, R.layout.notification_fast_big)
        bigView.setImageViewBitmap(R.id.banner, banner)
        bigView.setTextViewText(R.id.title, "HELLO MANDRIGIN")
        bigView.setTextViewText(R.id.text, "you found the egg.")

        val notification = NotificationCompat.Builder(context, NotificationChannels.ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("HELLO MANDRIGIN")
            .setContentText("you found the egg.")
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomBigContentView(bigView)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(FastNotificationReceiver.openAppIntent(context))
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(EGG_ID, notification)
        } catch (_: SecurityException) {
            // POST_NOTIFICATIONS not granted; silently no-op.
        }
    }
}
