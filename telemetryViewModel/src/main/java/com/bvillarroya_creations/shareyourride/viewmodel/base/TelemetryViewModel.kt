package com.bvillarroya_creations.shareyourride.viewmodel.base

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import com.bvillarroya_creations.shareyourride.datamodel.interfaces.IDataBaseTelemetry
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTopics
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.messenger.MessageHandler
import com.bvillarroya_creations.shareyourride.telemetry.events.TelemetryEvent
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryManager
import com.bvillarroya_creations.shareyourride.viewmodel.interfaces.ITelemetryViewModel

/**
 * This abstract class implement all common stuff for viewodels that recollect telemetry
 * data and process them to show in the view, and store it in the data base
 */
abstract class TelemetryViewModel(application: Application): ITelemetryViewModel, AndroidViewModel(application) {

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

    /**
     * The name of the class derived from this base class, used to log
     */
    protected abstract var mClassName: String
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
     * Start acquiring telemetry data initialization the data observer and commanding
     * the providers to star using telemetry tools
     */
    override fun initializeTelemetry()
    {
        mTelemetryManager.telemetryChanged.observeForever(handleTelemetryChanged)
        mTelemetryManager.startAcquiringData()
    }

    /**
     * Stop observing telemetry
     */
    override fun stopTelemetry()
    {
        mTelemetryManager.telemetryChanged.removeObserver(handleTelemetryChanged)
        mTelemetryManager.stopAcquiringData()
    }

    /**
     * When the view model is cleared, stop observing telemetry updates and
     * command the data providers to stop using sensors and other telemetry
     * tools
     */
    override fun onCleared() {
        stopTelemetry()
        removeHandler()
        super.onCleared()
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