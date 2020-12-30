/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.datamodel.dao


import androidx.room.*
import com.bvillarroya_creations.shareyourride.datamodel.data.Session
import com.bvillarroya_creations.shareyourride.datamodel.data.SessionTelemetry

/**
 * Database access object used to operate the data stored in the data base sessionsTelemetryDataBase
 */
@Dao
interface SessionTelemetryDao {

    /**
     * Insert a new telemetry configuration
     * If the session telemetry already existing, rollback and return an error
     *
     * @param sessionTelemetry: the new telemetry configuration
     * @return The session telemetry identifier
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSessionTelemetry(sessionTelemetry: SessionTelemetry): Long

    /**
     * Update an existing session telemetry with the given one
     *
     * @param sessionTelemetry:  the new telemetry configuration to update
     */
    @Update
    fun updateSessionTelemetry(sessionTelemetry: SessionTelemetry)

    /**
     * Delete the given session telemetry from the database
     *
     * @param sessionTelemetry: the session telemetry to delete
     */
    @Delete
    fun deleteSessionTelemetry(sessionTelemetry: SessionTelemetry)

    /**
     * Get the telemetry configured for the given session
     *
     * @return: The configured session telemetry
     */
    @Query("SELECT * FROM SessionTelemetry where sessionId like :session")
    fun getSessionTelemetry(session: String): SessionTelemetry

}