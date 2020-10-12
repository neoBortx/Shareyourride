package com.example.shareyourride.video.statemcahine


/**
 * Different states that the client can assume
 */
enum class VideoClientState
{

    /**
     * NO supported
     */
    None,

    /**
     * Client is not connected
     */
    Disconnected,

    /**
     * Client is not connected
     */
    Connected,

    /**
     * Client prompt the control message to sync the text to start calculating the video delay
     */
    WaitingToControlText,

    /**
     * Client is waiting to the sync text response to calculate the delay
     */
    WaitingToSyncText,

    /**
     * Client has determined the video delay but it isn't manage the stream
     */
    Synchronized,

    /**
     * Client is managing the video stream
     */
    Consuming,
}