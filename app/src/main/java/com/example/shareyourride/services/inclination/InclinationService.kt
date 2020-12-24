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
import com.example.shareyourride.userplayground.common.TelemetryDirectionIconConverter
import com.example.shareyourride.viewmodels.userplayground.InclinationViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

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

    private var synchronizationTimer: Disposable? = null
    //endregion


    //region message handlers
    init {
        this.createMessageHandler( "InclinationService", listOf<String>(MessageTopics.SESSION_CONTROL, MessageTopics.INCLINATION_CONTROL, MessageTopics.VIDEO_DATA))
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
                    stopVideoSynchronization()
                    calibrationManager.startCalibration()
                }
                MessageTypes.VIDEO_SYNCHRONIZATION_COMMAND ->
                {
                    Log.d(mClassName, "SYR -> received  VIDEO_SYNCHRONIZATION_COMMAND, starting the calibration process ")
                    initializeTelemetry()
                    startVideoSynchronization()
                }
                MessageTypes.VIDEO_SYNCHRONIZATION_END_COMMAND ->
                {
                    Log.d(mClassName, "SYR -> received  VIDEO_SYNCHRONIZATION_END_COMMAND, starting the calibration process ")
                    stopVideoSynchronization()
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

    /**
     * Start sending elan angle data to upper layers in order to synchronize the video
     */
    private fun startVideoSynchronization()
    {
        try
        {
            if (synchronizationTimer == null) {
                synchronizationTimer = Observable.interval(200, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe {
                    if (telemetryData != null)
                    {
                        val message = MessageBundle(MessageTypes.LEAN_ANGLE_SYNCHRONIZATION_DATA, (telemetryData as Inclination).roll, MessageTopics.VIDEO_SYNCHRONIZATION_DATA)
                        sendMessage(message)
                    }
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to start video synchronization because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Stops sending lean angle information to upper layers
     */
    private fun stopVideoSynchronization()
    {
        try {
            if (synchronizationTimer != null)
            {
                synchronizationTimer?.dispose()
                synchronizationTimer = null
            }
        }
        catch (ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to stop video synchronization because: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    override fun onCreate() {
        super.onCreate()

        calibrationManager.calibratedSuccessfully.observe(this, Observer {result ->

            val data = InclinationCalibrationData(result,
                                                  calibrationManager.referenceRoll,
                                                  calibrationManager.referencePitch,
                                                  calibrationManager.referenceAzimuth,
                                                  calibrationManager.referenceAcceleration)

            Log.d("InclinationService", "Sending the result of the calibration process INCLINATION_CALIBRATION_END -> $result")
            val message = MessageBundle(MessageTypes.INCLINATION_CALIBRATION_END, data, MessageTopics.INCLINATION_CONTROL)
            sendMessage(message)
            Log.e("InclinationService", "SYR -> Calculated reference rotation " +
                    "roll ${ calibrationManager.referenceRoll} " +
                    "pitch ${ calibrationManager.referencePitch} " +
                    "azimuth ${ calibrationManager.referenceAzimuth}")

            Log.e("InclinationService", "SYR -> Calculated reference acceleration vectors " +
                    "x ${ calibrationManager.referenceAcceleration[0]} " +
                    "y ${ calibrationManager.referenceAcceleration[1]} " +
                    "z ${ calibrationManager.referenceAcceleration[2]}")
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

            //get raw data
            val inclinationData = data as InclinationData

            if(calibrationManager.isInEditMode)
            {
                calibrationManager.insertValues(inclinationData.roll, inclinationData.pitch, inclinationData.azimuth, inclinationData.acceleration, inclinationData.gravity)
            }

            val acceleration = processAcceleration(inclinationData.acceleration)
            val accelerationScalar = getAccelerationScalar(acceleration,inclinationData.pitch)
            val accelerationDirection = TelemetryDirectionIconConverter.getAccelerationDirection(determineLongitudinalValue(acceleration[0], acceleration[2], inclinationData.pitch), acceleration[1]).ordinal

            val inclination = DataConverters.convertData(inclinationData, mSessionId, 0, accelerationScalar, accelerationDirection)

            //process teh data applying offsets and high pass filters
            val processedInclination = Inclination(
                    azimuth      = processAzimuth(inclination.azimuth),
                    pitch        = processPitch(inclination.pitch),
                    roll         =   processRoll(inclination.roll),
                    acceleration = acceleration,
                    gravity      =   inclination.gravity,
                    accelerationScalar = inclination.accelerationScalar,
                    accelerationDirection = inclination.accelerationDirection
            )
            telemetryData = processedInclination
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
            telemetryData!!.id.sessionId = mSessionId
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

    //region process telemetry

    /**
     * Get the device roll applying the reference offset calculated in the calibration phase
     *
     * @param roll: the obtained roll
     * @return the calculated roll
     */
    private fun processRoll(roll: Int):Int
    {
        return when {
            calibrationManager.referenceRoll > 0 -> {
                roll - calibrationManager.referenceRoll
            }
            calibrationManager.referenceRoll < 0 -> {
                roll + calibrationManager.referenceRoll
            }
            else -> {
                roll
            }
        }
    }

    /**
     * Get the device roll applying the reference offset calculated in the calibration phase
     *
     * @param pitch: the obtained roll
     * @return the calculated pitch
     */
    private fun processPitch(pitch: Int):Int
    {
        return when {
            calibrationManager.referencePitch > 0 -> {
                pitch - calibrationManager.referencePitch
            }
            calibrationManager.referencePitch < 0 -> {
                pitch + calibrationManager.referencePitch
            }
            else -> {
                pitch
            }
        }
    }

    /**
     * Get the device azimuth applying the reference offset calculated in the calibration phase
     *
     * @param azimuth: the obtained roll
     * @return the calculated azimuth
     */
    private fun processAzimuth(azimuth: Int):Int
    {
        return when {
            calibrationManager.referenceAzimuth > 0 -> {
                azimuth - calibrationManager.referenceAzimuth
            }
            calibrationManager. referenceAzimuth < 0 -> {
                azimuth + calibrationManager.referenceAzimuth
            }
            else -> {
                azimuth
            }
        }
    }

    private fun processAcceleration(acceleration: FloatArray):FloatArray
    {
        val processedAcceleration =  FloatArray(3)

        processedAcceleration[0] = processForceX(acceleration[0])
        processedAcceleration[1] = processForceY(acceleration[1])
        processedAcceleration[2] = processForceZ(acceleration[2])



        return processedAcceleration
    }

    /**
     *
     */
    private fun processForceX(xSample: Float):Float
    {
        return if (xSample.absoluteValue <= calibrationManager.referenceAcceleration[0].absoluteValue )
        {
            0.0F
        }
        else
        {
            xSample
        }
    }

    private fun processForceY(ySample: Float):Float
    {
        return if (ySample.absoluteValue <= calibrationManager.referenceAcceleration[1].absoluteValue )
        {
            0.0F
        }
        else
        {
            ySample
        }
    }

    private fun processForceZ(zSample: Float):Float
    {
        return if (zSample.absoluteValue <= calibrationManager.referenceAcceleration[0].absoluteValue )
        {
            0.0F
        }
        else
        {
            zSample
        }
    }

    /**
     * Calculate the acceleration magnitude
     *
     * @param accelerationVector: the acceleration in x,y and z axis
     * @param pitch: the rotation in the x axis
     */
    private fun getAccelerationScalar(accelerationVector: FloatArray, pitch: Int): Float
    {
        try
        {
            if (accelerationVector.count() >= 3)
            {
                //apply high pass filter
                val x  = accelerationVector[0]
                val y  = accelerationVector[1]
                val z= accelerationVector[2]

                val longitudinalValue = determineLongitudinalValue(x,y,pitch)

                return sqrt(longitudinalValue.pow(2.0F) + y.pow(2.0F) + z.pow(2.0F))
            }
        }
        catch (ex: Exception)
        {
            Log.e("LocationViewModel", "SYR -> Unable to process acceleration because: ${ex.message}")
            ex.printStackTrace()
        }

        return 0F
    }

    /**
     * Determine the higher force
     * @param xAxis: Force in x axis
     * @param zAxis: Force in z axis
     * @param pitch: Rotating in x axis
     *
     * @return the dominant force
     */
    private fun determineLongitudinalValue(xAxis: Float, zAxis: Float, pitch: Int): Float
    {
        return when {
            pitch.absoluteValue > 45 -> {
                zAxis
            }
            pitch.absoluteValue == 45 -> {
                if (zAxis.absoluteValue > xAxis.absoluteValue) zAxis else xAxis
            }
            else -> {
                xAxis
            }
        }
    }
    //endregion
}