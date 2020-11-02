package com.bvillarroya_creations.shareyourride.services.location

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.datamodel.data.Location
import com.bvillarroya_creations.shareyourride.datamodel.dataBase.ShareYourRideRepository
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryManager
import com.bvillarroya_creations.shareyourride.telemetry.location.LocationData
import com.bvillarroya_creations.shareyourride.telemetry.location.LocationManager
import com.bvillarroya_creations.shareyourride.services.DataConverters
import com.bvillarroya_creations.shareyourride.services.base.TelemetryServiceBase
import com.bvillarroya_creations.shareyourride.services.environment.EnvironmentService
import kotlinx.coroutines.runBlocking

/**
 * Service for location data
 */
class LocationService() : TelemetryServiceBase() {

    //override var mTelemetryManager: ITelemetryManager = LocationManager(context)
    override var mTelemetryManager: ITelemetryManager = LocationManager(applicationContext)
    override var mClassName: String = EnvironmentService::class.java.simpleName

    /**
     * The latitude position of the device, in degrees
     */
    val latitude = MutableLiveData<Double>()

    /**
     * The longitude position in degrees of the device
     */
    val longitude = MutableLiveData<Double>()

    /**
     * The current altitude of the device, in meters
     */
    val altitude = MutableLiveData<Double>()

    /**
     * The speed of the device, in meters per second
     */
    val speed = MutableLiveData<Float>()

    /**
     * The inclination of the terrain
     */
    val terrainInclination = MutableLiveData<Int>()

    init {
        providerReady = mTelemetryManager.providerReady
        mTelemetryManager.configure()
    }

    /**
     * Process the telemetry data to make easy upper layers to manage it
     *
     * @param data: The telemetry data given by the provider
     */
    override fun processTelemetry(data: ITelemetryData) {
        try {
            val location = DataConverters.convertData(data as LocationData, mSessionId, 0)

            telemetryData = location

            latitude.postValue(location.latitude)
            longitude.postValue(location.longitude)
            altitude.postValue(location.altitude)
            speed.postValue(location.speed)
            terrainInclination.postValue(location.terrainInclination)
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to process location telemetry data ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Saves the current telemetry in the data base
     *
     * @param timeStamp: The time stamp that will be used to index the telemetry
     * and join all different kinds of telemetry
     */
    override fun saveTelemetry(timeStamp: Long) {
        try {
            telemetryData!!.id.timeStamp = timeStamp

            runBlocking {
                ShareYourRideRepository.insertLocation(telemetryData as Location)
            }
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to save location telemetry data ${ex.message}")
            ex.printStackTrace()
        }
    }
}