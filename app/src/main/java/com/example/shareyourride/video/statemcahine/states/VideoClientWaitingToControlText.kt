package com.example.shareyourride.video.statemcahine.states

import android.util.Log
import com.example.shareyourride.video.statemcahine.VideoClientStateContext

class VideoClientWaitingToControlText: IVideoClientState {
    override fun connect(context: VideoClientStateContext) {
        Log.e("VideoClientWaitingToControlText", "SYR -> VideoClientWaitingToControlText no support connect")
    }

    override fun needSynchronization(context: VideoClientStateContext) {
        Log.e("VideoClientWaitingToControlText", "SYR -> VideoClientWaitingToControlText no support needSynchronization")
    }

    override fun calculateDelay(context: VideoClientStateContext) {
        Log.e("VideoClientWaitingToControlText", "SYR -> VideoClientWaitingToControlText no support calculatingDelay")
    }

    override fun saveDelay(context: VideoClientStateContext) {
        Log.e("VideoClientConnected", "SYR -> VideoClientWaitingToControlText transiting to save delay")
        context.videoClientState = VideoClientDisconnected()
        context.stateListener?.onDisconnect()
    }

    override fun consumeVideo(context: VideoClientStateContext) {
        Log.e("VideoClientWaitingToControlText", "SYR -> VideoClientWaitingToControlText no support consumeVideo")
    }

    override fun disconnect(context: VideoClientStateContext) {
        Log.e("VideoClientWaitingToControlText", "SYR -> VideoClientWaitingToControlText transiting to Disconnected")
        context.videoClientState = VideoClientDisconnected()
        context.stateListener?.onDisconnect()
    }
}