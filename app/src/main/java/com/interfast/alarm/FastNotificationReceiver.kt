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
                // After the final checked milestone there is nothing left to
                // wait for: rewind the tape so tomorrow starts from idle.
                // DONE badges survive (completeFast keeps reachedHours).
                val state = app.scheduleRepository.snapshot()
                val isFinal = state.checkedHours.isNotEmpty() &&
                    hour >= state.checkedHours.max()
                if (isFinal && state.active) {
                    app.alarmScheduler.cancelAll()
                    app.scheduleRepository.completeFast()
                }
                postNotification(context, hour, isFinal)
                Log.d("InterfastNotif", "posted notification for hour=$hour final=$isFinal id=${notificationIdFor(hour)}")
            } catch (t: Throwable) {
                Log.e("InterfastNotif", "post failed", t)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun postNotification(context: Context, hour: Int, isFinal: Boolean) {
        val title = context.getString(R.string.notification_title_fast_complete, hour)
        val text = if (isFinal) {
            context.getString(R.string.notification_text_final)
        } else {
            context.getString(R.string.notification_text_fast_complete)
        }

        val banner = NotificationArt.renderHourBanner(hour)

        // Big-content layout: image fills the body as backdrop, music-player
        // style. System chrome (app name, time, action buttons) wraps it via
        // DecoratedCustomViewStyle. Collapsed and heads-up views use the
        // system default (small icon + title + text) — OEM shades crop custom
        // heads-up views unpredictably, so we never hand them one.
        val bigView = RemoteViews(context.packageName, R.layout.notification_fast_big)
        bigView.setImageViewBitmap(R.id.banner, banner)
        bigView.setTextViewText(R.id.title, title)
        bigView.setTextViewText(R.id.text, text)

        val builder = NotificationCompat.Builder(context, NotificationChannels.ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomBigContentView(bigView)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(openAppIntent(context))
            .setAutoCancel(true)

        // The final milestone has nothing left to stop.
        if (!isFinal) {
            val stopIntent = Intent(context, StopActionReceiver::class.java)
            val stopPi = PendingIntent.getBroadcast(
                context,
                STOP_REQUEST_CODE,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(0, context.getString(R.string.disarm_action), stopPi)
        }

        val notification = builder.build()

        try {
            NotificationManagerCompat.from(context).notify(notificationIdFor(hour), notification)
        } catch (_: SecurityException) {
            // POST_NOTIFICATIONS not granted; silently no-op.
        }
    }

    companion object {
        private const val STOP_REQUEST_CODE = 9001
        private const val OPEN_APP_REQUEST_CODE = 9002

        fun notificationIdFor(hour: Int): Int = 2000 + hour

        fun openAppIntent(context: Context): PendingIntent =
            PendingIntent.getActivity(
                context,
                OPEN_APP_REQUEST_CODE,
                Intent(context, com.interfast.MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
    }
}
