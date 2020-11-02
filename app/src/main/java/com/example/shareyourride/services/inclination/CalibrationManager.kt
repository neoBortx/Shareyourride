package com.example.shareyourride.services.inclination

import android.util.Log
import androidx.lifecycle.MutableLiveData
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.math.abs


/**
 * Manage the calibration of the Gyroscopes sensors
 */
class CalibrationManager
{

    //region constants
    companion object {
        /**
         * Delay of the procedure in milliseconds
         */
        private const val CALIBRATION_DELAY: Long = 5000

        /**
         * Timer in milliseconds
         */
        private const val CALIBRATION_TIMER: Long = 5000

        /**
         * Maximum number of retries to calibrate sensors
         */
        private const val MAX_ATTEMPTS = 4

        /**
         * Maximum number of values inserted in the set of values for the calibration
         * Used to prevent memory issues
         */
        private const val MAX_VALUES = 100

        /**
         * The accepted deviation in the calibrations
         * maximum difference between the minimum and the maximum degrees
         */
        private const val ACCEPTED_DEVIATION = 5
    }
    //endregion

    //region properties
    /**
     * Stores all roll values during the calibration period
     */
    private var rollArray = mutableListOf<Int>()

    /**
     * Stores all pitch values during the calibration period
     */
    private var pitchArray = mutableListOf<Int>()

    /**
     * Stores all azimuth values during the calibration period
     */
    private var azimuthArray = mutableListOf<Int>()

    /**
     * The timer to rule the period to obtain data in order to calibrate the sensor
     */
    private var calibrationTimer = Timer()

    /**
     * All attempt to calibrate the sensors
     */
    private var calibrationAttempt = 0

    /**
     * Number of values in the sample
     */
    private var numOfGatheredValues = 0

    /**
     * True if the calibration finishes successfully, false otherwise
     */
    val calibratedSuccessfully = MutableLiveData<Boolean>()

    /**
     * Points that calibration is ongoing
     */
    var isInEditMode = false

    /**
     * The reference lock use as the base to calculate the lean angle in the activity
     */
    var referenceRoll = 0

    /**
     * For future uses
     */
    var referencePitch = 0

    /**
     * For future uses
     */
    var referenceAzimuth = 0

    /**
     * Lock to avoid concurrent accesses to the data
     */
    private val lock = ReentrantReadWriteLock()
    //endregion

    /**
     * Start the calibration process
     */
    fun startCalibration()
    {
        Log.i("CalibrationManager", "SYR -> Starting calibration")

        restartValues()

        isInEditMode = true

        calibrationTimer = Timer()
        calibrationTimer.schedule(object : TimerTask() {
            override fun run()
            {
                processGatheredValues()
            }
        }, CALIBRATION_DELAY, CALIBRATION_TIMER)
    }

    /**
     * Process the list of gathered values during the calibration period
     */
    private fun processGatheredValues()
    {
        try
        {
            val rollDeviation= getDeviation(rollArray)
            val pitchDeviation= getDeviation(pitchArray)
            val azimuthDeviation= getDeviation(azimuthArray)

            Log.i("CalibrationManager", "SYR -> Obtained deviation is, roll: $rollDeviation, pitch: $pitchDeviation, azimuth: $azimuthDeviation")

            if (rollDeviation <= ACCEPTED_DEVIATION && pitchDeviation <= ACCEPTED_DEVIATION && azimuthDeviation <= ACCEPTED_DEVIATION)
            {
                calculateReferenceValues()
                Log.i("CalibrationManager", "SYR -> The calibration finishes successfully, reference values roll: $referenceRoll, pich: $referencePitch, azimuth: $referenceAzimuth")
                isInEditMode = false
                calibratedSuccessfully.postValue(true)
                stopTimer()
            }
            else
            {
                if (calibrationAttempt <= MAX_ATTEMPTS)
                {
                    Log.i("CalibrationManager", "SYR -> The deviation is not acceptable, trying it again")
                    clearValues()
                }
                else {
                    Log.i("CalibrationManager", "SYR -> The deviation is not acceptable and exceeded the number of calibration attempts ($calibrationAttempt)")
                    isInEditMode = false
                    calibratedSuccessfully.postValue(false)
                    stopTimer()
                }
                calibrationAttempt++
            }
        }
        catch (ex: Exception) {
            Log.i("CalibrationManager", "SYR -> Unable to process Gathered values because: ${ex.message}")
            ex.printStackTrace()
        }
    }

     /**
     * Clear the gathered values
     */
    private fun clearValues()
    {
        lock.write {
            numOfGatheredValues = 0

            try {
                rollArray.clear()
                pitchArray.clear()
                azimuthArray.clear()
            }
            catch (ex: Exception) {
                Log.i("CalibrationManager", "SYR -> Unable to clear values because: ${ex.message}")
                ex.printStackTrace()
            }
        }
    }

    private fun restartValues()
    {
        clearValues()

        referenceRoll = 0
        referencePitch = 0
        referenceAzimuth = 0
        calibrationAttempt = 0

        stopTimer()
    }

    private fun calculateReferenceValues()
    {
        lock.write {
            try {
                referenceRoll = rollArray.sum() / rollArray.count()
                referencePitch = pitchArray.sum() / pitchArray.count()
                referenceAzimuth = azimuthArray.sum() / azimuthArray.count()
            }
            catch (ex: Exception) {
                Log.i("CalibrationManager", "SYR -> Unable to clear values because: ${ex.message}")
                ex.printStackTrace()
            }
        }
    }

    private fun stopTimer()
    {
        try {
            calibrationTimer.cancel()
        }
        catch (ex: Exception)
        {
            Log.e("CalibrationManager", "SYR -> Unable to stop timer because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Inserts values in the calibration system
     *
     * @param roll: In degrees
     * @param pitch: In degrees
     * @param azimuth: In degrees
     */
    fun insertValues(roll: Int, pitch: Int, azimuth: Int)
    {
        if (isInEditMode && numOfGatheredValues < MAX_VALUES)
        {
            numOfGatheredValues++

            lock.write {
                try {
                    rollArray.add(roll)
                    pitchArray.add(pitch)
                    azimuthArray.add(azimuth)
                }
                catch (ex: Exception)
                {
                    ex.printStackTrace()
                }
            }
        }
        else
        {
            Log.e("CalibrationManager", "SYR -> Values discarded in calibration model, edit mode : $isInEditMode, attempts: $numOfGatheredValues")
        }
    }

    /**
     * Gets the difference between the lower and the higher degrees obtained in the gathered values
     *
     * @param list: The this of values to check
     * @return the deviations is acceptable
     */
    private fun getDeviation(list: List<Int>): Int
    {
        var min = 0
        var max = 0


        lock.read {
            try {

                if (list.any()) {
                    min = Collections.min(list)
                    max = Collections.max(list)
                }
                else
                {
                    Log.e("CalibrationManager", "SYR -> The given list is empty, unable to calculate the deviation")
                    min = 0
                    max = Int.MAX_VALUE
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

        }

        //return absolute value
        return abs(max - min)
    }
}