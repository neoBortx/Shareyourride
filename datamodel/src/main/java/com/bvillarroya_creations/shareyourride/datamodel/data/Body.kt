package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.*
import com.bvillarroya_creations.shareyourride.datamodel.interfaces.IDataBaseTelemetry

/**
 * Store the information of the wearable connected to the user
 * Each row with the user telemetry belongs to a session and a determined frame
 */
@Entity(tableName = "body")
data class Body(

    /**
     * Row unique identifier
     * Contains the session id and the frame id
     */
    @PrimaryKey
    @Embedded override val id: TelemetryId,

    /**
     * Hearth rate
     */
    val heartRate: Double
): IDataBaseTelemetry