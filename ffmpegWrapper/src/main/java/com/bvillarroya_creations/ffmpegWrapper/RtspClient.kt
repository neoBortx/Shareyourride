package com.bvillarroya_creations.ffmpegWrapper

class RtspClient {

    init {
        System.loadLibrary("avutil")
        System.loadLibrary("avcodec")
        System.loadLibrary("avformat")
        System.loadLibrary("swscale")
        System.loadLibrary("ffmpeg-wrapper")
    }

    external fun getStream(/*ip: String*/)
}