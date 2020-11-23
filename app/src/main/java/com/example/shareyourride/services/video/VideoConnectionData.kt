package com.example.shareyourride.services.video

/**
 * In order to not
 */
class VideoConnectionData(
    /**
     * video protocol used
     */
    val protocol: String,

    /**
     * If the stream is secured a user name is required
     */
    val userName: String,
    /**
     * If the stream is secured a password is required
     */
    val password: String,
    /**
     * The ip of the video server
     */
    val ip: String,
    /**
     * The port used by the video server
     */
    val port: String,
    /**
     * The name of the served stream
     */
    val videoName: String)