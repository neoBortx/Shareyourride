package com.bvillarroya_creations

/**
 * Interface that allow access video functionality
 */
interface IVideoClient {

    /**
     * Function that gets a video stream and save it into a local directory
     *
     */
    fun saveVideoStream()

    /**
     * Initializes the server with the required data to process a video stream
     *
     * @param videoStream: The address of teh video stream,
     * @video videoId: THe identifier of the video
     */
    fun initializeStreamClient(videoStream: String, videoId: String,videoFormat: String)

    /**
     * Stops a running video capturing
     */
    fun stopVideoCapturing()

    /**
     * Checks if the API is recording a video in this moment
     *
     * @return True is a videos is being captured at this time
     */
    fun isVideoCapturing(): Boolean

}