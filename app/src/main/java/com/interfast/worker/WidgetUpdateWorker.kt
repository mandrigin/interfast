package com.interfast.worker

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.interfast.ui.widgets.InterfastBannerWidget
import com.interfast.ui.widgets.InterfastBannerWidgetReceiver
import com.interfast.ui.widgets.InterfastCompactWidget
import com.interfast.ui.widgets.InterfastCompactWidgetReceiver
import com.interfast.ui.widgets.InterfastDashboardWidget
import com.interfast.ui.widgets.InterfastDashboardWidgetReceiver
import com.interfast.ui.widgets.WidgetDataProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Worker that periodically updates all home screen widgets with current fasting data
 */
@HiltWorker
class WidgetUpdateWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val widgetDataProvider: WidgetDataProvider
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Update the cached widget data
            widgetDataProvider.updateWidgetData(context)

            // Trigger widget updates
            InterfastCompactWidget().updateAll(context)
            InterfastBannerWidget().updateAll(context)
            InterfastDashboardWidget().updateAll(context)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "widget_update_work"

        /**
         * Schedule periodic widget updates (every 15 minutes minimum for battery)
         */
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
                15, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        /**
         * Cancel periodic widget updates
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }

        /**
         * Check if any widgets are currently active
         */
        fun hasActiveWidgets(context: Context): Boolean {
            val widgetManager = AppWidgetManager.getInstance(context)
            val receivers = listOf(
                InterfastCompactWidgetReceiver::class.java,
                InterfastBannerWidgetReceiver::class.java,
                InterfastDashboardWidgetReceiver::class.java
            )

            return receivers.any { receiver ->
                val componentName = ComponentName(context, receiver)
                widgetManager.getAppWidgetIds(componentName).isNotEmpty()
            }
        }
    }
}
