package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bvillarroya_creations.shareyourride.datamodel.interfaces.IDataBaseTelemetry

/*
    Store the inclination of the device when the video is recorded
    Each row with the inclination to a session and a determined frame
 */
@Entity(tableName = "Inclination")
data class Inclination(

    /**
     * Row unique identifier
     * Contains the session id and the frame id
     */
    @PrimaryKey
    @Embedded override val id: TelemetryId = TelemetryId("",0),


    /**
     * gravity applied to the device in m/s2
     * [0]: x-axis
     * [1]: y-axis
     * [2]: z-axis
     */
    val acceleration: FloatArray = FloatArray(3),

    /**
     * linear acceleration of the device in m/s2
     * [0]: x-axis
     * [1]: y-axis
     * [2]: z-axis
     */
    val gravity: FloatArray = FloatArray(3),

    /**
     * Rotation angles of the device, in degrees
     * [0]:Pitch, angle of rotation about the x axis.
     * [1]:Roll, angle of rotation about the y axis
     * [2]:Azimuth, angle of rotation about the -z axis.
     */
    val orientationVector: IntArray = IntArray(3)
): IDataBaseTelemetry
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Inclination

        if (id != other.id) return false
        if (!acceleration.contentEquals(other.acceleration)) return false
        if (!orientationVector.contentEquals(other.orientationVector)) return false
        if (!gravity.contentEquals(other.gravity)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + acceleration.contentHashCode()
        result = 31 * result + orientationVector.contentHashCode()
        result = 31 * result + gravity.contentHashCode()
        return result
    }
}