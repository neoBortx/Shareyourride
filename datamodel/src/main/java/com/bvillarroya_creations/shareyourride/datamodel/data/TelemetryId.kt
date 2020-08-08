package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Ignore
import androidx.room.Index

/*
    Store the information of the wearable connected to the user
    Each row with the user telemetry belongs to a session and a determined
    frame
 */
@Entity(
    foreignKeys = [
        ForeignKey(entity = Session::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = CASCADE),
        ForeignKey(entity = Video::class,
            parentColumns = ["id"],
            childColumns = ["timeStamp"],
            onDelete = CASCADE)],
    indices = [ Index(value = ["sessionId", "timeStamp"])]
)
data class TelemetryId (

    /**
     * The session that owns this video frame
     */
    var sessionId: String,

    /**
     * The time stamp related to this data
    */
    var timeStamp: Long
)