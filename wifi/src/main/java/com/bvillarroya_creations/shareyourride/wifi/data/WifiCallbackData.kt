/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.wifi.data

/**
 * Class that holds the data related to a wifi event
 *
 * @param event: The type of event
 * @param state: The state of the event
 */
class WifiCallbackData(val event: EventType, val state: Boolean) {

    companion object
    {
        /**
         * Different types of events
         */
        enum class EventType
        {
            /**
             * Triggered when the WIFI connection is established or closed
             *
             * true: connection established
             * false: connection closed
             */
            WifiConnectionStateEvent,

            /**
             * Triggered when the WIFI is turn on or off
             *
             * true: WIFI turn on
             * false: WIFI turn off
             */
            WifiDeviceStateEvent
        }
    }


}