package com.example.shareyourride.viewmodels.userplayground

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
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
import com.example.shareyourride.configuration.SettingPreferencesIds
import com.example.shareyourride.services.video.VideoConnectionData

/**
 * view model that holds the data related to the video management
 *
 * It is shared by all fragments of the user play ground environment
 *
 * In general it is just a facade of a IRemoteVideoClient instance
 */
class VideoViewModel(application: Application) : AndroidViewModel(application), IMessageHandlerClient {

    //region private properties

    /**
     * APP context
     */
    private val context = getApplication<Application>().applicationContext

    /**
     * Unique UUID to identify each instance of this view model
     */
    private val guid: Int = java.util.UUID.randomUUID().toString().hashCode()
    //endregion


    //region public properties
    /**
     * true: The connection with the video is established
     * false: The connection with the video server is not established
     */
    val videoState = MutableLiveData<Boolean>()

    /**
     * The percentage of the video creation
     */
    val creationPercentage = MutableLiveData<Int>()

    /**
     * THe video creation state
     */
    val creationState = MutableLiveData<VideoState>()

    /**
     * The new frame received to show
     */
    val bitmapFrameToShow = MutableLiveData<Bitmap>()

    /**
     * The new frame received to show
     */
    val leanAngleForSynchronization = MutableLiveData<Int>()

    /**
     * The delay applied to the telemetry to synchronize the video with the data
     */
    val synchronizationDelay = MutableLiveData<Int>()

    //endregion

    //region init
    init
    {
        Log.i("VideoViewModel", "SYR -> $guid: Initiating VideoViewModel")
        this.createMessageHandler( "VideoViewModel", listOf(MessageTopics.VIDEO_DATA, MessageTopics.VIDEO_CREATION_DATA, MessageTopics.VIDEO_SYNCHRONIZATION_DATA))
        videoState.value = false
        creationPercentage.value = 0

        val getter = SettingPreferencesGetter(context)
        synchronizationDelay.postValue(getter.getIntOption(SettingPreferencesIds.VideoDelay))
    }
    //endregion

    //region message handling
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
                MessageTypes.VIDEO_STATE_EVENT ->
                {
                    Log.d("VideoViewModel", "SYR -> $guid: received VIDEO_STATE_EVENT updating state")
                    processVideoState(msg.messageData)
                }
                MessageTypes.VIDEO_CREATION_STATE_EVENT ->
                {
                    Log.d("VideoViewModel", "SYR -> $guid: received VIDEO_CREATION_STATE_EVENT updating state")
                    processVideoCreationState(msg.messageData)
                }
                MessageTypes.VIDEO_FRAME_SYNCHRONIZATION_DATA ->
                {
                    processVideoFrameData(msg.messageData)
                }
                MessageTypes.LEAN_ANGLE_SYNCHRONIZATION_DATA ->
                {
                    Log.d("VideoViewModel", "SYR -> $guid: received LEAN_ANGLE_SYNCHRONIZATION_DATA updating state")
                    processLeanAngleForSynchVideo(msg.messageData)
                }
                else ->
                {
                    Log.e("VideoViewModel", "SYR -> $guid: message ${msg.messageKey} no supported")
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("VideoViewModel", "SYR -> $guid: Unable to process message ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Update  the flag that points if the video connection is established or not
     */
    private fun processVideoState(messageBundleData: MessageBundleData)
    {
        try
        {
            if (messageBundleData.data is Boolean)
            {
                videoState.postValue(messageBundleData.data as Boolean)
            }
        }
        catch (ex: Exception)
        {
            Log.e("VideoViewModel", "SYR -> $guid: Unable to process video state because : ${ex.message}")
            ex.printStackTrace()
        }
    }

    private fun processVideoCreationState(messageBundleData: MessageBundleData)
    {
        try
        {
            if (messageBundleData.type == VideoCreationStateEvent::class)
            {
                val state = messageBundleData.data as VideoCreationStateEvent

                creationPercentage.postValue(state.creationPercentage)
                creationState.postValue(state.creationState)
            }
            else
            {
                Log.e("VideoViewModel","SYR -> $guid: Unable to process video creation state because received data is not supported")
            }
        }
        catch (ex: Exception)
        {
            Log.e("VideoViewModel","SYR -> $guid: Unable to process video creation state event because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Catch and notify to the view that there is one new frame to be printed
     */
    private fun processVideoFrameData(messageBundleData: MessageBundleData)
    {
        try
        {
            if (messageBundleData.type == Bitmap::class)
            {
                bitmapFrameToShow.postValue(messageBundleData.data as Bitmap)
            }
            else
            {
                Log.e("VideoViewModel","SYR -> $guid: Unable to process video frame for synchronization because received data is not supported")
            }
        }
        catch (ex: Exception)
        {
            Log.e("VideoViewModel","SYR -> $guid: Unable to process video frame because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Catch and notify to the view that there is one new lean data information to be printed
     */
    private fun processLeanAngleForSynchVideo(messageBundleData: MessageBundleData)
    {
        try
        {
            if (messageBundleData.type == Int::class)
            {
                leanAngleForSynchronization.postValue(messageBundleData.data as Int)
            }
            else
            {
                Log.e("VideoViewModel","SYR -> $guid: Unable to process lean angle for synchronization process because received data is not supported")
            }
        }
        catch (ex: Exception)
        {
            Log.e("VideoViewModel","SYR -> $guid: Unable to process video frame because ${ex.message}")
            ex.printStackTrace()
        }
    }


    /**
     * Creates the client depending on the configuration of the camera
     */
    private fun sendClientConfiguration()
    {
        Log.i("SessionViewModel", "SYR -> $guid: Sending VIDEO_CONNECTION_DATA message")
        val setting = SettingPreferencesGetter(getApplication())
        val protocol = setting.getStringOption(SettingPreferencesIds.CameraProtocol)
        val ip = setting.getStringOption(SettingPreferencesIds.CameraIp)
        val path = setting.getStringOption(SettingPreferencesIds.CameraPath)
        val port: String = setting.getStringOption(SettingPreferencesIds.CameraPort)
        val data = VideoConnectionData("rtsp"/*protocol*/,"", "",ip, port,path)

        val message = MessageBundle(MessageTypes.VIDEO_CONNECTION_DATA,data, MessageTopics.VIDEO_DATA)
        sendMessage(message)
    }

    //endregion

    //region public functions
    /**
     * Sends the VIDEO_STATE_REQUEST message to upper layers to ask for the video connection state
     */
    fun getVideoState()
    {
        try
        {
            Log.i("VideoViewModel", "SYR -> $guid: Send VIDEO_STATE_REQUEST")
            val messageAccuracy = MessageBundle(MessageTypes.VIDEO_STATE_REQUEST,"", MessageTopics.VIDEO_DATA)
            sendMessage(messageAccuracy)
        }
        catch (ex: Exception)
        {
            Log.e("VideoViewModel", "SYR -> $guid: Unable to ask for GPS state:  ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     *
     */
    fun changeVideoServer()
    {
        try {
            Log.i("VideoViewModel", "SYR -> $guid: changing the video server parameters")
            sendClientConfiguration()
        }
        catch(ex: java.lang.Exception)
        {
            Log.e("VideoViewModel", "SYR -> $guid: Unable to change wifi network because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    fun increaseDelay()
    {
        try
        {
            if (synchronizationDelay.value != null)
            {
                synchronizationDelay.postValue(synchronizationDelay.value!! + CommonConstants.VIDEO_DELAY_SEGMENT)
            }
            else
            {
                synchronizationDelay.postValue(0)
            }
        }
        catch(ex: java.lang.Exception)
        {
            Log.e("VideoViewModel", "SYR -> $guid: Unable to increase delay because: ${ex.message}")
            ex.printStackTrace()
        }

        val getter = SettingPreferencesGetter(context)
        getter.savePreferenceInt(synchronizationDelay.value!!,SettingPreferencesIds.VideoDelay)

        val message = MessageBundle(MessageTypes.CONFIGURE_VIDEO_DELAY, synchronizationDelay.value!!, MessageTopics.VIDEO_SYNCHRONIZATION_DATA)
        sendMessage(message)
    }

    fun decreaseDelay()
    {
        if (synchronizationDelay.value != null) {
            if (synchronizationDelay.value!! >= CommonConstants.VIDEO_DELAY_SEGMENT) {
                synchronizationDelay.postValue(synchronizationDelay.value!! - CommonConstants.VIDEO_DELAY_SEGMENT)
            }
        }
        else
        {
            synchronizationDelay.postValue(0)
        }

        val getter = SettingPreferencesGetter(context)
        getter.savePreferenceInt(synchronizationDelay.value!!,SettingPreferencesIds.VideoDelay)

        val message = MessageBundle(MessageTypes.CONFIGURE_VIDEO_DELAY, synchronizationDelay.value!!, MessageTopics.VIDEO_SYNCHRONIZATION_DATA)
        sendMessage(message)
    }
    //endregion

    /**
     * This method is called when the view model is destroyed
     */
    override fun onCleared()
    {
        try
        {
            Log.i("VideoViewModel", "SYR -> $guid: Clearing SessionViewModel")
            this.removeHandler()
        }
        catch (ex: Exception)
        {
            Log.e("VideoViewModel", "SYR -> $guid: Unable to process message ${ex.message}")
            ex.printStackTrace()
        }
    }

}