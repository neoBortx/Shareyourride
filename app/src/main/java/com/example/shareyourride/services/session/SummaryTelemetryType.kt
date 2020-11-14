package com.example.shareyourride.services.session

/**
 * Definition of all telemetry summary given at the end of the activity
 */
enum class SummaryTelemetryType {

    /**
     * The duration of the activity in milliseconds
     */
    Duration,

    /**
     * The maximum speed of the mobile phone during the session
     */
    MaxSpeed,

    /**
     * The average speed of the mobile phone during the session
     */
    AverageMaxSpeed,

    /**
     * The total distance of the session
     */
    Distance,

    /**
     * The maximum acceleration detected during the session
     */
    MaxAcceleration,

    /**
     * The maximum lean angle in the left side detected during session
     */
    MaxLeftLeanAngle,

    /**
     * The maximum lean angle in the right side detected during session
     */
    MaxRightLeanAngle,

    /**
     * The maximum altitude detected during the session
     */
    MaxAltitude,

    /**
     * The minimum altitude detected during the session
     */
    MinAltitude,

    /**
     * The maximum terrain inclination in Uphill
     */
    MaxUphillTerrainInclination,

    /**
     * The maximum terrain inclination in Downhill
     */
    MaxDownhillTerrainInclination,

    /**
     * The average terrain inclination
     */
    AverageTerrainInclination


}