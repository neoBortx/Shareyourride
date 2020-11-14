package com.example.shareyourride.services.environment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.datamodel.data.Environment
import com.bvillarroya_creations.shareyourride.datamodel.data.Inclination
import com.bvillarroya_creations.shareyourride.datamodel.dataBase.ShareYourRideRepository
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTopics
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.telemetry.environment.EnvironmentData
import com.bvillarroya_creations.shareyourride.telemetry.environment.EnvironmentManager
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.example.shareyourride.services.DataConverters
import com.example.shareyourride.services.base.TelemetryServiceBase
import kotlinx.coroutines.runBlocking

/**
 * View model for the environment data
 */
class EnvironmentService(): TelemetryServiceBase()
{

    /**
     * Initializes the manager in change of the environment
     */
    override fun initializeManager()
    {
        mTelemetryManager = EnvironmentManager(application)
    }

    override var mClassName: String = EnvironmentService::class.java.simpleName

    //region properties
    var temperature = MutableLiveData<Double>()

    var pressure = MutableLiveData<Double>()

    var humidity = MutableLiveData<Int>()

    var windDirection = MutableLiveData<Double>()

    var windSpeed = MutableLiveData<Double>()
    //endregion


    //region ServiceBase functions
    /**
     * Process the telemetry data to make easy upper layers to manage it
     *
     * @param data: The telemetry data given by the provider
     */
    override fun processTelemetry(data: ITelemetryData) {
        try {
            val environment = DataConverters.convertData(data as EnvironmentData, mSessionId, 0)
            telemetryData = environment

            temperature.value = environment.temperature
            humidity.value = environment.humidity
            pressure.value = environment.pressure
            windDirection.value = environment.windDirection
            windSpeed.value = environment.windSpeed

        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to process environment telemetry data ${ex.message}")
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
            if (telemetryData != null) {
                telemetryData!!.id.sessionId = mSessionId
                telemetryData!!.id.timeStamp = timeStamp

                runBlocking {
                    ShareYourRideRepository.insertEnvironment(telemetryData as Environment)
                }
            }
            else
            {
                Log.i("SYR", "SYR -> Unable to save telemetry data because there isn't any")
            }


        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to save location telemetry data ${ex.message}")
            ex.printStackTrace()
        }
    }

    override fun updateTelemetry()
    {
        try
        {
            //val message = MessageBundle(MessageTypes.INCLINATION_DATA_EVENT, telemetryData as Environment, MessageTopics.INCLINATION_DATA)
            //sendMessage(message)
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to update location telemetry data ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregions
}