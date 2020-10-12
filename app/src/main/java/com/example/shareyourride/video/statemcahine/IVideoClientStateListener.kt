package com.example.shareyourride.video.statemcahine

import android.util.Log
import androidx.lifecycle.MutableLiveData

interface IVideoClientStateListener {

    /**
     * Notify changes in the state of the client
     */
    var clientState : MutableLiveData<VideoClientState>

    /**
     * Event triggered when the client connects by wifi and it want to start playing a video stream
     */
    fun onConnect()

    /**
     * Even triggered when the client has to calculate the delay of the video
     */
    fun onNeedSynchronization()

    /**
     * Event triggered when the client is shown the sync text, to calculate the delay
     */
    fun onCalculateDelay()

    /**
     * Event triggered when the client has calculated the delay
     */
    fun onSaveDelay()

    /**
     * Event triggered when the client wants to record the video
     */
    fun onConsumeVideo()

    /**
     * EVen triggered to disconnect the client
     */
    fun onDisconnect()

}