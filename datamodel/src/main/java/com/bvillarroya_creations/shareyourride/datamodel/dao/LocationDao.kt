/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.datamodel.dao


import androidx.room.*
import com.bvillarroya_creations.shareyourride.datamodel.data.Location

/**
 * Database access object used to operate the data stored in the data base LocationsDataBase
 */
@Dao
interface LocationDao {

    /**
     * Insert a new Location into the collection
     * If the Location already existing, rollback and return an error
     *
     * @param location: the new Location to add
     * @return The Location identifier
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addLocation(location: Location): Long

    /**
     * Update an existing Location with the given one
     *
     * @param location: the new Location to update
     */
    @Update
    fun updateLocation(location: Location)

    /**
     * Delete the given Location from the database
     *
     * @param location: the new Location to delete
     */
    @Delete
    fun deleteLocation(location: Location)

    /**
     * Get the whole Location collection from the database
     *
     * @return: The list of Location
     */
    //@Query("SELECT * FROM Location Where sessionId like :sessionId and timeStamp like :timeStamp")
    @Query("SELECT * FROM Location WHERE sessionId LIKE :sessionId ORDER BY abs(timeStamp - :timeStamp) LIMIT 1")
    fun getLocation(sessionId: String, timeStamp: Long): Location?

    //region session summary
    /**
     * Get the maximum speed of the mobile phone during the session
     */
    @Query("SELECT MAX(speed) FROM Location WHERE sessionId LIKE :sessionId")
    fun getMaxSpeed(sessionId: String): Float


    /**
     * Get the average speed of the mobile phone during the session
     */
    @Query("SELECT AVG(speed) FROM Location WHERE sessionId LIKE :sessionId")
    fun getAverageMaxSpeed(sessionId: String): Float

    /**
     * Get the total distance of the session
     */
    @Query("SELECT MAX(distance) FROM Location WHERE sessionId LIKE :sessionId")
    fun getDistance(sessionId: String): Long

    /**
     * Get the maximum altitude detected during the session
     */
    @Query("SELECT MAX(altitude) FROM Location WHERE sessionId LIKE :sessionId")
    fun getMaxAltitude(sessionId: String): Double

    /**
     * Get the minimum altitude detected during the session
     */
    @Query("SELECT MIN(altitude) FROM Location WHERE sessionId LIKE :sessionId")
    fun getMinAltitude(sessionId: String): Double

    /**
     * Get the maximum terrain inclination in Uphill
     */
    @Query("SELECT MAX(terrainInclination) FROM Location WHERE sessionId LIKE :sessionId")
    fun getMaxUphillTerrainInclination(sessionId: String): Int
    /**
     * Get the maximum terrain inclination in Downhill
     */
    @Query("SELECT MIN(terrainInclination) FROM Location WHERE sessionId LIKE :sessionId")
    fun getMaxDownhillTerrainInclination(sessionId: String): Int

    /**
     * Get the average terrain inclination
     */
    @Query("SELECT AVG(terrainInclination) FROM Location WHERE sessionId LIKE :sessionId")
    fun getAverageTerrainInclination(sessionId: String): Int
    //endregion

}