package com.bvillarroya_creations.shareyourride.wifi

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.bvillarroya_creations.shareyourride.wifi.data.WifiCallbackData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit


/**
 *
 */
class WifiConnectionManager(): BroadcastReceiver() {

    //region wifi service
    /**
     * Class to manage wifi service
     */
    private var wifiManager: WifiManager? = null

    /**
     * Common functions for wifi management
     */
    private var wifiCommons: WifiCommons? = null
    //endregion

    //region private properties
    /**
     * The data of the connection
     */
    private var wifiConnectionData: WifiConnectionData? = null

    /**
     * The list of networks to suggest to the user
     */
    private var suggestionsList: MutableList<WifiNetworkSuggestion> = mutableListOf()

    /**
     * The aoo context
     */
    private var context: Context? = null

    /**
     * Callback called when the state of the connection has changed
     */
    private var connectionCallback: ((WifiCallbackData) -> Unit)? = null

    /**
     * Throttle to not invoke to many changes about the configuration
     */
    private val subject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    private var alreadyConfigured = false
    //endregion

    //region configure manager
    /**
     * Initialize internal handlers and the wifi connection data
     *
     * @param context: app context
     * @param connectionCallback: Call back
     */
    fun configureManager( context: Context?, connectionCallback: (WifiCallbackData) -> Unit)
    {
        if (!alreadyConfigured)
        {
            this.context = context
            this.connectionCallback = connectionCallback

            wifiCommons = WifiCommons(context!!)
            wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            context.applicationContext.registerReceiver(this, IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION))

            //context.applicationContext.registerReceiver(this, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))*/

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.applicationContext.registerReceiver(this, IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION))
            };

            val observable: Observable<Boolean>? = subject.debounce(5, TimeUnit.SECONDS)?.observeOn(AndroidSchedulers.mainThread())
            val subscribe = observable?.subscribe {
                checkIfConnected()
            }
        }
    }
    /**
     * Configure the wifi connection
     *
     * @param connectionData: preconfigured data to connect to the wifi network
     */
    fun configureConnection(connectionData: WifiConnectionData)
    {
        Log.d("WifiConnectionManager", "SYR -> Configuring WIFI connection ${connectionData.ssidName}")
        wifiConnectionData = connectionData
    }
    //endregion

    //region connect

    /**
     * Method called when the desired WIFI network is found
     * Depending on the authentication method the process to connect to the WIFI network will change
     *
     * @param ssid: The full ssid, required because several action cameras add a random
     * bunch of characters to its SSID, so we can't know the full SSID at pre-configuration time
     */
    fun connectToNetwork(ssid: String)
    {
        if (wifiCommons != null
            && !wifiCommons!!.isWifiEnabled())
        {
            Log.e("WifiConnectionManager", "SYR -> Enabling WIFI to connect with $ssid")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            Log.d("WifiConnectionManager", "SYR -> Connection to WIFI network $ssid with the new API")
            connectWithNewApi(ssid)
        }
        else
        {
            Log.d("WifiConnectionManager", "SYR -> Connection to WIFI network $ssid with the old API")
            connectWithOldApi(ssid)
        }
    }

    /**
     * Connects to the given WIFI network using the old fashioned way
     * deprecated in new version
     */
    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    private fun connectWithOldApi(ssid: String)
    {
        try {

            if (wifiConnectionData == null || wifiManager == null)
            {
                Log.e("WifiConnectionManager", "SYR -> Unable to connect with old API to wifi with $ssid because connection data is null")
                return
            }

            val conf = WifiConfiguration()
            conf.SSID = "\"" + ssid + "\""

            when (wifiConnectionData!!.connectionType) {
                ConnectionType.Open -> {
                    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                }
                ConnectionType.WPA -> {
                    conf.preSharedKey = "\"" + wifiConnectionData!!.password + "\""
                }
                ConnectionType.WEP -> {
                    conf.wepKeys[0] = "\"" + wifiConnectionData!!.password + "\""
                    conf.wepTxKeyIndex = 0
                    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                    conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                }
                ConnectionType.WPA2 -> {
                    conf.preSharedKey = "\"" + wifiConnectionData!!.password + "\""
                }
                else ->
                {
                    Log.e("WifiConnectionManager", "SYR -> No supported security ${wifiConnectionData!!.connectionType}")
                }
            }

            wifiManager!!.addNetwork(conf)

            val list = wifiManager!!.configuredNetworks
            for (i in list) {
                if (i.SSID != null && i.SSID == "\"" + ssid + "\"") {
                    wifiManager!!.disconnect()
                    wifiManager!!.enableNetwork(i.networkId, true)
                    wifiManager!!.reconnect()
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
     * Connects to the given WIFI network using the new API
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectWithNewApi(ssid: String)
    {
        try {

            if (wifiConnectionData == null || wifiManager == null)
            {
                Log.e("WifiConnectionManager", "SYR -> Unable to connect with new API to wifi with $ssid because connection data is null")
                return
            }

            val suggestion = WifiNetworkSuggestion.Builder()
                .setSsid(ssid)
                .setIsAppInteractionRequired(true)


            when (wifiConnectionData!!.connectionType) {
                ConnectionType.Open -> {
                    Log.d("WifiConnectionManager", "SYR -> connecting to an open WIFI")
                    suggestion.setIsEnhancedOpen(true)
                }
                ConnectionType.WPA -> {
                    suggestion.setWpa2Passphrase(wifiConnectionData!!.password)
                    Log.d("WifiConnectionManager", "SYR -> WPA personal ${wifiConnectionData!!.password}")
                }
                ConnectionType.WEP -> {
                    Log.e("WifiConnectionManager", "SYR -> WEP authentication not supported")
                }
                ConnectionType.WPA2 -> {
                    Log.d("WifiConnectionManager", "SYR -> WPA2 personal ${wifiConnectionData!!.password}")
                    suggestion.setWpa2Passphrase(wifiConnectionData!!.password)
                }
                ConnectionType.WPA3 -> {
                    Log.d("WifiConnectionManager", "SYR -> WPA3 personal ${wifiConnectionData!!.password}")
                    suggestion.setWpa3Passphrase(wifiConnectionData!!.password)
                }
            }

            Log.d("WifiConnectionManager","SYR -> Subscribing to connection events")
            suggestionsList.clear()
            suggestionsList.addAll(listOf(suggestion.build()))

            wifiManager!!.removeNetworkSuggestions(suggestionsList)
            val status = wifiManager!!.addNetworkSuggestions(suggestionsList)

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
    //endregion

    //region disconnect
    /**
     * Disconnect form the current network
     */
    fun disconnect()
    {
        try {
            if(wifiManager != null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Log.d("WifiConnectionManager", "SYR -> Disconnect from WIFI network with the new API")
                    wifiManager!!.disconnect()
                    wifiManager!!.removeNetworkSuggestions(suggestionsList)
                }
                else {
                    Log.d("WifiConnectionManager", "SYR -> Disconnect to WIFI network with the old API")
                    wifiManager!!.disconnect()
                }
            }
            else
            {
                Log.e("WifiConnectionManager", "SYR -> Unable to disconnect because wifiManager is null")
            }

        }
        catch(ex: Exception)
        {
            Log.e("WifiConnectionManager", "SYR -> Unable to disconnect of the wifi network because: ${ex.message}")
         ex.printStackTrace()
        }

    }
    //endregion

    //region check connection
    /**
     * Check if the device is connected to a wifi network and if the connected wifi has the SSID desired
     *
     * Update the wifiConnected with the result
     */
    fun checkIfConnected() {
        try
        {

            if (wifiConnectionData != null && connectionCallback != null && wifiManager != null)
            {
                val info: WifiInfo? = wifiManager!!.connectionInfo

                if (info != null)
                {
                    if (/*info.supplicantState == SupplicantState.COMPLETED
                        && */info.ssid.contains(wifiConnectionData!!.ssidName)) {
                        Log.i("WifiConnectionManager", "SYR -> Connected to SSID ${wifiConnectionData!!.ssidName}")
                        connectionCallback?.let { it(WifiCallbackData(WifiCallbackData.Companion.EventType.WifiConnectionStateEvent, true)) }
                        return
                    }
                    else
                    {
                        Log.i("WifiConnectionManager", "SYR -> SSID ${wifiConnectionData!!.ssidName} not connected")
                    }
                }
                else
                {
                    Log.e("WifiConnectionManager", "SYR -> No wifi connection established")
                }

            }
            else
            {
                Log.e("WifiConnectionManager", "SYR -> NO connection data configured, unable to check the connection state")
            }
        }
        catch (ex: Exception) {
            Log.e("WifiConnectionManager", "SYR -> Unable to check the connection, because: ${ex.message}")
            ex.printStackTrace()
        }
        connectionCallback?.let { it(WifiCallbackData(WifiCallbackData.Companion.EventType.WifiConnectionStateEvent, false)) }
    }

    /**
     * Method that handled events related to the connection procedure
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        try
        {

            when {
                intent?.action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) -> {
                    Log.d("WifiConnectionManager","SYR -> onReceive NETWORK_STATE_CHANGED_ACTION event when connect")
                    subject.onNext(false)
                    return
                }
                intent?.action.equals(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION) -> {
                    Log.d("WifiConnectionManager","SYR -> onReceive ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION event when connect")
                    subject.onNext(false)
                    return
                }
                else -> {
                    if (connectionCallback != null && wifiCommons != null)
                    {
                        Log.d("WifiConnectionManager", "SYR -> onReceive TYPE_WIFI event when connect")
                        //connectionCallback?.let { it(WifiCallbackData(WifiCallbackData.Companion.EventType.WifiDeviceStateEvent, wifiCommons!!.isWifiEnabled())) }
                    }
                    else
                    {
                        Log.e("WifiConnectionManager", "SYR -> Unable to process the TYPE_WIFI because the call back function or wifiCommons are null")
                    }
                    return
                }
            }
        }
        catch(ex: Exception)
        {
            Log.e("WifiConnectionManager", "SYR -> Unable to process on receive method because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    //endregion

}