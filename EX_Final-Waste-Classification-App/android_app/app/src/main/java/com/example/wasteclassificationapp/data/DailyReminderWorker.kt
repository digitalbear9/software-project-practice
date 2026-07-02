package com.example.wasteclassificationapp.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DailyReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        ReminderNotificationHelper.showReminderNotification(applicationContext)
        return Result.success()
    }
}