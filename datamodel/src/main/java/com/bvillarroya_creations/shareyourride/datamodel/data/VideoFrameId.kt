/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index

/**
 *  Key of all video frames
 */
@Entity(
    foreignKeys = [
        ForeignKey(entity = Video::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = CASCADE)],
    indices = [ Index(value = ["sessionId", "frameTimeStamp"])]
)
data class VideoFrameId (

    /**
     * The session that owns this video frame
     */
    var sessionId: String,

    /**
     * The time stamp when the video is received
    */
    var frameTimeStamp: Long
)