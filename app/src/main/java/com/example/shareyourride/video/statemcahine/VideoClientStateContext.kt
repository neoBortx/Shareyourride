package com.example.shareyourride.video.statemcahine

import android.util.Log
import com.example.shareyourride.video.statemcahine.states.*

class VideoClientStateContext {

    var videoClientState : IVideoClientState = VideoClientDisconnected()

    var stateListener: IVideoClientStateListener? = null

    fun setListener(l: IVideoClientStateListener)
    {
        stateListener = l
    }
    /**
     *
     */
    fun setNextState(nextState: VideoClientTransition)
    {
        when(nextState)
        {
            VideoClientTransition.None ->
            {
                Log.e("IVideoClientStateListener", "SYR -> NO supported state")
            }
            VideoClientTransition.Connect ->
            {
                videoClientState.connect(this)
            }
            VideoClientTransition.NeedSynchronization ->
            {
                videoClientState.needSynchronization(this)
            }
            VideoClientTransition.CalculateDelay ->
            {
                videoClientState.calculateDelay(this)
            }
            VideoClientTransition.SaveDelay ->
            {
                videoClientState.saveDelay(this)
            }
            VideoClientTransition.ConsumeVideo ->
            {
                videoClientState.consumeVideo(this)
            }
            VideoClientTransition.Disconnect ->
            {
                videoClientState.disconnect(this)
            }
        }
    }

    /**
     *
     */
    fun getCurrentState(): VideoClientState
    {
        when(videoClientState::class.java)
        {
            VideoClientConnected::class.java ->
            {
                return VideoClientState.Connected
            }
            VideoClientConsuming::class.java ->
            {
                return VideoClientState.Consuming
            }
            VideoClientDisconnected::class.java ->
            {
                return VideoClientState.Disconnected
            }
            VideoClientSynchronized::class.java ->
            {
                return VideoClientState.Synchronized
            }
            VideoClientWaitingToSyncText::class.java ->
            {
                return VideoClientState.WaitingToSyncText
            }
            VideoClientWaitingToControlText::class.java ->
            {
                return VideoClientState.WaitingToControlText
            }
        }

        return VideoClientState.None
    }

}