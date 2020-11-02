package com.example.shareyourride.services.video

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTopics
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messenger.IMessageHandlerClient
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.messenger.MessageHandler
import com.example.shareyourride.services.base.ServiceBase

/*
    Stores and mange the session
 */
class VideoService: IMessageHandlerClient, ServiceBase() {

    //region message handlers
    init {
        this.createMessageHandler( "VideoService", listOf<String>(MessageTopics.SESSION_COMMANDS, MessageTopics.VIDEO_DATA))
    }

    override lateinit var messageHandler: MessageHandler

    /**
     * Listen to session messages related to the session management
     *
     * @param msg: received message from the android internal queue
     */
    override fun processMessage(msg: MessageBundle)
    {
        try {

            when (msg.messageKey)
            {
                MessageTypes.VIDEO_STATE_REQUEST ->
                {
                    Log.d(mClassName, "SYR -> received  VIDEO_STATE_REQUEST")

                }
                MessageTypes.START_ACQUIRING_DATA ->
                {
                    Log.d(mClassName, "SYR -> received  START_SESSION")

                }
                MessageTypes.STOP_ACQUIRING_DATA ->
                {
                    Log.d(mClassName, "SYR -> received  STOP_SESSION")

                }
                MessageTypes.SAVE_TELEMETRY ->
                {
                    Log.d(mClassName, "SYR -> received  SAVE_TELEMETRY")

                }
                else ->
                {
                    Log.e(mClassName, "SYR -> message ${msg.messageKey} no supported")
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to process message ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

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

    override var mClassName: String = "VideoService"

    override fun startServiceActivity() {
        TODO("Not yet implemented")
    }

    override fun stopServiceActivity() {
        TODO("Not yet implemented")
    }

    //endregion
}