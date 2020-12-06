package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Store the information of teh video related to the session
 * Each frame belongs to a determined session
 */
@Entity(tableName = "Video",
        foreignKeys = [
            ForeignKey(entity = Session::class,
                       parentColumns = ["id"],
                       childColumns = ["sessionId"],
                       onDelete = CASCADE)],
        indices = [ Index(value = ["sessionId"])]
)
data class Video (

    /**
     * The session that owns this video
     */
    @PrimaryKey
    var sessionId: String,

    /**
     * The time stamp of the creating video
     */
    var startTimeStamp: Long,

    /**
     * The height of the received video
     */
    val height: Int,

    /**
     * The width of the received video
     */
    val width: Int,

    /**
     * The format of the received video
     */
    val format: String,

    /**
     * The codec used in the received video
     */
    val codec: Int,

    /**
     * The frame rate of the received video
     */
    val frameRate: Double,

    /**
     * The video bitrate
     */
    val bitRate: Int,

    /**
     * The absolute path of the video without any process
     */
    val rawVideoFilePath: String,

    /**
     * The count of all saved frames in the disk, used
     * in the video composition procedure
     */
    val totalVideoFrames: Long,

    /**
     * The absoulte path of the video with the telemetry
     */
    val generatedVideoPath: String
)