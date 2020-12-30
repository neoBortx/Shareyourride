/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.telemetry.messages

/**
 * This is a list of all kind of messages that can be sent through the message queue system
 * Only for the telemetry module
 *
 * NOTE: These fields only inform about the sent message, they aren't used to filter
 */
internal class TelemetryMessageTypes {
    companion object {
        /**
         * Send the current location data, this data is required by the environment provider
         * to make the query to the weather service
         *
         * Belongs to topic TELEMETRY_DATA
         *
         * @remarks this message contains the object LocationData
         */
        const val LOCATION_DATA = "locationData"
    }
}