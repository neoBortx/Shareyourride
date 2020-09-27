package com.example.shareyourride.wifi

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.bvillarroya_creations.shareyourride.wifi.WifiClient
import com.example.shareyourride.camera.CameraConnectionData
import com.example.shareyourride.userplayground.home.HomeFragment

/**
 * Client that commands the actions to connect to WIFI networks and handles
 * commands about the state of the connection
 *
 * @param context: To perform some operations
 * @param cycleOwner: To handle observable events
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
        configure(cameraData.connectionData)
    }
}