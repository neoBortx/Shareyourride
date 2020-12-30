/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.services.common

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.services.session.SessionState


/**
 * Creates a notification to show in the android drawer
 *
 * The notification is the same for all services in order to show just One notification in the drawer for all of them
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
         * Id for all notifications sent by services to display only One message for al services in the android notification drawer
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
         *
         * @param context: To build the notification
         * @param
         * @param sessionState: The state of the session used to chose text and buttons of the notification
         *
         *
         * @return The same notification for all services
         */
        fun getNotification(context: Context?, pendingIntent: PendingIntent, sessionState: SessionState): Notification?
        {
            val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
            val manager: NotificationManager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)!!

            val chan = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

             manager.createNotificationChannel(chan)

            notification = notificationBuilder.setOngoing(true)
                 .setContentTitle(getSessionState(context,sessionState))
                 .setSmallIcon(R.drawable.ic_app_icon)
                 .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                  //No cancel the
                 .setAutoCancel(false)
                 .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                 .setCategory(Notification.CATEGORY_SERVICE)
                 .setContentIntent(pendingIntent) //intent
                 .build()

            return notification
        }

        private fun getSessionState(context: Context?,sessionState: SessionState): String
        {
            if (context == null)
            {
                return ""
            }
            return when(sessionState)
            {
                SessionState.Unknown -> context.getString(R.string.session_unknown)
                SessionState.Stopped -> context.getString(R.string.session_not_initiated)
                SessionState.CalibratingSensors -> context.getString(R.string.session_calibrating)
                SessionState.SensorsCalibrated -> context.getString(R.string.session_calibrating)
                SessionState.Started -> context.getString(R.string.session_ongoing)
                SessionState.CreatingVideo -> context.getString(R.string.session_creating_video)
                SessionState.Finished -> context.getString(R.string.session_video_finished)
                SessionState.SynchronizingVideo -> context.getString(R.string.synchronizing_video)
            }
        }
    }
}