package com.example.shareyourride.services.session

/**
 * Definition of the supported telemetry
 */
enum class TelemetryType {

    /**
     * The speed of the mobile phone
     */
    Speed,

    /**
     * The distance of the activity
     */
    Distance,

    /**
     * Teh acceleration of the mobile phone
     */
    Acceleration,

    /**
     * Tilt in the Y axis of the mobile phone
     */
    LeanAngle,

    /**
     * The altitude
     */
    Altitude,

    /**
     * The current terrain inclination
     */
    TerrainInclination


}