/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.datamodel.dao


import androidx.lifecycle.LiveData
import androidx.room.*
import com.bvillarroya_creations.shareyourride.datamodel.data.Inclination

/**
 * Database access object used to operate the data stored in the data base InclinationsDataBase
 */
@Dao
interface InclinationDao {

    /**
     * Insert a new Inclination into the collection
     * If the Inclination already existing, rollback and return an error
     *
     * @param inclination: the new Inclination to add
     * @return The Inclination identifier
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addInclination(inclination: Inclination): Long

    /**
     * Update an existing Inclination with the given one
     *
     * @param inclination: the new Inclination to update
     */
    @Update
    fun updateInclination(inclination: Inclination)

    /**
     * Delete the given Inclination from the database
     *
     * @param inclination: the new Inclination to delete
     */
    @Delete
    fun deleteInclination(inclination: Inclination)

    /**
     * Get the whole Inclination collection from the database
     *
     * @return: The list of Inclination
     */
    //@Query("SELECT * FROM Inclination WHERE sessionId like :sessionId and timeStamp like :timeStamp")
    @Query("SELECT * FROM Inclination WHERE sessionId LIKE :sessionId ORDER BY abs(timeStamp - :timeStamp) LIMIT 1")
    fun getInclination(sessionId: String, timeStamp: Long): Inclination

    /**
     * Get the maximum lean angle in the left side detected during session
     */
    @Query("SELECT MIN(roll) FROM Inclination WHERE sessionId like :sessionId")
    fun getMaxLeftLeanAngle(sessionId: String): Int

    /**
     * The maximum lean angle in the right side detected during session
     */
    //@Query("SELECT max(roll) FROM Inclination WHERE sessionId like :sessionId LIMIT 1")
    @Query("SELECT MAX(roll) FROM Inclination WHERE sessionId like :sessionId")
    fun getMaxRightLeanAngle(sessionId: String): Int

    /**
     * The maximum lean angle in the right side detected during session
     */
    @Query("SELECT MAX(accelerationScalar) FROM Inclination WHERE sessionId like :sessionId LIMIT 1")
    fun getMaxAcceleration(sessionId: String): Float

    @Query("SELECT accelerationDirection FROM Inclination WHERE sessionId like :sessionId AND accelerationScalar like (SELECT  MAX(accelerationScalar) FROM Inclination AS maxAcceleration)")
    fun getMaxAccelerationDirection(sessionId: String): Int
}