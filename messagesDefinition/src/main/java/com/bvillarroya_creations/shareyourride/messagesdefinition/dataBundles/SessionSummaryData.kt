package com.bvillarroya_creations.shareyourride.messagesdefinition.dataBundles

/**
 * Summary data related to an ended session
 */
class SessionSummaryData {

    /**
     * In milliseconds
     */
    var duration: Long = 0

    /**
     * In km/h
     */
    var maxSpeed: Float = 0F

    /**
     * In km/h
     */
    var averageSpeed: Float = 0F

    /**
     * In meters
     */
    var distance: Long = 0

    /**
     * The maximum acceleration detected during the session
     */
    var maxAcceleration : Float = 0F

    /**
     * The maximum lean angle in the left side detected during session
     */
    var maxLeftLeanAngle : Int = 0

    /**
     * The maximum lean angle in the right side detected during session
     */
    var maxRightLeanAngle : Int = 0

    /**
     * The maximum altitude detected during the session
     */
    var maxAltitude : Double = 0.0

    /**
     * The minimum altitude detected during the session
     */
    var minAltitude : Double = 0.0

    /**
     * The maximum terrain inclination in Uphill
     */
    var maxUphillTerrainInclination : Int = 0

    /**
     * The maximum terrain inclination in Downhill
     */
    var maxDownhillTerrainInclination : Int = 0

    /**
     * The average terrain inclination
     */
    var averageTerrainInclination : Int = 0

}