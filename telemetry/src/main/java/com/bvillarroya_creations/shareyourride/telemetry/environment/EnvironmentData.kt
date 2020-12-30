/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.telemetry.environment

import android.os.Parcel
import android.os.Parcelable
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData

data class EnvironmentData(
    /**
     *
     */
    val temperature: Double,

    /**
     *
     */
    val windDirection: Double,

    /**
     *
     */
    val windSpeed: Double,

    /**
     *
     */
    val humidity: Int,

    /**
     *
     */
    val pressure: Double,

    val timeStamp: Long) : ITelemetryData {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readLong()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(temperature)
        parcel.writeDouble(windDirection)
        parcel.writeDouble(windSpeed)
        parcel.writeInt(humidity)
        parcel.writeDouble(pressure)
        parcel.writeLong(timeStamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EnvironmentData> {
        override fun createFromParcel(parcel: Parcel): EnvironmentData {
            return EnvironmentData(parcel)
        }

        override fun newArray(size: Int): Array<EnvironmentData?> {
            return arrayOfNulls(size)
        }
    }

}