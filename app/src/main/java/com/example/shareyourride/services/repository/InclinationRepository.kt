package com.example.shareyourride.services.repository

import androidx.lifecycle.MutableLiveData
import com.example.shareyourride.services.inclination.InclinationService
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
        fun getProviderReady(): MutableLiveData<Boolean>?
        {
            return inclinationService.providerReady
        }
    }
}