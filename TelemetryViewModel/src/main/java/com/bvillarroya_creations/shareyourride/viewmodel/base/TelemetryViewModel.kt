package com.bvillarroya_creations.shareyourride.viewmodel.base

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.telemetry.events.TelemetryEvent
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryManager
import com.bvillarroya_creations.shareyourride.viewmodel.constants.TelemetryMessageConstants
import com.bvillarroya_creations.shareyourride.viewmodel.interfaces.ITelemetryViewModel
import java.util.*

/**
 * This abstract class implement all common stuff for viewodels that recollect telemetry
 * data and process them to show in the view, and store it in the data base
 */
abstract class TelemetryViewModel(application: Application): ITelemetryViewModel, AndroidViewModel(application) {

    init {
        mMessageHandler.attachHandler(UUID.randomUUID().toString())
    }

    //region protected vars
    /**
     * The identifier of the sesstion
     * All telemetry data stored in the data base must use this identifier
     */
    protected var mSessionId: String = ""

    /**
     * The identifier of the vide frame to link it to the telemetry
     * TODO hay que ver si esto es necesario, empiezo a pensar que es acoplar funcionalidades
     */
    protected var mVideoId: Long = 0
    //endregion

    //region abstract vars
    /**
        The data model that is going to provide telemetry data to this view model
     */
    protected abstract var mTelemetryManager: ITelemetryManager
    //endregion

    //region telemetry handler
    /**
     * Creates the observer which handle telemetryChanged
     */
    private val handleTelemetryChanged = Observer<TelemetryEvent> { event ->
        if (event != null
            && event.EventType == mTelemetryManager.telemetryEventType)
        {
            processTelemetry(event.TelemetryData)
        }
    }
    //endregion

    //region message handlers
    /**
     * Listen to session related messages to start the telemetry or stop it
     */
    override fun processMessage(msg: MessageBundle)
    {
        try {

            when (TelemetryMessageConstants.Companion.MessageType.valueOf(msg.messageKey))
            {
                TelemetryMessageConstants.Companion.MessageType.StartSession -> {
                    var id = msg.messageData.data as String
                    Log.d("SYR", "SYR -> received  start session message id $id")
                    initializeTelemetry()
                }
                TelemetryMessageConstants.Companion.MessageType.StopSession -> {
                    Log.d("SYR", "SYR -> received  start session messaged ${msg.messageData.data as String}")
                    stopTelemetry()
                }
                else -> {
                    Log.w("SYR", "SYR -> No supported message type $msg.messageKey")
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to process message ${ex.message}")
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
        mMessageHandler.removeHandler()
        super.onCleared()
    }
    //endregion

    //region abstract functions
    /**
     * Process the telemetry data to make easy upper layers to manage it
     * Also send the telemetry data to the view
     *
     * @param data: The telemetry data given by the provider
     */
    abstract fun processTelemetry(data: ITelemetryData)
    //endregion
}