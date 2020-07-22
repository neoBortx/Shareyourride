package com.bvillarroya_creations.shareyourride.viewmodel.session

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.datamodel.data.Session
import com.bvillarroya_creations.shareyourride.datamodel.dataBase.ShareYourRideRepository
import com.bvillarroya_creations.shareyourride.messenger.IMessageHandlerClient
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.viewmodel.constants.TelemetryMessageConstants
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * Stores and mange the current user session

 */
class SessionViewModel(application: Application) : AndroidViewModel(application), IMessageHandlerClient {

    //region Live data
    /**
     * The state of the current session,
     * TODO para test, borrar
     */
    val sessionState = MutableLiveData<String>()

    /**
     * The session identifier
     * TODO para test, borrar
     */
    val sessionId  = MutableLiveData<String>()
    //endregion

    //region private vars
    /**
     * Object that holds the current session data
     */
    private var mSession = Session()
    //endregion


    fun init(context: Context)
    {
        sessionState.value = ""
        sessionId.value = ""
        runBlocking {
            ShareYourRideRepository.buildDataBase(context)
        }
    }

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
        sessionState.value = "started"
        val id = UUID.randomUUID().toString()
        sessionId.value = id
        mSession = Session()
        mSession.id = id
        mSession.name = "no name"
        mSession.initTimeStamp = System.currentTimeMillis()

        runBlocking {
            ShareYourRideRepository.buildDataBase(getApplication())
            ShareYourRideRepository.insertSession (mSession)

            val message = MessageBundle(
                TelemetryMessageConstants.Companion.MessageType.StartSession.toString(),
                id)

            Log.d("SYR", "SYR -> Sending  start session message $id")

            mMessageHandler.sendMessage(message)
        }
    }

    /**
     * Stop the session with:
     * - Update the state of the session
     * -
     */
    fun stopSession()
    {
        sessionState.value = "stopped"
        mSession.endTimeStamp = System.currentTimeMillis()

        runBlocking {
            ShareYourRideRepository.updateSession(mSession)

            val message = MessageBundle(TelemetryMessageConstants.Companion.MessageType.StopSession.toString(),
                sessionId.value)
            mMessageHandler.sendMessage(message)
        }
    }

    //endregion

    //region IMessageHandlerClient implementation
    /**
     * No message required
     */
    override fun processMessage(msg: MessageBundle) {
        Log.d("SYR", "SYR -> Skipping message")
    }
    //endregion



}