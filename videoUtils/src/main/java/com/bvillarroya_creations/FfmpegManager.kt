package com.bvillarroya_creations

import android.util.Log
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.Level


/**
 * Function to access to FFmpeg libraries
 */
class FfmpegManager {

    fun initializeLibrary()
    {
        try {
            //Enabling log
            Config.enableLogCallback({ message -> Log.d("FfmpegManager", message.text) })

            Config.enableStatisticsCallback { newStatistics -> Log.d(Config.TAG, String.format("frame: %d, time: %d", newStatistics.videoFrameNumber, newStatistics.time)) }

            Config.setLogLevel(Level.AV_LOG_DEBUG)
        }
        catch(ex: java.lang.Exception)
        {
            Log.e("FfmpegManager", "SYR -> Unable to initialize FFMPEG library: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Functions that execute the given command
     */
    fun executeFfmpegCommand(command: String)
    {
        try {

            FFmpeg.executeAsync(command) { executionId, returnCode ->
                when (returnCode){
                    RETURN_CODE_SUCCESS -> {

                        Log.i("FfmpegManager", "SYR -> FFMPEG execution id $executionId Async command execution completed successfully.")
                    }
                    RETURN_CODE_CANCEL -> {
                        Log.i("FfmpegManager", "SYR -> FFMPEG execution id $executionId Async command execution cancelled by user.")
                    }
                    else -> {
                        Log.i("FfmpegManager","SYR -> FFMPEG execution id $executionId Async command execution failed with rc=$returnCode")
                    }
                }
            }

        }
        catch (ex: java.lang.Exception)
        {
            Log.e("FfmpegManager", "SYR -> Unable to execute command  the FFMPEG is supported ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Functions that execute the given command
     */

    /**
     * Command that stops the current running FFMPEG or FFPROBE command
     */
    fun stopFfmpegCommand()
    {
        try
        {
            Log.i("FfmpegManager", "SYR -> Stopping FFMPEG or FFPROBE task")
            FFmpeg.cancel()
        }
        catch (ex: java.lang.Exception)
        {
            Log.e("FfmpegManager", "SYR -> Unable to stop the current task because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Returns true if there is any command ongoing
     */
    fun isVideoCapturing(): Boolean
    {
        return FFmpeg.listExecutions().any()
    }
}