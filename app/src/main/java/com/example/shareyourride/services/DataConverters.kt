package com.example.shareyourride.services


import com.bvillarroya_creations.shareyourride.datamodel.data.*
import com.bvillarroya_creations.shareyourride.telemetry.environment.EnvironmentData
import com.bvillarroya_creations.shareyourride.telemetry.inclination.InclinationData
import com.bvillarroya_creations.shareyourride.telemetry.location.LocationData

/**
 * Convert the model of data defined in telemetry layer to be used in the rest of the APP
 */
class DataConverters {

    companion object
    {
        fun convertData(data: LocationData, sessionId: String, videoId: Long): Location
        {

            return Location(
                    TelemetryId(sessionId, videoId), data.latitude, data.longitude, data.altitude, data.speed, data.bearing, data.terrainInclination)
        }

        fun convertData(data: InclinationData, sessionId: String, videoId: Long): Inclination
        {

            return Inclination(
                TelemetryId(sessionId,videoId),
                data.acceleration,
                data.gravity,
                data.azimuth,
                data.pitch,
                data.roll)
        }

        fun convertData(data: EnvironmentData, sessionId: String, videoId: Long): Environment
        {

            return Environment(
                TelemetryId(sessionId,videoId),
                data.temperature,
                data.windDirection,
                data.windSpeed,
                data.humidity,
                data.pressure)
        }
    }
}