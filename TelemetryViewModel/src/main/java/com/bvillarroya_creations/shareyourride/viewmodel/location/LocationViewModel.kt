package com.bvillarroya_creations.shareyourride.viewmodel.location

import android.app.Activity
import android.app.Application
import android.util.Log
import com.bvillarroya_creations.shareyourride.datamodel.dataBase.ShareYourRideRepository
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryManager
import com.bvillarroya_creations.shareyourride.telemetry.location.LocationData
import com.bvillarroya_creations.shareyourride.telemetry.location.LocationManager
import com.bvillarroya_creations.shareyourride.viewmodel.DataConverters
import com.bvillarroya_creations.shareyourride.viewmodel.base.TelemetryViewModel
import kotlinx.coroutines.runBlocking

/*
    View model for location data
 */
class LocationViewModel(application: Application) : TelemetryViewModel(application) {

    override var mTelemetryManager: ITelemetryManager = LocationManager(application)

    override fun processTelemetry(data: ITelemetryData) {
        try {
            val location = DataConverters.convertData(data as LocationData, mSessionId, mVideoId)

            runBlocking {
                ShareYourRideRepository.insertLocation(location);
            }
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to process location telemetry data ${ex.message} - ${ex.stackTrace}")
        }
    }
}