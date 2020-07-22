package com.bvillarroya_creations.shareyourride.viewmodel.inclination

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.datamodel.dataBase.ShareYourRideRepository
import com.bvillarroya_creations.shareyourride.telemetry.inclination.InclinationData
import com.bvillarroya_creations.shareyourride.telemetry.inclination.InclinationManager
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryManager
import com.bvillarroya_creations.shareyourride.viewmodel.DataConverters
import com.bvillarroya_creations.shareyourride.viewmodel.base.TelemetryViewModel
import kotlinx.coroutines.runBlocking

/*
    View model for tilt data
 */
class InclinationViewModel(application: Application): TelemetryViewModel(application) {

    override var mTelemetryManager: ITelemetryManager = InclinationManager(application)

    val acceleration = MutableLiveData<FloatArray>()

    val gravity = MutableLiveData<FloatArray>()

    val rotationVector = MutableLiveData<FloatArray>()

    override fun processTelemetry(data: ITelemetryData) {
        try {
            val inclination = DataConverters.convertData(data as InclinationData, mSessionId, mVideoId)

            acceleration.value = inclination.acceleration
            gravity.value = inclination.gravity
            rotationVector.value = inclination.rotationVector

            runBlocking {
                ShareYourRideRepository.insertInclination(inclination)
            }
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to process inclination telemetry data ${ex.message} - ${ex.stackTrace}")
        }
    }
}