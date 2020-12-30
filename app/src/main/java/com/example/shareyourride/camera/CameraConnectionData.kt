/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

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
    val connectionData: WifiConnectionData,

    /**
     * The network protocol required to access to the video stream of the camera
     */
    val networkProtocol: String,

    /**
     * The IP address of the default gateway
     */
    val videoServerIp: String,

    /**
     * The port used by the server POrt
     */
    val videoServerPort: String,

    /**
     * The path that is used by the network camera to serve the video
     */
    val videoName: String
)