package com.example.taskscheduler.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.taskscheduler.MainActivity // Assuming MainActivity is your main entry point
import com.example.taskscheduler.R

object NotificationUtils {

    const val CHANNEL_ID_TASK_REMINDERS = "task_reminders_channel"
    const val NOTIFICATION_ID_TASK_ENDED = 1 // Use different IDs for different notifications

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name_task_reminders)
            val descriptionText = context.getString(R.string.channel_description_task_reminders)
            val importance = NotificationManager.IMPORTANCE_HIGH // Or other importance
            val channel = NotificationChannel(CHANNEL_ID_TASK_REMINDERS, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendTaskEndedNotification(context: Context, taskName: String) {
        // create an explicit intent for an Activity in your app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_TASK_REMINDERS)
            .setSmallIcon(R.drawable.runner) // TODO Change this
            .setContentTitle(context.getString(R.string.notification_task_ended_title))
            .setContentText(context.getString(R.string.notification_task_ended_message, taskName))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Set the intent that will fire when the user taps the notification
            .setAutoCancel(true) // Automatically removes the notification when the user taps it
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Or other visibility

        with(NotificationManagerCompat.from(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Handle the case where the permission is not granted.
                    // You should request the permission at an appropriate time in your app.
                    // For now, we'll just log or skip.
                    println("Notification permission not granted.")
                    return
                }
            }
            notify(NOTIFICATION_ID_TASK_ENDED + System.currentTimeMillis().toInt(), builder.build()) // Use unique ID to show multiple if needed
        }
    }
}