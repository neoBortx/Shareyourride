package com.example.shareyourride.video.statemcahine

/**
 * All transitions that changes the state of the video client
 */
enum class VideoClientTransition
{
    None,

    /**
     * Disconnected -> Connecting
     */
    Connect,

    /**
     *
     */



    /**
     * Connecting -> WaitingToControlText
     */
    NeedSynchronization,

    /**
     * WaitingToControlText -> WaitingToSyncText
     */
    CalculateDelay,

    /**
     * Client has determined the video delay but it isn't manage the stream
     */
    SaveDelay,

    ConsumeVideo,

    /**
     * All states -> Disconnect
     */
    Disconnect,
}