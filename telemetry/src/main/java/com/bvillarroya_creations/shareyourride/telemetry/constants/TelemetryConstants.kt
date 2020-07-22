package com.bvillarroya_creations.shareyourride.telemetry.constants

class TelemetryConstants {

    /*
        List of request codes by functionality, request codes must be different
     */
    companion object{

        enum class TelemetryEventType
        {
            Location,
            Environment,
            Inclination,
            Body,
            Video
        }

    }
}