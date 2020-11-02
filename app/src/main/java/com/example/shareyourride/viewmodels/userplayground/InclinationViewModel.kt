package com.example.shareyourride.viewmodels.userplayground

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bvillarroya_creations.shareyourride.datamodel.data.Inclination
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTopics
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messagesdefinition.dataBundles.InclinationCalibrationData
import com.bvillarroya_creations.shareyourride.messenger.IMessageHandlerClient
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.messenger.MessageBundleData
import com.bvillarroya_creations.shareyourride.messenger.MessageHandler
import kotlin.math.abs

/**
 * View model that holds the current data given by the gyroscopes and accelerometers sensors
 */
class InclinationViewModel : ViewModel(), IMessageHandlerClient
{

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
                MessageTypes.INCLINATION_CALIBRATION_END ->
                {
                    Log.d("InclinationViewModel", "SYR -> received INCLINATION_CALIBRATION_END updating state")
                    //processInclinationCalibrationEnd(msg.messageData)
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

                //get the absolute value
                leanAngle.postValue(abs(inclination.roll))
                when {
                    inclination.roll == 0 -> {
                        leanDirection.postValue(LeanDirection.Unknown)
                    }
                    inclination.roll > 0 -> {
                        leanDirection.postValue(LeanDirection.Right)
                    }
                    inclination.roll < 0 -> {
                        leanDirection.postValue(LeanDirection.Left)
                    }
                }
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
     * Process the INCLINATION_CALIBRATION_END
     */
    /*private fun processInclinationCalibrationEnd(msgData: MessageBundleData)
    {
        try
        {
            if (msgData.type == InclinationCalibrationData::class)
            {
                val resultData = msgData.data as InclinationCalibrationData
                sensorsCalibrated.postValue(resultData.result)
            }
            else
            {
                Log.e("InclinationViewModel", "SYR -> No supported data type in a INCLINATION_DATA_EVENT")
            }
        }
        catch (ex: Exception)
        {
            Log.e("InclinationViewModel", "SYR -> Unable to process message GPS_STATE_EVENT: ${ex.message}")
            ex.printStackTrace()
        }
    }*/
    //endregion

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
     *
     */
    val acceleration = MutableLiveData<Float>()

    //endregion
}