package com.interfast.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.interfast.MainActivity
import com.interfast.R
import com.interfast.data.repository.FastingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Smart notification worker with contextual, motivational messages.
 *
 * Design philosophy: Notifications should feel like a supportive friend,
 * not a nagging alarm. Each message is contextual based on:
 * - Time of day
 * - Progress in fast
 * - Streak status
 * - Historical patterns
 *
 * We rotate through messages to prevent notification fatigue.
 */
@HiltWorker
class SmartNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: FastingRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val activeSession = repository.getActiveSession() ?: return Result.success()

        val progress = activeSession.calculateProgress()
        val milestone = getMilestone(progress)

        if (milestone != null) {
            val message = getContextualMessage(milestone, progress)
            showNotification(
                title = message.title,
                message = message.body,
                notificationId = NOTIFICATION_ID_BASE + milestone
            )
        }

        return Result.success()
    }

    private fun getMilestone(progress: Float): Int? {
        val percentage = (progress * 100).toInt()
        return when {
            percentage in 24..26 -> 25
            percentage in 49..51 -> 50
            percentage in 74..76 -> 75
            percentage >= 99 -> 100
            else -> null
        }
    }

    private fun getContextualMessage(milestone: Int, progress: Float): NotificationMessage {
        val timeOfDay = LocalTime.now()
        val isNight = timeOfDay.hour in 22..23 || timeOfDay.hour in 0..6
        val isMorning = timeOfDay.hour in 7..11

        return when (milestone) {
            25 -> get25PercentMessage(isNight, isMorning)
            50 -> get50PercentMessage(isNight, isMorning)
            75 -> get75PercentMessage(isNight, isMorning)
            100 -> get100PercentMessage()
            else -> NotificationMessage(
                title = "Fasting Update",
                body = "You're making progress!"
            )
        }
    }

    private fun get25PercentMessage(isNight: Boolean, isMorning: Boolean): NotificationMessage {
        val messages = listOf(
            NotificationMessage(
                "Quarter Way There!",
                "Your body is starting to switch from glucose to stored fat. Keep going!"
            ),
            NotificationMessage(
                "25% Complete",
                "Insulin levels are dropping. Your body is beginning its metabolic shift."
            ),
            NotificationMessage(
                "First Quarter Done",
                "You're building momentum. The first few hours are often the toughest."
            )
        )

        return if (isNight) {
            NotificationMessage(
                "Sleep Through It",
                "25% done! Rest well - your body works its magic while you sleep."
            )
        } else {
            messages.random()
        }
    }

    private fun get50PercentMessage(isNight: Boolean, isMorning: Boolean): NotificationMessage {
        val messages = listOf(
            NotificationMessage(
                "Halfway There!",
                "Your body is now in full fat-burning mode. You're doing great!"
            ),
            NotificationMessage(
                "50% Complete",
                "Autophagy may be kicking in - your cells are cleaning house."
            ),
            NotificationMessage(
                "The Midpoint",
                "Half done! Many report mental clarity peaks around now. Notice anything?"
            )
        )

        return when {
            isMorning -> NotificationMessage(
                "Morning Milestone",
                "50% complete! A glass of water and you've got this."
            )
            isNight -> messages.random()
            else -> messages.random()
        }
    }

    private fun get75PercentMessage(isNight: Boolean, isMorning: Boolean): NotificationMessage {
        val messages = listOf(
            NotificationMessage(
                "The Home Stretch!",
                "75% done. Your body has been burning fat for hours. Almost there!"
            ),
            NotificationMessage(
                "Three Quarters Complete",
                "Just a bit more! Your discipline today builds strength for tomorrow."
            ),
            NotificationMessage(
                "75% - You've Got This",
                "The finish line is in sight. Stay hydrated and keep going!"
            )
        )

        return if (isMorning) {
            NotificationMessage(
                "Morning Push",
                "75% done! Your eating window opens soon. You've earned it."
            )
        } else {
            messages.random()
        }
    }

    private fun get100PercentMessage(): NotificationMessage {
        val messages = listOf(
            NotificationMessage(
                "Fast Complete!",
                "Congratulations! Your eating window is now open. You did it!"
            ),
            NotificationMessage(
                "Goal Achieved!",
                "100%! Your body thanks you. Time to refuel mindfully."
            ),
            NotificationMessage(
                "Success!",
                "You completed your fast. Every finish builds the habit stronger."
            )
        )
        return messages.random()
    }

    private fun showNotification(title: String, message: String, notificationId: Int) {
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Fasting Milestones",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Motivational notifications for fasting progress"
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "interfast_smart_notifications"
        private const val NOTIFICATION_ID_BASE = 2000
        private const val WORK_NAME = "smart_notification_work"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<SmartNotificationWorker>(
                15, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}

data class NotificationMessage(
    val title: String,
    val body: String
)

/**
 * Streak reminder worker - fires once daily to encourage consistency.
 */
@HiltWorker
class StreakReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: FastingRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val stats = repository.observeStats()
        // Check if user has an active streak and no active fast
        val activeSession = repository.getActiveSession()

        if (activeSession == null) {
            // No active fast - send streak reminder
            showStreakReminder()
        }

        return Result.success()
    }

    private fun showStreakReminder() {
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val messages = listOf(
            NotificationMessage(
                "Time to Fast?",
                "Start your fast now to keep your streak going!"
            ),
            NotificationMessage(
                "Don't Break the Chain",
                "Your body has built momentum. Ready for today's fast?"
            ),
            NotificationMessage(
                "Quick Start",
                "Tap to begin your fast. Each day builds the habit stronger."
            )
        )

        val message = messages.random()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(STREAK_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Streak Reminders",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Daily reminders to maintain your fasting streak"
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "interfast_streak_reminders"
        private const val STREAK_NOTIFICATION_ID = 3001
    }
}
