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
     * @param Body: the new Body to add
     * @return The Body identifier
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addBody(Body: Body): Long

    /**
     * Update an existing Body with the given one
     *
     * @param Body: the new Body to update
     */
    @Update
    fun updateBody(Body: Body)

    /**
     * Delete the given Body from the database
     *
     * @param Body: the new Body to delete
     */
    @Delete
    fun deleteBody(Body: Body)

    /**
     * Get the whole Body collection from the database
     *
     * @return: The list of Body
     */
    @Query("SELECT * FROM Body Where sessionId like :session and videoId like :videoFrame")
    fun getBodyList(session: Int, videoFrame: Int): List<Body>
}