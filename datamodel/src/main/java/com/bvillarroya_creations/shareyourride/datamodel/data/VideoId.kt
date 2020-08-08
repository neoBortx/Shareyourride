package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
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
            onDelete = CASCADE)],
    indices = [ Index(value = ["sessionId", "timeStamp"])]
)
data class VideoId(
    /**
     * The session that owns this video frame
    */
    var sessionId: String,
    /**
     * The time stamp of the video frame
     */
    var timeStamp: Long
)