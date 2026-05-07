package com.interfast.alarm

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.interfast.InterfastApplication
import com.interfast.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Triggers when a scheduled hour milestone is reached.
 *
 * Marks the hour as reached in the repository and posts a high-importance
 * notification with a STOP action that cancels remaining scheduler entries.
 */
class FastNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val hour = intent.getIntExtra(AlarmScheduler.EXTRA_HOUR, -1)
        Log.d("InterfastNotif", "onReceive hour=$hour action=${intent.action}")
        if (hour <= 0) {
            Log.w("InterfastNotif", "ignored: invalid hour")
            return
        }

        val app = context.applicationContext as InterfastApplication
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                app.scheduleRepository.markReached(hour)
                postNotification(context, hour)
                Log.d("InterfastNotif", "posted notification for hour=$hour id=${notificationIdFor(hour)}")
            } catch (t: Throwable) {
                Log.e("InterfastNotif", "post failed", t)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun postNotification(context: Context, hour: Int) {
        val title = context.getString(R.string.notification_title_fast_complete, hour)
        val text = context.getString(R.string.notification_text_fast_complete)

        val stopIntent = Intent(context, StopActionReceiver::class.java)
        val stopPi = PendingIntent.getBroadcast(
            context,
            STOP_REQUEST_CODE,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val banner = NotificationArt.renderHourBanner(hour)

        // Big-content layout: image fills the body as backdrop, music-player
        // style. System chrome (app name, time, action buttons) wraps it via
        // DecoratedCustomViewStyle. Collapsed view falls back to the system
        // default (small icon + title + text) which is what we want for the
        // single-line preview in the shade.
        val bigView = RemoteViews(context.packageName, R.layout.notification_fast_big)
        bigView.setImageViewBitmap(R.id.banner, banner)
        bigView.setTextViewText(R.id.title, title)
        bigView.setTextViewText(R.id.text, text)

        val notification = NotificationCompat.Builder(context, NotificationChannels.ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomBigContentView(bigView)
            .setCustomHeadsUpContentView(bigView)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .addAction(
                0,
                context.getString(R.string.disarm_action),
                stopPi
            )
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationIdFor(hour), notification)
        } catch (_: SecurityException) {
            // POST_NOTIFICATIONS not granted; silently no-op.
        }
    }

    companion object {
        private const val STOP_REQUEST_CODE = 9001
        fun notificationIdFor(hour: Int): Int = 2000 + hour
    }
}
