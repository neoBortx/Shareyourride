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
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

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

    enum class AccelerationDirection {
        /**
         * Unknown force
         */
        Unknown,

        /**
         * Positive force in the x axis
         */
        Right,

        /**
         * Negative force in the x axis
         */
        Left,

        /**
         * Positive force in the y axis
         */
        Front,

        /**
         * Negative force in the y axis
         */
        Back,
    }
    //endregion

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
                    Log.d("InclinationViewModel", "SYR -> received INCLINATION_DATA_EVENT updating data")
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

                processAcceleration(inclination.acceleration,inclination.pitch)
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

    /**
     * Calculate the acceleration direction and magnitude
     *
     * @param accelerationVector: the acceleration in x,y and z axis
     * @param pitch: the rotation in the x axis
     */
    private fun processAcceleration(accelerationVector: FloatArray, pitch: Int)
    {
        try
        {
            if (accelerationVector.count() >= 3)
            {
                //apply high pass filter
                val x  = accelerationVector[0]
                val y  = accelerationVector[1]
                val z= accelerationVector[2]

                val longitudinalValue = determineLongitudinalValue(x,y,pitch)

                getAccelerationDirection(longitudinalValue, y)

                acceleration.postValue(sqrt((longitudinalValue.pow(2.0F) + y.pow(2.0F) + z.pow(2.0F)).toDouble()))
            }
        }
        catch (ex: Exception)
        {
            Log.e("LocationViewModel", "SYR -> Unable to process acceleration because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Calculate the direction of the acceleration getting the higher absolute value
     */
    private fun getAccelerationDirection(longitudinalValue: Float, y :Float)
    {
        //To simplify the interface, only show the dominant direction of the vector
        if(longitudinalValue.absoluteValue >= y.absoluteValue)
        {
            accelerationDirection.postValue(if (longitudinalValue < 0) AccelerationDirection.Left else AccelerationDirection.Right)
        }
        else
        {
            accelerationDirection.postValue(if (y < 0) AccelerationDirection.Back else AccelerationDirection.Front)
        }

    }

    /**
     * Determine the higher force
     * @param xAxis: Force in x axis
     * @param zAxis: Force in z axis
     * @param pitch: Rotating in x axis
     *
     * @return the dominant force
     */
    private fun determineLongitudinalValue(xAxis: Float, zAxis: Float, pitch: Int): Float
    {
        return when {
            pitch.absoluteValue > 45 -> {
                zAxis
            }
            pitch.absoluteValue == 45 -> {
                if (zAxis.absoluteValue > xAxis.absoluteValue) zAxis else xAxis
            }
            else -> {
                xAxis
            }
        }
    }
    //endregion
}