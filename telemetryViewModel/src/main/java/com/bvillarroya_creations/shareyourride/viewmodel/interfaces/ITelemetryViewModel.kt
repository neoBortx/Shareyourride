package com.bvillarroya_creations.shareyourride.viewmodel.interfaces

import com.bvillarroya_creations.shareyourride.messenger.IMessageHandlerClient

/**
 * Interface that must implement all view models that recollect and process telemetry data
 */
interface ITelemetryViewModel: IMessageHandlerClient {

    /**
        Initialize data models and observers outside the constructor
     */
    fun initializeTelemetry()

    /**
     * Stop collecting data
     */
    fun stopTelemetry()
}