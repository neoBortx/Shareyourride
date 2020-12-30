/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.telemetry.location

import android.os.Parcel
import android.os.Parcelable
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData

/**
 * Holds the location given by the location provider in a concrete instant of time
 */
data class LocationData(
    /**
     * Latitude of the device in degrees
     */
    val latitude: Double,

    /**
     * Longitude of the device in degrees
     */
    val longitude: Double,

    /**
     * Altitude of the device in meters above the WGS 84 reference ellipsoid.
     *
     * If this location does not have an altitude then 0.0 is set.
     */
    val altitude: Double,

    /**
     * Speed of the device in meters per seconds
     *
     * If this location does not have a speed then 0.0 is set.
     */
    val speed: Float,

    /**
     * The bearing of the device in degrees. (es el rumbo)
     *
     * Bearing is the horizontal direction of travel of this device,
     * and is not related to the device orientation. It is guaranteed to
     * be in the range (0.0, 360.0] if the device has a bearing.
     *
     * If this location does not have a bearing then 0.0 is set
     */
    val bearing: Float,

    /**
     * The inclination of the terrain expressed in percentage
     */
    val terrainInclination: Int,
    /**
     * The timeStamp when the frame is captured
     */

    /**
     * The accumulated distance of the session
     */
    val distance: Long,

    /**
     *
     */
    val accuracy: Float,
    /**
     * The timeStamp when the frame is captured
     */
    val timeStamp: Long): ITelemetryData {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readFloat(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeDouble(altitude)
        parcel.writeFloat(speed)
        parcel.writeFloat(bearing)
        parcel.writeInt(terrainInclination)
        parcel.writeLong(distance)
        parcel.writeFloat(accuracy)
        parcel.writeLong(timeStamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocationData> {
        override fun createFromParcel(parcel: Parcel): LocationData {
            return LocationData(parcel)
        }

        override fun newArray(size: Int): Array<LocationData?> {
            return arrayOfNulls(size)
        }
    }
}