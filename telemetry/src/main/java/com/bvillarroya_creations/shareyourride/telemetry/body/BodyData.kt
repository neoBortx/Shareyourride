package com.bvillarroya_creations.shareyourride.telemetry.body

import android.os.Parcel
import android.os.Parcelable
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData

/**
 * Holds the location given by the location provider in a concrete instant of time
 */
data class BodyData(
    /**
     * Hearth rate
     */
    val heartRate: Double,
    /**
     * The timeStamp when the frame is captured
     */
    val timeStamp: Long): ITelemetryData {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(heartRate)
        parcel.writeLong(timeStamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyData> {
        override fun createFromParcel(parcel: Parcel): BodyData {
            return BodyData(parcel)
        }

        override fun newArray(size: Int): Array<BodyData?> {
            return arrayOfNulls(size)
        }
    }
}