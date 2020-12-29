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
import com.example.shareyourride.configuration.SettingPreferencesIds
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write


/**
 * Stores and mange the video session
 */
class VideoService(): IMessageHandlerClient, ServiceBase() {


    //region properties
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

    private var videoMillisecondsPerFrame: Long = 0

    /**
     * The video bitrate
     */
    private var videoBitRate: Int = 0

    /**
     * The path to store the raw video of the session
     */
    private var videoRawPath : String = ""

    /**
     * The path to store the composed final video
     */
    private var videoSharedPath: String = ""

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
     * Lock to avoid concurrent accesses to the recorder API
     */
    private val recorderLock: ReentrantReadWriteLock = ReentrantReadWriteLock()

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

    /**
     * Time stamp of the mobile phone when the first frame of the video is processed, it is used to synchronize the video
     */
    private var referenceTimeStampSystem: Long = 0

    /**
     * Time stamp of the video phone when the first frame of the video is processed, it is used to synchronize the video
     */
    private var referenceTimeStampVideo: Long = 0

    /**
     * The timestamp associated to the current frame
     */
    private var frameTimeStamp: Long = 0

    /**
     * Used to count the null received frames
     * If the connection throws ten null frames in a consecutive way, we declare the connection down
     */
    private var nullFrames: Int = 0

    /**
     * Counter of syncrhonization frames. Only send the half of them to the view to limit the cpu consumption due the poor
     * performance of image converters
     */
    private var syncFramesCounter: Long = 0
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
                MessageTypes.START_ACQUIRING_DATA ->
                {
                    Log.d(mClassName, "SYR -> received  START_ACQUIRING_DATA")
                    processStartAcquiringData(msg.messageData)
                }
                MessageTypes.STOP_ACQUIRING_DATA -> {
                    Log.d(mClassName, "SYR -> received  STOP_ACQUIRING_DATA")
                    val discarded = msg.messageData.data as Boolean
                    processStopAcquiringData(discarded)
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
     * Get the full path to the directory to store the video in its public location
     * Also creates the directory if it doesn't exists
     *
     * @return The full path address of the directory
     */
    private fun getSharedFileDir(): String {
        return try
        {
            var fileName = ""

            val getter = applicationContext?.let { SettingPreferencesGetter(it) }

            if (getter!= null)
            {
                fileName += getter.getStringOption(SettingPreferencesIds.ActivityKind)
            }

            fileName = fileName +"."+getDateTime()+".mp4"

            return fileName
        }
        catch (ex: Exception) {
            ex.printStackTrace()
            ""
        }
    }

    private fun getDateTime(): String {
        try
        {
            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
            val netDate = Date(System.currentTimeMillis())
            return sdf.format(netDate)

        } catch (ex: Exception)
        {
            Log.e(mClassName,"SYR -> Unable to create date because ${ex.message}")
            ex.printStackTrace()
        }

        return System.currentTimeMillis().toString()
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

                if (connectionData.ip.isEmpty())
                {
                    Log.e(mClassName, "SYR -> Unable to configure the connection address because the IP is empty")
                    return
                }

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
                          videoSharedPath)

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
                        videoSharedPath)

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
            //Log.i(mClassName, "SYR ->Saving frames ${timeStampRelationMap.count()} in disk")
            timeStampRelationMap.forEach {
                try
                {
                    val videoFrame = VideoFrame(VideoFrameId(sessionId,it.key),it.value)
                    ShareYourRideRepository.insertVideoFrame(videoFrame)
                    //Log.i(mClassName, "SYR ->Saving frame ${videoFrame.id.frameTimeStamp} ----------------- ${videoFrame.syncTimeStamp}")
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
                av_log_set_level(AV_LOG_INFO);

                Log.i(mClassName, "SYR -> Trying to connect to video service $addressString")

                grabber = FFmpegFrameGrabber(addressString)
                grabber!!.setOption("rtsp_transport", "tcp")
                grabber!!.timeout = 5000
                grabber!!.imageMode = FrameGrabber.ImageMode.COLOR
                //As is explained in https://github.com/bytedeco/javacv/issues/214,
                //In the perform test AV_PIX_FMT_NV12 was the fastest pixel format
                //grabber!!.pixelFormat = AV_PIX_FMT_RGBA
                //grabber!!.pixelFormat = AV_PIX_FMT_RGB565LE
                //grabber!!.pixelFormat = AV_PIX_FMT_YUV420P
                grabber!!.pixelFormat = AV_PIX_FMT_NV12
                grabber!!.videoCodec = AV_CODEC_ID_H264
                grabber!!.imageWidth=960
                grabber!!.imageHeight=518

                grabber!!.setOption("hwaccel", "h264_videotoolbox")
                grabber!!.setOption("probesize", "256")
                grabber!!.setOption("flags", "low_delay")
                grabber!!.setOption("flags", "discardcorrupt")
                grabber!!.setOption("preset", "ultrafast")
                grabber!!.setOption("tune", "fastdecode")
                grabber!!.setOption("crf", "51")

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
                    videoMillisecondsPerFrame = getMillisecondsPerFrame()
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
                referenceTimeStampSystem = 0
                referenceTimeStampVideo  = 0
                timeStampRelationMap.clear()
                framesCounter = 0

                sessionId = data.data as String
                Log.d(mClassName, "SYR -> Start reading video frames in a new thread for session $sessionId ")

                videoRawPath = getRawFileDir()
                videoSharedPath = getSharedFileDir()

                Log.i(mClassName, "SYR -> recording video $videoRawPath, final video : $videoSharedPath")

                recorderLock.write {
                    recorder = FFmpegFrameRecorder(videoRawPath, videoWidth, videoHeight)
                    recorder!!.videoCodec = AV_CODEC_ID_H264
                    recorder!!.format = "matroska"
                    recorder!!.isInterleaved = false
                    //recorder!!.pixelFormat = AV_PIX_FMT_YUV420P
                    recorder!!.pixelFormat = AV_PIX_FMT_NV12

                    recorder!!.setOption("preset", "ultrafast")
                    recorder!!.setOption("tune", "zerolatency")
                    recorder!!.setOption("crf", "51")
                    recorder!!.setOption("hwaccel", "h264_videotoolbox")

                    recorder!!.frameRate = videoFrameRate
                    recorder!!.videoBitrate = videoBitRate
                    recorder!!.imageHeight = videoHeight
                    recorder!!.imageWidth = videoWidth

                    if (grabber != null) {
                        recorder!!.start()
                    }
                }

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
            Log.e(mClassName, "SYR -> Unable to processStartAcquiringData due: ${ex.message}")
            ex.printStackTrace()

            processErrorInConnection()
        }
    }



    /**
     * Loop to read the incoming video stream
     */
    private fun getVideoFrames()
    {

        GlobalScope.async(Dispatchers.IO) {


            //Log.d(mClassName, "SYR ->Starting  recording frame grabber timestamp ${grabber!!.timestamp} recorder timepstamp ${recorder!!.timestamp}")

            while (connectionEstablished && grabber != null && !grabber!!.isCloseInputStream) {
                try {
                    val frame: Frame? = grabber!!.grabImage()
                    if (frame != null) {

                        nullFrames = 0
                        //for debuging, uncoment
                        //Log.d(mClassName, "SYR ->Grabbed frame ${frame.timestamp}  ---------------- ${System.currentTimeMillis()}")
                        if (synchronizeVideoFlag) {
                            sendVideoFrame(frame)
                        }
                        else
                        {
                            processVideoFrame(frame)
                        }
                    }
                    else
                    {
                        nullFrames++

                        if (nullFrames > 50) {
                            Log.e(mClassName, "SYR -> Grabbed 50 consecutive null frames ------ ${Process.myTid()} is closed ${grabber!!.isCloseInputStream} has video ${grabber!!.hasVideo()}")
                            processErrorInConnection()
                        }
                    }
                }
                catch (ex: Exception) {
                    Log.e(mClassName, "SYR -> Unable execute get video frame due: ${ex.message}")
                    ex.printStackTrace()
                }
            }
        }
    }


    private fun getMillisecondsPerFrame(): Long
    {
        return if(grabber!= null) {
            (1000/grabber!!.videoFrameRate).toLong()
        }
        else {
            1000/25
        }

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
            val frameTimestamp = (frame.timestamp / 1000).toLong()
            //val frameTimestamp = (frame.timestamp).toLong()
            //synchronize the first processed frame with start of the video
            if (framesCounter == 0L)
            {
                referenceTimeStampSystem = System.currentTimeMillis()
                referenceTimeStampVideo = frameTimestamp
                recorder!!.timestamp = 0
            }
            else
            {
                //frame.timestamp = (System.currentTimeMillis() - referenceTimeStampSystem) * 1000
                frameTimeStamp+= videoMillisecondsPerFrame
                frame.timestamp = frameTimeStamp * 1000
            }

            try
            {
                recorderLock.write {
                    Log.d(mClassName, "SYR ->saved frame ${frame.timestamp} - count $framesCounter")
                    recorder!!.setTimestamp(frame.timestamp)
                    recorder!!.record(frame)
                }
                timeStampRelationMap[frame.timestamp] = referenceTimeStampSystem + frameTimestamp - referenceTimeStampVideo
                framesCounter++
            }
            catch(ex: Exception)
            {
                ex.printStackTrace()
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
            syncFramesCounter = 0
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
    private fun processStopAcquiringData(discarded: Boolean)
    {
        captureVideoFlag = false
        connectionEstablished = false

        closeStreams()

        if (!discarded)
        {
            Log.i(mClassName, "SYR -> Saving video and data in the system")
            GlobalScope.async(Dispatchers.IO) {
                updateVideoFileInDataBase()
                saveFramesInDataBase()
                Log.i(mClassName, "SYR -> Sending message VIDEO_CREATION_COMMAND")
                val message = MessageBundle(MessageTypes.VIDEO_CREATION_COMMAND, sessionId, MessageTopics.VIDEO_CREATION_DATA)
                sendMessage(message)
            }
        }
        else
        {
            Log.i(mClassName, "SYR -> Deleting video and data in the system")
            val file = File(getRawFileDir())
            file.delete()
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

        closeStreams()

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

    private fun processErrorInConnection()
    {
        captureVideoFlag = false
        connectionEstablished = false

        notifyVideoState()

        closeStreams()

        startConnectionTimer()
    }

    private fun closeStreams()
    {
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
            closeOutputStream()
        }
        catch (ex: java.lang.Exception)
        {
            Log.e(mClassName,"SYR -> Unable to close the recorder due : ${ex.message}")
            ex.printStackTrace()
        }


    }

    /**
     * Just send the given video frame to the view model
     *
     * @param frame: Frame to record
     */
    private fun sendVideoFrame(frame: Frame)
    {
        if (syncFramesCounter % 2 == 0L)
        {
            val message = MessageBundle(MessageTypes.VIDEO_FRAME_SYNCHRONIZATION_DATA, bitmapConverter.convert(frame), MessageTopics.VIDEO_SYNCHRONIZATION_DATA)
            sendMessage(message)
        }
        syncFramesCounter++
    }


    /**
     * Closes de current connection and starts a new one
     */
    private fun closeInputStream()
    {
        try
        {
            Log.d(mClassName, "SYR -> Closing grabber to url: $addressString closed")
            if (grabber != null)
            {
                grabber!!.stop()
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

            recorderLock.write {
                if (recorder != null) {
                    //recorder!!.flush()
                    recorder!!.close()
                    recorder = null
                }
            }

            Log.i(mClassName, "SYR -> recorder of url: $addressString closed")
        }
        catch(ex: Exception)
        {
            connectionEstablished = false
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

    //private  fun startConnectionTimer()
    public fun startConnectionTimer()
    {
        try {

            if (updateVideoStateTimer != null)
            {
                updateVideoStateTimer!!.dispose()
            }

            updateVideoStateTimer = Observable.timer( 10000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe(
                    {
                        Log.d(mClassName, "SYR -> Launching scheduled connection process")
                        startConnection()
                    },
                    {
                        it.printStackTrace()
                        startConnectionTimer()
                    })
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