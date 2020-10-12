package com.example.shareyourride.video.rtsp

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.util.Log
import android.view.Surface
import java.lang.Exception

class RtspStreamReceiver: MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    /**
     *
     */
    private lateinit var mediaPlayer: MediaPlayer

    private var mediaUrl : String = ""

    private var surfaceTexture : SurfaceTexture? = null


    //region states
    /**
     * Because the media player doesn't publish its state, I have to track them manually
     *
     * https://developer.android.com/reference/android/media/MediaPlayer.html#StateDiagram
     */
    private enum class MediaState{
        None,
        End,
        Initialized,
        Preparing,
        Prepared,
        Started,
        Stopped,
    }

    private var mediaState = MediaState.None
    //endregion

    fun configureStream(url: String)
    {
        mediaUrl = url

        try {
            Log.d("RtspStreamReceived", "SYR -> configuring stream in url $url")
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                        android.media.AudioAttributes.Builder()
                            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                            .build())
            }

            mediaPlayer.setDataSource(url)

            mediaState = MediaState.Initialized
        }
        catch(ex: Exception)
        {
            Log.e("RtspStreamReceived", "SYR -> Unable to configure stream receiver${ex.message}")
            ex.printStackTrace()
        }
    }


    private fun prepareStream()
    {
        try
        {
            if (mediaState != MediaState.Initialized && mediaState != MediaState.Stopped)
            {
                Log.e("RtspStreamReceived","There is no point on preparing a stream that is state $mediaState")
               recreateStream()
            }

            Log.d("RtspStreamReceived", "SYR -> Preparing stream")
            val surface = Surface(surfaceTexture)
            mediaPlayer.setSurface(surface)
            mediaState = MediaState.Preparing
            mediaPlayer.setOnPreparedListener(this)
            mediaPlayer.setOnErrorListener(this)
            mediaPlayer.prepareAsync()
        }
        catch(ex: Exception)
        {
            Log.e("RtspStreamReceived", "SYR -> Unable to prepare stream ${ex.message}")
            ex.printStackTrace()
        }

    }

    /**
     * Start receiving playing the received video stream
     */
    fun startReceiving(texture: SurfaceTexture)
    {
        try
        {
            surfaceTexture = texture
            //mediaPlayer.setDisplay(surface)
            prepareStream()
            Log.i("RtspStreamReceived", "SYR -> Connecting to video server and receiving a stream")
        }
        catch(ex: Exception)
        {
            Log.e("RtspStreamReceived", "SYR -> ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Stop receiving the video stream and saving it
     */
    fun closeStream()
    {
        try
        {
            if (mediaState == MediaState.Preparing || mediaState == MediaState.Prepared || mediaState == MediaState.Started) {
                mediaPlayer.stop()
                mediaState = MediaState.Stopped
                Log.i("RtspStreamReceived", "SYR -> Stopping to video connection")
            }
            else
            {
                Log.e("RtspStreamReceived","There is no point in stop a stream that is state $mediaState")
            }
        }
        catch(ex: Exception)
        {
            Log.e("RtspStreamReceived", "SYR -> ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Some error has happen, so restart the video play
     */
    private fun recreateStream()
    {
        try
        {
            Log.i("RtspStreamReceived", "SYR -> Recreating Video stream")
            closeStream()
            mediaPlayer.release()
            mediaState = MediaState.End
            configureStream(mediaUrl)

        }
        catch(ex: Exception)
        {
            Log.e("RtspStreamReceived", "SYR -> ${ex.message}")
            ex.printStackTrace()
        }
    }

    override fun onPrepared(p0: MediaPlayer?) {
        try
        {
            Log.d("RtspStreamReceived", "SYR -> staring receiving stream")
            mediaState = MediaState.Prepared
            mediaPlayer.start()
            mediaState = MediaState.Started
        }
        catch(ex: Exception)
        {
            Log.e("RtspStreamReceived", "SYR -> ${ex.message}")
            ex.printStackTrace()
        }
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        try
        {
            Log.d("RtspStreamReceived", "SYR -> some error has happen with the stream lecture")
            recreateStream()
            prepareStream()
        }
        catch(ex: Exception)
        {
            Log.e("RtspStreamReceived", "SYR -> ${ex.message}")
            ex.printStackTrace()
        }

        return true
    }

}