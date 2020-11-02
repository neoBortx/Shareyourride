package com.bvillarroya_creations.shareyourride.services.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bvillarroya_creations.shareyourride.datamodel.interfaces.IDataBaseTelemetry
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTopics
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.messenger.MessageHandler
import com.bvillarroya_creations.shareyourride.telemetry.events.TelemetryEvent
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryManager
import com.bvillarroya_creations.shareyourride.services.interfaces.ITelemetryViewModel

abstract class TelemetryServiceBase : ITelemetryViewModel, ServiceBase() {

    init {
        this.createMessageHandler( listOf<String>(MessageTopics.SESSION_COMMANDS))
    }

    override lateinit var messageHandler: MessageHandler

    //region protected vars
    /**
     * The identifier of the session
     * All telemetry data stored in the data base must use this identifier
     */
    protected var mSessionId: String = ""
    //endregion

    //region abstract vars
    /**
     * The data model that is going to provide telemetry data to this view model
     */
    protected abstract var mTelemetryManager: ITelemetryManager
    //endregion

    //region telemetry handler
    /**
     * Creates the observer which handle telemetryChanged
     */
    private val handleTelemetryChanged = Observer<TelemetryEvent> { event ->
        if (event != null
            && event.eventType == mTelemetryManager.telemetryEventType)
        {
            processTelemetry(event.telemetryData)
        }
    }
    //endregion

    //region message handlers
    /**
     * Listen to session messages related to the session management
     *
     * @param msg: received message from the android internal queue
     */
    override fun processMessage(msg: MessageBundle)
    {
        try {

            when (msg.messageKey)
            {
                MessageTypes.START_SESSION -> {
                    val id = msg.messageData.data as String
                    Log.d(mClassName, "SYR -> received  start session message id $id")
                    mSessionId = id
                    initializeTelemetry()
                }
                MessageTypes.STOP_SESSION -> {
                    Log.d(mClassName, "SYR -> received  start session messaged ${msg.messageData.data as String}")
                    stopTelemetry()
                }
                MessageTypes.SAVE_TELEMETRY -> {
                    val timeStamp = msg.messageData.data as Long
                    Log.d(mClassName, "SYR -> received  save telemetry, timestamp $timeStamp")
                    saveTelemetry(timeStamp)
                }
                else -> {
                    Log.w(mClassName, "SYR -> No supported message type ${msg.messageKey}")
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

    //region override ITelemetryViewModel and AndroidViewModel
    /**
     * Flag that points if the provider is ready
     */
    override lateinit var providerReady : MutableLiveData<Boolean>

    /**
     * Start acquiring telemetry data initialization the data observer and commanding
     * the providers to star using telemetry tools
     */
    private fun initializeTelemetry()
    {
        mTelemetryManager.telemetryChanged.observe(this, handleTelemetryChanged)
        mTelemetryManager.startAcquiringData()
    }

    /**
     * Stop observing telemetry
     */
    private fun stopTelemetry()
    {
        mTelemetryManager.telemetryChanged.removeObserver(handleTelemetryChanged)
        mTelemetryManager.stopAcquiringData()
    }

    //endregion

    //region ServiceBase
    override fun startServiceActivity() {
        initializeTelemetry()
    }

    override fun stopServiceActivity() {
        stopTelemetry()
    }
    //endregion

    //region abstract data and manage telemetry
    /**
     * Process the telemetry data to make easy upper layers to manage it
     *
     * @param data: The telemetry data given by the provider
     */
    protected abstract fun processTelemetry(data: ITelemetryData)

    /**
     * Saves the current telemetry in the data base
     *
     * @param timeStamp: The time stamp that will be used to index the telemetry
     * and join all different kinds of telemetry
     */
    protected abstract fun saveTelemetry(timeStamp: Long)

    /**
     * The last obtained telemetryData
     */
    protected var telemetryData: IDataBaseTelemetry? = null
    //endregion
}