package com.bvillarroya_creations.shareyourride.viewmodel.body

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.datamodel.data.Body
import com.bvillarroya_creations.shareyourride.datamodel.data.Location
import com.bvillarroya_creations.shareyourride.datamodel.dataBase.ShareYourRideRepository
import com.bvillarroya_creations.shareyourride.telemetry.body.BodyData
import com.bvillarroya_creations.shareyourride.telemetry.body.BodyManager
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryManager
import com.bvillarroya_creations.shareyourride.viewmodel.DataConverters
import com.bvillarroya_creations.shareyourride.viewmodel.base.TelemetryViewModel
import kotlinx.coroutines.runBlocking

/**
 * View model for body data
 */
class BodyViewModel(application: Application) : TelemetryViewModel(application) {

    override var mTelemetryManager: ITelemetryManager = BodyManager(application)

    override var mClassName: String = BodyViewModel::class.java.simpleName

    /**
     * The latitude position of the device, in degrees
     */
    val heartRate = MutableLiveData<Double>()

    /**
     * Process the telemetry data to make easy upper layers to manage it
     *
     * @param data: The telemetry data given by the provider
     */
    override fun processTelemetry(data: ITelemetryData) {
        try {
            val body = DataConverters.convertData(data as BodyData, mSessionId, 0)

            telemetryData = body

            heartRate.value = body.heartRate
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
            if (telemetryData != null)
            {
                telemetryData!!.id.timeStamp = timeStamp

                runBlocking {
                    ShareYourRideRepository.insertbody(telemetryData as Body)
                }
            }
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to save body telemetry data ${ex.message}")
            ex.printStackTrace()
        }
    }
}