package com.example.shareyourride.video.rtsp

import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Build
import android.util.Log
import android.view.TextureView
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.video.IRemoteVideoClient
import com.example.shareyourride.video.RemoteVideoConfiguration
import com.example.shareyourride.video.TextDetector
import com.example.shareyourride.video.statemcahine.VideoClientState
import com.example.shareyourride.video.statemcahine.VideoClientStateContext
import com.example.shareyourride.video.statemcahine.VideoClientTransition
import io.reactivex.disposables.Disposable

/**
 * Class that handles video stream from a given URL, The video stream is going
 * to be stored in the disk. Also each frame is stored in the data base.
 * Formats supported are listed in:
 * https://developer.android.com/guide/topics/media/media-formats
 */
class RtspVideoClient(val context: Context) : IRemoteVideoClient, TextureView.SurfaceTextureListener{

    /**
     * This parameter is calculated after start
     */
    override var delayInMilliseconds: MutableLiveData<Long> = MutableLiveData()

    override var clientState: MutableLiveData<VideoClientState> = MutableLiveData()

    //region private vals
    private val syncControlText = context.getString(R.string.sync_control_text)

    private val syncDelayText = context.getString(R.string.sync_delay_text)
    //endregion

    //region private vars
    /**
     * The configurayion of the client
      */
    private lateinit var configuration: RemoteVideoConfiguration

    /**
     * The texture view that holds the video
     */
    private var textureView: TextureView? = null

    /**
     * The receiver of the stream
     */
    private val streamReceiver = RtspStreamReceiver()

    /**
     * In charge of the detect a given text in a frame
     */
    private val imageDetector = TextDetector(context, this::getSyncResult)

    /**
     * Timer that rules the period to examine the received frame in order to search the control timer
     */
    private var needSynchronizationTimer: Disposable? = null

    /**
     * Timer that rules the period to examine the received frame in order to search the sync timer
     */
    private var calculateDelayTimer: Disposable? = null

    /**
     * State machine that rules the behaviour of the video client
     */
    private var videoClientStateMachine = VideoClientStateContext()

    /**
     * The time stamp of when the test is shown
     */
    private var syncControlMilliseconds: Long = 0

    private var iswifiConnection = true
    /*
     ImageReader.OnImageAvailableListener{
    private lateinit var imageReader: ImageReader
    imageReader = ImageReader.newInstance(800, 800, ImageFormat.PRIVATE, 5)
            imageReader.setOnImageAvailableListener(this, Handler.createAsync(Looper.getMainLooper()))

                override fun onImageAvailable(p0: ImageReader?) {

    }

            */

    //endregion

    //region init
    init {
        delayInMilliseconds.value = 0
        clientState.value = VideoClientState.None
        videoClientStateMachine.setListener(this)
    }
    //endregion

    //region surface management
    override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int)
    {
        //Do nothing
    }

    /**
     * Method invoked for each frame
     */
    override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
        try {
            when(videoClientStateMachine.getCurrentState())
            {
                VideoClientState.WaitingToControlText ->
                {
                    checkImage(syncControlText,System.currentTimeMillis())
                }
                VideoClientState.WaitingToSyncText ->
                {
                    checkImage(syncDelayText,System.currentTimeMillis())
                }
                VideoClientState.Consuming ->
                {
                    val time = System.currentTimeMillis()
                    Log.d("RtspVideoClient","Client handling frame -> $time")
                }
                else ->
                {
                    //do nothing
                }
            }
        }
        catch(ex: java.lang.Exception)
        {
            Log.e("RtspVideoClient","SYR -> onSurfaceTextureUpdated unable to manage the change of image")
            ex.printStackTrace()
        }
    }

    override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
        streamReceiver.closeStream()
        return false
    }

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture?, p1: Int, p2: Int) {
        Log.d("RtspVideoClient", "SYR -> Processing texture surface available")

        videoClientStateMachine.setNextState(VideoClientTransition.Connect)
    }
    //endregion

    //region public functions
    /**
     * Send the configuration required to get and process the video stream
     *
     * @param config: The configuration
     */
    @RequiresApi(Build.VERSION_CODES.P)
    override fun configureClient(config: RemoteVideoConfiguration)
    {
        try
        {
            configuration = config
            streamReceiver.configureStream(configuration.serverUrl)
            textureView = config.textureView
            textureView?.surfaceTextureListener = this
        }
        catch(ex: Exception)
        {
            Log.e("VideoHandler", "SYR -> ${ex.message}")
            ex.printStackTrace()
        }
    }

    override fun updateConfiguration(config: RemoteVideoConfiguration) {
        TODO("Not yet implemented")
    }

    override fun connect() {
        videoClientStateMachine.setNextState(VideoClientTransition.Connect)
    }

    override fun startSync(detectedControlFrameCallback: (detectionTime: Long) -> Unit) {
        videoClientStateMachine.setNextState(VideoClientTransition.NeedSynchronization)
    }

    override fun startConsuming() {
        videoClientStateMachine.setNextState(VideoClientTransition.ConsumeVideo)
    }

    override fun disconnect() {
        videoClientStateMachine.setNextState(VideoClientTransition.Disconnect)
    }


    //endregion

    //region IVideoClientStateListener
    /**
     * The client is connected
     */
    override fun onConnect()
    {
        try
        {
            if (textureView?.surfaceTexture != null)
            {
                Log.d("RtspVideoClient","SYR -> Setting surface ${textureView?.id} in the stream receiver")
                streamReceiver.startReceiving(textureView!!.surfaceTexture)
                clientState.value = VideoClientState.Connected
            }
            else
            {
                Log.d("RtspVideoClient","SYR -> no surface available yet")
                videoClientStateMachine.setNextState(VideoClientTransition.Disconnect)
                clientState.value = VideoClientState.Disconnected
            }
        }
        catch(ex: Exception)
        {
            Log.e("VideoHandler", "SYR -> ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * The client need to be synchronized before start recording the video
     */
    override fun onNeedSynchronization()
    {
        /*try
        {
            stopTimers()
            needSynchronizationTimer = Observable.interval(0,1000L, TimeUnit.MILLISECONDS).timeInterval().subscribe {
                checkImage()
            }
        }
        catch(ex: Exception)
        {
            Log.e("RtspVideoClient", "SYR -> Unable to start synchronization procedure: ${ex.message}")
            ex.printStackTrace()
        }*/
        try
        {
            Log.i("RtspVideoClient","SYR -> Starting teh synchronization method the delay in the video")
            clientState.value = VideoClientState.WaitingToSyncText
        }
        catch(ex: Exception)
        {
            Log.e("RtspVideoClient", "SYR -> Unable to finish synchronization procedure: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * The client has to calculate the delay before start recording the video
     */
    override fun onCalculateDelay()
    {
        try
        {
            syncControlMilliseconds = System.currentTimeMillis()
            clientState.value = VideoClientState.WaitingToControlText
            Log.i("RtspVideoClient","SYR -> Calculating the delay in the video")
        }
        catch(ex: Exception)
        {
            Log.e("RtspVideoClient", "SYR -> Unable to finish synchronization procedure: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Delay calculatued
     */
    override fun onSaveDelay()
    {
        Log.i("RtspVideoClient", "SYR -> The delay with the video is ${delayInMilliseconds}")
        clientState.value = VideoClientState.Synchronized
    }

    override fun onConsumeVideo()
    {
        clientState.value = VideoClientState.Consuming
    }

    override fun onDisconnect()
    {
        try
        {
            streamReceiver.closeStream()
            clientState.value = VideoClientState.Disconnected
        }
        catch(ex: Exception)
        {
            Log.e("RtspVideoClient", "SYR -> Unable to disconnect procedure: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    //region private functions
    /**
     * Manages the response of the Text detection system
     */
    private fun getSyncResult(time: Long)
    {
        when(videoClientStateMachine.getCurrentState())
        {
            VideoClientState.WaitingToControlText ->
            {
                syncControlMilliseconds = System.currentTimeMillis()
                videoClientStateMachine.setNextState(VideoClientTransition.CalculateDelay)
            }

            VideoClientState.WaitingToSyncText ->
            {
                delayInMilliseconds.value = time - syncControlMilliseconds
                videoClientStateMachine.setNextState(VideoClientTransition.SaveDelay)
            }
            else ->
            {
                Log.e("RtspVideoClient", "SYR -> No supported state $videoClientStateMachine.getCurrentState() while getting the syn result")
            }
        }
    }

    private fun stopTimers()
    {
        if (calculateDelayTimer != null)
        {
            calculateDelayTimer!!.dispose()
            calculateDelayTimer = null
        }

        if (needSynchronizationTimer != null)
        {
            needSynchronizationTimer!!.dispose()
            needSynchronizationTimer = null
        }
    }

    /**
     * Check if the frame contains the control text
     */
    private fun checkImage(controlString: String, time: Long)
    {
        try {
            val image = textureView?.bitmap

            if (image != null)
            {
                imageDetector.detectControlTextInImage(image, controlString, time)
            }
        }
        catch (ex: java.lang.Exception)
        {
            ex.printStackTrace()
        }
    }
    //endregion

}