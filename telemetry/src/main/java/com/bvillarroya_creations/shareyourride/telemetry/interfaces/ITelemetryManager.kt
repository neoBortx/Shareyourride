/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.telemetry.interfaces

import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.telemetry.constants.TelemetryConstants
import com.bvillarroya_creations.shareyourride.telemetry.events.TelemetryEvent

/**
 * Common interface that all telemetry managers have to implement
 */
interface ITelemetryManager {

    /**
     * Event triggered when the data provider detects a new value for the telemetry
     */
    val telemetryChanged : MutableLiveData<TelemetryEvent>

    /**
     * Points the kind of telemetry sent in the event
     */
    val telemetryEventType: TelemetryConstants.Companion.TelemetryEventType

    /**
     * Initialize internal handlers
     */
    fun configure()

    /**
     * Start to monitoring sensors
     */
    fun startAcquiringData()

    /**
     * Stop monitoring sensors
     */
    fun stopAcquiringData()
}