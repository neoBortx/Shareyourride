package com.bvillarroya_creations.shareyourride.datamodel.dao


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
     * @param Inclination: the new Inclination to add
     * @return The Inclination identifier
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addInclination(Inclination: Inclination): Long

    /**
     * Update an existing Inclination with the given one
     *
     * @param Inclination: the new Inclination to update
     */
    @Update
    fun updateInclination(Inclination: Inclination)

    /**
     * Delete the given Inclination from the database
     *
     * @param Inclination: the new Inclination to delete
     */
    @Delete
    fun deleteInclination(Inclination: Inclination)

    /**
     * Get the whole Inclination collection from the database
     *
     * @return: The list of Inclination
     */
    @Query("SELECT * FROM Inclination Where sessionId like :session and videoId like :videoFrame")
    fun getInclinationList(session: Int, videoFrame: Int): List<Inclination>
}