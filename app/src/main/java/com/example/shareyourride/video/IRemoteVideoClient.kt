package com.example.shareyourride.video

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shareyourride.video.statemcahine.IVideoClientStateListener
import com.example.shareyourride.video.statemcahine.VideoClientState

/**
 * Client that allow to retrieve a remote video stream
 * When the video stream is received, this client will:
 *
 *                    Get the stream
 *                          |
 * Get the timestamp of each frame applying the calculated delay
 *                          |
 *         Insert the timestamp as metadata
 *         |                              |
 * Save in disk                      Invoke the callback FrameReceiver
 *
 */
interface IRemoteVideoClient: IVideoClientStateListener
{

    /**
     * This parameter is calculated after start
     */
    var delayInMilliseconds: MutableLiveData<Long>

    /**
     * Send to the remote client the configuration
     *
     * @param config: The configuration of the client
     */
    fun configureClient(config: RemoteVideoConfiguration)

    /**
     * Updates the configuration of the client, used to change the texture view
     *
     * @param config: The configuration of the client
     */
    fun updateConfiguration(config: RemoteVideoConfiguration)

    /**
     * Connects to the video stream and start playing the video in a texture view
     */
    fun connect()

    /**
     * Start analysing the received video stream searching the control text in it
     * when the image is found, detectedControlFrameCallback is invoked with the timestamp
     * of the reception of the video
     * @param detectedControlFrameCallback: Call back function called when the control frame is detected
     */
    fun startSync(detectedControlFrameCallback: (detectionTime: Long) -> Unit)


    /**
     * Start processing the video stream
     */
    fun startConsuming()

    /**
     * Finish the connection
     */
    fun disconnect()
}