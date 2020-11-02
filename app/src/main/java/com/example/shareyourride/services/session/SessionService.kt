package com.example.shareyourride.services.session

import android.util.Log
import com.bvillarroya_creations.shareyourride.datamodel.data.Session
import com.bvillarroya_creations.shareyourride.datamodel.dataBase.ShareYourRideRepository
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTopics
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messagesdefinition.dataBundles.InclinationCalibrationData
import com.bvillarroya_creations.shareyourride.messenger.IMessageHandlerClient
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.messenger.MessageBundleData
import com.bvillarroya_creations.shareyourride.messenger.MessageHandler
import com.example.shareyourride.configuration.SettingPreferencesGetter
import com.example.shareyourride.configuration.SettingPreferencesIds
import com.example.shareyourride.services.base.ServiceBase
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Stores and mange the current user session
 */
class SessionService() : ServiceBase(), IMessageHandlerClient {


    //region private vars
    /**
     * The current state of the session
     */
    private var sessionState = SessionState.Stopped

    /**
     * Object that holds the current session data
     */
    private var mSession = Session()

    /**
     * Timer to send the save telemetry messages to other services
     */
    private var saveTelemetryTimer: Disposable? = null

    /**
     * Timer to send the update telemetry messages to other services
     */
    private var updateTelemetryTimer: Disposable? = null

    /**
     * In charge of access to the application settings
     */
    private var settingsGetter : SettingPreferencesGetter? = null
    //endregion

    override fun onCreate() {
        super.onCreate()
        settingsGetter = SettingPreferencesGetter(application.applicationContext)
    }

    //region public functions
    /**
     * Initialize the session with:
     * - A random unique identifier
     * - Initialize the data base in case it hasn't
     * - Insert the new session in the data base
     * - Send the rest of the view models the message to start acquiring data
     */
    private fun processStartSessionCommand()
    {
        try
        {
            val id = UUID.randomUUID().toString()
            mSession = Session()
            mSession.id = id
            mSession.name = id
            mSession.initTimeStamp = System.currentTimeMillis()

            Log.i("SYR", "SessionService -> starting session")

            sessionState = if (settingsGetter!!.getBooleanOption(SettingPreferencesIds.LeanAngleMetric)) SessionState.CalibratingSensors else SessionState.Started
            mSession.state = sessionState.value

            //the current running thread will be blocked until this piece of code is executed
            runBlocking {
                ShareYourRideRepository.buildDataBase(application)
                ShareYourRideRepository.insertSession(mSession)
            }

            sendSessionState()

            if (!settingsGetter!!.getBooleanOption(SettingPreferencesIds.LeanAngleMetric))
            {
                startSession()
            }
            else
            {
                startCalibratingSensors()
            }
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to process start session because ${ex.message}")
            ex.printStackTrace()
        }
    }

    private fun processRetryCalibration()
    {
        try
        {
            Log.i("SYR", "SessionService -> retrying calibration")

            sessionState = SessionState.CalibratingSensors
            mSession.state = SessionState.CalibratingSensors.value
            mSession.sensorsCalibrated = false

            //the current running thread will be blocked until this piece of code is executed
            runBlocking {
                ShareYourRideRepository.updateSession(mSession)
            }

            sendSessionState()

            startCalibratingSensors()
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to process start session because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     *
     */
    private fun startSession()
    {
        try
        {
            sendAcquiringData()
            configurePeriodicTimer()
        }
        catch (ex: java.lang.Exception)
        {
            Log.e("SessionService","SYR -> Unable to start session because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Configures a periodic timer to send save telemetry message
     */
    private fun configurePeriodicTimer()
    {
        try
        {
            Log.i("SessionService","SYR -> Configuring periodic timers for session ${mSession.id}")

            saveTelemetryTimer = Observable.interval(1000, 100, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe {
                sendSaveTelemetry()
            }

            updateTelemetryTimer = Observable.interval(1000, 1000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe {
                sendUpdateTelemetry()
            }
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to configure a periodic timer to send telemetry messages because ${ex.message}")
            ex.printStackTrace()
        }

    }

    private fun startCalibratingSensors ()
    {
        try
        {
            sendStartCalibratingSensors()
        }
        catch (ex: java.lang.Exception)
        {
            Log.e("SessionService","SYR -> Unable to start calibrating sensors because ${ex.message}")
            ex.printStackTrace()
        }

    }

    /**
     * Process the continue session message
     * Set the session state to started
     */
    private fun processContinueSession()
    {
        try
        {
            Log.i("SYR", "SessionService -> Resuming session")

            sessionState = SessionState.Started
            mSession.state = sessionState.value

            //the current running thread will be blocked until this piece of code is executed
            runBlocking {
                ShareYourRideRepository.updateSession(mSession)
            }

            sendSessionState()
            startSession()
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to resume session because ${ex.message}")
            ex.printStackTrace()
        }
    }

    private fun stopSession()
    {
        try
        {
            sendStopAcquiringData()

            saveTelemetryTimer?.dispose()

            updateTelemetryTimer?.dispose()
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to stop session because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Stop the session with:
     * - Update the state of the session
     * - Send a message to all telemetry view models to stop collecting data
     * - Send a message to the video view model to stop collecting data
     */
    private fun processStopSession()
    {
        try
        {
            sessionState = SessionState.CreatingVideo
            mSession.state = sessionState.value
            mSession.endTimeStamp = System.currentTimeMillis()

            stopSession()

            runBlocking {
                ShareYourRideRepository.updateSession(mSession)
            }

            sendSessionState()
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to process stop session message, because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Removes the session from the data data base.
     * If the state is running, perform the stop operation
     */
    private fun processDiscardSession()
    {
        try
        {
            if (sessionState != SessionState.Stopped)
            {
                sessionState = SessionState.Stopped
                mSession.state = SessionState.Stopped.value

                stopSession()

                sendVideoDiscardCommand()

                sendSessionState()

                //runBlocking {
                //    ShareYourRideRepository.deleteSession(mSession)
                //}
            }
            else
            {
                Log.e("SessionService","SYR -> processDiscardSession, Transition from  $sessionState to ${SessionState.Stopped} not supported")
            }
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to discard session because ${ex.message}")
            ex.printStackTrace()
        }
    }

    private fun processCancelSession()
    {
        try
        {
            if (sessionState != SessionState.SensorsCalibrated && sessionState != SessionState.CalibratingSensors)
            {
                sessionState = SessionState.Stopped
                mSession.state = SessionState.Stopped.value

                stopSession()

                runBlocking {
                    ShareYourRideRepository.deleteSession(mSession)
                }

                sendSessionState()
            }
            else
            {
                Log.e("SessionService","SYR -> processCancelSession, Transition from  $sessionState to ${SessionState.Stopped} not supported")
            }
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to discard session because ${ex.message}")
            ex.printStackTrace()
        }
    }

    private fun processInclinationCalibrationEnd(msgData: MessageBundleData)
    {
        try
        {
            if (sessionState == SessionState.CalibratingSensors)
            {
                if (msgData.type == InclinationCalibrationData::class)
                {
                    val calibration = msgData.data as InclinationCalibrationData
                    mSession.sensorsCalibrated = calibration.result
                    mSession.referenceAzimuth = calibration.azimuth
                    mSession.referencePitch = calibration.pitch
                    mSession.referenceRoll = calibration.roll
                }
                else
                {
                    Log.e("SessionService", "SYR -> Unable to process data in Inclination Calibration message")
                    mSession.sensorsCalibrated = false
                    mSession.referenceAzimuth = 0
                    mSession.referencePitch = 0
                    mSession.referenceRoll = 0
                }

                sessionState   = SessionState.SensorsCalibrated
                mSession.state = SessionState.SensorsCalibrated.value

                runBlocking {
                    ShareYourRideRepository.updateSession(mSession)
                }

                sendSessionState()
            }
            else
            {
                Log.e("SessionService","SYR -> processInclinationCalibrationEnd, Transition from  $sessionState to ${SessionState.SensorsCalibrated} not supported")
            }


        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to process inclination calibration end because ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    //region IMessageHandlerClient implementation
    init {
        this.createMessageHandler( "SessionService", listOf<String>(MessageTopics.SESSION_COMMANDS, MessageTopics.INCLINATION_DATA))
    }

    override lateinit var messageHandler: MessageHandler

    /**
     * No message required
     */
    override fun processMessage(msg: MessageBundle)
    {
        try
        {

            when (msg.messageKey)
            {
                MessageTypes.START_SESSION ->
                {
                    Log.d("SessionViewModel", "SYR -> received START_SESSION updating event")
                    processStartSessionCommand()
                }
                MessageTypes.END_SESSION ->
                {
                    Log.d("SessionViewModel", "SYR -> received END_SESSION updating event")
                    processStopSession()
                }
                MessageTypes.CONTINUE_SESSION ->
                {
                    Log.d("SessionViewModel", "SYR -> received CONTINUE_SESSION updating event")
                    processContinueSession()
                }
                MessageTypes.RETRY_CALIBRATION ->
                {
                    Log.d("SessionViewModel", "SYR -> received RETRY_CALIBRATION updating event")
                    processRetryCalibration()
                }
                MessageTypes.SESSION_STATE_REQUEST ->
                {
                    Log.d("SessionViewModel", "SYR -> received SESSION_STATE_REQUEST updating event")
                    sendSessionState()
                }
                MessageTypes.DISCARD_SESSION ->
                {
                    Log.d("SessionViewModel", "SYR -> received DISCARD_SESSION updating event")
                    processDiscardSession()
                }
                MessageTypes.CANCEL_SESSION ->
                {
                    Log.d("SessionViewModel", "SYR -> received CANCEL_SESSION updating event")
                    processCancelSession()
                }
                MessageTypes.INCLINATION_CALIBRATION_END ->
                {
                    Log.d("SessionViewModel", "SYR -> received INCLINATION_CALIBRATION_END updating event")
                    processInclinationCalibrationEnd(msg.messageData)
                }
                else ->
                {
                    Log.e("SessionViewModel", "SYR -> message ${msg.messageKey} no supported")
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("SessionViewModel", "SYR -> Unable to process message ${ex.message}")
            ex.printStackTrace()
        }
    }

    //region send messages
    /**
     * Sends the start session message to all telemetry services to start
     * recollect data
     * Send the start video message to the video view model to start processing the video
     * source
     */
    private fun sendAcquiringData()
    {
        try
        {
            Log.d("SessionService", "SYR -> Sending  START_ACQUIRING_DATA session message ${mSession.id}")
            val message = MessageBundle( MessageTypes.START_ACQUIRING_DATA, mSession.id, MessageTopics.SESSION_CONTROL)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to send start session message because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Command to the rest of telemetry services to stop acquiring data
     */
    private fun sendStopAcquiringData()
    {
        try
        {
            Log.d("SessionService", "SYR -> Sending  stop session message ${mSession.id}")
            val message = MessageBundle( MessageTypes.STOP_ACQUIRING_DATA, mSession.id, MessageTopics.SESSION_CONTROL)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to send start session message because ${ex.message}")
            ex.printStackTrace()
        }
    }


    /**
     * Send the save telemetry message to all telemetry services to save the current data
     * in the data base and notify upper layer the new value
     */
    private fun sendSaveTelemetry()
    {
        try
        {
            val message = MessageBundle(MessageTypes.SAVE_TELEMETRY, System.currentTimeMillis(), MessageTopics.SESSION_CONTROL)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to send save telemetry message because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Send the save telemetry message to all telemetry services to save the current data
     * in the data base and notify upper layer the new value
     */
    private fun sendUpdateTelemetry()
    {
        try
        {
            val message = MessageBundle(MessageTypes.UPDATE_TELEMETRY, "", MessageTopics.SESSION_CONTROL)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to send save telemetry message because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Send the session state to upper layers
     */
    private fun sendSessionState()
    {
        try
        {
            Log.d("SessionService", "SYR -> Sending  session state ")
            val session = SessionData()
            session.state = SessionState.fromInt(mSession.state)
            session.sensorCalibrated = mSession.sensorsCalibrated

            val message = MessageBundle( MessageTypes.SESSION_STATE_EVENT, session, MessageTopics.SESSION_COMMANDS)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to send session state message because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Send the session state to upper layers
     */
    private fun sendStartCalibratingSensors()
    {
        try
        {
            Log.d("SessionService", "SYR -> Sending  session state")
            val message = MessageBundle( MessageTypes.INCLINATION_CALIBRATION_START, mSession.id, MessageTopics.INCLINATION_DATA)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to send session state message because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Command the video service to discard and remove the stored video data
     */
    private fun sendVideoDiscardCommand()
    {
        try
        {
            Log.d("SessionService", "SYR -> Sending  video discard command")
            val message = MessageBundle( MessageTypes.VIDEO_DISCARD_COMMAND, mSession.id, MessageTopics.VIDEO_DATA)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to send video discard command state message because ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion


    //region ServiceBase
    override var mClassName: String = "SessionService"

    override fun startServiceActivity() {
        TODO("Not yet implemented")
    }

    override fun stopServiceActivity() {
        TODO("Not yet implemented")
    }
    //endregion
    //endregion



}