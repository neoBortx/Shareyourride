package com.bvillarroya_creations.shareyourride.telemetry.interfaces

import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.telemetry.constants.TelemetryConstants
import com.bvillarroya_creations.shareyourride.telemetry.events.TelemetryEvent

interface ITelemetryManager {

    /*
        Event triggered when the data provider detects a new value for the telemetry
     */
    val telemetryChanged : MutableLiveData<TelemetryEvent>

    /*
        Points the kind of telemetry sent in the event
     */
    val telemetryEventType: TelemetryConstants.Companion.TelemetryEventType

    /*
        Start to monitoring sensors
     */
    fun startAcquiringData()

    /*
        Stop monitoring sensors
     */
    fun stopAcquiringData()

    /*
        Returns the current state of the manager
     */
    fun getManagerState(): IDataProvider.ProviderState
}