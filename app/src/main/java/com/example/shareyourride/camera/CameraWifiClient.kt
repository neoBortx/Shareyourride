/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.camera

import android.content.Context
import com.bvillarroya_creations.shareyourride.wifi.WifiClient

/**
 * Client that commands the actions to connect to WIFI networks and handles
 * commands about the state of the connection
 *
 * Used to not to couple wifi implementation with the video camera
 *
 * @param context: To perform some operations
 */
class CameraWifiClient(private val context: Context): WifiClient(context) {


    /**
     * The name of the camera that creates the wifi network
     */
    private var cameraName = ""

    /**
     * Configure the wifi client with the required handlers and the connection
     * to the wifi network
     *
     * @param cameraData: The required data to access to the wifi network
     */
    fun configureClient(cameraData: CameraConnectionData )
    {
        cameraName = cameraData.name
        configureConnection(cameraData.connectionData)
    }
}