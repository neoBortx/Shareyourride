package com.bvillarroya_creations.shareyourride.datamodel.dao


import androidx.room.*
import com.bvillarroya_creations.shareyourride.datamodel.data.Body

/**
 * Database access object used to operate the data stored in the data base BodysDataBase
 */
@Dao
interface BodyDao {

    /**
     * Insert a new Body into the collection
     * If the Body already existing, rollback and return an error
     *
     * @param body: the new Body to add
     * @return The Body identifier
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addBody(body: Body): Long

    /**
     * Update an existing Body with the given one
     *
     * @param body: the new Body to update
     */
    @Update
    fun updateBody(body: Body)

    /**
     * Delete the given Body from the database
     *
     * @param body: the new Body to delete
     */
    @Delete
    fun deleteBody(body: Body)

    /**
     * Get the whole Body collection from the database
     *
     * @return: The list of Body
     */
    @Query("SELECT * FROM Body Where sessionId like :session and timeStamp like :timeStamp")
    fun getBodyList(session: Int, timeStamp: Int): List<Body>
}