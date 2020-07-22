package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.*
import com.bvillarroya_creations.shareyourride.datamodel.converters.FloatArrayToStringConverter

/*
    Store the inclination of the device when the video is recorded
    Each row with the inclination to a session and a determined frame
 */
@Entity(tableName = "Inclination")
data class Inclination(

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
        The acceleration of the device
     */
    val acceleration: FloatArray = FloatArray(3),

    /*
        The rotation of the device
     */
    val rotationVector: FloatArray= FloatArray(3),

    /*
        The rotation of the device
     */
    val gravity: FloatArray = FloatArray(3)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Inclination

        if (id != other.id) return false
        if (timeStamp != other.timeStamp) return false
        if (!acceleration.contentEquals(other.acceleration)) return false
        if (!rotationVector.contentEquals(other.rotationVector)) return false
        if (!gravity.contentEquals(other.gravity)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + timeStamp.hashCode()
        result = 31 * result + acceleration.contentHashCode()
        result = 31 * result + rotationVector.contentHashCode()
        result = 31 * result + gravity.contentHashCode()
        return result
    }
}