package com.bvillarroya_creations.shareyourride.services.session

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.datamodel.data.Session
import com.bvillarroya_creations.shareyourride.datamodel.dataBase.ShareYourRideRepository
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTopics
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messenger.IMessageHandlerClient
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.messenger.MessageHandler
import com.bvillarroya_creations.shareyourride.services.base.ServiceBase
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Stores and mange the current user session
 */
class SessionService() : ServiceBase(), IMessageHandlerClient {

    /**
     * Different states that the session can be
     */
    enum class SessionState{
        /**
         * Session is not running
         */
        Stopped,

        /**
         * Session running
         */
        Started,
    }

    //region Live data
    /**
     * The state of the current session,
     */
    val sessionState = MutableLiveData<SessionState>()

    /**
     * The session identifier
     */
    val sessionId  = MutableLiveData<String>()
    //endregion

    //region private vars
    /**
     * Object that holds the current session data
     */
    private var mSession = Session()

    /**
     * Timer to send the save telemetry messages to other services
     */
    private var saveTelemetryTimer: Disposable? = null
    //endregion

    //region public functions
    /**
     * Initialize the session with:
     * - A random unique identifier
     * - Initialize the data base in case it hasn't
     * - Insert the new session in the data base
     * - Send the rest of the view models the message to start acquiring data
     */
    fun startSession()
    {
        try {
            Log.i("SYR", "SessionService -> starting session")
            sessionState.value = SessionState.Started
            val id = UUID.randomUUID().toString()
            sessionId.value = id
            mSession = Session()
            mSession.id = id
            mSession.name = "no name"
            mSession.initTimeStamp = System.currentTimeMillis()


            //the current running thread will be blocked until this piece of code is executed
            runBlocking {
                ShareYourRideRepository.buildDataBase(getApplication())
                ShareYourRideRepository.insertSession(mSession)
            }

            sendStartSession(id)

            configurePeriodicTimer()
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to start session because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Stop the session with:
     * - Update the state of the session
     * - Send a message to all telemetry view models to stop collecting data
     * - Send a message to the video view model to stop collecting data
     */
    fun stopSession()
    {
        try
        {
            sessionState.value = SessionState.Stopped
            mSession.endTimeStamp = System.currentTimeMillis()

            runBlocking {
                ShareYourRideRepository.updateSession(mSession)
            }

            val message = MessageBundle(MessageTypes.STOP_SESSION, sessionId.value)
            sendMessage(message)

            saveTelemetryTimer?.dispose()
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to stop session because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Removes the session from the data data base.
     * If the state is running, perform the stop operation
     */
    fun discardSession()
    {
        try
        {
            if (sessionState.value != SessionState.Stopped)
            {
                stopSession()
            }
            runBlocking {
                ShareYourRideRepository.deleteSession(mSession)
            }
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to discard session because ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    //region private functions
    /**
     * Sends the start session message to all telemetry services to start
     * recollect data
     * Send the start video message to the video view model to start processing the video
     * source
     *
     * @param id: The id of the new session
     */
    private fun sendStartSession(id: String)
    {
        try
        {

            Log.d("SYR", "SYR -> Sending  start session message $id")
            val message = MessageBundle( MessageTypes.START_SESSION, id)
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
            val message = MessageBundle(MessageTypes.SAVE_TELEMETRY, System.currentTimeMillis())
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to send save telemetry message because ${ex.message}")
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
            saveTelemetryTimer = Observable.interval(1000, 5000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe {
                    sendSaveTelemetry()
                }
        }
        catch (ex: Exception)
        {
            Log.e("SessionService","SYR -> Unable to configure a periodic timer to send telemetry messages because ${ex.message}")
            ex.printStackTrace()
        }

    }

    //endregion

    //region IMessageHandlerClient implementation
    init {
        this.createMessageHandler( listOf<String>(MessageTopics.SESSION_COMMANDS))
    }

    override lateinit var messageHandler: MessageHandler

    /**
     * No message required
     */
    override fun processMessage(msg: MessageBundle) {
        Log.d("SYR", "SYR -> Skipping message")
    }

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