package com.bvillarroya_creations.shareyourride.datamodel.interfaces

import com.bvillarroya_creations.shareyourride.datamodel.data.TelemetryId

/**
 * Interface that has to be implemented by all data base entities
 */
interface IDataBaseTelemetry {

    /**
     * Row unique identifier
     * Contains the session id and the frame id
     */
    val id: TelemetryId
}