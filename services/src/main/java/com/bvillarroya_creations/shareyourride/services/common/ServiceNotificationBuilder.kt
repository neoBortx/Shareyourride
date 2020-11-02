package com.bvillarroya_creations.shareyourride.services.common

import android.app.Notification
import android.app.Notification.Builder
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import com.bvillarroya_creations.shareyourride.services.R

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
        private const val CHANNEL_ID = "Share your ride service Channel"
    }

    /**
     * Return an unique notification id for all services that request it
     * @param context: To build the notification
     *
     *
     * @return The same notification for all services
     */
    fun getNotification(context: Context?, pendingIntent: PendingIntent): Notification?
    {
        if (notification == null) {
            notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Builder(context, CHANNEL_ID).setContentTitle(context?.getString(R.string.app_name)).setContentText("Recording activity").setContentIntent(pendingIntent).setTicker(context?.getString(R.string.app_name)).build()
            }
            else {
                Builder(context).build()
            }
        }
        return notification
    }
}