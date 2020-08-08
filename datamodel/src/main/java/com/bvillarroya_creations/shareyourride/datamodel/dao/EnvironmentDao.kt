package com.bvillarroya_creations.shareyourride.datamodel.dao


import androidx.room.*
import com.bvillarroya_creations.shareyourride.datamodel.data.Environment

/**
 * Database access object used to operate the data stored in the data base EnvironmentsDataBase
 */
@Dao
interface EnvironmentDao {

    /**
     * Insert a new Environment into the collection
     * If the Environment already existing, rollback and return an error
     *
     * @param environment: the new Environment to add
     * @return The Environment identifier
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addEnvironment(environment: Environment): Long

    /**
     * Update an existing Environment with the given one
     *
     * @param environment: the new Environment to update
     */
    @Update
    fun updateEnvironment(environment: Environment)

    /**
     * Delete the given Environment from the database
     *
     * @param environment: the new Environment to delete
     */
    @Delete
    fun deleteEnvironment(environment: Environment)

    /**
     * Get the whole Environment collection from the database
     *
     * @return: The list of Environment
     */
    @Query("SELECT * FROM Environment Where sessionId like :session and timeStamp like :timeStamp")
    fun getEnvironmentList(session: Int, timeStamp: Int): List<Environment>
}