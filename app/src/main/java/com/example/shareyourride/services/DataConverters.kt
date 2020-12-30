/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.services


import com.bvillarroya_creations.shareyourride.datamodel.data.Inclination
import com.bvillarroya_creations.shareyourride.datamodel.data.Location
import com.bvillarroya_creations.shareyourride.datamodel.data.TelemetryId
import com.bvillarroya_creations.shareyourride.telemetry.environment.EnvironmentData
import com.bvillarroya_creations.shareyourride.telemetry.inclination.InclinationData
import com.bvillarroya_creations.shareyourride.telemetry.location.LocationData

/**
 * Convert the model of data defined in telemetry layer to be used in the rest of the APP
 */
class DataConverters {

    companion object
    {
        fun convertData(data: LocationData, sessionId: String, timestamp: Long): Location
        {

            return Location(
                    TelemetryId(sessionId, timestamp),
                    data.latitude,
                    data.longitude,
                    data.altitude,
                    data.speed,
                    data.bearing,
                    data.terrainInclination,
                    data.distance)
        }

        fun convertData(data: InclinationData, sessionId: String, timestamp: Long, accelerationScalar: Float, accelerationDirection: Int): Inclination
        {

            return Inclination(
                TelemetryId(sessionId,timestamp),
                data.acceleration,
                data.gravity,
                data.azimuth,
                data.pitch,
                data.roll,
                accelerationScalar,
                accelerationDirection)
        }
    }
}