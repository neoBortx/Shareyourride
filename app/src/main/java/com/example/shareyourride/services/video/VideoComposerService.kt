package com.example.shareyourride.services.video

import android.graphics.*
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import com.bvillarroya_creations.shareyourride.R
import com.bvillarroya_creations.shareyourride.datamodel.data.*
import com.bvillarroya_creations.shareyourride.datamodel.dataBase.ShareYourRideRepository
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTopics
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messagesdefinition.dataBundles.VideoCreationStateEvent
import com.bvillarroya_creations.shareyourride.messagesdefinition.dataBundles.VideoState
import com.bvillarroya_creations.shareyourride.messenger.IMessageHandlerClient
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.messenger.MessageBundleData
import com.bvillarroya_creations.shareyourride.messenger.MessageHandler
import com.example.shareyourride.common.CommonConstants
import com.example.shareyourride.configuration.SettingPreferencesGetter
import com.example.shareyourride.services.base.ServiceBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.ffmpeg.global.avutil
import org.bytedeco.javacv.AndroidFrameConverter
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Frame
import java.io.File
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


/**
 * Stores and mange the video session
 */
class VideoComposerService: IMessageHandlerClient, ServiceBase() {


    //region private constants
    /**
     * Milliseconds to update the composition state
     * While the timer expires, sends the VIDEO_CREATION_STATE_EVENT
     */
    private val millisecondsToUpdateVideoConverterState: Int = 5000
    //endregion

    //region properties
    /**
     * In charge of access to the application settings
     */
    private var settingsGetter : SettingPreferencesGetter? = null

    /**
     * The name of the service
     */
    override var mClassName: String = "VideoComposerService"

    /**
     * Holds the video information of the session
     */
    private var video: Video? = null

    /**
     * Holds the video information of the session
     */
    private var sessionData: Session? = null

    /**
     * The telemetry configured for the session
     */
    private var sessionTelemetry: SessionTelemetry? = null

    /**
     * The max speed of the session
     */
    private var maxSessionSpeed: Float = 0F

    /**
     * A map with all the frames
     */
    private var framesMap:  MutableMap<Long, Long> = mutableMapOf()

    /**
     * The size of the text
     */
    private var textSize: Float = 0F

    /**
     * The color of the text
     */
    private var color: Int = 0

    /**
     * Paint for the text
     */
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * Time stamp of the last VIDEO_CREATION_STATE_EVENT message sent
     * the periodicity of the messages is ruled by the constant millisecondsToUpdateVideoConverterState
     */
    private var lastUpdateTimeStamp: Long = 0

    /**
     * The time stamp of the creation of the video, used to calculate the duration of the creation process in order to
     * benchmark it
     */
    private var startTimeStamp: Long = 0

    /**
     * The current amount of frames composed. Used to calculate the % of video composed
     * to send to upper layers
     */
    private var composedFramesCount: Long = 0

    /**
     * Because several frames will hold the same telemetry data, this variable hold the last
     * telemetry synchronization frame processed, to skip useless data base access
     */
    private var lastProcessedSyncTimeStamp: Long = -1

    /**
     * Last processed location telemetry
     */
    var location: Location? = null

    /**
     * Last processed inclination telemetry
     */
    var inclination: Inclination? = null

    /**
     * Coordinates of the speed text in the canvas
     */
    private var speedLocation : Pair<Float,Float>? = null

    /**
     * Coordinates of the distance text in the canvas
     */
    private var distanceLocation : Pair<Float,Float>? = null

    /**
     * Coordinates of the acceleration text in the canvas
     */
    private var accelerationLocation : Pair<Float,Float>? = null

    /**
     * Coordinates of the terrain inclination text in the canvas
     */
    private var terrainInclinationLocation : Pair<Float,Float>? = null

    /**
     * Coordinates of the altitude text in the canvas
     */
    private var heightLocation : Pair<Float,Float>? = null

    /**
     * Coordinates of the lean angle text in the canvas
     */
    private var leanAngleLocation : Pair<Float,Float>? = null

    /**
     * Selects the speedometer image
     */
    private lateinit var speedometerPrinter: SpeedometerPrinter

    /**
     * Composes the lean angle image
     */
    private lateinit var leanAnglePrinter: LeanAnglePrinter

    /**
     * Refernce points
     */
    private var leftBorder: Float   = 0F
    private var bottomBorder: Float = 0F
    private var rightBorder: Float  = 0F
    private var topBorder: Float    = 0F
    private var centerBorder: Float = 0F
    private var scale: Float        = 0F
    //endregion

    //region message handlers
    init {
        this.createMessageHandler( "VideoComposerService", listOf(MessageTopics.VIDEO_CREATION_DATA))
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
                MessageTypes.VIDEO_CREATION_COMMAND ->
                {
                    Log.d(mClassName, "SYR -> received  VIDEO_CREATION_COMMAND with data")
                    processVideoCreationCommand(msg.messageData)
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
                dir.path +"/"+454532121212121241+"_raw.mp4"
            }
            else {
                ""
            }
        }
        catch (ex: Exception) {
            Log.e("VideoClient", "SYR -> Unable to get the directory to save the video 454532121212121241")
            ex.printStackTrace()
            ""
        }
    }


    /**
     *
     */
    private fun processVideoCreationCommand(messageData: MessageBundleData)
    {
        try
        {
            if (messageData.type == String::class)
            {
                val sessionId = messageData.data as String

                startTimeStamp = System.currentTimeMillis()

                GlobalScope.async {

                    sessionData      = ShareYourRideRepository.getSession(sessionId)
                    sessionTelemetry = ShareYourRideRepository.getSessionTelemetry(sessionId)
                    video            = ShareYourRideRepository.getVideo(sessionId)
                    maxSessionSpeed  = ShareYourRideRepository.getMaxSpeed(sessionId)
                    if (video != null)
                    {
                        scale= getTextScale(video!!)
                        textSize       = 30 * scale
                        color          = applicationContext?.getColor(R.color.colorPrimary) ?: Color.CYAN
                        paint.color    = color
                        paint.textSize = textSize
                        paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY)

                        //places
                        bottomBorder = video!!.height-(30*scale)
                        rightBorder  = video!!.width-(150*scale)
                        leftBorder   = 40 * scale
                        centerBorder = ((video!!.width/2).toFloat())
                        topBorder    = 30 * scale

                        //bottom
                        speedLocation                = Pair(leftBorder, bottomBorder)
                        leanAngleLocation            = Pair(centerBorder-20*scale, bottomBorder)
                        accelerationLocation         = Pair(rightBorder, bottomBorder)

                        //top
                        distanceLocation           = Pair(leftBorder, topBorder)
                        heightLocation             = Pair(centerBorder, topBorder)
                        terrainInclinationLocation = Pair(rightBorder , topBorder)

                        speedometerPrinter = SpeedometerPrinter(bottomBorder, leftBorder, scale, maxSessionSpeed, applicationContext)
                        leanAnglePrinter   = LeanAnglePrinter(bottomBorder, leftBorder, scale, applicationContext)

                        val frameList = ShareYourRideRepository.getVideoFrameList(video!!.sessionId)
                        var count : Long = 1
                        frameList.forEach{
                            //framesMap[it.id.frameTimeStamp] = it.syncTimeStamp
                            framesMap.put(count,it.syncTimeStamp)
                            count++
                        }

                        Log.i(mClassName, "SYR -> Composing video for session id $sessionId")

                        composeVideo()
                    }
                    else
                    {
                        Log.e(mClassName, "SYR -> There isn't any video with the given session id $sessionId")
                    }
                }
            }
            else
            {
                Log.e(mClassName, "SYR -> Message VIDEO_CREATION_COMMAND comes with out data")
            }
        }
        catch(ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to process message ${ex.message}")
            ex.printStackTrace()
        }
    }

    //region process messages

    /**
     * Get the full path to the directory to store the video
     * Also creates the directory if it doesn't exists
     *
     * @return The full path address of the directory
     */
    private fun composeVideo()
    {
        val file = File(video!!.rawVideoFilePath)
        val grabber = FFmpegFrameGrabber(file)
        val recorder = FFmpegFrameRecorder(getRawFileDir(), video!!.width, video!!.height)
        val converter = AndroidFrameConverter()

        recorder.videoCodec = avcodec.AV_CODEC_ID_H264
        recorder.format = "matroska"
        recorder.isInterleaved = true
        recorder.pixelFormat = avutil.AV_PIX_FMT_YUV420P

        recorder.frameRate = video!!.frameRate
        recorder.videoBitrate = video!!.bitRate
        recorder.timestamp = 0

        Log.d(mClassName, "SYR -> Starting recorder to file ${video!!.generatedVideoPath}")

        recorder.start()

        Log.d(mClassName, "SYR -> Recorder started")

        //start the grabber in another thread because this is a blocking operation
        GlobalScope.async(Dispatchers.IO) {

            Log.d(mClassName, "SYR -> Starting grabber")
            grabber.start()
            Log.d(mClassName, "SYR -> grabber started")

            var continueProcessing = true
            lastUpdateTimeStamp = 0
            while (continueProcessing)
            {
                val frame = grabber.grab()

                if (frame != null)
                {
                    composeFrame(frame, recorder, converter)
                }
                else
                {
                    val millisecondsElapsed = System.currentTimeMillis() - startTimeStamp
                    val time = String.format("%d min, %d sec",
                                                TimeUnit.MILLISECONDS.toMinutes(millisecondsElapsed),
                                                TimeUnit.MILLISECONDS.toSeconds(millisecondsElapsed) -
                                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisecondsElapsed)))
                    continueProcessing = false
                    if (composedFramesCount >= video!!.totalVideoFrames)
                    {
                        Log.i(mClassName, "SYR -> Video of session ${video!!.sessionId} composed in $time, Hell yeah!!!!!!!!!!!!")
                        notifyCreationState(VideoState.Finished)
                    }
                    else
                    {
                        Log.i(mClassName, "SYR -> Video of session ${video!!.sessionId} failed in $time")
                        notifyCreationState(VideoState.Failed)
                    }
                }
            }

            recorder.stop()
            recorder.release()
            grabber.stop()
            grabber.release()
        }
    }

    /**
     * The size of the text is hardcoded to match a resolution of 1024x720
     * So, to make it  good looking in videos with different resolutions,
     * calculate the multiplier of the resolution.
     *
     * @param video: The video information with the current height and width
     * @return
     */
    private fun getTextScale(video: Video): Float
    {
        try
        {
            val referenceSize: Int = 1024*720
            val currentSize: Float = (video.width*video.height).toFloat()

            return currentSize.div(referenceSize)
        }
        catch(ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to calculate the text scale")
            ex.printStackTrace()
        }

        return 0.0F
    }

    /**
     * Steps of processing a frame:
     *
     *  - Convert the given JavaCV frame into a bitmap compatible with the android library
     *  - Get the telemetry related to the frame timestamp
     *  - Compose the video frame drawing the telemetry into the bitmap
     *  - Convert the frame to JavaCV frame
     *  - Save the frame with the ffmpeg recorder tool
     *
     *  @param frame: The grabbed frame
     *  @param recorder: saves the frame in disk
     *  @param converter: converts frames is bitmaps and vice-versa, pass as parameter instead of initialize it in each frame to speed up the operation
     */
    private suspend fun composeFrame(frame: Frame, recorder: FFmpegFrameRecorder, converter: AndroidFrameConverter)
    {
        try
        {
            composedFramesCount++

            val currentMillis = System.currentTimeMillis()

            if(lastUpdateTimeStamp - currentMillis > millisecondsToUpdateVideoConverterState)
            {
                notifyCreationState(VideoState.Composing)
                lastUpdateTimeStamp = currentMillis
            }
            //Generate a bitmap compatible with the Android API to add text and images on it
            val frameBitmap: Bitmap = converter.convert(frame)
            val canvas = Canvas(frameBitmap)

            if (/*framesMap.containsKey(frame.timestamp)*/ framesMap.containsKey(composedFramesCount)
                && lastProcessedSyncTimeStamp != framesMap[composedFramesCount])
            {
                lastProcessedSyncTimeStamp = framesMap[composedFramesCount]!!
                location = ShareYourRideRepository.getLocation(video!!.sessionId, lastProcessedSyncTimeStamp)
                inclination = ShareYourRideRepository.getInclination(video!!.sessionId, lastProcessedSyncTimeStamp)
            }

            if (sessionTelemetry!!.speed && location != null)
            {
                val bitmap = speedometerPrinter.getSpeedometer(location!!.speed)
                if(bitmap != null)
                {
                    val rect = Rect(leftBorder.toInt(), (bottomBorder - 40*scale).toInt(),(bitmap.width*scale).toInt(),(bitmap.height*scale).toInt())
                    canvas.drawBitmap(bitmap,leftBorder, bottomBorder - (40*scale) - bitmap.height, null)
                }

                val text = (location!!.speed.times(CommonConstants.getSpeedConverter(applicationContext)).roundToInt().toString() + CommonConstants.getSpeedText(applicationContext))
                canvas.drawText(text, speedLocation!!.first, speedLocation!!.second, paint)
            }

            if (sessionTelemetry!!.leanAngle && inclination != null)
            {
                val bitmap = leanAnglePrinter.getAngle(inclination!!.roll)
                if(bitmap != null)
                {
                    val rect = Rect(((centerBorder - bitmap.width/2)*scale).toInt(), (bottomBorder - 40*scale).toInt(),(bitmap.width*scale).toInt(),(bitmap.height*scale).toInt())
                    canvas.drawBitmap(bitmap,centerBorder - (bitmap.width / 2), bottomBorder - (40*scale) - bitmap.height, null)
                }

                val text = (inclination!!.roll.absoluteValue.toString() + applicationContext.getString(R.string.degrees))
                canvas.drawText(text, leanAngleLocation!!.first, leanAngleLocation!!.second, paint)
            }

            if (sessionTelemetry!!.acceleration && inclination != null)
            {
                val text = DecimalFormat("##.#").format(inclination!!.accelerationScalar.times(CommonConstants.getAccelerationConverter(applicationContext)).absoluteValue).toString() + CommonConstants.getAccelerationText(applicationContext)
                canvas.drawText(text, accelerationLocation!!.first, accelerationLocation!!.second, paint)
            }

            if (sessionTelemetry!!.distance && location != null)
            {
                val distance = location!!.distance.times(CommonConstants.getShortDistanceConverter(applicationContext))
                val text = distance.roundToInt().toString() + CommonConstants.getShortDistanceText(applicationContext)
                canvas.drawText(text, distanceLocation!!.first, distanceLocation!!.second, paint)
            }

            if (sessionTelemetry!!.altitude  && location != null)
            {
                val text = (location!!.altitude.times(CommonConstants.getShortDistanceConverter(applicationContext)).roundToInt().toString() + CommonConstants.getShortDistanceText(applicationContext))
                canvas.drawText(text, heightLocation!!.first, heightLocation!!.second, paint)
            }

            if (sessionTelemetry!!.terrainInclination && location != null)
            {
                val text = (location!!.terrainInclination.toString() + applicationContext.getString(R.string.percentage))
                canvas.drawText(text, terrainInclinationLocation!!.first, terrainInclinationLocation!!.second, paint)
            }

            //Generate a ffmpeg frame
            val videoFrame: Frame = converter.convert(frameBitmap)
            videoFrame.timestamp = frame.timestamp

            recorder.record(videoFrame)
        }
        catch (ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to compose frame ${ex.message}")
            ex.printStackTrace()
        }
    }

    private fun notifyCreationState(state: VideoState)
    {
        lastUpdateTimeStamp = 0
        val percentage = (composedFramesCount / video!!.totalVideoFrames)*100
        Log.d("SessionService", "SYR -> Sending  VIDEO_CREATION_STATE_EVENT ===> $percentage")
        val message = MessageBundle(MessageTypes.VIDEO_CREATION_STATE_EVENT, VideoCreationStateEvent(state, percentage.toInt()), MessageTopics.VIDEO_CREATION_DATA)
        sendMessage(message)
    }

    //region start stop service handlers
    override fun startServiceActivity()
    {
        try {

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