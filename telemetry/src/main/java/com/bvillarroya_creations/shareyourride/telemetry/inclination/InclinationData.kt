package com.bvillarroya_creations.shareyourride.telemetry.inclination

import android.os.Parcel
import android.os.Parcelable
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData

/**
 * Holds the information related to an event produced by movement sensors
 */
data class InclinationData(
    /**
     * gravity applied to the device in m/s2
     * [0]: x-axis
     * [1]: y-axis
     * [2]: z-axis
     */
    val gravity: FloatArray,
    /**
     * linear acceleration of the device in m/s2
     * [0]: x-axis
     * [1]: y-axis
     * [2]: z-axis
     */
    val acceleration: FloatArray,
    /**
     * Rotation angles of the device, in degrees
     * [0]:Pitch, angle of rotation about the x axis.
     * [1]:Roll, angle of rotation about the y axis
     * [2]:Azimuth, angle of rotation about the -z axis.
     */
    val orientationVector: IntArray,

    val timeStamp: Long) : ITelemetryData {
    constructor(parcel: Parcel) : this(
        parcel.createFloatArray()!!,
        parcel.createFloatArray()!!,
        parcel.createIntArray()!!,
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloatArray(gravity)
        parcel.writeFloatArray(acceleration)
        parcel.writeIntArray(orientationVector)
        parcel.writeLong(timeStamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InclinationData

        if (!gravity.contentEquals(other.gravity)) return false
        if (!acceleration.contentEquals(other.acceleration)) return false
        if (!orientationVector.contentEquals(other.orientationVector)) return false
        if (timeStamp != other.timeStamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = gravity.contentHashCode()
        result = 31 * result + acceleration.contentHashCode()
        result = 31 * result + orientationVector.contentHashCode()
        result = 31 * result + timeStamp.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<InclinationData> {
        override fun createFromParcel(parcel: Parcel): InclinationData {
            return InclinationData(parcel)
        }

        override fun newArray(size: Int): Array<InclinationData?> {
            return arrayOfNulls(size)
        }
    }

}