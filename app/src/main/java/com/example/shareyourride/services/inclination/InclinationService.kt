package com.example.shareyourride.services.inclination

import android.util.Log
import androidx.lifecycle.Observer
import com.bvillarroya_creations.shareyourride.datamodel.data.Inclination
import com.bvillarroya_creations.shareyourride.datamodel.dataBase.ShareYourRideRepository
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTopics
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messagesdefinition.dataBundles.InclinationCalibrationData
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.telemetry.inclination.InclinationData
import com.bvillarroya_creations.shareyourride.telemetry.inclination.InclinationManager
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.example.shareyourride.services.DataConverters
import com.example.shareyourride.services.base.TelemetryServiceBase
import kotlinx.coroutines.runBlocking

/**
 * Service for tilt data
 */
class InclinationService(): TelemetryServiceBase()
{

    //region private properties

    /**
     * To manage the calibration
     */
    private var calibrationManager = CalibrationManager()

    override var mClassName: String = InclinationService::class.java.simpleName
    //endregion


    //region message handlers
    init {
        this.createMessageHandler( "InclinationService", listOf<String>(MessageTopics.SESSION_CONTROL, MessageTopics.INCLINATION_DATA))
    }

    /**
     * Listen to session messages related to the session management
     *
     * @param msg: received message from the android internal queue
     */
    override fun processMessage(msg: MessageBundle)
    {
        try
        {
            when (msg.messageKey)
            {
                MessageTypes.INCLINATION_CALIBRATION_START ->
                {
                    Log.d(mClassName, "SYR -> received  INCLINATION_CALIBRATION_START, starting the calibration process ")
                    initializeTelemetry()

                    calibrationManager.startCalibration()
                }
                else ->
                {
                    super.processMessage(msg)
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to process message ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    override fun onCreate() {
        super.onCreate()

        calibrationManager.calibratedSuccessfully.observe(this, Observer {result ->

            val data = InclinationCalibrationData(result, calibrationManager.referenceRoll, calibrationManager.referencePitch, calibrationManager.referenceAzimuth)
            Log.d("InclinationService", "Sending the result of the calibration process INCLINATION_CALIBRATION_END -> $result")
            val message = MessageBundle(MessageTypes.INCLINATION_CALIBRATION_END, data, MessageTopics.INCLINATION_DATA)
            sendMessage(message)
        })

    }

    /**
     * Initializes the manager in change of the environment
     */
    override fun initializeManager()
    {
        mTelemetryManager = InclinationManager(application)
    }

    //region ServiceBase
    /**
     * Process the telemetry data to make easy upper layers to manage it
     *
     * @param data: The telemetry data given by the provider
     */
    override fun processTelemetry(data: ITelemetryData) {
        try
        {
            val inclination = DataConverters.convertData(data as InclinationData, mSessionId, 0)
            telemetryData = inclination

            if(calibrationManager.isInEditMode)
            {
                calibrationManager.insertValues(inclination.roll, inclination.pitch, inclination.azimuth)
            }
        }
        catch(ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to process inclination telemetry data ${ex.message}")
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
                ShareYourRideRepository.insertInclination(telemetryData as Inclination)
            }
        }
        catch(ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to save location telemetry data ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Sends the telemetry to upper layers
     */
    override fun updateTelemetry()
    {
        try
        {
            val message = MessageBundle(MessageTypes.INCLINATION_DATA_EVENT, telemetryData as Inclination, MessageTopics.INCLINATION_DATA)
            sendMessage(message)
        }
        catch(ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to update location telemetry data ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion
}