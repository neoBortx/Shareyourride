package com.example.shareyourride.services.interfaces

import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.messenger.IMessageHandlerClient

/**
 * Interface that must implement all view models that recollect and process telemetry data
 */
interface ITelemetryService: IMessageHandlerClient {

    /**
     * Flag that points if the provider is ready
     */
    var providerReady: Boolean
}