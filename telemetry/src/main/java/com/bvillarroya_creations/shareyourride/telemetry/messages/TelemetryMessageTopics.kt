package com.bvillarroya_creations.shareyourride.telemetry.messages

/**
 * This is the list of supported topics in the telemetry package,
 */
internal class TelemetryMessageTopics {
    companion object{
        /**
         * To send temletry data between providers
         */
        const val TELEMETRY_DATA = "telemetryData"
    }
}