package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index

/**
 *  Key of all telemetry tables
 */
@Entity(
    foreignKeys = [
        ForeignKey(entity = Session::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
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