package com.bvillarroya_creations.shareyourride.services.repository

import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.services.inclination.InclinationService
import com.bvillarroya_creations.shareyourride.services.session.SessionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Provides data for the rest of the View Models
 */
class InclinationRepository
{
    companion object {

        private val inclinationService: InclinationService = InclinationService()

        /**
         * Gets the flag that points if the provider is ready
         */
        suspend fun getProviderReady(): MutableLiveData<Boolean>
        {
            return withContext(Dispatchers.Default)
            {
                inclinationService.providerReady
            }
        }
    }
}