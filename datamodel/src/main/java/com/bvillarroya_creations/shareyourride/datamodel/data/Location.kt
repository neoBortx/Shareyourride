package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.*
import com.bvillarroya_creations.shareyourride.datamodel.interfaces.IDataBaseTelemetry

/*
    Store the information of the Location of the video has taken
    Each Location data belongs to a session and a video frame
 */
@Entity(tableName = "Location")
data class Location(

    /*
        Row unique identifier
        Contains the session id and the frame id
     */
    @PrimaryKey
    @Embedded override val id: TelemetryId = TelemetryId("",0),

    /**
     * Latitude of the device in degrees
     */
    val latitude: Double = 0.0,

    /**
     * Longitude of the device in degrees
     */
    val longitude: Double = 0.0,

    /**
     * Altitude of the device in meters above the WGS 84 reference ellipsoid.
     *
     * If this location does not have an altitude then 0.0 is set.
     */
    val altitude: Double = 0.0,

    /**
     * Speed of the device in meters per seconds
     *
     * If this location does not have a speed then 0.0 is set.
     */
    val speed : Float = 0F,

    /**
     * The bearing of the device in degrees. (es el rumbo)
     *
     * Bearing is the horizontal direction of travel of this device,
     * and is not related to the device orientation. It is guaranteed to
     * be in the range (0.0, 360.0] if the device has a bearing.
     *
     * If this location does not have a bearing then 0.0 is set
     */
    val bearing: Float = 0F
): IDataBaseTelemetry