package com.bvillarroya_creations.shareyourride.telemetry.constants

class TelemetryConstants {

    /**
     * List of different events that holds telemetry data and are triggered in the telemetry module
     */
    companion object{

        enum class TelemetryEventType
        {
            /**
             * Holds GPS data, speed, altitude and distance
             */
            Location,

            /**
             * Holds temperature, pressure, etc
             */
            Environment,

            /**
             * Holds acceleration and lean angle
             */
            Inclination
        }

    }
}