package com.bvillarroya_creations.shareyourride.datamodel.dao


import androidx.room.*
import com.bvillarroya_creations.shareyourride.datamodel.data.Video

/**
 * Database access object used to operate the data stored in the data base VideosDataBase
 */
@Dao
interface VideoDao {

    /**
     * Insert a new Video into the collection
     * If the Video already existing, rollback and return an error
     *
     * @param video: the new Video to add
     * @return The Video identifier
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addVideo(video: Video): Long

    /**
     * Update an existing Video with the given one
     *
     * @param video: the new Video to update
     */
    @Update
    fun updateVideo(video: Video)

    /**
     * Delete the given Video from the database
     *
     * @param video: the new Video to delete
     */
    @Delete
    fun deleteVideo(video: Video)

    /**
     * Get the video information related to the session
     *
     * @param sessionId: The id of the session that owns all frames to retrieve
     *
     * @return: The list of Video
     */
    @Query("SELECT * FROM Video Where sessionId like :sessionId")
    fun getVideo(sessionId: String): Video?
}