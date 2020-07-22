package com.bvillarroya_creations.shareyourride.telemetry.events

import com.bvillarroya_creations.shareyourride.telemetry.constants.TelemetryConstants
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData

/*
    Class to send the telemetry data through the ITelemetryObservable
    I send the data encapsulated in this message with a enum with the type because
    this method is more efficient than make a cast to determine the kind of sent data
 */
class TelemetryEvent(
    /*
        Enum with the kind of data
     */
    val EventType: TelemetryConstants.Companion.TelemetryEventType,
    /*
        Sent data
     */
    val TelemetryData: ITelemetryData)