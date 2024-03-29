/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.wifi.interfaces.IWifiScanner


/**
 * Class in charge of the scan the wifi looking available networks
 */
class WifiScanner(private val context: Context): IWifiScanner {

    //region events
    /**
     * Event that notifies that the list of available wifi networks has been retrieved
     */
    override val scanFinished: MutableLiveData<List<ScanResult>> = MutableLiveData()

    /**
     * Event that notifies that something has failed while scanning the available wifi networks
     */
    override val scanFails: MutableLiveData<Boolean> = MutableLiveData()
    //endregion

    //region wifi service
    /**
     * Class to manage wifi service
     */
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    /**
     * Common functions for wifi management
     */
    private val wifiCommons = WifiCommons(context)
    //endregion

    //region result handlers
    /**
     * Object that handles the result of scan available wifi networks
     */
    private val wifiScanReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            try
            {
                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                if (success)
                {
                    processScan()
                }
                else
                {
                    scanFailure()
                }
           }
            catch (ex: Exception)
            {
                Log.e("WifiScanner", "SYR -> Unable to process the wifi scan result, exception: ${ex.message}")
                ex.printStackTrace()
                scanFailure()
            }
        }
    }

    /**
     *
     * @remarks StartScan method is masked as deprecated but there aren't any alternative, so i keep using it
     */
    override fun startScanning()
    {
        try {

            if (!wifiCommons.isWifiEnabled())
            {
                Log.e("WifiScanner", "SYR -> Unable to scan networks but wifi is disabled")
                return
            }

            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            context.registerReceiver(wifiScanReceiver, intentFilter)

            val success = wifiManager.startScan()
            if (!success) {
                // scan failure handling
                scanFailure()
            }
        }
        catch (ex: Exception)
        {
            Log.e("WifiScanner", "SYR -> Unable to get the wifi networks list, exception: ${ex.message}")
            ex.printStackTrace()
            scanFailure()
        }
    }

    /**
     * Send the scanFinished event to a client that is observing changes in the list of networks
     */
    private fun processScan()
    {
        try
        {
            val results = wifiManager.scanResults
            Log.d("WifiScanner", "SYR -> obtained the following wifi networks")
            results.forEach{
                Log.d("WifiScanner", "SYR -> SSID: ${it.SSID} BSSID: ${it.BSSID} friendlyname: ${it.operatorFriendlyName} capabilities: ${it.capabilities}" +
                        " freq: ${it.frequency} width: ${it.channelWidth}")
            }

            scanFinished.value = results
        }
        catch (ex: Exception)
        {
            Log.e("WifiScanner", "SYR -> Unable to get the wifi networks list, exception: ${ex.message}")
            ex.printStackTrace()
            scanFailure()
        }
    }

    /**
     * Process the failure of the network scan
     */
    private fun scanFailure()
    {
        try
        {
            Log.e("WifiScanner", "SYR -> Wifi networks scan fails")
            scanFails.postValue(true)
        }
        catch (ex: Exception)
        {
            Log.e("WifiScanner", "SYR -> Exception while processing the scan failure: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    //endregion
}