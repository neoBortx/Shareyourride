package com.example.shareyourride.services.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.shareyourride.services.location.LocationService

/**
 * Provides data related to location
 */
class LocationRepository
{
    companion object {

        /**
         * Service in charge of manage the location
         */
        private var locationService: LocationService? = null

        /**
         * Starts the location service
         */
        fun initializeService(context: Context)
        {
            if (locationService == null)
            {
                locationService = LocationService()
            }
        }

        /**
         * Gets the flag that points if the provider is ready
         */
        fun getProviderReady(): MutableLiveData<Boolean>?
        {
            return if (locationService != null) {
                locationService?.providerReady
            }
            else {
                Log.e("LocationRepository", "SYR -> Location service is not initialized yet, unable to get the state of the provider")
                null
            }
        }

        /**
         * Gets the altitude
         */
        fun getAltitude(): MutableLiveData<Double>?
        {
            return if (locationService != null) {
                locationService?.altitude
            }
            else {
                Log.e("LocationRepository", "SYR -> Location service is not initialized yet, unable to get the altitude")
                null
            }
        }

        /**
         * Gets the speed
         */
        fun getSpeed(): MutableLiveData<Float>?
        {
            return if (locationService != null) {
                locationService?.speed
            }
            else {
                Log.e("LocationRepository", "SYR -> Location service is not initialized yet, unable to get the speed")
                null
            }
        }

        /**
         * Gets the terrain inclination
         */
        fun getTerrainInclination(): MutableLiveData<Int>?
        {
            return if (locationService != null) {
                locationService?.terrainInclination
            }
            else {
                Log.e("LocationRepository", "SYR -> Location service is not initialized yet, unable to get the terrain inclination")
                null
            }
        }
    }
}