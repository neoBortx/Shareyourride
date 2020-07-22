package com.bvillarroya_creations.shareyourride.telemetry.interfaces

import android.media.session.MediaSession

interface IDataProvider {

    enum class ProviderState{
        STOPED,
        WAITING_TO_PERMISSIONS,
        SUBSCRIBED,
    }

    fun configureProvider()

    fun subscribeProvider(callback: (ITelemetryData) -> Unit)

    fun stopProvider()

    fun getProviderState(): ProviderState
}