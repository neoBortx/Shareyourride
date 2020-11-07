package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores the current session information
 */

@Entity(tableName = "session")
data class Session(

    /**
     * Row unique identifier. Also identifies the session. Generate this field with a GUID generator
     */
    @PrimaryKey
    var id: String = "",

    /**
     * Name given by the user
     */
    var name: String = "",

    /**
     *  Date of creation (time stamp)
     */
    var initTimeStamp: Long = 0,

    /**
     *  Date of the end of the session (time stamp)
     */
    var endTimeStamp: Long = 0,

    /**
     * The state of the current session
     * 0: Unknown
     * 1: Stopped
     * 2: CalibratingSensors
     * 3: SensorsCalibrated
     * 4: Started
     * 5: CreatingVideo
     * 6: Finished
     *
     * The have to match with com.example.shareyourride.services.session
     */
    var state: Int = 0,

    /**
     * The type of activity configured by the user
     */
    var activityKind: Int = 0,

    /**
     * The path and the name when the video is saved
     */
    var videoPath: String = "",

    /**
     * THe path and name of the converted vídeo
     */
    var videoConvertedPath: String = "",

    /**
     * Points if inclinations have been calibrated for the session
     */
    var sensorsCalibrated: Boolean = false,

    /**
     * Azimuth, angle of rotation about the -z axis.
     */
    var referenceAzimuth: Int = 0,
    /**
     * Pitch, angle of rotation about the x axis.
     */
    var referencePitch: Int = 0,
    /**
     * Roll, angle of rotation about the y axis
     */
    var referenceRoll: Int = 0,

    /**
     * acceleration applied to the device in m/s2
     * [0]: x-axis
     * [1]: y-axis
     * [2]: z-axis
     */
    var referenceAcceleration: FloatArray = FloatArray(3)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Session

        if (id != other.id) return false
        if (name != other.name) return false
        if (initTimeStamp != other.initTimeStamp) return false
        if (endTimeStamp != other.endTimeStamp) return false
        if (state != other.state) return false
        if (activityKind != other.activityKind) return false
        if (videoPath != other.videoPath) return false
        if (videoConvertedPath != other.videoConvertedPath) return false
        if (sensorsCalibrated != other.sensorsCalibrated) return false
        if (referenceAzimuth != other.referenceAzimuth) return false
        if (referencePitch != other.referencePitch) return false
        if (referenceRoll != other.referenceRoll) return false
        if (referenceAcceleration.contentEquals(other.referenceAcceleration)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + initTimeStamp.hashCode()
        result = 31 * result + endTimeStamp.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + activityKind.hashCode()
        result = 31 * result + videoPath.hashCode()
        result = 31 * result + videoConvertedPath.hashCode()
        result = 31 * result + sensorsCalibrated.hashCode()
        result = 31 * result + referenceAzimuth.hashCode()
        result = 31 * result + referencePitch.hashCode()
        result = 31 * result + referenceRoll.hashCode()
        result = 31 * result + referenceAcceleration.contentHashCode()
        return result
    }
}