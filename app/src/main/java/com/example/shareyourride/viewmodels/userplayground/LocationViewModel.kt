/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.viewmodels.userplayground

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bvillarroya_creations.shareyourride.datamodel.data.Location
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTopics
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messenger.IMessageHandlerClient
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.messenger.MessageHandler
import com.example.shareyourride.common.CommonConstants
import kotlin.math.roundToInt

class LocationViewModel : ViewModel(), IMessageHandlerClient
{
    //region public properties
    /**
     * true: enabled and received at least a gps position with an acceptable accuracy
     * false: disabled or no received a GOS position with a valid accuracy
     */
    val gpsState = MutableLiveData<Boolean>()

    /**
     * Speed in kilometers per hour
     */
    val speed = MutableLiveData<Float>()

    /**
     * Altitude in meters
     */
    val altitude = MutableLiveData<Int>()

    /**
     * Terrain inclination in percentage
     */
    val terrainInclination = MutableLiveData<Int>()

    /**
     * Terrain inclination in percentage
     */
    val distance = MutableLiveData<Long>()
    //endregion

    //region message handlers
    init {
        this.createMessageHandler( "LocationViewModel", listOf<String>(MessageTopics.GPS_DATA))

        distance.postValue(0)
        terrainInclination.postValue(0)
        altitude.postValue(0)
        speed.postValue(0F)
    }

    override lateinit var messageHandler: MessageHandler

    /**
     * Listen to session messages related to the session management
     *
     * @param msg: received message from the android internal queue
     */
    override fun processMessage(msg: MessageBundle)
    {
        try {

            when (msg.messageKey)
            {
                MessageTypes.GPS_DATA_EVENT ->
                {
                    processGpsData(msg)
                }
                MessageTypes.GPS_STATE_EVENT ->
                {
                    Log.d("LocationViewModel", "SYR -> received GPS_STATE_EVENT updating state")
                    processGpsState(msg)
                }
                else ->
                {
                    Log.e("LocationViewModel", "SYR -> message ${msg.messageKey} no supported")
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("LocationViewModel", "SYR -> Unable to process message ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    //region process messages
    /**
     * Process the message GPS_STATE_EVENT
     *
     * @param msg: The content of the message
     */
    private fun processGpsState(msg: MessageBundle)
    {
        try
        {
            if (msg.messageData.type == Boolean::class)
            {
                gpsState.postValue(msg.messageData.data as Boolean)
                Log.d("LocationViewModel", "SYR -> GPS state ${gpsState.value}")
            }
        }
        catch (ex: Exception)
        {
            Log.e("LocationViewModel", "SYR -> Unable to process message GPS_STATE_EVENT: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Process the message GPS_DATA_EVENT
     *
     * @param msg: The content of the message
     */
    private fun processGpsData(msg: MessageBundle)
    {
        try
        {
            if (msg.messageData.type == Location::class)
            {
                val location = msg.messageData.data as Location

                speed.postValue((location.speed))
                altitude.postValue(location.altitude.roundToInt())
                terrainInclination.postValue(location.terrainInclination)
                distance.postValue(location.distance)
            }
        }
        catch (ex: Exception)
        {
            Log.e("LocationViewModel", "SYR -> Unable to process message GPS_STATE_EVENT: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    //region public functions
    fun getGpsState()
    {
        try
        {
            Log.i("LocationViewModel", "SYR -> Send GPS_START_ACQUIRING_ACCURACY")
            val messageAccuracy = MessageBundle(MessageTypes.GPS_START_ACQUIRING_ACCURACY,"", MessageTopics.GPS_DATA)
            sendMessage(messageAccuracy)
            Log.i("LocationViewModel", "SYR -> Send GPS_STATE_REQUEST")
            val message = MessageBundle(MessageTypes.GPS_STATE_REQUEST,"", MessageTopics.GPS_DATA)
            sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("LocationViewModel", "SYR -> Unable to ask for GPS state:  ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion
}