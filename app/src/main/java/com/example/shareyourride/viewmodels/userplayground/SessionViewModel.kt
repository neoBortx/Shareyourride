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

class SessionViewModel(application: Application) : AndroidViewModel(application), IMessageHandlerClient
{
    //region message handlers
    init {
        this.createMessageHandler( "SessionViewModel", listOf<String>(MessageTopics.SESSION_COMMANDS))
    }

    /**
     * The current state of the session
     */
    val sessionData = MutableLiveData<SessionData>()

    val sessionSummaryData = MutableLiveData<SessionSummaryData>()

    /**
     * Command to start session
     */
    fun startSession()
    {
        try
        {
            val message = MessageBundle(MessageTypes.START_SESSION,"", MessageTopics.SESSION_COMMANDS)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionViewModel", "SYR -> Unable to process start session command because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    fun cancelSession()
    {
        try
        {
            val message = MessageBundle(MessageTypes.CANCEL_SESSION,"", MessageTopics.SESSION_COMMANDS)
            sendMessage(message)
        }
        catch(ex: java.lang.Exception)
        {
            Log.e("SessionViewModel", "SYR -> Unable to process retry calibration command because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     *
     */
    private fun sendStopSessionMessage()
    {
        try
        {
            Log.i("SessionViewModel", "SYR -> Sending END_SESSION message")
            val message = MessageBundle(MessageTypes.END_SESSION,"",MessageTopics.SESSION_COMMANDS)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionViewModel", "SYR -> Unable to process stop session command because: ${ex.message}")
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
            Log.i("SessionViewModel", "SYR -> Sending DISCARD_SESSION message")
            val message = MessageBundle(MessageTypes.DISCARD_SESSION,"",MessageTopics.SESSION_COMMANDS)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionViewModel", "SYR -> Unable to process stop session command because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     *
     */
    fun continueSession()
    {
        try
        {
            Log.i("SessionViewModel", "SYR -> Sending END_SESSION message")
            val message = MessageBundle(MessageTypes.CONTINUE_SESSION,"", MessageTopics.SESSION_COMMANDS)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionViewModel", "SYR -> Unable to process continue session command because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    fun retryCalibration()
    {
        try
        {
            val message = MessageBundle(MessageTypes.RETRY_CALIBRATION,"", MessageTopics.SESSION_COMMANDS)
            sendMessage(message)
        }
        catch(ex: java.lang.Exception)
        {
            Log.e("SessionViewModel", "SYR -> Unable to process retry calibration command because: ${ex.message}")
            ex.printStackTrace()
        }
    }

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
                    Log.d("SessionViewModel", "SYR -> received SESSION_STATE_EVENT updating event")
                    processSessionState(msg.messageData)
                }
                MessageTypes.SESSION_SUMMARY_RESPONSE ->
                {
                    Log.d("SessionViewModel", "SYR -> received SESSION_SUMMARY_RESPONSE updating data")
                    processSessionSummaryResponse(msg.messageData)
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
    //endregion


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
                Log.e("SessionViewModel", "SYR -> Processing state ${session.state}")
                sessionData.postValue(session)
            }
            else
            {
                Log.e("SessionViewModel", "SYR -> Unable to process session state event because ${msg.type} is not supported")
            }
        }
        catch (ex: Exception)
        {
            Log.e("SessionViewModel", "SYR -> Unable to process session state event because: ${ex.message}")
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
                Log.e("SessionViewModel", "SYR -> Unable to process session state event because ${msg.type} is not supported")
            }
        }
        catch (ex: Exception)
        {
            Log.e("SessionViewModel", "SYR -> Unable to process session state event because: ${ex.message}")
            ex.printStackTrace()
        }
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

    fun requestSummaryData()
    {
        try
        {
            Log.i("SessionViewModel", "SYR -> Sending SESSION_SUMMARY_REQUEST message")
            val message = MessageBundle(MessageTypes.SESSION_SUMMARY_REQUEST,"",MessageTopics.SESSION_COMMANDS)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SessionViewModel", "SYR -> Unable to send summary request message because: ${ex.message}")
            ex.printStackTrace()
        }
    }
}