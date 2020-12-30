/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.datamodel.dao


import androidx.room.*
import com.bvillarroya_creations.shareyourride.datamodel.data.VideoFrame

/**
 * Database access object used to operate the data stored in the data base VideosDataBase
 */
@Dao
interface VideoFrameDao {

    /**
     * Insert a new Video frame  into the collection
     * If the Video already existing, rollback and return an error
     *
     * @param videoFrame: the new Video frame to add
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addVideoFrame(videoFrame: VideoFrame)

    /**
     * Delete the given Video frame from the database
     *
     * @param videoFrame: the new Video to delete
     */
    @Delete
    fun deleteVideoFrame(videoFrame: VideoFrame)

    /**
     * Get the whole Video frames of the given session
     *@param sessionId: The id of the session that owns all frames to retrieve
     *
     * @return: The list of Video
     */
    @Query("SELECT * FROM VideoFrame Where sessionId like :sessionId")
    fun getVideoFrameList(sessionId: String): List<VideoFrame>
}