/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.services.video

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
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
import com.example.shareyourride.common.CommonConstants.Companion.GRAVITY_ACCELERATION
import com.example.shareyourride.configuration.SettingPreferencesGetter
import com.example.shareyourride.configuration.SettingPreferencesIds
import com.example.shareyourride.services.base.ServiceBase
import com.example.shareyourride.userplayground.common.AccelerationDirection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.ffmpeg.global.avutil
import org.bytedeco.javacv.AndroidFrameConverter
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Frame
import org.bytedeco.librealsense.context
import java.io.File
import java.io.OutputStream
import java.util.*
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
    private val millisecondsToUpdateVideoConverterState: Int = 1000
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


    //region bitmaps
    /**
     * Bitmap with teh altitude icon
     */
    private var altitudeDrawable: Bitmap? = null

    /**
     * Bitmap with the distance icon
     */
    private var distanceDrawable: Bitmap? = null

    /**
     * Birmap with the terrain inclination icon
     */
    private var terrainInclinationDrawable: Bitmap? = null


    /**
     * The last inserted speedometer image, in order to speed up the generation of
     * frames when the telemetry values are the same
     */
    private var lastSpeedometerDrawable: Bitmap? = null

    /**
     * The last inserted lean angle image, in order to speed up the generation of
     * frames when the telemetry values are the same
     */
    private var lastLeanAngleDrawable: Bitmap? = null

    /**
     * The last inserted force image, in order to speed up the generation of
     * frames when the telemetry values are the same
     */
    private var lastForceDrawable: Bitmap? = null

    /**
     * The configured delay of the video
     */
    private var configuredDelay: Int = 0
    //endregion


    //region telemetry data
    /**
     * Last processed location telemetry
     */
    private var location: Location? = null

    /**
     * Last processed inclination telemetry
     */
    private var inclination: Inclination? = null
    //endregion

    //region locations of all elements in screen
    /**
     * Coordinates of the speed value text in the canvas
     */
    private var speedValueLocation : Pair<Float,Float>? = null

    /**
     * Coordinates of the speed units text in the canvas
     */
    private var speedUnitsLocation : Pair<Float,Float>? = null

    /**
     * Coordinates of the distance text in the canvas
     */
    private var distanceLocation : Pair<Float,Float>? = null

    /**
     * Coordinates of the distance drwable in the canvas
     */
    private var distanceDrawableLocation : Pair<Float,Float>? = null

    /**
     * Coordinates of the acceleration value text in the canvas
     */
    private var accelerationValueLocation : Pair<Float,Float>? = null

    /**
     * Coordinates of the acceleration units in the canvas
     */
    private var accelerationUnitLocation : Pair<Float,Float>? = null

    /**
     * Coordinates of the terrain inclination text in the canvas
     */
    private var terrainInclinationLocation : Pair<Float,Float>? = null

    /**
     * Coordinates of the terrain inclination drawable in the canvas
     */
    private var terrainInclinationDrawableLocation : Pair<Float,Float>? = null

    /**
     * Coordinates of the altitude text in the canvas
     */
    private var altitudeLocation : Pair<Float,Float>? = null

    /**
     * Coordinates of the altitude drawable in the canvas
     */
    private var altitudeDrawableLocation : Pair<Float,Float>? = null

    /**
     * Coordinates of the lean angle text in the canvas
     */
    private var leanAngleLocation : Pair<Float,Float>? = null
    //endregion

    //region image selectors
    /**
     * Selects the speedometer image
     */
    private lateinit var speedometerPrinter: SpeedometerPrinter

    /**
     * Composes the lean angle image
     */
    private lateinit var leanAnglePrinter: LeanAnglePrinter

    /**
     * Selects the force image
     */
    private lateinit var forcePrinter: ForcePrinter
    //endregion


    /**
     * Reference points used to locate image and text in the final canvas
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
                dir.path +"/"+454554585421224178+"_raw.mp4"
            }
            else {
                ""
            }
        }
        catch (ex: Exception) {
            Log.e("VideoClient", "SYR -> Unable to get the directory to save the video")
            ex.printStackTrace()
            ""
        }
    }

    /**
     *
     */
    @Suppress("DeferredResultUnused")
    private fun processVideoCreationCommand(messageData: MessageBundleData)
    {
        try
        {
            if (messageData.type == String::class)
            {
                val sessionId = messageData.data as String

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
                        bottomBorder = video!!.height-(15*scale)
                        rightBorder  = video!!.width-(90*scale)
                        leftBorder   = 40 * scale
                        centerBorder = ((video!!.width/2).toFloat())
                        topBorder    = 35 * scale

                        //bottom
                        speedValueLocation           = Pair(leftBorder, bottomBorder)
                        speedUnitsLocation           = Pair(leftBorder + 198*scale, bottomBorder)
                        leanAngleLocation            = Pair(centerBorder- 20*scale, bottomBorder)
                        accelerationValueLocation    = Pair(rightBorder - 35*scale, bottomBorder)
                        accelerationUnitLocation     = Pair(rightBorder + 25*scale, bottomBorder)

                        //top
                        distanceLocation                   = Pair(leftBorder+65*scale, topBorder)
                        distanceDrawableLocation           = Pair(leftBorder, topBorder - 39*scale)
                        altitudeLocation                   = Pair(centerBorder- 10*scale, topBorder)
                        altitudeDrawableLocation           = Pair(centerBorder - 45, topBorder-24*scale)
                        terrainInclinationLocation         = Pair(rightBorder + 5*scale , topBorder)
                        terrainInclinationDrawableLocation = Pair(rightBorder - 65*scale, topBorder-22*scale)

                        speedometerPrinter = SpeedometerPrinter(scale, maxSessionSpeed, applicationContext)
                        leanAnglePrinter   = LeanAnglePrinter(scale, applicationContext)
                        forcePrinter = ForcePrinter(scale, applicationContext)

                        //bitmaps
                        var bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.distance)!!.toBitmap()
                        distanceDrawable = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *scale).toInt(), (bitmap.height * scale).toInt(), true)

                        bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.altitude)!!.toBitmap()
                        altitudeDrawable = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *scale).toInt(), (bitmap.height * scale).toInt(), true)

                        bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.inclination)!!.toBitmap()
                        terrainInclinationDrawable = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *scale).toInt(), (bitmap.height * scale).toInt(), true)

                        val frameList = ShareYourRideRepository.getVideoFrameList(video!!.sessionId)
                        var count : Long = 1
                        frameList.forEach{
                            //framesMap[it.id.frameTimeStamp] = it.syncTimeStamp
                            framesMap.put(it.id.frameTimeStamp,it.syncTimeStamp)
                            count++
                        }

                        Log.i(mClassName, "SYR -> Composing video for session id $sessionId")

                        composeVideo()
                    }
                    else
                    {
                        Log.e(mClassName, "SYR -> There isn't any video with the given session id $sessionId")
                        notifyCreationState(VideoState.Failed)
                    }
                }
            }
            else
            {
                Log.e(mClassName, "SYR -> Message VIDEO_CREATION_COMMAND comes with out data")
                notifyCreationState(VideoState.Failed)
            }
        }
        catch(ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to process message ${ex.message}")
            ex.printStackTrace()
            notifyCreationState(VideoState.Failed)
        }
    }

    //region process messages

    /**
     * Get the full path to the directory to store the video
     * Also creates the directory if it doesn't exists
     *
     * @return The full path address of the directory
     */
    private fun composeVideo() {
        try {

            composedFramesCount = 0
            notifyCreationState(VideoState.Composing)

            val file = File(video!!.rawVideoFilePath)
            val grabber = FFmpegFrameGrabber(file)

            val stream = createFileStream((video!!.totalVideoFrames / video!!.frameRate).toLong())

            if (stream == null) {
                Log.e(mClassName, "SYR -> Unable to compose video because stream is not accessible")
                notifyCreationState(VideoState.Failed)
                return
            }
            val recorder = FFmpegFrameRecorder(stream, video!!.width, video!!.height)
            val converter = AndroidFrameConverter()

            recorder.videoCodec = avcodec.AV_CODEC_ID_H264
            recorder.format = "matroska"
            //recorder.pixelFormat = avutil.AV_PIX_FMT_YUV420P
            //To avoid h264 warnings in the creation of the video
            recorder.pixelFormat = avutil.AV_PIX_FMT_YUV420P

            recorder.frameRate = video!!.frameRate
            recorder.videoBitrate = video!!.bitRate

            Log.d(mClassName, "SYR -> Starting recorder to file ${video!!.generatedVideoPath}")

            val getter = SettingPreferencesGetter(applicationContext)
            configuredDelay = getter.getIntOption(SettingPreferencesIds.VideoDelay)

            startTimeStamp = System.currentTimeMillis()

            recorder.start()

            Log.d(mClassName, "SYR -> Recorder started")

            //start the grabber in another thread because this is a blocking operation
            GlobalScope.async(Dispatchers.IO) {

                Log.d(mClassName, "SYR -> Starting grabber")
                grabber.start()
                Log.d(mClassName, "SYR -> grabber started")

                var continueProcessing = true
                lastUpdateTimeStamp = 0
                while (continueProcessing) {
                    val frame = grabber.grab()

                    val millisecondsElapsed = System.currentTimeMillis() - startTimeStamp

                    if (frame != null) {
                        composeFrame(frame, recorder, converter, millisecondsElapsed)
                    }
                    else {
                        val time = String.format(
                                "%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(millisecondsElapsed), TimeUnit.MILLISECONDS.toSeconds(millisecondsElapsed) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisecondsElapsed)))
                        continueProcessing = false
                        //if (composedFramesCount >= video!!.totalVideoFrames) {
                            Log.i(mClassName, "SYR -> Video of session ${video!!.sessionId} composed in $time, Hell yeah!!!!!!!!!!!!")
                            notifyCreationState(VideoState.Finished)
                        /*}
                        //else {
                            Log.i(mClassName, "SYR -> Video of session ${video!!.sessionId} failed in $time")
                            notifyCreationState(VideoState.Failed)
                        }*/
                    }
                }

                recorder.close()
                grabber.close()
                stream.close()
            }
        }
        catch (ex: java.lang.Exception)
        {
            Log.e(mClassName, "SYR -> Unable to create video because ${ex.message}")
            ex.printStackTrace()
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
    private suspend fun composeFrame(frame: Frame, recorder: FFmpegFrameRecorder, converter: AndroidFrameConverter, millisecondsElapsed: Long)
    {
        try
        {
            //Uncommet for developing purposes
            Log.d("SessionService", "SYR -> composing frame ${frame.timestamp} --------------------- $composedFramesCount")
            composedFramesCount++

            val currentMillis = System.currentTimeMillis()

            if(currentMillis - lastUpdateTimeStamp > millisecondsToUpdateVideoConverterState)
            {
                notifyCreationState(VideoState.Composing)
                lastUpdateTimeStamp = currentMillis
            }

            //Generate a bitmap compatible with the Android API to add text and images on it
            val frameBitmap: Bitmap = converter.convert(frame)
            val canvas = Canvas(frameBitmap)

            if (framesMap.containsKey(frame.timestamp)
                && lastProcessedSyncTimeStamp != (framesMap[frame.timestamp]?.minus(configuredDelay)))
            {
                lastProcessedSyncTimeStamp = framesMap[frame.timestamp]!!.minus(configuredDelay)
                location = ShareYourRideRepository.getLocation(video!!.sessionId, lastProcessedSyncTimeStamp)
                inclination = ShareYourRideRepository.getInclination(video!!.sessionId, lastProcessedSyncTimeStamp)
            }

            if (sessionTelemetry!!.speed && location != null)
            {
                val bitmap = speedometerPrinter.getSpeedometer(location!!.speed)
                if(bitmap != null)
                {
                    canvas.drawBitmap(bitmap,leftBorder, bottomBorder - (30*scale) - bitmap.height, null)
                }

                val text = location!!.speed.times(CommonConstants.getSpeedConverter(applicationContext)).roundToInt().toString()
                canvas.drawText(text, speedValueLocation!!.first, speedValueLocation!!.second, paint)
                canvas.drawText(CommonConstants.getSpeedText(applicationContext), speedUnitsLocation!!.first, speedUnitsLocation!!.second, paint)
            }

            if (sessionTelemetry!!.leanAngle && inclination != null)
            {
                val bitmap = leanAnglePrinter.getAngle(inclination!!.roll)
                if(bitmap != null)
                {
                    canvas.drawBitmap(bitmap,centerBorder - (bitmap.width / 2), bottomBorder - (30*scale) - bitmap.height/2, null)
                }

                val text = (inclination!!.roll.absoluteValue.toString() + applicationContext.getString(R.string.degrees))
                canvas.drawText(text, leanAngleLocation!!.first, leanAngleLocation!!.second, paint)
            }

            if (sessionTelemetry!!.acceleration && inclination != null)
            {

                val bitmap = forcePrinter.getForceRepresentation((inclination!!.accelerationScalar/GRAVITY_ACCELERATION).toFloat(), AccelerationDirection.fromInt(inclination!!.accelerationDirection))

                if (bitmap != null)
                {
                    canvas.drawBitmap(bitmap,rightBorder - (bitmap.width/2), bottomBorder - (30*scale) - bitmap.height, null)
                }

                val text = String.format("%.2f", (inclination!!.accelerationScalar/GRAVITY_ACCELERATION).absoluteValue)
                canvas.drawText(text, accelerationValueLocation!!.first, accelerationValueLocation!!.second, paint)
                canvas.drawText(CommonConstants.getAccelerationText(applicationContext), accelerationUnitLocation!!.first, accelerationUnitLocation!!.second, paint)
            }

            if (sessionTelemetry!!.distance && location != null)
            {
                val distance = location!!.distance.times(CommonConstants.getShortDistanceConverter(applicationContext))
                val text = distance.roundToInt().toString() + CommonConstants.getShortDistanceText(applicationContext)
                canvas.drawText(text, distanceLocation!!.first, distanceLocation!!.second, paint)

                if (distanceDrawable != null)
                {
                    canvas.drawBitmap(distanceDrawable!!,distanceDrawableLocation!!.first,distanceDrawableLocation!!.second,null)
                }
            }

            if (sessionTelemetry!!.altitude  && location != null)
            {
                val text = (location!!.altitude.times(CommonConstants.getShortDistanceConverter(applicationContext)).roundToInt().toString() + CommonConstants.getShortDistanceText(applicationContext))
                canvas.drawText(text, altitudeLocation!!.first, altitudeLocation!!.second, paint)

                if (altitudeDrawable != null)
                {
                    canvas.drawBitmap(altitudeDrawable!!,altitudeDrawableLocation!!.first,altitudeDrawableLocation!!.second,null)
                }
            }

            if (sessionTelemetry!!.terrainInclination && location != null)
            {
                val text = (location!!.terrainInclination.toString() + applicationContext.getString(R.string.percentage))
                canvas.drawText(text, terrainInclinationLocation!!.first, terrainInclinationLocation!!.second, paint)

                if (terrainInclinationDrawable != null)
                {
                    canvas.drawBitmap(terrainInclinationDrawable!!,terrainInclinationDrawableLocation!!.first,terrainInclinationDrawableLocation!!.second,null)
                }
            }

            //Generate a ffmpeg frame
            val videoFrame: Frame = converter.convert(frameBitmap)
            videoFrame.timestamp = frame.timestamp
            recorder.setTimestamp( frame.timestamp)
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
        var percentage: Int = 0
        if (video!= null && video!!.totalVideoFrames > 0)
        {
            percentage = ((composedFramesCount.toFloat().div(video!!.totalVideoFrames.toFloat()))*100).toInt()

            Log.d(mClassName, "SYR -> Sending  VIDEO_CREATION_STATE_EVENT ===> $composedFramesCount - ${video!!.totalVideoFrames} - $percentage $state")
        }
        else
        {
            Log.d(mClassName, "SYR -> Sending  VIDEO_CREATION_STATE_EVENT ===> State ${state}")
        }

        if (state == VideoState.Failed || state == VideoState.Finished)
        {
            percentage = 100
        }

        val message = MessageBundle(MessageTypes.VIDEO_CREATION_STATE_EVENT, VideoCreationStateEvent(state, percentage), MessageTopics.VIDEO_CREATION_DATA)
        sendMessage(message)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createFileStream(duration: Long):OutputStream? {

        var imageUri: Uri? = null
        val resolver: ContentResolver = applicationContext.contentResolver
        var stream :OutputStream? = null
        try {

            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, sessionData!!.videoConvertedPath)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES+"/ShareYourRide/")
            contentValues.put(MediaStore.Video.Media.DURATION, duration)
            contentValues.put(MediaStore.Video.Media.TITLE, sessionData!!.videoConvertedPath)
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, sessionData!!.videoConvertedPath)
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            imageUri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (imageUri != null)
            {
                stream = resolver.openOutputStream(imageUri)
            }
        }
        catch (ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to create stream writing because ${ex.message}")
            ex.printStackTrace()
            if (imageUri != null) {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(imageUri, null, null)
            }
            stream?.close()
        }

        return stream
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