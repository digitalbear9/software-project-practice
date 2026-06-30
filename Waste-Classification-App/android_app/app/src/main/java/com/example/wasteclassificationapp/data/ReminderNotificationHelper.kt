package com.example.wasteclassificationapp.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object ReminderNotificationHelper {

    private const val CHANNEL_ID = "waste_sorting_reminder_channel"
    private const val CHANNEL_NAME = "垃圾分类每日提醒"
    private const val NOTIFICATION_ID = 1001

    private val tips = listOf(
        "塑料瓶投放前请清空液体，并简单压扁后投入可回收物桶。",
        "果皮属于厨余垃圾，投放时不要连同塑料袋一起丢入厨余垃圾桶。",
        "使用后的纸杯通常按其他垃圾处理，不建议直接按普通纸张回收。",
        "外卖餐盒投放前请倒掉剩余食物，并尽量沥干。",
        "快递纸箱投放前建议去除胶带，压平后投入可回收物回收点。",
        "食品包装袋如果有明显残渣，建议清理后按其他垃圾处理。"
    )

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "用于发送校园垃圾分类每日提醒"
            }

            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    fun showReminderNotification(context: Context) {
        if (!hasNotificationPermission(context)) {
            return
        }

        createNotificationChannel(context)

        val tip = tips.random()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("垃圾分类提醒")
            .setContentText(tip)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(tip)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(
            NOTIFICATION_ID,
            notification
        )
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}