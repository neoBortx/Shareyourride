package com.example.shareyourride.viewmodels.userplayground

import android.app.Application
import android.view.TextureView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.shareyourride.configuration.SettingPreferencesGetter
import com.example.shareyourride.configuration.SettingPreferencesIds
import com.example.shareyourride.video.IRemoteVideoClient
import com.example.shareyourride.video.RemoteVideoConfiguration
import com.example.shareyourride.video.VideoRemoteClientFactory
import com.example.shareyourride.video.statemcahine.VideoClientState

/**
 * view model that holds the data related to the video management
 *
 * It is shared by all fragments of the user play ground environment
 *
 * In general it is just a facade of a IRemoteVideoClient instance
 */
class VideoViewModel(application: Application) : AndroidViewModel(application) {

    init {
        createClient()
    }

    /**
     * The client that handles de video
     */
    private lateinit var videoClient: IRemoteVideoClient

    /**
     * APP context
     */
    private val context = getApplication<Application>().applicationContext

    /**
     * facade of the delay of the video
     */
    //var videoDelay =  videoClient.delayInMilliseconds

    /**
     * Facade of teh state of the video client
     */
    var clientState = videoClient.clientState

    /**
     * flag to point if the video window has to be shown or hidden
     */
    var showVideo = MutableLiveData<Boolean>()

    init {
        showVideo.value = false
        clientState.observeForever {
            showVideo.value = (it != VideoClientState.None && it != VideoClientState.Disconnected)
        }
    }

    //region video management fun
    /**
     * Creates the client depending on the configuration of the camera
     */
    fun createClient()
    {
        val setting = SettingPreferencesGetter(getApplication())
        val protocol = setting.getStringOption(SettingPreferencesIds.CameraProtocol)
        val videoFactory = VideoRemoteClientFactory()

        //if (protocol == context.getString(R.string.rtsp_protocol))
        //{
            videoClient = videoFactory.createVideoClient(VideoRemoteClientFactory.RemoteClientType.RtspClient, getApplication())
        //}
        /*else
        {
            Log.e("VideoViewModel", "SYR -> NO supported video protocol $protocol")
        }*/
    }

    /**
     *
     */
    fun configureClient(textureView: TextureView)
    {

        val setting = SettingPreferencesGetter(getApplication())
        val protocol = setting.getStringOption(SettingPreferencesIds.CameraProtocol)
        val ip = setting.getStringOption(SettingPreferencesIds.CameraIp)
        val path = setting.getStringOption(SettingPreferencesIds.CameraPath)
        val port: String = setting.getStringOption(SettingPreferencesIds.CameraPort)
        val videoStream = if (port != "0")
        {
            "rtsp" +"://"+ ip+":"+port.toString()+"/"+ path
        }
        else {
            "rtsp" +"://"+ ip+"/"+ path
        }

        videoClient.configureClient(RemoteVideoConfiguration(textureView, videoStream))
    }

    /**
     *
     */
    fun connect()
    {
        videoClient.connect()
    }

    /**
     * Get the delay of the video sent by the camera
     */
    fun getTheVideoDelay()
    {
        videoClient.startSync(this::handleDelay)
    }

    /**
     * Start processing the video stream
     */
    fun startConsuming()
    {
        videoClient.startConsuming()
    }

    /**
     * Finish the connection
     */
    fun disconnect()
    {
        videoClient.disconnect()
    }

    //endregion

    //region private functions
    private fun handleDelay(delay: Long)
    {
        //ideoDelay.value = videoClient.delayInMilliseconds
    }
    //endregion

}