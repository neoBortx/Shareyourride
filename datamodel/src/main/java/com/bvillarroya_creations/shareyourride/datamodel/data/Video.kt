package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bvillarroya_creations.shareyourride.datamodel.interfaces.IDataBaseTelemetry

/*
    Store the information of each frame of the video
    Each frame belongs to a determined session
 */
@Entity(tableName = "Video")
data class Video (

    /*
        Row unique identifier
        Contains the session id and the frame id
     */
    @PrimaryKey
    @Embedded val id: VideoId = VideoId("",0)

)