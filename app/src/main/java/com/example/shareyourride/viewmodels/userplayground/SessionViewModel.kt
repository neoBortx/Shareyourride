/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.viewmodels.userplayground

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.R
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTopics
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messagesdefinition.dataBundles.SessionSummaryData
import com.bvillarroya_creations.shareyourride.messenger.IMessageHandlerClient
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.messenger.MessageBundleData
import com.bvillarroya_creations.shareyourride.messenger.MessageHandler
import com.example.shareyourride.services.session.SessionData
import java.util.*

class SessionViewModel(application: Application) : AndroidViewModel(application), IMessageHandlerClient
{
    //region message handlers

    /**
     * The current state of the session
     */
    val sessionData = MutableLiveData<SessionData>()

    /**
     * The summary data of a finished session
     */
    val sessionSummaryData = MutableLiveData<SessionSummaryData>()

    /**
     * Unique UUID to identify each instance of this view model
     */
    private val guid: Int = UUID.randomUUID().toString().hashCode()

    init {
        Log.i("SessionViewModel", "SYR -> $guid: Initiating SessionViewModel")
        this.createMessageHandler( "SessionViewModel", listOf<String>(MessageTopics.SESSION_COMMANDS, MessageTopics.VIDEO_DATA))
    }

    //region public functions
    /**
     * Command to start session
     */
    fun startSession()
    {
        try
        {
            Log.i("SessionViewModel", "SYR -> $guid: Sending START_SESSION message")
            val message = MessageBundle(MessageTypes.START_SESSION,"", MessageTopics.SESSION_COMMANDS)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionViewModel", "SYR -> $guid: Unable to process start session command because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Command yo stop the session
     */
    fun cancelSession()
    {
        try
        {
            Log.i("SessionViewModel", "SYR -> $guid: Sending CANCEL_SESSION message")
            val message = MessageBundle(MessageTypes.CANCEL_SESSION,"", MessageTopics.SESSION_COMMANDS)
            sendMessage(message)
        }
        catch(ex: java.lang.Exception)
        {
            Log.e("SessionViewModel", "SYR ->$guid: Unable to send CANCEL_SESSION command because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Command to continue the session after gyroscopes calibration
     */
    fun continueSession()
    {
        try
        {
            Log.i("SessionViewModel", "SYR -> $guid: Sending CONTINUE_SESSION message")
            val message = MessageBundle(MessageTypes.CONTINUE_SESSION,"", MessageTopics.SESSION_COMMANDS)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionViewModel", "SYR -> $guid: Unable to process continue session command because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Command to restart the gyroscopes calibration procedure
     */
    fun retryCalibration()
    {
        try
        {
            val message = MessageBundle(MessageTypes.RETRY_CALIBRATION,"", MessageTopics.SESSION_COMMANDS)
            sendMessage(message)
        }
        catch(ex: java.lang.Exception)
        {
            Log.e("SessionViewModel", "SYR -> $guid: Unable to process retry calibration command because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Sends the SESSION_STATE_REQUEST message to the service to get the current session state
     */
    fun getSessionState()
    {
        Log.i("SessionViewModel", "SYR -> $guid: Sending SESSION_STATE_REQUEST message")
        val message = MessageBundle(MessageTypes.SESSION_STATE_REQUEST,"",MessageTopics.SESSION_COMMANDS)
        sendMessage(message)
    }

    /**
     * Shows a dialog to finish the activity saving or discarding the data
     */
    fun showFinishActivityDialog(context: Context)
    {
        AlertDialog.Builder(context).setTitle("Delete entry").setMessage("Do you want to save the activity?")
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(getApplication<Application>().resources.getString(R.string.save)) { _, _ ->
                Log.i("SessionFragment", "SYR -> User selects save the activity")
                sendStopSessionMessage()
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(getApplication<Application>().resources.getString(R.string.discard)){ _, _ ->
                Log.i("SessionFragment", "SYR -> User selects discard the activity")
                sendDiscardSessionMessage()
            }
            .show()
    }

    /**
     * Sends SESSION_SUMMARY_REQUEST message to get the summary data of a finished session
     */
    fun requestSummaryData()
    {
        try
        {
            Log.i("SessionViewModel", "SYR -> $guid: Sending SESSION_SUMMARY_REQUEST message")
            val message = MessageBundle(MessageTypes.SESSION_SUMMARY_REQUEST,"",MessageTopics.SESSION_COMMANDS)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionViewModel", "SYR -> $guid: Unable to send summary request message because: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion


    //region private functions
    /**
     *
     */
    private fun sendStopSessionMessage()
    {
        try
        {
            Log.i("SessionViewModel", "SYR -> $guid: Sending END_SESSION message")
            val message = MessageBundle(MessageTypes.END_SESSION,"",MessageTopics.SESSION_COMMANDS)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionViewModel", "SYR -> $guid: Unable to process stop session command because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     *
     */
    private fun sendDiscardSessionMessage()
    {
        try
        {
            Log.i("SessionViewModel", "SYR -> $guid: Sending DISCARD_SESSION message")
            val message = MessageBundle(MessageTypes.DISCARD_SESSION,"",MessageTopics.SESSION_COMMANDS)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionViewModel", "SYR -> $guid: Unable to process stop session command because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Process the session state message
     *
     * @param msg: the data of the message
     */
    private fun processSessionState(msg: MessageBundleData)
    {
        try
        {
            if (msg.type == SessionData::class)
            {
                val session = msg.data as SessionData
                Log.e("SessionViewModel", "SYR -> $guid: Processing state ${session.state}")
                sessionData.postValue(session)
            }
            else
            {
                Log.e("SessionViewModel", "SYR -> $guid: Unable to process session state event because ${msg.type} is not supported")
            }
        }
        catch (ex: Exception)
        {
            Log.e("SessionViewModel", "SYR -> $guid: Unable to process session state event because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    private fun processSessionSummaryResponse(msg: MessageBundleData)
    {
        try
        {
            if (msg.type == SessionSummaryData::class)
            {
                val sessionSummary = msg.data as SessionSummaryData
                Log.e("SessionViewModel", "SYR -> Processing summary data")
                sessionSummaryData.postValue(sessionSummary)
            }
            else
            {
                Log.e("SessionViewModel", "SYR -> $guid: Unable to process session state event because ${msg.type} is not supported")
            }
        }
        catch (ex: Exception)
        {
            Log.e("SessionViewModel", "SYR -> $guid:  Unable to process session state event because: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    //region message handler
    override lateinit var messageHandler: MessageHandler

    /**
     * Process messages from the SessionService
     *
     * @param msg: The received message
     */
    override fun processMessage(msg: MessageBundle)
    {
        try
        {
            when (msg.messageKey)
            {
                MessageTypes.SESSION_STATE_EVENT ->
                {
                    Log.d("SessionViewModel", "SYR -> $guid: received SESSION_STATE_EVENT updating event")
                    processSessionState(msg.messageData)
                }
                MessageTypes.SESSION_SUMMARY_RESPONSE ->
                {
                    Log.d("SessionViewModel", "SYR -> $guid: received SESSION_SUMMARY_RESPONSE updating data")
                    processSessionSummaryResponse(msg.messageData)
                }
                else ->
                {
                    Log.e("SessionViewModel", "SYR -> $guid: message ${msg.messageKey} no supported")
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("SessionViewModel", "SYR -> $guid: Unable to process message ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    /**
     * This method is called when the view model is destroyed
     */
    override fun onCleared()
    {
        try
        {
            Log.i("SessionViewModel", "SYR -> $guid: Clearing SessionViewModel")
            this.removeHandler()
        }
        catch (ex: Exception)
        {
            Log.e("SessionViewModel", "SYR -> $guid: Unable to process message ${ex.message}")
            ex.printStackTrace()
        }
    }
}