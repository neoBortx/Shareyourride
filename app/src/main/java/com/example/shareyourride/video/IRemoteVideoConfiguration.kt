package com.example.shareyourride.video

import android.view.TextureView

/**
 * The configuration of the video client
 *
 * Each client requires a different configuration, so each configuration data must implement this interface
 */
class RemoteVideoConfiguration(

    /**
     * This is the surface that shows the video
     */
    val textureView: TextureView,

    /**
     * The Url of the video server
     */
    val serverUrl: String
)