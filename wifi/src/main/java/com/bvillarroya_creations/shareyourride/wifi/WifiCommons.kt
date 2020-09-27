package com.bvillarroya_creations.shareyourride.wifi

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.util.Log

/**
 * Class with common functions related to the wifi management
 */
class WifiCommons(val context: Context) {

    //region wifi service
    /**
     * Class to manage wifi service
     */
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    //endregion

    /**
     * Android Q not allow to enable or disable wifi connections
     *
     * Enable ir or open the settings window depending on the
     * Android version
     */
    fun enableWifi(activity: Activity)
    {
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                val panelIntent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
                activity.startActivity(panelIntent)
            }
            else
            {
                wifiManager.setWifiEnabled(true)
            }
        }
        catch (ex: Exception)
        {
            Log.e("WifiScanner", "SYR -> Unable to get check if the wifi is available, exception: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Checks if the wifi is enabled
     */
    fun isWifiEnabled(): Boolean
    {
        try {
            return wifiManager.isWifiEnabled;
        }
        catch (ex: Exception)
        {
            Log.e("WifiScanner", "SYR -> Unable to get check if the wifi is available, exception: ${ex.message}")
            ex.printStackTrace()
        }

        return false
    }

}