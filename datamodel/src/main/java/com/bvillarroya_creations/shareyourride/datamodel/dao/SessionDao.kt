package com.bvillarroya_creations.shareyourride.datamodel.dao


import androidx.room.*
import com.bvillarroya_creations.shareyourride.datamodel.data.Session

/**
 * Database access object used to operate the data stored in the data base sessionsDataBase
 */
@Dao
interface SessionDao {

    /**
     * Insert a new session into the collection
     * If the session already existing, rollback and return an error
     *
     * @param session: the new session to add
     * @return The session identifier
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addSession(session: Session): Long

    /**
     * Update an existing session with the given one
     *
     * @param session: the new session to update
     */
    @Update
    fun updateSession(session: Session)

    /**
     * Delete the given session from the database
     *
     * @param session: the new session to delete
     */
    @Delete
    fun deleteSession(session: Session)

    /**
     * Get the whole session collection from the database
     *
     * @return: The list of session
     */
    @Query("SELECT * FROM session")
    fun getSessionList(): List<Session>

    /**
     * Get the last session on the list (the one with the newest date)
     *
     * @return: The session
     */
    @Query("SELECT * FROM session WHERE initTimeStamp IN (SELECT max(initTimeStamp) FROM session);")
    fun getLastSession(): Session?

}