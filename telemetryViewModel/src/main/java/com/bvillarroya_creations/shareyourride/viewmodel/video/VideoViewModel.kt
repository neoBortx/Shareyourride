package com.bvillarroya_creations.shareyourride.viewmodel.session

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/*
    Stores and mange the session
 */
class VideoViewModel: ViewModel() {

    //region Live data
    val videoFrameId = MutableLiveData<Int>()
    //endregion

    //region locks
    /*
    Lock to avoid concurrent access to the video frame id
     */
    private val videoLock = Any()
    //endregion

    fun init()
    {
        videoFrameId.value = 0
    }

    //region public functions
    /*
        Returns the current video frame identifier
     */
    fun getVideoId(): Int
    {
        synchronized(videoLock)
        {
            return  videoFrameId.value ?: 0 ;
        }
    }
    //endregion

    //region setters
    /*
        Updates the video frame identifier
        @param id: the new frame identifier
     */
    private fun setVideoFrameId(id: Int)
    {
        synchronized(videoLock)
        {
            videoFrameId.value = id
        }
    }

    //endregion
}