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
            onDelete = CASCADE)],
    indices = [ Index(value = ["sessionId", "videoId"])]
)
class VideoId {

    /*
        The session that owns this video frame
    */
    var sessionId: String = ""
    /*
        The time stamp of the video frame
    */
    var videoId: Long = 0

    constructor(sessionId: String, videoId: Long)
    {
        this.sessionId = sessionId
        this.videoId = videoId
    }

    /**
     * Default constructor
     * Put @ignore tag to avoid compilation warnings related to the auto generated code with Room library
     */
    @Ignore
    constructor()

}