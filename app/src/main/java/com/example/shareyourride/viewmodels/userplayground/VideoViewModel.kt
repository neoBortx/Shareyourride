package com.example.shareyourride.viewmodels.userplayground

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTopics
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messenger.IMessageHandlerClient
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.messenger.MessageBundleData
import com.bvillarroya_creations.shareyourride.messenger.MessageHandler
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
    //endregion


    //region public properties
    /**
     * true: The connection with the video is established
     * false: The connection with the video server is not established
     */
    val videoState = MutableLiveData<Boolean>()
    //endregion

    //region init
    init {

        this.createMessageHandler( "VideoViewModel", listOf(MessageTopics.VIDEO_DATA))
        sendClientConfiguration()
        videoState.value = false
        /*clientState.observeForever {
            showVideo.value = (it != VideoClientState.None && it != VideoClientState.Disconnected)
        }*/
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
                    Log.d("VideoViewModel", "SYR -> received VIDEO_STATE_EVENT updating state")
                    processVideoState(msg.messageData)
                }
                else ->
                {
                    Log.e("VideoViewModel", "SYR -> message ${msg.messageKey} no supported")
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("VideoViewModel", "SYR -> Unable to process message ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Update  the flag that points if the video connection is established or not
     */
    private fun processVideoState(messageBundleData: MessageBundleData)
    {
        try {
            if (messageBundleData.data is Boolean)
            {
                videoState.postValue(messageBundleData.data as Boolean)
            }
        }
        catch (ex: Exception)
        {
            Log.e("VideoViewModel", "SYR -> Unable to process video state because : ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Creates the client depending on the configuration of the camera
     */
    private fun sendClientConfiguration()
    {
        Log.i("SessionViewModel", "SYR -> Sending VIDEO_CONNECTION_DATA message")
        val setting = SettingPreferencesGetter(getApplication())
        val protocol = setting.getStringOption(SettingPreferencesIds.CameraProtocol)
        val ip = setting.getStringOption(SettingPreferencesIds.CameraIp)
        val path = setting.getStringOption(SettingPreferencesIds.CameraPath)
        val port: String = setting.getStringOption(SettingPreferencesIds.CameraPort)
        val data = VideoConnectionData(protocol,"", "",ip, port,path)

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
            Log.i("VideoViewModel", "SYR -> Send VIDEO_STATE_REQUEST")
            val messageAccuracy = MessageBundle(MessageTypes.VIDEO_STATE_REQUEST,"", MessageTopics.VIDEO_DATA)
            sendMessage(messageAccuracy)
        }
        catch (ex: Exception)
        {
            Log.e("LocationViewModel", "SYR -> Unable to ask for GPS state:  ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     *
     */
    fun changeVideoServer()
    {
        try {
            Log.i("LocationViewModel", "SYR -> changing the video server parameters")
            sendClientConfiguration()
        }
        catch(ex: java.lang.Exception)
        {
            Log.e("WifiViewModel", "SYR -> Unable to change wifi network because: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

}