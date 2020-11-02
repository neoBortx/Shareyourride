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
    Location::class,
    Environment::class,
    Body::class,
    Inclination::class], version = 14)
@TypeConverters(FloatArrayToStringConverter::class,IntArrayToStringConverter::class)
abstract class ShareYourRideDatabase : RoomDatabase()
{
    companion object {
        const val  DATABASE_NAME = "ShareYourRideDatabase"
    }

    abstract fun sessionDao(): SessionDao
    abstract fun sessionTelemetryDao(): SessionTelemetryDao
    abstract fun videoDao(): VideoDao
    abstract fun locationDao(): LocationDao
    abstract fun environmentDao(): EnvironmentDao
    abstract fun bodyDao(): BodyDao
    abstract fun inclinationDao(): InclinationDao

}