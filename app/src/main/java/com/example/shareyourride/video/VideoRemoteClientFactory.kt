package com.example.shareyourride.video

import android.content.Context
import com.example.shareyourride.video.rtsp.RtspVideoClient

/**
 * Class in charge of instantiate the desired video client thinking on in the future
 * more video types of streams will be supported
 *
 * using the Factory pattern
 */
class VideoRemoteClientFactory {

    /**
     * All kind of supported video streams
     * At the moment RTSP client is implemented
     */
    enum class RemoteClientType
    {
        RtspClient,
    }


    /**
     * Class that generates a video client
     *
     * @param clientType: the type of client requested
     * @param context: The context of the APP
     * @return the video client
     */
    fun createVideoClient(clientType: RemoteClientType, context: Context): IRemoteVideoClient
    {
        when (clientType)
        {
            RemoteClientType.RtspClient ->
            {
                return RtspVideoClient(context)
            }
        }

    }


}