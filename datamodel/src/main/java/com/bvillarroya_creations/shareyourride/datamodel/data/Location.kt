package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.*

/*
    Store the information of the Location of the video has taken
    Each Location data belongs to a session and a video frame
 */
@Entity(tableName = "Location")
data class Location(

    /*
        Row unique identifier
        Contains the session id and the frame id
     */
    @PrimaryKey
    @Embedded val id: TelemetryId = TelemetryId("",0),

    /*
        the timeStamp when the frame is captured
     */
    val timeStamp: Long = 0,

    /*
        Latitude
     */
    val latitude: Double = 0.0,

    /*
        Longitude
     */
    val longitude: Double = 0.0,

    /*
        Altitude
     */
    val altitude: Double = 0.0,

    /*
        Speed
     */
    val speed : Float = 0F
)