/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.wifi.interfaces

import android.net.wifi.ScanResult
import androidx.lifecycle.MutableLiveData

/**
 * Public methods and events of the wifi scanner
 */
interface IWifiScanner {

    /**
     * Function that scan looking for the available Wifi networks
     * The scan will trigger the change of
     * - scanFinished
     * - scanFails
     *
     */
    fun startScanning()

    /**
     * The list of scanned networks
     * This values is set when the scan finish successfully
     * The client that has launched the scan action must be observing this live data
     */
    val scanFinished : MutableLiveData<List<ScanResult>>

    /**
     * This values is set when the scan fails
     * The client that has launched the scan action must be observing this live data
     */
    val scanFails: MutableLiveData<Boolean>


}