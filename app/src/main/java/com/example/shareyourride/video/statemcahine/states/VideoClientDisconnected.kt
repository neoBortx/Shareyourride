package com.example.shareyourride.video.statemcahine.states

import android.util.Log
import com.example.shareyourride.video.statemcahine.VideoClientStateContext
import com.example.shareyourride.video.statemcahine.states.IVideoClientState

/**
 * Client is not connected
 */
class VideoClientDisconnected : IVideoClientState {
    override fun connect(context: VideoClientStateContext) {
        Log.e("VideoClientDisconnected", "SYR -> VideoClientDisconnected transiting to connect")
        context.videoClientState = VideoClientConnected()
        context.stateListener?.onConnect()
    }

    override fun needSynchronization(context: VideoClientStateContext) {
        Log.e("VideoClientDisconnected", "SYR -> VideoClientDisconnected no support needSynchronization")
    }

    override fun calculateDelay(context: VideoClientStateContext) {
        Log.e("VideoClientDisconnected", "SYR -> VideoClientDisconnected no support calculatingDelay")
    }

    override fun saveDelay(context: VideoClientStateContext) {
        Log.e("VideoClientDisconnected", "SYR -> VideoClientDisconnected no support saveDelay")
    }

    override fun consumeVideo(context: VideoClientStateContext) {
        Log.e("VideoClientDisconnected", "SYR -> VideoClientDisconnected no support consumeVideo")
    }

    override fun disconnect(context: VideoClientStateContext) {
        Log.e("VideoClientDisconnected", "SYR -> VideoClientDisconnected no support disconnect")
    }
}