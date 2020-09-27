package com.bvillarroya_creations.shareyourride.viewmodel.location

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.datamodel.data.Location
import com.bvillarroya_creations.shareyourride.datamodel.dataBase.ShareYourRideRepository
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryManager
import com.bvillarroya_creations.shareyourride.telemetry.location.LocationData
import com.bvillarroya_creations.shareyourride.telemetry.location.LocationManager
import com.bvillarroya_creations.shareyourride.viewmodel.DataConverters
import com.bvillarroya_creations.shareyourride.viewmodel.base.TelemetryViewModel
import com.bvillarroya_creations.shareyourride.viewmodel.environment.EnvironmentViewModel
import kotlinx.coroutines.runBlocking

/**
 * View model for location data
 */
class LocationViewModel(application: Application) : TelemetryViewModel(application) {

    override var mTelemetryManager: ITelemetryManager = LocationManager(application)

    override var mClassName: String = EnvironmentViewModel::class.java.simpleName

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

            latitude.value = location.latitude
            longitude.value = location.longitude
            altitude.value = location.altitude
            speed.value = location.speed
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