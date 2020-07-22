package com.bvillarroya_creations.shareyourride.viewmodel.environment

import android.app.Activity
import android.app.Application
import android.util.Log
import com.bvillarroya_creations.shareyourride.datamodel.dataBase.ShareYourRideRepository
import com.bvillarroya_creations.shareyourride.telemetry.environment.EnvironmentData
import com.bvillarroya_creations.shareyourride.telemetry.environment.EnvironmentManager
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryManager
import com.bvillarroya_creations.shareyourride.viewmodel.DataConverters
import com.bvillarroya_creations.shareyourride.viewmodel.base.TelemetryViewModel
import kotlinx.coroutines.runBlocking

/*
    View model for the environment data
 */
class EnvironmentViewModel(application: Application): TelemetryViewModel(application) {
    override var mTelemetryManager: ITelemetryManager = EnvironmentManager(application)

    override fun processTelemetry(data: ITelemetryData) {
        try {
            val environment = DataConverters.convertData(data as EnvironmentData, mSessionId, mVideoId)

            runBlocking {
                ShareYourRideRepository.insertEnvironment(environment)
            }
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to process environment telemetry data ${ex.message} - ${ex.stackTrace}")
        }
    }
}