package com.example.shareyourride.video.statemcahine.states

import android.util.Log
import com.example.shareyourride.video.statemcahine.VideoClientStateContext

class VideoClientSynchronized : IVideoClientState {
    override fun connect(context: VideoClientStateContext) {
        Log.e("VideoClientConnected", "SYR -> Disconnected no support disconnect")
    }

    override fun needSynchronization(context: VideoClientStateContext) {
        Log.e("VideoClientConnected", "SYR -> Disconnected no support needSynchronization")
    }

    override fun calculateDelay(context: VideoClientStateContext) {
        Log.e("VideoClientConnected", "SYR -> Disconnected no support calculatingDelay")
    }

    override fun saveDelay(context: VideoClientStateContext) {
        Log.e("VideoClientConnected", "SYR -> Disconnected no support saveDelay")
    }

    override fun consumeVideo(context: VideoClientStateContext) {
        Log.e("VideoClientConnected", "SYR -> VideoClientConnected transiting to consuming video")
        context.videoClientState = VideoClientConsuming()
        context.stateListener?.onConsumeVideo()
    }

    override fun disconnect(context: VideoClientStateContext) {
        Log.e("VideoClientConnected", "SYR -> VideoClientConnected transiting to Disconnected")
        context.videoClientState = VideoClientDisconnected()
        context.stateListener?.onDisconnect()
    }
}