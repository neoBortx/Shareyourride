package com.example.shareyourride.services.video

import android.util.Log
import com.bvillarroya_creations.shareyourride.datamodel.data.Video
import com.bvillarroya_creations.shareyourride.datamodel.data.VideoFrame
import com.bvillarroya_creations.shareyourride.datamodel.data.VideoFrameId
import com.bvillarroya_creations.shareyourride.datamodel.dataBase.ShareYourRideRepository
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTopics
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messenger.IMessageHandlerClient
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.messenger.MessageBundleData
import com.bvillarroya_creations.shareyourride.messenger.MessageHandler
import com.example.shareyourride.common.MediaStoreUtils
import com.example.shareyourride.configuration.SettingPreferencesGetter
import com.example.shareyourride.services.base.ServiceBase
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264
import org.bytedeco.ffmpeg.global.avutil.*
import org.bytedeco.javacv.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write


/**
 * Stores and mange the video session
 */
class VideoService: IMessageHandlerClient, ServiceBase() {


    //region properties
    /**
     * In charge of access to the application settings
     */
    private var settingsGetter : SettingPreferencesGetter? = null

    /**
     * The name of the service
     */
    override var mClassName: String = "VideoService"


    //Address can be different. Check your cameras manual. :554 a standard RTSP port for cameras but it can be different
    //"rtsp://login:password@192.168.1.14:554/cam/realmonitor?channel=11&subtype=0"
    private var addressString = ""

    /**
     * In charge of manages the video stream,
     */
    private var grabber: FFmpegFrameGrabber? = null

    /**
     * In charge of writes video frames in a file
     */
    private var recorder: FFmpegFrameRecorder? = null

    /**
     * The height of the received video
     */
    private var videoHeight: Int = 0

    /**
     * The width of the received video
     */
    private var videoWidth: Int = 0

    /**
     * The format of the received video
     */
    private var videoFormat: String = ""

    /**
     * The codec used in the received video
     */
    private var videoCodec: Int = 0

    /**
     * The pixel format used in the received video
     */
    private var videoPixelFormat: Int = 0

    /**
     * The time stamp of the received video
     */
    private var videoTimeStamp: Long = 0

    /**
     * The codec used in the received video
     */
    private var videoCodecName: String = ""

    /**
     * The frame rate of the received video
     */
    private var videoFrameRate: Double = 0.0

    /**
     * The video bitrate
     */
    private var videoBitRate: Int = 0

    /**
     * The path to store the raw video of the session
     */
    private var videoRawPath : String = ""

    /**
     * Timer to send the update GPS events
     */
    private var updateVideoStateTimer: Disposable? = null

    /**
     *Flag to point if the frames of the received has to be processed or not, breaks the read boucle
     */
    private var captureVideoFlag = false

    /**
     * Points if the connection with the video is established
     */
    private var connectionEstablished = false

    /**
     * Points if the grabber is trying to access to the stream, used to avoid parallel access to the same stream
     */
    private var tryingToConnect = false

    /**
     * Class that manages the way of save files in the system
     */
    private var mediaStoreUtils: MediaStoreUtils? = null

    /**
     * The unique identifier of the session
     */
    private var sessionId = ""

    /**
     * Time stamp given by the session control. It has to be inserted in every frame in order to link them with
     * the telemetry data
     */
    private var tokenTimestamp: Long = 0

    /**
     * Lock to avoid concurrent accesses to the recorder API
     */
    private val lock = ReentrantReadWriteLock()

    /**
     * Contains the relation of the timestamp of the frame and the synchronization timestamp sent in the SAVE_TELEMETRY message
     */
    private val timeStampRelationMap: MutableMap<Long,Long> = mutableMapOf()

    /**
     * Counts the total amount of frames saved in disk for a video
     */
    private var framesCounter: Long = 0

    /**
     * Stores the infromation related to the video
     */
    private var video : Video? = null
    //endregion

    //region message handlers
    init {
        this.createMessageHandler( "VideoService", listOf(MessageTopics.SESSION_CONTROL, MessageTopics.VIDEO_DATA))
        FFmpegLogCallback.set()
        FFmpegFrameRecorder.tryLoad()
        FFmpegFrameGrabber.tryLoad()
    }

    override lateinit var messageHandler: MessageHandler

    /**
     * Listen to session messages related to the session management
     *
     * @param msg: received message from the android internal queue
     */
    override fun processMessage(msg: MessageBundle)
    {
        try {

            when (msg.messageKey)
            {
                MessageTypes.VIDEO_CONNECTION_DATA ->
                {
                    Log.d(mClassName, "SYR -> received  VIDEO_CONNECTION_DATA with data")
                    processVideoConnectionData(msg.messageData)

                }
                MessageTypes.VIDEO_STATE_REQUEST ->
                {
                    Log.d(mClassName, "SYR -> received  VIDEO_STATE_REQUEST")

                }
                MessageTypes.SAVE_TELEMETRY ->
                {
                    tokenTimestamp = msg.messageData.data as Long

                }
                MessageTypes.START_ACQUIRING_DATA ->
                {
                    Log.d(mClassName, "SYR -> received  START_ACQUIRING_DATA")
                    processStartAcquiringData(msg.messageData)
                }
                MessageTypes.STOP_ACQUIRING_DATA -> {
                    Log.d(mClassName, "SYR -> received  STOP_ACQUIRING_DATA")
                    processStopAcquiringData()
                }

                MessageTypes.VIDEO_DISCARD_COMMAND ->
                {
                    Log.d(mClassName, "SYR -> received  VIDEO_DISCARD_COMMAND")

                }
                else ->
                {
                    //Log.e(mClassName, "SYR -> message ${msg.messageKey} no supported")
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to process message ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion


    //region process messages

    /**
     * Get the full path to the directory to store the video
     * Also creates the directory if it doesn't exists
     *
     * @return The full path address of the directory
     */
    private fun getRawFileDir(): String {
        return try {
            val dir = applicationContext.getExternalFilesDir("")

            return if (dir != null) {
                dir.path +"/"+sessionId+"_raw.mp4"
            }
            else {
                ""
            }
        }
        catch (ex: Exception) {
            Log.e("VideoClient", "SYR -> Unable to get the directory to save the video $sessionId")
            ex.printStackTrace()
            ""
        }
    }

    /**
     * Compose de video address with the given data
     */
    private fun processVideoConnectionData(msgData: MessageBundleData)
    {
        try
        {
            if (msgData.type == VideoConnectionData::class)
            {
                val connectionData = msgData.data as VideoConnectionData

                val port = if (connectionData.port.isNotEmpty()) ":"+ connectionData.port else ""

                addressString = if (connectionData.userName.isEmpty() || connectionData.password.isEmpty()) {
                    connectionData.protocol + "://" + connectionData.ip + connectionData.port + "/" + connectionData.videoName
                }
                else
                {
                    connectionData.protocol + "://" + connectionData.userName + ":" + connectionData.password + "@" + connectionData.ip + port + "/" + connectionData.videoName
                }

                Log.i(mClassName, "SYR -> Composed video URL $addressString for video connection")
            }

            if (grabber != null && !grabber!!.isCloseInputStream)
            {
                GlobalScope.async{
                    grabber!!.stop()
                }
            }
         }
        catch (ex:Exception)
        {
            Log.e(mClassName, "SYR -> Unable to process video connection data due: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Save the video data into the database
     */
    private suspend fun saveVideoFileInDataBase()
    {
        try
        {
            video = Video(sessionId,
                              System.currentTimeMillis(),
                              recorder!!.imageHeight,
                              recorder!!.imageWidth,
                              recorder!!.format,
                              recorder!!.videoCodec,
                              recorder!!.frameRate,
                              recorder!!.videoBitrate,
                              videoRawPath,
                              0,
                              "")

            ShareYourRideRepository.insertVideo(video!!)
        }
        catch(ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to save video data into data due: ${ex.message}")
            ex.printStackTrace()
        }

    }

    /**
     * Send the video data into the database
     */
    private suspend fun updateVideoFileInDataBase()
    {
        try
        {
            if (video != null) {
                video = Video(
                        video!!.sessionId,
                        video!!.startTimeStamp,
                        video!!.height,
                        video!!.width,
                        video!!.format,
                        video!!.codec,
                        video!!.frameRate,
                        video!!.bitRate,
                        videoRawPath,
                        framesCounter,
                        "")

                ShareYourRideRepository.updateVideo(video!!)

            }
        }
        catch(ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to save video data into data due: ${ex.message}")
            ex.printStackTrace()
        }

    }


    /**
     * Save all frames of the session into the data base
     */
    private suspend fun saveFramesInDataBase()
    {
        try
        {
            timeStampRelationMap.forEach {
                Log.e(mClassName, "SYR ->Saving frame ${it.key}    ---------------  ${it.value}")
                val videoFrame = VideoFrame(VideoFrameId(sessionId,it.key),it.value)
                ShareYourRideRepository.insertVideoFrame(videoFrame)
            }

            timeStampRelationMap.clear()
        }
        catch(ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to save video data into data due: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Crete the frame recorder, configure it and start processing frames
     */
    private fun processStartAcquiringData(data: MessageBundleData)
    {
        try
        {
            if (data.type == String::class) {

                timeStampRelationMap.clear()
                framesCounter = 0

                sessionId = data.data as String
                Log.e(mClassName, "SYR -> Start reading video frames in a new thread for session $sessionId ")

                if (mediaStoreUtils == null) {
                    mediaStoreUtils = MediaStoreUtils(applicationContext)
                }

                videoRawPath = getRawFileDir()

                Log.e(mClassName, "SYR -> recording video $videoRawPath")

                recorder = FFmpegFrameRecorder(videoRawPath, videoWidth, videoHeight)
                recorder!!.videoCodec = AV_CODEC_ID_H264
                recorder!!.format = "matroska"
                recorder!!.isInterleaved = true
                recorder!!.pixelFormat = AV_PIX_FMT_YUV420P

                recorder!!.frameRate = videoFrameRate
                recorder!!.videoBitrate = videoBitRate
                recorder!!.timestamp = videoTimeStamp

                GlobalScope.async(Dispatchers.IO) {
                    recorder!!.start()
                    saveVideoFileInDataBase()
                    captureVideoFlag = true

                    Log.i(mClassName, "SYR -> Start reading video frames in a new thread")
                    // Blocking network request code
                    getVideoFrames()
                }
                            }
            else {
                Log.e(mClassName, "SYR -> Unable to get video because session is is null or not valid")
            }
        }
        catch (ex:Exception)
        {
            Log.e(mClassName, "SYR -> Unable to processStartAcquiringData due: ${ex.message}")
            ex.printStackTrace()

            val observable = Observable.timer(5000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe {
                Log.e(mClassName, "SYR -> Retrying connect to video")
                processStartAcquiringData(data)
            }
        }
    }

    /**
     * Close and clear recorder and grabber
     *
     * Also fill the video data with all frames data
     */
    private fun processStopAcquiringData()
    {
        captureVideoFlag = false

        try
        {
            lock.write {
                recorder?.stop()
                recorder?.release()
            }
        }
        catch (ex: java.lang.Exception)
        {
            ex.printStackTrace()
        }

        try
        {
            grabber?.stop()
        }
        catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }

        GlobalScope.async {
            updateVideoFileInDataBase()
            saveFramesInDataBase()
        }.invokeOnCompletion {
            val message = MessageBundle(MessageTypes.VIDEO_CREATION_COMMAND, sessionId, MessageTopics.VIDEO_CREATION_DATA)
            sendMessage(message)
        }

    }

    /**
     * Loop to read the incoming video stream
     */
    private fun getVideoFrames()
    {

        while(captureVideoFlag && connectionEstablished)
        {
            try
            {
                val frame = grabber!!.grabFrame()
                if (!captureVideoFlag)
                {
                    break
                }

                if (frame != null)
                {
                    Log.e(mClassName, "SYR ->Grabbed frame -------------------------- ${frame.timestamp}")
                    processVideoFrame(frame.clone())
                }
                else
                {
                    Log.d(mClassName, "SYR -> Grabbed null frame")
                    connectionEstablished = false
                }
            }
            catch (ex:Exception)
            {
                Log.e(mClassName, "SYR -> Unable execute get video frame due: ${ex.message}")
                ex.printStackTrace()
            }
        }
    }

    /**
     * Just record the given video frame with FFmpegFrameRecorder tool
     *
     * @param frame: Frame to record
     */
    private fun processVideoFrame(frame: Frame)
    {
        lock.write {
            if (recorder != null)
            {
                recorder!!.record(frame)
                framesCounter++
                Log.e(mClassName, "SYR ->Saving in map frame -------------------------- ${frame.timestamp}")
                //timeStampRelationMap.put(frame.timestamp,tokenTimestamp)
                timeStampRelationMap.put(framesCounter,tokenTimestamp)
            }
        }

    }

    /**
     * Create a RTSP client to the given video path
     * Use TCP as transport protocol to minimize the packet loss
     * In order to minimize the delay, configure fflags, avioflags, sync, probesize, preset and tune FFMPEG options
     * Then connect the video client to the server
     */
    private fun openVideoStream()
    {
        try
        {
            if (addressString.isNotEmpty())
            {
                 Log.i(mClassName, "SYR -> Trying to connect to video service $addressString")
                grabber = FFmpegFrameGrabber(addressString) // rtsp url
                grabber!!.setOption("rtsp_transport", "tcp")
                grabber!!.timeout = 5
                grabber!!.imageMode = FrameGrabber.ImageMode.COLOR

                //to speed up the video processing
                grabber!!.setOption("stimeout", "5000000")
                grabber!!.setOption("thread", "1")
                grabber!!.setOption("threads", "1")
                grabber!!.setOption("fflags", "nobuffer")
                grabber!!.setOption("avioflags ", "direct")
                grabber!!.setOption("sync", "ext")
                grabber!!.setOption("probesize", "32")
                grabber!!.setOption("preset", "superfast")
                grabber!!.setOption("tune", "zerolatency")

                //start the grabber in another thread because this is a blocking operation
                GlobalScope.async(Dispatchers.IO) {

                    tryingToConnect = true
                    grabber!!.start(false)
                    tryingToConnect = false

                    if (grabber!= null)
                    {
                        connectionEstablished = true
                        videoHeight = grabber!!.imageHeight
                        videoWidth = grabber!!.imageWidth
                        videoFormat = grabber!!.format
                        videoCodec = grabber!!.videoCodec
                        videoFrameRate = grabber!!.frameRate
                        videoBitRate = grabber!!.videoBitrate
                        videoPixelFormat = grabber!!.pixelFormat
                        videoTimeStamp = grabber!!.timestamp

                    }

                    notifyVideoState()

                    Log.d(mClassName, "SYR -> connected to video, frame rate: $videoFrameRate, height: $videoHeight," +
                            " width: $videoWidth, format: $videoFormat codec - $videoCodec pixelformat $videoPixelFormat")
                }
            }
            else
            {
                Log.i(mClassName, "SYR -> Skipping connection to video service because the address is empty")
            }
        }
        catch (e: FrameGrabber.Exception) {
            grabber!!.stop()
            grabber!!.close()
        }
        catch (ex:Exception)
        {
            Log.e(mClassName, "SYR -> Unable to connect to video stream, url: $addressString data due: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    private fun notifyVideoState()
    {
        try
        {
            val message = MessageBundle(MessageTypes.VIDEO_STATE_EVENT, connectionEstablished, MessageTopics.VIDEO_DATA)
            sendMessage(message)
        }
        catch (ex: java.lang.Exception)
        {
            Log.e(mClassName,"SYR -> Unable to notify the GPS state because ${ex.message}")
            ex.printStackTrace()
        }
    }

    //region start stop service handlers
    override fun startServiceActivity()
    {
        try {

            updateVideoStateTimer = Observable.interval(5000, 10000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe {

                if (!connectionEstablished)
                {
                    openVideoStream()
                }

                notifyVideoState()
            }

            settingsGetter = SettingPreferencesGetter(applicationContext)
        }
        catch (ex: java.lang.Exception)
        {
            Log.e(mClassName,"SYR -> Unable to create openCV controllers because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    override fun stopServiceActivity() {
        TODO("Not yet implemented")
    }
    //endregion
}