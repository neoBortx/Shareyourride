package com.bvillarroya_creations.shareyourride.viewmodel

import com.bvillarroya_creations.shareyourride.datamodel.data.Environment
import com.bvillarroya_creations.shareyourride.datamodel.data.Inclination
import com.bvillarroya_creations.shareyourride.datamodel.data.Location
import com.bvillarroya_creations.shareyourride.datamodel.data.TelemetryId
import com.bvillarroya_creations.shareyourride.telemetry.environment.EnvironmentData
import com.bvillarroya_creations.shareyourride.telemetry.inclination.InclinationData
import com.bvillarroya_creations.shareyourride.telemetry.location.LocationData

class DataConverters {

    companion object
    {
        fun convertData(data: LocationData, sessionId: String, videoId: Long): Location
        {
            val location = Location(
                TelemetryId(sessionId,videoId),
                data.timeStamp,
                data.latitude,
                data.longitude,
                data.altitude,
                data.speed)

            return location
        }

        fun convertData(data: InclinationData, sessionId: String, videoId: Long): Inclination
        {
            val inclination = Inclination(
                TelemetryId(sessionId,videoId),
                data.TimeStamp,
                data.acceleration,
                data.rotationVector,
                data.gravity)

            return inclination
        }

        fun convertData(data: EnvironmentData, sessionId: String, videoId: Long): Environment
        {
            val environment = Environment(
                TelemetryId(sessionId,videoId),
                data.TimeStamp)

            return environment
        }


    }
}