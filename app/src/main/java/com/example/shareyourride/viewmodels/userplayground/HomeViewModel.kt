package com.example.shareyourride.viewmodels.userplayground

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.bvillarroya_creations.shareyourride.datamodel.data.Location
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTopics
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messenger.IMessageHandlerClient
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.example.shareyourride.services.session.SessionService

class HomeViewModel(application: Application) : AndroidViewModel(application)
{

    /**
     * The context of the application
     */
    val context = application.applicationContext

    /*var gpsProviderReady = LocationRepository.getProviderReady()

    suspend fun startSession()
    {
        SessionRepository.startSession()
    }

    suspend fun stopSession()
    {
        SessionRepository.stopSession()
    }*/
}