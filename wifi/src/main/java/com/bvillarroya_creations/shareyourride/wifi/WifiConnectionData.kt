/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.wifi


/**
 * The different types of Authentication methods
 */
enum class ConnectionType
{
    /**
     * No authentication
     */
    Open,

    /**
     * WPA
     */
    WPA,

    /**
     * WEP
     */
    WEP,

    /**
     * WPA2
     */
    WPA2,

    /**
     * WPA3
     */
    WPA3,
}

/**
 * The data required to connect to a wifi network
 */
data class WifiConnectionData(

    /**
     * The full or partial SSID name of the WIFI connection
     */
    val ssidName: String,

    /**
     * The type of credentials used to connect to the wifi
     */
    val connectionType: ConnectionType,

    /**
     * Credentials
     */
    val password: String

)