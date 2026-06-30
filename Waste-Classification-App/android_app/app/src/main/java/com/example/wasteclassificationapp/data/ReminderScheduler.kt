package com.example.wasteclassificationapp.data

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    private const val DAILY_REMINDER_WORK_NAME = "daily_waste_sorting_reminder"

    fun scheduleDailyReminder(context: Context) {
        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(
            1,
            TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DAILY_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancelDailyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(
            DAILY_REMINDER_WORK_NAME
        )
    }

    fun sendTestReminder(context: Context) {
        ReminderNotificationHelper.showReminderNotification(context)
    }
}