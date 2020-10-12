package com.example.shareyourride.video.statemcahine.states

import android.util.Log
import com.example.shareyourride.video.statemcahine.VideoClientStateContext
import com.example.shareyourride.video.statemcahine.states.IVideoClientState

class VideoClientConnected: IVideoClientState {
    override fun connect(context: VideoClientStateContext) {
        Log.e("VideoClientConnected", "SYR -> VideoClientConnected no support connect")
    }

    override fun needSynchronization(context: VideoClientStateContext) {
        Log.e("VideoClientConnected", "SYR -> VideoClientConnected transiting to needSynchronization")
        context.videoClientState = VideoClientWaitingToControlText()
        context.stateListener?.onNeedSynchronization()
    }

    override fun calculateDelay(context: VideoClientStateContext) {
        Log.e("VideoClientConnected", "SYR -> VideoClientConnected no support calculatingDelay")
    }

    override fun saveDelay(context: VideoClientStateContext) {
        Log.e("VideoClientConnected", "SYR -> VideoClientConnected transiting to Synchronized")
        context.videoClientState = VideoClientSynchronized()
        context.stateListener?.onSaveDelay()
    }

    override fun consumeVideo(context: VideoClientStateContext) {
        Log.e("VideoClientConnected", "SYR -> VideoClientConnected no support consumeVideo")
    }

    override fun disconnect(context: VideoClientStateContext) {
        Log.e("VideoClientConnected", "SYR -> VideoClientConnected transiting to Disconnected")
        context.videoClientState = VideoClientDisconnected()
        context.stateListener?.onDisconnect()
    }
}