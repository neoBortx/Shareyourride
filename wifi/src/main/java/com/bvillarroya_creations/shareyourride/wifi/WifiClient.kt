/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.wifi

import android.app.Activity
import android.content.Context
import android.net.wifi.ScanResult
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bvillarroya_creations.shareyourride.wifi.data.WifiCallbackData

/**
 * Interface that must be implemented for classes that want to use the WIFI library
 * To connect a given WIFI network
 *
 * @param context: Of the application
 * @param cycleOwner: To observe operations
 */
abstract class WifiClient(private val context: Context) {

    //region wifi managers
    /**
     * Class in charge of scanning wifi networks
     */
    private val wifiScanner = WifiScanner(context)

    /**
     * Class in charge of connects to a WFI network
     */
    private val wifiConnectionManager = WifiConnectionManager()

    /**
     * Common functions for wifi management
     */
    private val wifiCommons = WifiCommons(context)
    //endregion

    //region protected
    /**
     * The configuration of the target wifi network
     */
    private var wifiConnectionData: WifiConnectionData? = null
    //endregion

    //region public data
    /**
     * Used to notify upper layers that the state of the connection
     */
    var wifiConnected : MutableLiveData<Boolean> = MutableLiveData()

    /**
     * Used to notify upper layers that the state of the connection
     */
    var wifiEnabled : MutableLiveData<Boolean> = MutableLiveData()
    //endregion

    //region init
    init {
        wifiConnectionManager.configureManager(context, ::processWifiCallbackEvent)
        wifiConnected.value = false
        wifiEnabled.value = wifiCommons.isWifiEnabled()
    }
    //endregion

    //region observers
    /**
     * Handles the result of a scan operation, If some of the given networks matches
     * with the given SSID in the configuration function, the connection procedure is
     * started.
     *
     * look for a wifi network that contains part of the given SSID
     */
    private val scanFinishedWhileConnectingObserver = Observer<List<ScanResult>> { items ->
        if (wifiConnectionData != null)
        {
            val index = items.indexOfFirst { it.SSID.contains(wifiConnectionData!!.ssidName) }
            if (index >= 0)
            {
                Log.i("WifiClient", "SYR -> Trying to connect to network with SSID: ${items[index].SSID}")

                wifiConnectionManager.connectToNetwork(items[index].SSID)
                stopScanning()
            }
            else {
                Log.e("WifiClient", "SYR -> SSID ${wifiConnectionData!!.ssidName} not found, unable to connect to wfi")
                wifiConnected.value = false
            }
        }
        else
        {
            Log.e("WifiClient", "SYR -> connectionData is null, unable to complete the connection procedure")
        }
    }

    /**
     * Handles failures in the connection operation
     */
    private val scanFailsWhileConnectingObserver = Observer<Boolean> {
        Log.e("WifiClient", "SYR -> Something has failed during the WIFI connection procedure")
        stopScanning()
        wifiConnected.value = false
    }
    //endregion

    //region public functions
    /**
     * Configure the wifi client with the required handlers and the connection
     * to the wifi network
     * Create handlers to manage WIFI events
     *
     * @param connectionData: THe wifi connection data to use in scan and connect operation
     */
    fun configureConnection(connectionData: WifiConnectionData)
    {
        try
        {
            wifiConnectionData = connectionData
            //initialize
            wifiConnectionManager.configureConnection(connectionData)
            //update the connection state
            wifiConnectionManager.checkIfConnected()
        }
        catch (ex: Exception)
        {
            Log.e("WifiClient", "SYR -> Unable to configure WifiClient: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Check if the configured client is already connected
     */
    fun checkIfConnected(): Boolean
    {
        try
        {
            return wifiConnectionManager.checkIfConnected()
        }
        catch (ex: Exception)
        {
            Log.e("WifiClient", "SYR -> Unable to configure WifiClient: ${ex.message}")
            ex.printStackTrace()
        }

        return false
    }

    /**
     * Looks for a WIFI network that has or contains the given SSID in configure function
     * Then, using the connection parameters, will try to connect to the
     * Wifi network.
     *
     * When the connection is established the call back method processWifiConnectionEstablished is
     * invoked.
     * If the attempt fails, the same call back method is triggered with a null value
     */
    fun connectToNetwork()
    {
        try
        {
            if (!wifiCommons.isWifiEnabled())
            {
                Log.e("WifiScanner", "SYR -> Unable to scan networks but wifi is disabled")
                return
            }

            if (wifiConnectionData != null)
            {
                Log.d(
                        "CameraWifiClient", "SYR -> Searching for wifi connection with SSID: ${wifiConnectionData?.ssidName} in order to make a connection operation: ")

                wifiScanner.scanFinished.observeForever(scanFinishedWhileConnectingObserver)
                wifiScanner.scanFails.observeForever(scanFailsWhileConnectingObserver)

                wifiScanner.startScanning()
            }
            else
            {
                Log.e("IWifiClient", "SYR -> Unable to connect to the WIFI network because client is not configured")
            }

        }
        catch (ex: Exception)
        {
            Log.e("WifiClient", "SYR -> Exception while scanning for networks with SSID: ${wifiConnectionData?.ssidName}, exception: ${ex.message}")
            ex.printStackTrace()
        }
    }


    /**
     *
     */
    fun disconnectFromNetwork()
    {
        try {
            wifiConnectionManager.disconnect()
        }
        catch (ex: Exception)
        {
            Log.e("WifiClient", "SYR -> Exception while disconnection for networks with SSID: ${wifiConnectionData?.ssidName}, exception: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Enabled the wifi device or open a configuration dialog
     * depending on the Android version
     *
     * @param activity: Required to open the configuration dialog
     */
    fun enableWifi(activity: Activity)
    {
        try {
            return wifiCommons.enableWifi(activity)
        }
        catch (ex: Exception)
        {
            Log.e("WifiClient", "SYR -> Unable to check the state of the wifi connection, exception: ${ex.message}")
            ex.printStackTrace()
        }

    }
    //endregion

    //region abstract functions

    /**
     * Method invoked when a event related to the WIFI functionality happens
     *
     * @param wifiCallbackData: data related to the wifi call back
     */
    private fun processWifiCallbackEvent(wifiCallbackData: WifiCallbackData)
    {
        try
        {
            Log.d("WifiClient", "SYR -> Processing wifi call back ${wifiCallbackData.event}")
            when (wifiCallbackData.event)
            {
                WifiCallbackData.Companion.EventType.WifiDeviceStateEvent ->
                {
                    wifiEnabled.postValue(wifiCallbackData.state)
                }
                WifiCallbackData.Companion.EventType.WifiConnectionStateEvent ->
                {
                    wifiConnected.postValue(wifiCallbackData.state)
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("WifiClient", "SYR -> Exception while updating wifi connection state, excetion: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    //region private functions
    /**
     * Unsubscribe to scanFinished and scan fail events
     */
    private fun stopScanning()
    {
        try {

            Log.d("WifiClient", "SYR -> Stop scanning WIFI networks")

            wifiScanner.scanFinished.removeObserver(scanFinishedWhileConnectingObserver)
            wifiScanner.scanFails.removeObserver(scanFailsWhileConnectingObserver)
        }
        catch (ex: Exception)
        {
            Log.e("WifiClient", "SYR -> Exception while stopping scanning for wifi networks: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

}