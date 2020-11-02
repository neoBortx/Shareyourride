package com.example.shareyourride.services.common

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bvillarroya_creations.shareyourride.R


/**
 * Creates a notification to show in the android drawer
 *
 * The notification is the same for all services in order to show just one notification in the drawer for all of them
 *
 */
class ServiceNotificationBuilder()
{
    companion object {
        /**
         * Instance of the notification
         */
        private var notification: Notification? = null

        /**
         * Id for all notifications sent by services to display only one message for al services in the android notification drawer
         */
        const val NOTIFICATION_ID = 1985

        /**
         * The identifier of the channel
         */
        private const val CHANNEL_ID = "SYRChannel"

        /**
         * The identifier of the channel
         */
        private const val CHANNEL_NAME = "SYR"

        /**
         * Return an unique notification id for all services that request it
         * @param context: To build the notification
         *
         *
         * @return The same notification for all services
         */
        fun getNotification(context: Context?, pendingIntent: PendingIntent): Notification?
        {
            val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
            val manager: NotificationManager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)!!

            val chan = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

             manager.createNotificationChannel(chan)

             notification = notificationBuilder.setOngoing(true)
                .setContentTitle(context.getString(R.string.app_name))
                .setSmallIcon(R.drawable.menu_icon)
                .setContentText("SYR activity")
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent) //intent
                .build()


            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(1, notificationBuilder.build())


            return notification
        }
    }
}