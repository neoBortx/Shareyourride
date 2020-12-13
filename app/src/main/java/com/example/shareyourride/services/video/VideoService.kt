package com.example.shareyourride.services.video

import android.annotation.SuppressLint
import android.os.Process
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
import com.example.shareyourride.configuration.SettingPreferencesGetter
import com.example.shareyourride.services.base.ServiceBase
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264
import org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_RGBA
import org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P
import org.bytedeco.javacv.*
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock


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
     *Flag to point if the frames of the received have to be processed or not
     */
    private var captureVideoFlag = false

    /**
      * Flag to point if the frames of the received stream have to be sent to upper layers to be shown
     */
    private var synchronizeVideoFlag = false

    /**
     * Points if the connection with the video is established
     */
    private var connectionEstablished = false

    /**
     * Points if the grabber is trying to access to the stream, used to avoid parallel access to the same stream
     */
    private var tryingToConnect = false

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
    private val recorderLock: ReentrantLock = ReentrantLock()

    /**
     * Contains the relation of the timestamp of the frame and the synchronization timestamp sent in the SAVE_TELEMETRY message
     */
    private val timeStampRelationMap: MutableMap<Long,Long> = mutableMapOf()

    /**
     * Counts the total amount of frames saved in disk for a video
     */
    private var framesCounter: Long = 0

    /**
     * Count the total amount of frames that can't be processed
     */
    private var failedFrames: Int = 0

    /**
     * Stores the information related to the video
     */
    private var video : Video? = null

    private var bitmapConverter = AndroidFrameConverter()

    /**
     * The delay configured by the user, it means that all frames have this average delay respect to the received telemetry data
     */
    private var videoDelay: Int =0
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
                    notifyVideoState()

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
                    processDiscardVideo()
                }

                MessageTypes.VIDEO_SYNCHRONIZATION_COMMAND ->
                {
                    Log.d(mClassName, "SYR -> received  VIDEO_SYNCHRONIZATION_COMMAND")
                    processStartSynchronization()
                }
                MessageTypes.VIDEO_SYNCHRONIZATION_END_COMMAND ->
                {
                    Log.d(mClassName, "SYR -> received  VIDEO_SYNCHRONIZATION_COMMAND")
                    processEndSynchronization()
                }
                MessageTypes.CONFIGURE_VIDEO_DELAY -> {
                    val delay = msg.messageData.data as Int
                    Log.d(mClassName, "SYR -> received  CONFIGURE_TELEMETRY_DELAY  with delay $delay")
                    configureDelay(delay)
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
                    connectionData.protocol + "://" + connectionData.ip + port + "/" + connectionData.videoName
                }
                else
                {
                    connectionData.protocol + "://" + connectionData.userName + ":" + connectionData.password + "@" + connectionData.ip + port + "/" + connectionData.videoName
                }

                Log.i(mClassName, "SYR -> Composed video URL $addressString for video connection")
            }

            if (grabber != null)
            {
                Log.i(mClassName, "SYR -> Removing old instance of grabber")
                startConnectionTimer()
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
                          grabber!!.imageHeight,
                          grabber!!.imageWidth,
                          grabber!!.format,
                          grabber!!.videoCodec,
                          grabber!!.frameRate,
                          grabber!!.videoBitrate,
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
            Log.i(mClassName, "SYR ->Saving frames ${timeStampRelationMap.count()} in disk")
            timeStampRelationMap.forEach {
                try
                {
                    val videoFrame = VideoFrame(VideoFrameId(sessionId,it.key),it.value)
                    ShareYourRideRepository.insertVideoFrame(videoFrame)
                    Log.i(mClassName, "SYR ->Saving frame ${videoFrame.id.frameTimeStamp} ----------------- ${videoFrame.syncTimeStamp}")
                }
                catch(ex: Exception)
                {
                    Log.e(mClassName, "SYR -> Unable to record frame: ${ex.message}")
                    ex.printStackTrace()
                }
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
    @SuppressLint("CheckResult")
    private fun processStartAcquiringData(data: MessageBundleData)
    {
        try
        {
            synchronizeVideoFlag = false

            if (data.type == String::class)
            {
                timeStampRelationMap.clear()
                framesCounter = 0
                failedFrames = 0

                sessionId = data.data as String
                Log.d(mClassName, "SYR -> Start reading video frames in a new thread for session $sessionId ")

                videoRawPath = getRawFileDir()

                Log.i(mClassName, "SYR -> recording video $videoRawPath")

                recorderLock.lock()

                recorder = FFmpegFrameRecorder(videoRawPath, videoWidth, videoHeight)
                recorder!!.videoCodec = AV_CODEC_ID_H264
                recorder!!.format = "matroska"
                recorder!!.isInterleaved = true
                recorder!!.pixelFormat = AV_PIX_FMT_YUV420P

                recorder!!.frameRate = videoFrameRate
                recorder!!.videoBitrate = videoBitRate
                recorder!!.timestamp = grabber!!.timestamp

                recorder!!.start()

                recorderLock.unlock()

                GlobalScope.async(Dispatchers.IO) {
                    saveVideoFileInDataBase()
                }

                captureVideoFlag = true
            }
            else
            {
                Log.e(mClassName, "SYR -> Unable to get video because session is is null or not valid")
            }
        }
        catch (ex:Exception)
        {
            recorderLock.unlock()

            Log.e(mClassName, "SYR -> Unable to processStartAcquiringData due: ${ex.message}")
            ex.printStackTrace()

            Observable.timer(5000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe {
                Log.e(mClassName, "SYR -> Retrying connect to video")
                closeInputStream()
                closeOutputStream()
                processStartAcquiringData(data)
            }
        }
    }

    /**
     * Start to send received frames to other layers in order to be shown
     */
    private fun processStartSynchronization()
    {
        try
        {
            synchronizeVideoFlag = true
        }
        catch(ex: Exception)
        {

            ex.printStackTrace()
        }
    }

    /**
     * Stop sending received frames to other layers
     */
    private fun processEndSynchronization()
    {
        try
        {
            synchronizeVideoFlag = false
        }
        catch(ex: Exception)
        {

            ex.printStackTrace()
        }
    }

    /**
     * Configures a delay in milliseconds to apply in the video association data with the telemetry, this delay provoke that
     * received frames are associated to previous telemetry data, because the TCP connection will add some delay to the connection
     *
     * @param delay: In milliseconds, delay to apply to each video frame to associate them to older telemetry data
     */
    private fun configureDelay(delay: Int)
    {
        try {
            videoDelay = delay
        }
        catch (ex: java.lang.Exception)
        {
            Log.e(mClassName, "SYR -> Unable to configure the telemetry delay because: ${ex.message}")
            ex.printStackTrace()
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
        connectionEstablished = false
        try
        {
            closeOutputStream()
        }
        catch (ex: java.lang.Exception)
        {
            ex.printStackTrace()
        }

        try
        {
            closeInputStream()
        }
        catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }


        GlobalScope.async(Dispatchers.IO)
        {
            updateVideoFileInDataBase()
            saveFramesInDataBase()
            Log.i(mClassName, "SYR -> Sending message VIDEO_CREATION_COMMAND")
            val message = MessageBundle(MessageTypes.VIDEO_CREATION_COMMAND, sessionId, MessageTopics.VIDEO_CREATION_DATA)
            sendMessage(message)
        }

        startConnectionTimer()
    }

    /**
     * Stop acquiring data and remove the file
     * - Close recorder
     * - close de grabber
     * - Delete the file
     * - Start again the connection process
     */
    private fun processDiscardVideo()
    {
        captureVideoFlag = false
        connectionEstablished = false
        try
        {
            closeOutputStream()
        }
        catch (ex: java.lang.Exception)
        {
            Log.e(mClassName,"SYR -> Unable to close the recorder due : ${ex.message}")
            ex.printStackTrace()
        }

        try
        {
            closeInputStream()
        }
        catch (ex: java.lang.Exception)
        {
            Log.e(mClassName,"SYR -> Unable to close the grabber due : ${ex.message}")
            ex.printStackTrace()
        }

        try
        {
            Log.i(mClassName,"SYR -> Deleting video file ${getRawFileDir()}")
            val file = File(getRawFileDir())
            file.delete()
        }
        catch (ex: java.lang.Exception)
        {
            Log.e(mClassName,"SYR -> Unable to delete the video of the session ${ex.message}")
            ex.printStackTrace()
        }


        startConnectionTimer()

    }

    /**
     * Loop to read the incoming video stream
     */
    private fun getVideoFrames()
    {

        while(connectionEstablished && grabber != null && !grabber!!.isCloseInputStream)
        {
            try
            {
                val frame : Frame? = grabber!!.grabImage()
                if (frame != null)
                {
                    //for debuging, uncoment
                    //Log.d(mClassName, "SYR ->Grabbed frame ${frame.timestamp} -------------------------- ${Process.myTid()}")
                    if (synchronizeVideoFlag)
                    {
                        sendVideoFrame(frame)
                    }
                    else
                    {
                        processVideoFrame(frame)
                    }
                }
                else
                {
                    Log.e(mClassName, "SYR -> Grabbed null frame ------ ${Process.myTid()} is closed ${grabber!!.isCloseInputStream} has video ${grabber!!.hasVideo()}")
                    failedFrames++
                    Thread.sleep(20)
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
     * Just send the given video frame to the view model
     *
     * @param frame: Frame to record
     */
    private fun sendVideoFrame(frame: Frame)
    {
        Log.d(mClassName, "SYR ->Sending frame ${frame.timestamp} ------------------------- $tokenTimestamp  frames ${timeStampRelationMap.count()}")
        val message = MessageBundle(MessageTypes.VIDEO_FRAME_SYNCHRONIZATION_DATA, bitmapConverter.convert(frame), MessageTopics.VIDEO_SYNCHRONIZATION_DATA)
        sendMessage(message)
    }

    /**
     * Just record the given video frame with FFmpegFrameRecorder tool
     *
     * @param frame: Frame to record
     */
    private fun processVideoFrame(frame: Frame)
    {
        if (recorder != null && captureVideoFlag)
        {
            //synchronize the first processed frame with start of the video
            if (framesCounter == 0L)
            {
                recorder!!.timestamp = frame.timestamp
            }

            try {
                //Uncomment for developing purposes
                //Log.d(mClassName, "SYR ->Processing frame ${frame.timestamp} ------------------------- $tokenTimestamp  frames ${timeStampRelationMap.count()}")
                synchronized(recorderLock) {
                    recorder!!.record(frame)
                }
                timeStampRelationMap[framesCounter] = tokenTimestamp
                framesCounter++
            }
            catch(ex: OutOfMemoryError)
            {
                ex.printStackTrace()
            }
            catch(ex: Exception)
            {
                ex.printStackTrace()
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

                grabber = FFmpegFrameGrabber(addressString)
                grabber!!.setOption("rtsp_transport", "tcp")
                grabber!!.timeout = 5000
                grabber!!.imageMode = FrameGrabber.ImageMode.COLOR
                //As is explained in https://github.com/bytedeco/javacv/issues/214,
                //use RGBA format to increase the performance of bitmap management
                grabber!!.pixelFormat = AV_PIX_FMT_RGBA

                grabber!!.setOption("fflags", "nobuffer")
                grabber!!.setOption("flags", "low_delay")
                grabber!!.setOption("flags", "discardcorrupt")
                grabber!!.setOption("avioflags ", "direct")
                grabber!!.setOption("probesize", "100")
                grabber!!.setOption("preset", "superfast")
                grabber!!.setOption("tune", "zerolatency")

                tryingToConnect = true
                grabber!!.start(true)
                tryingToConnect = false

                if (grabber != null) {
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

                Log.i(mClassName, "SYR -> connected to video, frame rate: $videoFrameRate, height: $videoHeight width: $videoWidth, format: $videoFormat codec - $videoCodec pixelformat $videoPixelFormat")

                getVideoFrames()

            }
            else
            {
                Log.i(mClassName, "SYR -> Skipping connection to video service because the address is empty")
            }
        }
        catch (ex:Exception)
        {
            Log.e(mClassName, "SYR -> Unable to connect to video stream, url: $addressString due: ${ex.message}")
            ex.printStackTrace()
            startConnectionTimer()
        }
    }

    /**
     * Closes de current connection and starts a new one
     */
    private fun closeInputStream()
    {
        try
        {
            Log.d(mClassName, "SYR -> Closing grabber to url: $addressString closed")
            if (grabber != null) {
                Log.d(mClassName, "SYR -> calling releaseUnsafe")
                grabber!!.stop()
                Log.d(mClassName, "SYR -> executed!")
                grabber = null
            }
            Log.i(mClassName, "SYR -> Grabber to url: $addressString closed")
        }
        catch(ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to close grabber connection due: ${ex.message}")
            ex.printStackTrace()
        }
    }

    private fun closeOutputStream()
    {
        try
        {
            Log.d(mClassName, "SYR -> Closing recorder to url: $addressString closed")

            recorderLock.lock()

            if (recorder != null) {
                recorder!!.close()
                recorder = null
            }

            recorderLock.unlock()

            Log.i(mClassName, "SYR -> recorder of url: $addressString closed")
            connectionEstablished = false
        }
        catch(ex: Exception)
        {
            recorderLock.unlock()
            Log.e(mClassName, "SYR -> Unable to close recorder of connection due: ${ex.message}")
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
        startConnectionTimer()
    }

    private fun startConnectionTimer()
    {
        try {

            updateVideoStateTimer = Observable.timer( 10000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe({startConnection()},{
                it.printStackTrace()
                startConnectionTimer()
            })


            settingsGetter = SettingPreferencesGetter(applicationContext)
        }
        catch (ex: java.lang.Exception)
        {
            Log.e(mClassName,"SYR -> Unable to create openCV controllers because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * DO NOT CALL DIRECTLY
     */
    private fun startConnection()
    {
        try
        {
            if (!connectionEstablished)
            {
                closeInputStream()
                closeOutputStream()
                openVideoStream()
            }
            notifyVideoState()
        }
        catch (ex: java.lang.Exception)
        {
            Log.e(mClassName,"SYR -> Unable to startConnection because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    override fun stopServiceActivity() {
        TODO("Not yet implemented")
    }
    //endregion
}