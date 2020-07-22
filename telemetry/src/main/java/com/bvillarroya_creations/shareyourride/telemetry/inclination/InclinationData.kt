package com.bvillarroya_creations.shareyourride.telemetry.inclination

import android.os.Parcel
import android.os.Parcelable
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData

data class InclinationData(val gravity: FloatArray, val acceleration: FloatArray, val rotationVector: FloatArray, val TimeStamp: Long) : ITelemetryData {
    constructor(parcel: Parcel) : this(
        parcel.createFloatArray()!!,
        parcel.createFloatArray()!!,
        parcel.createFloatArray()!!,
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloatArray(gravity)
        parcel.writeFloatArray(acceleration)
        parcel.writeFloatArray(rotationVector)
        parcel.writeLong(TimeStamp)
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
        if (!rotationVector.contentEquals(other.rotationVector)) return false
        if (TimeStamp != other.TimeStamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = gravity.contentHashCode()
        result = 31 * result + acceleration.contentHashCode()
        result = 31 * result + rotationVector.contentHashCode()
        result = 31 * result + TimeStamp.hashCode()
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