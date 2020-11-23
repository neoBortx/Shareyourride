package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Store the information of all video frames of the received video
 */
@Entity(tableName = "VideoFrame")
data class VideoFrame (

    /**
     * Row unique identifier
     * Contains the session id and the frame id
     */
    @PrimaryKey
    @Embedded val id: VideoFrameId = VideoFrameId("",0),

    /**
     * timestamp given with the message SAVE_TELEMETRY
     */
    val syncTimeStamp: Long
)