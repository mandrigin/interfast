package com.interfast

import android.app.Application
import com.interfast.alarm.AlarmScheduler
import com.interfast.alarm.NotificationChannels
import com.interfast.data.ScheduleRepository

/**
 * Manual ServiceLocator. No DI framework — just lazy app-scoped singletons
 * surfaced through public properties on the [Application].
 *
 * ViewModels reach these via:
 *   (application as InterfastApplication).scheduleRepository
 */
class InterfastApplication : Application() {

    lateinit var scheduleRepository: ScheduleRepository
        private set

    lateinit var alarmScheduler: AlarmScheduler
        private set

    override fun onCreate() {
        super.onCreate()
        scheduleRepository = ScheduleRepository(applicationContext)
        alarmScheduler = AlarmScheduler(applicationContext)
        NotificationChannels.create(this)
    }
}
