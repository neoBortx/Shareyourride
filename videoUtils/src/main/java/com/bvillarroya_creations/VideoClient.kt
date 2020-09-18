package com.bvillarroya_creations

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File

/**
 * Class used to access to video capabilities
 */
class VideoClient(private val context: Context): IVideoClient
{

    //region private properties
    /**
     * Manager of ffmpeg libraries
     */
    private val videoManager = FfmpegManager()

    /**
     * THe full address of the video stream
     */
    private var streamAddress: String = ""

    /**
     * The identifier of the video that is going to be handled
     */
    private var videoId: String = ""

    /**
     * The format of the video
     */
    private var videoFormat: String = ""

    /**
     * The format of the video
     */
    private var videoProtocol: String = ""
    //endregion

    //region override IVideoClient
    /**
     * Initializes the server with the required data to process a video stream
     *
     * @param videoStream: The address of teh video strea,
     * @video videoId: THe identifier of the video
     */
    override fun initializeStreamClient(videoStream: String, videoId: String, videoFormat: String)
    {
        streamAddress = videoStream
        this.videoId = videoId
        this.videoFormat = videoFormat
    }

    /**
     * Save the video that is streamed by the given RTSP server and is written in a file
     * using as name the video identifier
     *
     */
    override fun saveVideoStream()
    {
        val fileDir = getFileDir()+"_"+videoId+"."+videoFormat
        val command = "$fileDir -i $streamAddress -acodec copy -vcodec copy"

        Log.i("VideoClient", "SYR -> Connection to $streamAddress and saving video into $fileDir ")

        videoManager.initializeLibrary()
        videoManager.executeFfmpegCommand(command)
    }

    /**
     * Stops a running video capturing
     */
    override fun stopVideoCapturing()
    {
        videoManager.stopFfmpegCommand()
    }

    override fun isVideoCapturing(): Boolean
    {
        return videoManager.isVideoCapturing()
    }
    //endregion

    //region private functions
    /**
     * Get the full path to the directory to store the video
     * Also creates the directory if it doesn't exists
     *
     * @return The full path address of the directory
     */
    private fun getFileDir(): String {
        return try {
            val dir = context.getExternalFilesDir("")

            return if (dir != null) {
                dir.path
            }
            else {
                ""
            }
        }
        catch (ex: Exception) {
            Log.e("VideoClient", "SYR -> Unable to get the directory to save the video $videoId")
            ex.printStackTrace()
            ""
        }
    }
    //endregion
}