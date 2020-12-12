package com.example.shareyourride.viewmodels.userplayground

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bvillarroya_creations.shareyourride.datamodel.data.Inclination
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTopics
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messenger.IMessageHandlerClient
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.messenger.MessageBundleData
import com.bvillarroya_creations.shareyourride.messenger.MessageHandler
import com.example.shareyourride.userplayground.common.AccelerationDirection
import kotlin.math.abs

/**
 * View model that holds the current data given by the gyroscopes and accelerometers sensors
 */
class InclinationViewModel : ViewModel(), IMessageHandlerClient
{

    //region public properties
    /**
     * The lean angle Y axis (roll) of the mobile phone
     */
    val leanAngle = MutableLiveData<Int>()

    /**
     * The direction of the lean angle, tilt to the right or the left
     */
    val leanDirection = MutableLiveData<LeanDirection>()

    /**
     * The direction of the acceleration
     */
    val accelerationDirection = MutableLiveData<AccelerationDirection>()

    /**
     * Acceleration magnitude combining x and y axis
     */
    val acceleration = MutableLiveData<Double>()
    //endregion

    //region enums
    enum class LeanDirection {
        /**
         * Unknown lean angle and direction
         */
        Unknown,

        /**
         * Positive lean angle, so the phone tilts to the right
         */
        Right,

        /**
         * Negative lean angle, so the phone tilts to the left
         */
        Left
    }

    //region message handlers
    init {
        this.createMessageHandler( "InclinationViewModel", listOf<String>(MessageTopics.INCLINATION_DATA))

        leanAngle.value = 0
        leanDirection.value = LeanDirection.Unknown

        acceleration.value = 0.0
        accelerationDirection.value = AccelerationDirection.Unknown
    }

    override lateinit var messageHandler: MessageHandler

    /**
     * Listen to session messages related to the inclination management
     *
     * @param msg: received message from the android internal queue
     */
    override fun processMessage(msg: MessageBundle)
    {
        try {

            when (msg.messageKey)
            {
                MessageTypes.INCLINATION_DATA_EVENT ->
                {
                    processInclinationData(msg.messageData)
                }

                else ->
                {
                    Log.e("InclinationViewModel", "SYR -> message ${msg.messageKey} no supported")
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("InclinationViewModel", "SYR -> Unable to process message ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    //region process messages
    /**
     * Process the message INCLINATION_DATA_EVENT
     *
     * @param msgData: The inclination values
     */
    private fun processInclinationData(msgData: MessageBundleData)
    {
        try
        {
            if (msgData.type == Inclination::class)
            {
                val inclination = msgData.data as Inclination

                processLean(inclination.roll)

                acceleration.postValue(inclination.accelerationScalar.toDouble())
                accelerationDirection.postValue(AccelerationDirection.fromInt(inclination.accelerationDirection))
            }
            else
            {
                Log.e("InclinationViewModel", "SYR -> No supported data type in a INCLINATION_DATA_EVENT")
            }
        }
        catch (ex: Exception)
        {
            Log.e("LocationViewModel", "SYR -> Unable to process message GPS_STATE_EVENT: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Calculate the lean direction and magnitude
     *
     * @param roll: the tilt in the y axis
     */
    private fun processLean(roll: Int)
    {
        try
        {
            //get the absolute value
            leanAngle.postValue(abs(roll))

            when {
                roll == 0 -> {
                    leanDirection.postValue(LeanDirection.Unknown)
                }
                roll > 0 -> {
                    leanDirection.postValue(LeanDirection.Right)
                }
                roll < 0 -> {
                    leanDirection.postValue(LeanDirection.Left)
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("LocationViewModel", "SYR -> Unable to process tilt because: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion
}