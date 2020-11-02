package com.bvillarroya_creations.shareyourride.services.repository

import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.services.session.SessionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Provides data for the rest of the View Models
 */
class VideoRepository
{
    companion object {

        private val sessionService: SessionService = SessionService()

        /**
         * Initialize the session with:
         * - A random unique identifier
         * - Initialize the data base in case it hasn't
         * - Insert the new session in the data base
         * - Send the rest of the view models the message to start acquiring data
         */
        suspend fun startSession()
        {
            withContext(Dispatchers.Default)
            {
                sessionService.startSession()
            }
        }

        /**
         * Stop the session with:
         * - Update the state of the session
         * - Send a message to all telemetry view models to stop collecting data
         * - Send a message to the video view model to stop collecting data
         */
        suspend fun stopSession()
        {
            withContext(Dispatchers.Default)
            {
                sessionService.stopSession()
            }
        }

        /**
         * Removes the session from the data data base.
         * If the state is running, perform the stop operation
         */
        suspend fun discardSession()
        {
            withContext(Dispatchers.Default)
            {
                sessionService.discardSession()
            }
        }

        /**
         * Gets the session identifier
         */
        suspend fun getSessionId(): MutableLiveData<String>
        {
            return withContext(Dispatchers.Default)
            {
                sessionService.sessionId
            }
        }

        /**
         * Gets the state of the current session,
         */
        suspend fun getSessionState(): MutableLiveData<SessionService.SessionState>
        {
            return withContext(Dispatchers.Default)
            {
                sessionService.sessionState
            }
        }
    }
}