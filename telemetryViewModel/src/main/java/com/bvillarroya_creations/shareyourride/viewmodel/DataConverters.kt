package com.bvillarroya_creations.shareyourride.viewmodel

import com.bvillarroya_creations.shareyourride.datamodel.data.*
import com.bvillarroya_creations.shareyourride.telemetry.body.BodyData
import com.bvillarroya_creations.shareyourride.telemetry.environment.EnvironmentData
import com.bvillarroya_creations.shareyourride.telemetry.inclination.InclinationData
import com.bvillarroya_creations.shareyourride.telemetry.location.LocationData

class DataConverters {

    companion object
    {
        fun convertData(data: LocationData, sessionId: String, videoId: Long): Location
        {

            return Location(
                TelemetryId(sessionId,videoId),
                data.latitude,
                data.longitude,
                data.altitude,
                data.speed)
        }

        fun convertData(data: InclinationData, sessionId: String, videoId: Long): Inclination
        {

            return Inclination(
                TelemetryId(sessionId,videoId),
                data.acceleration,
                data.gravity,
                data.orientationVector)
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

        fun convertData(data: BodyData, sessionId: String, videoId: Long): Body
        {

            return Body(
                    TelemetryId(sessionId,videoId),
                    data.heartRate)
        }
    }
}