package com.example.shareyourride.services.base

import android.util.Log
import androidx.lifecycle.Observer
import com.bvillarroya_creations.shareyourride.datamodel.interfaces.IDataBaseTelemetry
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.messenger.MessageHandler
import com.bvillarroya_creations.shareyourride.telemetry.events.TelemetryEvent
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryManager
import com.example.shareyourride.services.interfaces.ITelemetryViewModel

abstract class TelemetryServiceBase : ITelemetryViewModel, ServiceBase() {

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
    protected var mTelemetryManager: ITelemetryManager? = null
    //endregion

    //region telemetry handler
    /**
     * Creates the observer which handle telemetryChanged
     */
    private var handleTelemetryChanged: Observer<TelemetryEvent>? = null
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
                MessageTypes.START_ACQUIRING_DATA -> {
                    val id = msg.messageData.data as String
                    Log.d(mClassName, "SYR -> received  START_ACQUIRING_DATA message id $id")
                    mSessionId = id
                    initializeTelemetry()
                }
                MessageTypes.STOP_ACQUIRING_DATA -> {
                    Log.d(mClassName, "SYR -> received  STOP_ACQUIRING_DATA messaged ${msg.messageData.data as String}")
                    stopTelemetry()
                }
                MessageTypes.SAVE_TELEMETRY -> {
                    val timeStamp = msg.messageData.data as Long
                    saveTelemetry(timeStamp)
                }
                MessageTypes.UPDATE_TELEMETRY -> {
                    updateTelemetry()
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
    override var providerReady = false

    /**
     * Start acquiring telemetry data initialization the data observer and commanding
     * the providers to star using telemetry tools
     */
    protected fun initializeTelemetry()
    {
        if (mTelemetryManager == null)
        {
            initializeManager()
        }

        if (mTelemetryManager!= null)
        {
            handleTelemetryChanged = Observer<TelemetryEvent> { event ->
                if (event != null
                    && event.eventType == mTelemetryManager?.telemetryEventType)
                {
                    processTelemetry(event.telemetryData)
                }
            }
            mTelemetryManager?.telemetryChanged?.observe(this, handleTelemetryChanged!!)
            mTelemetryManager?.startAcquiringData()
        }
        else
        {
            Log.i(mClassName, "SYR -> Telemetry manager is not initialized yet, unable to start")
        }
    }

    /**
     * Stop observing telemetry
     */
    private fun stopTelemetry()
    {
        if (mTelemetryManager != null)
        {
            mTelemetryManager?.telemetryChanged?.removeObserver(handleTelemetryChanged!!)
            mTelemetryManager?.stopAcquiringData()
        }
        else
        {
            Log.i(mClassName, "SYR -> Telemetry manager is not initialized yet, unable to start")
        }
    }

    //endregion

    //region ServiceBase
    override fun startServiceActivity() {
        Log.i(mClassName , "Starting $mClassName")
    }

    override fun stopServiceActivity() {
        Log.i(mClassName , "Stopping $mClassName")
    }
    //endregion

    //region abstract data and manage telemetry
    /**
     * Initialize the manager
     */
    protected abstract fun initializeManager()

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
     * Sends to upper layers the current telemetry
     *
     */
    protected abstract fun updateTelemetry()

    /**
     * The last obtained telemetryData
     */
    protected var telemetryData: IDataBaseTelemetry? = null
    //endregion
}