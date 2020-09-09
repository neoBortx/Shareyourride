@file:Suppress("DEPRECATION")

package com.bvillarroya_creations.shareyourride.wifi

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi


/**
 *
 */
class WifiConnectionManager(private val context: Context, private val connectionCallback: (Boolean) -> Unit): BroadcastReceiver() {


    //region wifi service
    /**
     * Class to manage wifi service
     */
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    //endregion

    /**
     * Method called when the desired WIFI network is found
     * Depending on the authentication method the process to connect to the WIFI network will change
     *
     * @param connectionData: preconfigured data to connect to the wifi network
     * @param ssid: The full ssid, required because several action cameras add a random
     * bunch of characters to its SSID, so we can't know the full SSID at pre-configuration time
     */
    fun connectToNetwork(connectionData: WifiConnectionData, ssid: String)
    {
        if (!wifiManager.isWifiEnabled)
        {
            Log.e("WifiConnectionManager", "SYR -> Wifi is not enabled, unable to connect to $ssid")
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                Log.d("WifiConnectionManager", "SYR -> Connection to WIFI network $ssid with the new API")
                connectWithNewApi(connectionData,ssid)
            }
            else
            {
                Log.d("WifiConnectionManager", "SYR -> Connection to WIFI network $ssid with the old API")
                connectWithOldApi(connectionData,ssid)
            }
        }
    }

    /**
     * Connects to the given WIFI network using the old fashioned way
     * deprecated in new version
     */
    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    private fun connectWithOldApi(connectionData: WifiConnectionData, ssid: String)
    {
        try {
            val conf = WifiConfiguration()
            conf.SSID = "\"" + ssid + "\""

            when (connectionData.connectionType) {
                ConnectionType.Open -> {
                    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                }
                ConnectionType.WPA -> {
                    conf.preSharedKey = "\"" + connectionData.password + "\""
                }
                ConnectionType.WEP -> {
                    conf.wepKeys[0] = "\"" + connectionData.password + "\""
                    conf.wepTxKeyIndex = 0
                    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                    conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                }
                ConnectionType.WPA2 -> {
                    conf.preSharedKey = "\"" + connectionData.password + "\""
                }
                else ->
                {
                    Log.e("WifiConnectionManager", "SYR -> No supported security ${connectionData.connectionType}")
                }
            }

            wifiManager.addNetwork(conf)

            val list = wifiManager.configuredNetworks
            for (i in list) {
                if (i.SSID != null && i.SSID == "\"" + ssid + "\"") {
                    wifiManager.disconnect()
                    wifiManager.enableNetwork(i.networkId, true)
                    wifiManager.reconnect()
                    break
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("WifiConnectionManager", "SYR -> Unable to connect with old API to to WIFI $ssid, because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Callback function invoked when the state of the WIFI connection changed
     */
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network?) {
            Log.d("WifiConnectionManager","SYR -> onAvailable event when connection to $network.")
            connectionCallback(true)
        }

        override fun onLost(network: Network?) {
            Log.d("WifiConnectionManager","SYR -> onLoots event when connection to $network.")
            connectionCallback(false)
        }

        override fun onUnavailable() {
            Log.d("WifiConnectionManager","SYR -> onUnavailable called")
            super.onUnavailable()
        }


    }

    /**
     * Connects to the given WIFI network using the new API
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectWithNewApi(connectionData: WifiConnectionData, ssid: String)
    {
        try {
            val suggestion = WifiNetworkSuggestion.Builder()
                .setSsid(ssid)
                .setIsAppInteractionRequired(true)


            when (connectionData.connectionType) {
                ConnectionType.Open -> {
                    Log.d("WifiConnectionManager", "SYR -> connecting to an open WIFI")
                    suggestion.setIsEnhancedOpen(true)
                }
                ConnectionType.WPA -> {
                    suggestion.setWpa2Passphrase(connectionData.password)
                    Log.d("WifiConnectionManager", "SYR -> WPA personal ${connectionData.password}")
                }
                ConnectionType.WEP -> {
                    Log.e("WifiConnectionManager", "SYR -> WEP authentication not supported")
                }
                ConnectionType.WPA2 -> {
                    Log.d("WifiConnectionManager", "SYR -> WPA2 personal ${connectionData.password}")
                    suggestion.setWpa2Passphrase(connectionData.password)
                }
                ConnectionType.WPA3 -> {
                    Log.d("WifiConnectionManager", "SYR -> WPA3 personal ${connectionData.password}")
                    suggestion.setWpa3Passphrase(connectionData.password)
                }
            }

            Log.d("WifiConnectionManager","SYR -> Subscribing to connection events")

            val suggestionsList = listOf(suggestion.build())

            val broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (!intent.action.equals(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
                        Log.d("WifiConnectionManager","SYR -> onReceive event when connect to kjasfdhkjsdfhdskj.")
                        connectionCallback(true)
                        return
                    }
                    else
                    {
                        Log.d("WifiConnectionManager","SYR -> AAAAAAAAAAAAAAAA ${intent.action} - ${intent.categories} - ${intent.dataString}")
                    }
                    // do post connect processing here
                }
            }

            Log.d("WifiConnectionManager","SYR -> Subscribing to connection events 2")

            // Optional (Wait for post connection broadcast to one of your suggestions)
            val intentFilter = IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)

            Log.d("WifiConnectionManager","SYR -> Subscribing to connection events 3")

            context.registerReceiver(broadcastReceiver, intentFilter)


            wifiManager.removeNetworkSuggestions(suggestionsList)
            val status = wifiManager.addNetworkSuggestions(suggestionsList)

            Log.d("WifiConnectionManager","SYR -> Subscribing to connection events 4")

            if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                Log.e("WifiConnectionManager", "SYR -> Unable to connect to wifi $ssid, error code: $status")
            }
        }
        catch (ex: Exception)
        {
            Log.e("WifiConnectionManager", "SYR -> Unable to connect with new API to to WIFI $ssid, because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Method that handled events related to the connection procedure
     */
    override fun onReceive(p0: Context?, p1: Intent?) {
        try {


            val conMan = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo: NetworkInfo? = conMan.activeNetworkInfo
            if (netInfo != null && netInfo.type == ConnectivityManager.TYPE_WIFI) {
                Log.d("WifiConnectionManager", "Have Wifi Connection")
            }
            else {
                Log.d("WifiConnectionManager", "Don't have Wifi Connection")
            }
        }
        catch(ex: Exception)
        {
            Log.e("WifiConnectionManager", "SYR -> Unable to process on receive method because: ${ex.message}")
            ex.printStackTrace()
        }
    }

}