package com.example.shareyourride.services.session

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bvillarroya_creations.shareyourride.R
import com.bvillarroya_creations.shareyourride.datamodel.data.Session
import com.bvillarroya_creations.shareyourride.datamodel.data.SessionTelemetry
import com.bvillarroya_creations.shareyourride.datamodel.dataBase.ShareYourRideRepository
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTopics
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messagesdefinition.dataBundles.InclinationCalibrationData
import com.bvillarroya_creations.shareyourride.messagesdefinition.dataBundles.SessionSummaryData
import com.bvillarroya_creations.shareyourride.messagesdefinition.dataBundles.VideoCreationStateEvent
import com.bvillarroya_creations.shareyourride.messagesdefinition.dataBundles.VideoState
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
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Stores and mange the current user session
 */
internal class SessionService() : ServiceBase(), IMessageHandlerClient {


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
    private fun generateSessionTelemetry(): SessionTelemetry
    {
        return SessionTelemetry(
                mSession.id,
                SettingPreferencesGetter(applicationContext).getBooleanOption(SettingPreferencesIds.SpeedMetric),
                SettingPreferencesGetter(applicationContext).getBooleanOption(SettingPreferencesIds.DistanceMetric),
                SettingPreferencesGetter(applicationContext).getBooleanOption(SettingPreferencesIds.GforceMetric),
                SettingPreferencesGetter(applicationContext).getBooleanOption(SettingPreferencesIds.LeanAngleMetric),
                SettingPreferencesGetter(applicationContext).getBooleanOption(SettingPreferencesIds.AltitudeMetric),
                SettingPreferencesGetter(applicationContext).getBooleanOption(SettingPreferencesIds.InclinationMetric))
    }


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

            sessionState = SessionState.SynchronizingVideo
            mSession.state = sessionState.value

            sendSessionState()

            sendStartVideoSynchronization()
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to process start session because ${ex.message}")
            ex.printStackTrace()
        }
    }

    private fun processContinueSession()
    {
        try
        {
            Log.i("SessionService", "SYR -> Resuming session")

            if (sessionState == SessionState.SynchronizingVideo)
            {
                Log.i("SessionService","SYR -> Processing continue session and showing the on session or calibrating gyroscopes sessions")
                sessionState = if (settingsGetter!!.getBooleanOption(SettingPreferencesIds.LeanAngleMetric)) SessionState.CalibratingSensors else SessionState.Started
                mSession.startTimeStamp = if (settingsGetter!!.getBooleanOption(SettingPreferencesIds.LeanAngleMetric)) 0 else System.currentTimeMillis()
                mSession.state = sessionState.value

                //the current running thread will be blocked until this piece of code is executed


                runBlocking {
                    ShareYourRideRepository.buildDataBase(application)
                    ShareYourRideRepository.insertSession(mSession)
                    ShareYourRideRepository.insertSessionTelemetry(generateSessionTelemetry())
                }

                sendSessionState()

                sendStopVideoSynchronization()

                if (!settingsGetter!!.getBooleanOption(SettingPreferencesIds.LeanAngleMetric)) {
                    startSession()
                }
                else
                {
                    startCalibratingSensors()
                }
            }
            else if (sessionState == SessionState.SensorsCalibrated)
            {
                Log.i("SessionService","SYR -> Processing continue session and showing the on session")

                sessionState = SessionState.Started
                mSession.state = sessionState.value
                mSession.startTimeStamp = System.currentTimeMillis()

                //the current running thread will be blocked until this piece of code is executed
                runBlocking {
                    ShareYourRideRepository.updateSession(mSession)
                }

                sendSessionState()
                startSession()
            }
            else {
                Log.e("SessionService", "SYR -> Unable to process continue session command because a non supported transition, current state $sessionState")
            }
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to process continue session because ${ex.message}")
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

            saveTelemetryTimer = Observable.interval(1000, 200, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe {
                sendSaveTelemetry()
            }

            updateTelemetryTimer = Observable.interval(1000, 400, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe {
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
            mSession.sensorsCalibrated = false
            mSession.referenceAzimuth = 0
            mSession.referencePitch = 0
            mSession.referenceRoll = 0
            mSession.referenceAcceleration = FloatArray(3)

            //the current running thread will be blocked until this piece of code is executed
            runBlocking {
                ShareYourRideRepository.updateSession(mSession)
            }

            sendStartCalibratingSensors()
        }
        catch (ex: java.lang.Exception)
        {
            Log.e("SessionService","SYR -> Unable to start calibrating sensors because ${ex.message}")
            ex.printStackTrace()
        }

    }

    /**
     * Stops the session because user interaction or due a failure
     *
     * @param discarded: flag that points if the current session has to be erased
     */
    private fun stopSession(discarded: Boolean)
    {
        try
        {
            sendStopAcquiringData(discarded)

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

            stopSession(false)

            Log.e("SessionService","SYR -> stopping session ${mSession.id}")

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

                stopSession(true)

                sendVideoDiscardCommand()

                sendSessionState()

                runBlocking {
                    ShareYourRideRepository.deleteSession(mSession)
                }
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

    /**
     * The session has been cancelled by the user. Its state transition happens when the user press back when is calibrating sensors or synchronizing
     * the video. Also happens when the connection with the camera is lost in some states.
     *
     * Depending on the current state this procedure includes to stop
     */
    private fun cancelSession()
    {
        try
        {
            sessionState = SessionState.Stopped
            mSession.state = SessionState.Stopped.value

            stopSession(true)

            runBlocking {
                ShareYourRideRepository.deleteSession(mSession)
            }

            sendSessionState()
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to discard session because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     *
     */
    private fun processCancelSession()
    {
        //Every funtion here is surrounded with a try catch

        when (sessionState) {
            SessionState.SynchronizingVideo -> {
                sendStopVideoSynchronization()
            }
            SessionState.CalibratingSensors -> {
                sendStopCalibratingSensors()
            }
            else -> {
                //do nothing
            }
        }

        cancelSession()
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
                    mSession.referenceAcceleration = calibration.linealAcceleration
                }
                else
                {
                    Log.e("SessionService", "SYR -> Unable to process data in Inclination Calibration message")
                    mSession.sensorsCalibrated = false
                    mSession.referenceAzimuth = 0
                    mSession.referencePitch = 0
                    mSession.referenceRoll = 0
                    mSession.referenceAcceleration = FloatArray(3)
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

    private fun processSessionSummaryRequest()
    {
        try
        {
            // Job and Dispatcher are combined into a CoroutineContext which
            // will be discussed shortly
            val scope = CoroutineScope(Job() + Dispatchers.IO)

            val sessionSummary = SessionSummaryData()

            scope.launch {
                sessionSummary.duration = mSession.endTimeStamp - mSession.startTimeStamp
                sessionSummary.maxSpeed = ShareYourRideRepository.getMaxSpeed(mSession.id)
                sessionSummary.averageSpeed = ShareYourRideRepository.getAverageMaxSpeed(mSession.id)
                sessionSummary.distance = ShareYourRideRepository.getDistance(mSession.id)
                sessionSummary.maxAcceleration = ShareYourRideRepository.getMaxAcceleration(mSession.id)
                sessionSummary.maxLeftLeanAngle = ShareYourRideRepository.getMaxLeftLeanAngle(mSession.id)
                sessionSummary.maxRightLeanAngle = ShareYourRideRepository.getMaxRightLeanAngle(mSession.id)
                sessionSummary.maxAltitude = ShareYourRideRepository.getMaxAltitude(mSession.id)
                sessionSummary.minAltitude = ShareYourRideRepository.getMinAltitude(mSession.id)
                sessionSummary.maxUphillTerrainInclination = ShareYourRideRepository.getMaxUphillTerrainInclination(mSession.id)
                sessionSummary.maxDownhillTerrainInclination = ShareYourRideRepository.getMaxDownhillTerrainInclination(mSession.id)
                sessionSummary.averageTerrainInclination = ShareYourRideRepository.getAverageTerrainInclination(mSession.id)

                withContext(Dispatchers.Main) {
                    Log.d("SessionService", "SYR -> Sending  SESSION_SUMMARY_RESPONSE session message ${mSession.id}")
                    val message = MessageBundle( MessageTypes.SESSION_SUMMARY_RESPONSE, sessionSummary, MessageTopics.SESSION_COMMANDS)
                    sendMessage(message)
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to process session summary request because ${ex.message}")
            ex.printStackTrace()
        }
    }

    private fun processVideoCreationStateEvent(msgData: MessageBundleData)
    {
        try
        {
            if (msgData.type == VideoCreationStateEvent::class)
            {
                val state = msgData.data as VideoCreationStateEvent
                if (state.creationState == VideoState.Finished || state.creationState == VideoState.Failed)
                {
                    sessionState   = SessionState.Finished
                    mSession.state = SessionState.Finished.value

                    sendSessionState()
                }
            }
            else
            {
                Log.e("SessionService","SYR -> Unable to process video creation state because received data is not supported")
            }
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to process video creation state event because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Process VIDEO_STATE_EVENT message.
     *
     * If the state is disconnected (false value), depending on the current session state the state machine of the
     * session will perform different actions:
     */
    private fun processVideoState(msg: MessageBundleData)
    {
        try
        {
            if (msg.data is Boolean)
            {
                val wifiState = msg.data as Boolean

                if (!wifiState)
                {
                    when(sessionState)
                    {
                        SessionState.Unknown ->
                        {
                            //do nothing
                            Log.i(mClassName, "SYR -> The video connection has been lost in state ${sessionState}, ignoring event")
                        }
                        SessionState.Stopped ->
                        {
                            //do nothing
                            Log.i(mClassName, "SYR -> The video connection has been lost in state ${sessionState}, ignoring event")
                        }
                        SessionState.SynchronizingVideo -> {
                            //cancel. discard session, stop synchronizing video
                            Log.i(mClassName, "SYR -> The video connection has been lost in state ${sessionState}, canceling session")
                            processCancelSession()
                            printConnetionLostToast()
                        }
                        SessionState.CalibratingSensors ->
                        {
                            //cancel, discard session, stop calibrating sensors
                            Log.i(mClassName, "SYR -> The video connection has been lost in state ${sessionState}, canceling session")
                            processCancelSession()
                            printConnetionLostToast()
                        }
                        SessionState.SensorsCalibrated ->
                        {
                            //cancel and discard session
                            Log.i(mClassName, "SYR -> The video connection has been lost in state ${sessionState}, canceling session")
                            cancelSession()
                            printConnetionLostToast()
                        }
                        SessionState.Started ->
                        {
                            //stop and save session
                            Log.i(mClassName, "SYR -> The video connection has been lost in state ${sessionState}, finishing and saving session")
                            processStopSession()
                            printConnetionLostToast()
                        }
                        SessionState.CreatingVideo ->
                        {
                            //do nothing
                            Log.i(mClassName, "SYR -> The video connection has been lost in state ${sessionState}, ignoring event")
                        }
                        SessionState.Finished ->
                        {
                            //do nothing
                            Log.i(mClassName, "SYR -> The video connection has been lost in state ${sessionState}, ignoring event")
                        }
                    }
                }

            }
        }
        catch (ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to process processVideoState state event because: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    //region IMessageHandlerClient implementation
    init {
        this.createMessageHandler( "SessionService", listOf(MessageTopics.SESSION_COMMANDS, MessageTopics.INCLINATION_CONTROL, MessageTopics.VIDEO_CREATION_DATA, MessageTopics.VIDEO_DATA))
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
                    Log.d("SessionService", "SYR -> received START_SESSION updating event")
                    processStartSessionCommand()
                }
                MessageTypes.END_SESSION ->
                {
                    Log.d("SessionService", "SYR -> received END_SESSION updating event")
                    processStopSession()
                }
                MessageTypes.CONTINUE_SESSION ->
                {
                    Log.d("SessionService", "SYR -> received CONTINUE_SESSION updating event")
                    processContinueSession()
                }
                MessageTypes.RETRY_CALIBRATION ->
                {
                    Log.d("SessionService", "SYR -> received RETRY_CALIBRATION updating event")
                    processRetryCalibration()
                }
                MessageTypes.SESSION_STATE_REQUEST ->
                {
                    Log.d("SessionService", "SYR -> received SESSION_STATE_REQUEST updating event")
                    sendSessionState()
                }
                MessageTypes.DISCARD_SESSION ->
                {
                    Log.d("SessionService", "SYR -> received DISCARD_SESSION updating event")
                    processDiscardSession()
                }
                MessageTypes.CANCEL_SESSION ->
                {
                    Log.d("SessionService", "SYR -> received CANCEL_SESSION updating event")
                    processCancelSession()
                }
                MessageTypes.INCLINATION_CALIBRATION_END ->
                {
                    Log.d("SessionService", "SYR -> received INCLINATION_CALIBRATION_END updating event")
                    processInclinationCalibrationEnd(msg.messageData)
                }
                MessageTypes.SESSION_SUMMARY_REQUEST ->
                {
                    Log.d("SessionService", "SYR -> received SESSION_SUMMARY_REQUEST updating event")
                    processSessionSummaryRequest()
                }
                MessageTypes.VIDEO_CREATION_STATE_EVENT ->
                {
                    Log.d("SessionService", "SYR -> received VIDEO_CREATION_STATE_EVENT updating event")
                    processVideoCreationStateEvent(msg.messageData)
                }
                MessageTypes.VIDEO_STATE_EVENT ->
                {
                    Log.d("VideoViewModel", "SYR -> received VIDEO_STATE_EVENT updating state")
                    processVideoState(msg.messageData)
                }
                else ->
                {
                    Log.e("SessionService", "SYR -> message ${msg.messageKey} no supported")
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("SessionService", "SYR -> Unable to process message ${ex.message}")
            ex.printStackTrace()
        }
    }

    //region toast
    /**
     * Shows a toast message to notify the user that the connection with the camera have been lost
     */
    private fun printConnetionLostToast()
    {
        try
        {
            lifecycleScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, getString(R.string.connection_lost), Toast.LENGTH_LONG).show()
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to show toast message because ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

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
     *
     * @param discarded: flag that points if the current session has to be erased
     */
    private fun sendStopAcquiringData(discarded: Boolean)
    {
        try
        {
            Log.d("SessionService", "SYR -> Sending  stop session message ${mSession.id}, discarded ${discarded}")
            val message = MessageBundle( MessageTypes.STOP_ACQUIRING_DATA, discarded, MessageTopics.SESSION_CONTROL)
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
     * Command the inclination service to start calibrating sensors
     */
    private fun sendStartCalibratingSensors()
    {
        try
        {
            Log.d("SessionService", "SYR -> Sending  INCLINATION_CALIBRATION_START")
            val message = MessageBundle( MessageTypes.INCLINATION_CALIBRATION_START, mSession.id, MessageTopics.INCLINATION_CONTROL)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to send INCLINATION_CALIBRATION_START because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Command the inclination service to stop calibrating sensors
     */
    private fun sendStopCalibratingSensors()
    {
        try
        {
            Log.d("SessionService", "SYR -> Sending  INCLINATION_CALIBRATION_STOP")
            val message = MessageBundle( MessageTypes.INCLINATION_CALIBRATION_STOP, mSession.id, MessageTopics.INCLINATION_CONTROL)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to send sINCLINATION_CALIBRATION_STOP message because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Send the session state to upper layers
     */
    private fun sendStartVideoSynchronization()
    {
        try
        {
            Log.d("SessionService", "SYR -> Sending  VIDEO_SYNCHRONIZATION_COMMAND")
            val message = MessageBundle( MessageTypes.VIDEO_SYNCHRONIZATION_COMMAND, mSession.id, MessageTopics.VIDEO_DATA)
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
    private fun sendStopVideoSynchronization()
    {
        try
        {
            Log.d("SessionService", "SYR -> Sending  VIDEO_SYNCHRONIZATION_END_COMMAND")
            val message = MessageBundle( MessageTypes.VIDEO_SYNCHRONIZATION_END_COMMAND, mSession.id, MessageTopics.VIDEO_DATA)
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
        Log.i("SessionService" , "Starting Session service")
    }

    override fun stopServiceActivity() {
        Log.i("SessionService" , "Stopping Session service")
    }
    //endregion
    //endregion



}