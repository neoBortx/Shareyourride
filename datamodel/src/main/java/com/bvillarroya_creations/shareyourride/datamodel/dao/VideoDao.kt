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
     * @param Video: the new Video to add
     * @return The Video identifier
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addVideo(Video: Video): Long

    /**
     * Update an existing Video with the given one
     *
     * @param Video: the new Video to update
     */
    @Update
    fun updateVideo(Video: Video)

    /**
     * Delete the given Video from the database
     *
     * @param Video: the new Video to delete
     */
    @Delete
    fun deleteVideo(Video: Video)

    /**
     * Get the whole Video collection from the database
     *
     * @return: The list of Video
     */
    @Query("SELECT * FROM Video Where sessionId like :session")
    fun getVideoList(session: Int): List<Video>

    /**
     * Get the vide frame information that belongs to the given session and time stamp
     *
     * @return: The list of Video
     */
    @Query("SELECT * FROM Video Where sessionId like :session and timeStamp like :timeStamp")
    fun getVideoFrame(session: Int, timeStamp: Long): Video
}