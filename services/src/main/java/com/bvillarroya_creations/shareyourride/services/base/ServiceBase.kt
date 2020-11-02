package com.bvillarroya_creations.shareyourride.services.base

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.lifecycle.LifecycleService

/**
 * This abstract class implement all common stuff for services that are going to run in the app.
 *
 * To implement the service behaviour i use as example:
 * https://robertohuertas.com/2019/06/29/android_foreground_services/
 *
 */
abstract class ServiceBase():  LifecycleService() {

    private var wakeLock: PowerManager.WakeLock? = null

    private var isServiceStarted = false


    //region abstract vars
    /**
     * The name of the class derived from this base class, used to log
     */
    protected abstract var mClassName: String
    //endregion


    //region service handler
    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        Log.i(mClassName, "SYR -> Some component want to bind with the service")
        // We don't provide binding, so return null
        return null
    }

    /**
     * Handler executed when the service received the order of start its activity
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        super.onStartCommand(intent, flags, startId)
        try
        {

            /*val pendingIntent: PendingIntent =
                Intent(this, MainActivity::class.java).let { notificationIntent ->
                    PendingIntent.getActivity(this, 0, notificationIntent, 0)
                }

            startForeground(com.example.shareyourride.services.ServiceNotificationBuilder.NOTIFICATION_ID, com.example.shareyourride.services.ServiceNotificationBuilder().getNotification(this))*/
            if (intent != null)
            {
                val action = intent.action
                Log.i(mClassName, "SYR -> onStartCommand executed with startId: $startId with action ${intent.action}")
                when (action)
                {
                    "1" -> startService()
                    "2" -> stopService()
                    else -> Log.e(mClassName, "SYR -> This should never happen. No action in the received intent")
                }
            }
            else
            {
                Log.e(mClassName,"SYR -> Unable to process onStartCommand, the received intent is null")
            }
        }
        catch(ex: Exception)
        {
            Log.i(mClassName, "SYR -> unable to handle onStartCommand due: ${ex.message}")
            ex.printStackTrace()
        }
        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }

    /**
     * Called when the service is called
     */
    override fun onCreate() {
        super.onCreate()
        Log.i(mClassName,"SYR -> Service $mClassName has been created")
    }

    /**
     * Called when teh service is destroyed
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.i(mClassName,"SYR -> The service has been destroyed")
    }

    /**
     * Start service activity
     */
    private fun startService()
    {
        try
        {
            if (isServiceStarted)
            {
                Log.i(mClassName,"SYR -> Service already started")
                return
            }
            else
            {
                Log.i(mClassName,"SYR -> Starting foreground service")
            }

            isServiceStarted = true

            // we need this lock so our service gets not affected by Doze Mode
            wakeLock =
                (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                    newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "$mClassName::lock").apply {
                        acquire()
                    }
                }
        }
        catch(ex: Exception)
        {
            Log.i(mClassName, "SYR -> unable to start service due: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Stop the service activity
     */
    private  fun stopService()
    {
        try
        {
            Log.i(mClassName,"SYR -> Stopping service")
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
            isServiceStarted = false
        }
        catch(ex: Exception)
        {
            Log.i(mClassName, "SYR -> unable to stop service due: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion


    //region abstract data and manage telemetry
    /**
     * Implements the logic required to initialize the service functionality
     */
    protected abstract fun startServiceActivity()

    /**
     * Implements the logic to stop the service
     */
    protected abstract fun stopServiceActivity()
    //endregion
}