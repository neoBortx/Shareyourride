package com.example.shareyourride.video.statemcahine.states

import com.example.shareyourride.video.statemcahine.VideoClientStateContext

/**
 * Interface that must be implemented to
 */
interface IVideoClientState {

    /**
     * The WIFI connection is established, so connect to the video server
     */
    fun connect(context: VideoClientStateContext)

    /**
     * The connection is established but the app doesn't know the delay of this camera
     */
    fun needSynchronization(context: VideoClientStateContext)

    /**
     * The control text is shown and we are waiting to the response with the
     * synchronization text
     */
    fun calculateDelay(context: VideoClientStateContext)

    /**
     * Delay is calculated, save it
     */
    fun saveDelay(context: VideoClientStateContext)

    /**
     * The user trigger the start of recording the activity
     */
    fun consumeVideo(context: VideoClientStateContext)

    /**
     * The connection is lost or the user cancel the video consumption
     */
    fun disconnect(context: VideoClientStateContext)
}