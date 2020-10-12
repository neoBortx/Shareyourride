package com.example.shareyourride.video.statemcahine.states

import android.util.Log
import com.example.shareyourride.video.statemcahine.VideoClientStateContext
import com.example.shareyourride.video.statemcahine.states.IVideoClientState

class VideoClientWaitingToSyncText: IVideoClientState {
    override fun connect(context: VideoClientStateContext) {
        Log.e("VideoClientWaitingToSyncText", "SYR -> VideoClientWaitingToSyncText no support connect")
    }

    override fun needSynchronization(context: VideoClientStateContext) {
        Log.e("VideoClientWaitingToSyncText", "SYR -> VideoClientWaitingToSyncText no support needSynchronization")
    }

    override fun calculateDelay(context: VideoClientStateContext) {
        Log.e("VideoClientWaitingToSyncText", "SYR -> VideoClientWaitingToSyncText no support calculatingDelay")
        context.videoClientState = VideoClientWaitingToSyncText()
        context.stateListener?.onCalculateDelay()
    }

    override fun saveDelay(context: VideoClientStateContext) {
        Log.e("VideoClientWaitingToSyncText", "SYR -> VideoClientWaitingToSyncText no support saveDelay")
    }

    override fun consumeVideo(context: VideoClientStateContext) {
        Log.e("VideoClientWaitingToSyncText", "SYR -> VideoClientWaitingToSyncText no support consumeVideo")
    }

    override fun disconnect(context: VideoClientStateContext) {
        Log.e("VideoClientWaitingToSyncText", "SYR -> VideoClientWaitingToSyncText transiting to Disconnected")
        context.videoClientState = VideoClientDisconnected()
        context.stateListener?.onDisconnect()
    }
}