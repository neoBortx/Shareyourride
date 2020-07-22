package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.*

/*
    Store the information of the environment when the video is recorded
    Each row with the environment telemetry belongs to a session and a determined
    frame
 */
@Entity(tableName = "Environment")
data class Environment(

    /*
        Row unique identifier
        Contains the session id and the frame id
     */
    @PrimaryKey
    @Embedded val id: TelemetryId = TelemetryId("",0),

    /*
        the timeStamp when the frame is captured
     */
    val timeStamp: Long = 0
)