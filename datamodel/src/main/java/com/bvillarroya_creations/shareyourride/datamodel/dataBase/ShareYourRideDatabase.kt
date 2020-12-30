/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.datamodel.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bvillarroya_creations.shareyourride.datamodel.converters.FloatArrayToStringConverter
import com.bvillarroya_creations.shareyourride.datamodel.converters.IntArrayToStringConverter
import com.bvillarroya_creations.shareyourride.datamodel.dao.*
import com.bvillarroya_creations.shareyourride.datamodel.data.*

/**
 * Data base that stored the collection of signs (SignItem)
 * Use the Room library
 */
@Database(entities = [Session::class,
    SessionTelemetry::class,
    Video::class,
    VideoFrame::class,
    Location::class,
    Inclination::class], version = 18)
@TypeConverters(FloatArrayToStringConverter::class,IntArrayToStringConverter::class)
abstract class ShareYourRideDatabase : RoomDatabase()
{
    companion object {
        const val  DATABASE_NAME = "ShareYourRideDatabase"
    }

    abstract fun sessionDao(): SessionDao
    abstract fun sessionTelemetryDao(): SessionTelemetryDao
    abstract fun videoDao(): VideoDao
    abstract fun videoFrameDao(): VideoFrameDao
    abstract fun locationDao(): LocationDao
    abstract fun inclinationDao(): InclinationDao

}