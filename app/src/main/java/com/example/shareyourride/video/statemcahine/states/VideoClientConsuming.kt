package com.example.shareyourride.video.statemcahine.states

import android.util.Log
import com.example.shareyourride.video.statemcahine.VideoClientStateContext
import com.example.shareyourride.video.statemcahine.states.IVideoClientState

class VideoClientConsuming : IVideoClientState {
    override fun connect(context: VideoClientStateContext) {
        Log.e("VideoClientConsuming", "SYR -> VideoClientConsuming no support connect")
    }

    override fun needSynchronization(context: VideoClientStateContext) {
        Log.e("VideoClientConsuming", "SYR -> VideoClientConsuming no support needSynchronization")
    }

    override fun calculateDelay(context: VideoClientStateContext) {
        Log.e("VideoClientConsuming", "SYR -> VideoClientConsuming no support calculatingDelay")
    }

    override fun saveDelay(context: VideoClientStateContext) {
        Log.e("VideoClientConsuming", "SYR -> VideoClientConsuming no support saveDelay")
    }

    override fun consumeVideo(context: VideoClientStateContext) {
        Log.e("VideoClientConsuming", "SYR -> VideoClientConsuming no support consumeVideo")
    }

    override fun disconnect(context: VideoClientStateContext) {
        Log.e("VideoClientConsuming", "SYR -> VideoClientConsuming transiting to Disconnected")
        context.videoClientState = VideoClientDisconnected()
        context.stateListener?.onDisconnect()
    }
}