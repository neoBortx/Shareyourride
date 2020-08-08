package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.*
import com.bvillarroya_creations.shareyourride.datamodel.interfaces.IDataBaseTelemetry

/**
 * Store the information of the environment when the video is recorded
 * Each row with the environment telemetry belongs to a session and a determined frame
 */
@Entity(tableName = "Environment")
data class Environment(

    /**
     * Row unique identifier
     * Contains the session id and the frame id
     */
    @PrimaryKey
    @Embedded override val id: TelemetryId = TelemetryId("",0),

    /**
     * Temperature in Celsius
     */
    val temperature: Double,

    /**
     * In degrees
     */
    val windDirection: Double,

    /**
     * Speed of wind in meters per seconds
     */
    val windSpeed: Double,

    /**
     * Ambient humidity in percentage
     */
    val humidity: Int,

    /**
     * Current air pressure in hPa
     */
    val pressure: Double
): IDataBaseTelemetry