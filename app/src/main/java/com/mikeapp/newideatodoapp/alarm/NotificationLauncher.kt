package com.mikeapp.newideatodoapp.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.annotation.DrawableRes
import com.mikeapp.newideatodoapp.DebugActivity
import com.mikeapp.newideatodoapp.R

object NotificationLauncher {
    fun notify(
        context: Context,
        title: String,
        msg: String,
        longMessageToPass: String? = null,
        @DrawableRes icon: Int = R.mipmap.ic_launcher,
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        val intent = Intent(context, DebugActivity::class.java).apply {
            longMessageToPass?.let {
                Log.d("bbbb", "longMessageToPass: $longMessageToPass")
                putExtra("extra_long_string", longMessageToPass)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.enableLights(true)
        channel.lightColor = Color.GREEN
        channel.enableVibration(true)
        channel.setShowBadge(true)
        notificationManager?.createNotificationChannel(channel)
        val notification = Notification.Builder(context)
            .setChannelId(NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(msg)
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .setVibrate(longArrayOf())
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setSmallIcon(icon).build()
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    private const val NOTIFICATION_CHANNEL_ID = "my_channel_01"
    private const val NOTIFICATION_CHANNEL_NAME = "I'm channel name"
    private const val NOTIFICATION_ID = 111123
}
