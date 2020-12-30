/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.services.inclination

import android.util.Log
import androidx.lifecycle.MutableLiveData
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


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
        private const val CALIBRATION_DELAY: Long = 6000

        /**
         * Timer in milliseconds
         */
        private const val CALIBRATION_TIMER: Long = 6000

        /**
         * Maximum number of retries to calibrate sensors
         */
        private const val MAX_ATTEMPTS = 10

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
     * Stores all lineal acceleration values
     */
    private var linealAcceleration : MutableList<FloatArray> = ArrayList()

    /**
     * Stores all gravity values
     */
    private var gravity : MutableList<FloatArray> = ArrayList()

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
     * The acceleration in three axis
     */
    var referenceAcceleration = FloatArray(3)

    /**
     *
     */
    var referenceGravity = FloatArray(3)
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
                linealAcceleration.clear()
                gravity.clear()
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
        referenceAcceleration = FloatArray(3)
        referenceGravity = FloatArray(3)

        calibrationAttempt = 0

        stopTimer()
    }

    private fun calculateReferenceValues()
    {
        lock.write {
            try {
                referenceRoll = rollArray.average().roundToInt()
                referencePitch = pitchArray.average().roundToInt()
                referenceAzimuth = azimuthArray.average().roundToInt()

                referenceAcceleration[0] = linealAcceleration.map { it[0].absoluteValue }.max() ?: 0.0F
                referenceAcceleration[1] = linealAcceleration.map { it[1].absoluteValue }.max() ?: 0.0F
                referenceAcceleration[2] = linealAcceleration.map { it[2].absoluteValue }.max() ?: 0.0F

                referenceAcceleration[0] += (referenceAcceleration[0] * 0.8).toFloat()
                referenceAcceleration[1] += (referenceAcceleration[1] * 0.8).toFloat()
                referenceAcceleration[2] += (referenceAcceleration[0] * 0.8).toFloat()

                referenceGravity[0] = gravity.map { it[0].absoluteValue }.max() ?: 0.0F
                referenceGravity[0] = gravity.map { it[0].absoluteValue }.max() ?: 0.0F
                referenceGravity[0] = gravity.map { it[0].absoluteValue }.max() ?: 0.0F
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
     * @param acceleration: In meters per second 2
     */
    fun insertValues(roll: Int, pitch: Int, azimuth: Int, acceleration: FloatArray, gravity: FloatArray)
    {
        if (isInEditMode && numOfGatheredValues < MAX_VALUES)
        {
            numOfGatheredValues++

            lock.write {
                try {
                    rollArray.add(roll)
                    pitchArray.add(pitch)
                    azimuthArray.add(azimuth)
                    linealAcceleration.add(acceleration)
                    this.gravity.add(gravity)
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