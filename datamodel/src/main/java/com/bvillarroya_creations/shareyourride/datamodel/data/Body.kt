package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.*

/*
    Store the information of the wearable connected to the user
    Each row with the user telemetry belongs to a session and a determined
    frame
 */
@Entity(tableName = "body")
data class Body(

    /*
        Row unique identifier
        Contains the session id and the frame id
     */
    @PrimaryKey
    @Embedded val id: TelemetryId,

    /*
        the timeStamp when the frame is captured
     */
    val timeStamp: Long
)