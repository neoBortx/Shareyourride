package com.example.shareyourride.camera

import com.bvillarroya_creations.shareyourride.wifi.WifiConnectionData

/**
 * Class that holds the required data to connect to a certain
 * camera over wifi
 */
data class CameraConnectionData(
    /**
     * The unique identifier of the camera kind, for internal use
     */
    val cameraId: String,
    /**
     * The name of the camera, just to show to the user
     */
    val name: String,
    /**
     * The data required to connect to the wifi created by the camera
     */
     val connectionData: WifiConnectionData
)