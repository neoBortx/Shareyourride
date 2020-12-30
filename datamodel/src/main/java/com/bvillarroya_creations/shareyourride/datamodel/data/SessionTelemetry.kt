/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Stores the telemetry configured in the session
 */

@Entity(tableName = "SessionTelemetry",
        foreignKeys = [
            ForeignKey(entity = Session::class,
                       parentColumns = ["id"],
                       childColumns = ["sessionId"],
                       onDelete = ForeignKey.CASCADE)]
)
data class SessionTelemetry(

    /**
     * The session that owns this configuration
     */
    @PrimaryKey
    var sessionId: String,

    /**
     * The speed of the phone
     */
    val speed :Boolean = false,

    /**
     * The distance of the session
     */
    val distance :Boolean = false,

    /**
     * The acceleration of the phone
     */
    val acceleration: Boolean = false,

    /**
     * The horizontal inclination of the phone
     */
    val leanAngle: Boolean = false,

    /**
     * The altitude of the phone
     */
    val altitude: Boolean = false,

    /**
     * The difference of altitude between points in degree
     */
    val terrainInclination: Boolean = false

)