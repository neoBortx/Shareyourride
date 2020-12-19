package com.bvillarroya_creations.shareyourride.messagesdefinition.dataBundles

/**
 * Video creation state
 */
enum class VideoState{
    Unknown,
    Composing,
    Finished,
    Failed
}

/**
 * Holds the current state of the video creation, sent to update the video of the video creation window
 *
 * @param creationState: The current state of the video
 * @param creationPercentage: The percentage of the video conversion. If the process fails, set to 0
 */
class VideoCreationStateEvent(val creationState: VideoState, val creationPercentage: Int)