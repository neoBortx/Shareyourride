/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.telemetry.inclination

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.IDataProvider
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import io.reactivex.rxjava3.disposables.Disposable
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class InclinationProvider(private val context: Context): IDataProvider, SensorEventListener {

    //region sensor properties
    /**
     * Sensor in charge of manage the rotation of the mobile phone
     */
    private var mSensorManagerRotationVector: Sensor? = null

    /**
     * Sensor in charge of manage the g-force of each axis in the mobile phone
     */
    private var mSensorManagerGravity: Sensor? = null

    /**
     * Sensor in charge of manage the acceleration of each axis of the mobile phone
     */
    private var mSensorManagerLinearAcceleration: Sensor? = null

    /**
     * Sensor in charge of manage the magnetic field
    */
    private var mSensorManagerMagnetometer: Sensor? = null

    /**
     * The android sensor manager
     */
    private lateinit var sensorManager: SensorManager

    /**
     * Store the current state of the telemetry data provider
     */
    private var mProviderState: IDataProvider.ProviderState = IDataProvider.ProviderState.STOPPED

    private var mTimer : Disposable? = null
    //endregion


    //region buffers
    private val lock = Any()

    /**
     * The rotation angles of the device
     * The last ten positions
     */
    private var mRollBuffer = mutableListOf<Int>()

    /**
     * The rotation angles of the device
     * The last ten positions
     */
    private var mPitchBuffer = mutableListOf<Int>()

    /**
     * The rotation angles of the device
     * The last ten positions
     */
    private var mAzimuthBuffer =  mutableListOf<Int>()

    /**
     * The linear acceleration of the device
     * The linear acceleration of the device buffer
     */
    private var mLinearAccelerationBuffer : MutableList<FloatArray> = ArrayList()

    /**
     * The gravity that is affected to the device
     * The linear acceleration of the device buffer
     */
    private var mGravityBuffer  : MutableList<FloatArray> = ArrayList()
    //endregion
    /**
     * Initialize sensors:
     * Gravity
     * Rotation vector
     * Linear Acceleration
     */
    override fun configureProvider() {
        sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
        mSensorManagerGravity            = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        mSensorManagerRotationVector     = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        mSensorManagerLinearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        mSensorManagerMagnetometer       = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.e("InclinationProvider", "SYR -> Accuracy Changed")
    }

    /**
     * Subscribe to listen gravity, acceleration and orientation
     *
     * @param callback: function that will process sensor changed
     */
    override fun subscribeProvider(callback: (ITelemetryData) -> Unit)
    {
        try {
            Log.d("InclinationProvider", "SYR-> Subscribing to inclination provider")
            if (mSensorManagerGravity != null)
            {
                Log.d("InclinationProvider", "SYR-> Subscribing to mSensorManagerGravity provider")
                sensorManager.registerListener(
                    this,
                    mSensorManagerGravity,
                    SensorManager.SENSOR_DELAY_FASTEST
                )
                mProviderState = IDataProvider.ProviderState.SUBSCRIBED
            }

            if (mSensorManagerRotationVector != null)
            {
                Log.d("InclinationProvider", "SYR-> Subscribing to mSensorManagerTilt provider")
                sensorManager.registerListener(
                    this,
                    mSensorManagerRotationVector,
                    SensorManager.SENSOR_DELAY_FASTEST
                )
                mProviderState = IDataProvider.ProviderState.SUBSCRIBED
            }

            if (mSensorManagerLinearAcceleration != null)
            {
                Log.d("InclinationProvider", "SYR-> Subscribing to mSensorManagerAcceleration provider")
                sensorManager.registerListener(
                    this,
                    mSensorManagerLinearAcceleration,
                    SensorManager.SENSOR_DELAY_FASTEST
                )
                mProviderState = IDataProvider.ProviderState.SUBSCRIBED
            }

            if (mSensorManagerMagnetometer != null)
            {
                Log.d("InclinationProvider", "SYR-> Subscribing to mSensorManagerMagnetometer provider")
                sensorManager.registerListener(
                        this, mSensorManagerMagnetometer, SensorManager.SENSOR_DELAY_FASTEST)
                mProviderState = IDataProvider.ProviderState.SUBSCRIBED
            }

            if (mTimer == null) {
                mTimer = io.reactivex.rxjava3.core.Observable.interval(100L, TimeUnit.MILLISECONDS).timeInterval().subscribe { notifyValues(callback) }
            }
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to subscribe to inclination sensors: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     *
     */
    private fun notifyValues(callback: (ITelemetryData) -> Unit)
    {
        try {

            var azimuth = 0
            var pitch = 0
            var roll = 0

            val linearAcceleration = FloatArray(3)
            val gravity = FloatArray(3)

            synchronized(lock) {

                linearAcceleration[0] = mLinearAccelerationBuffer.map { it[0] }.average().toFloat()
                linearAcceleration[1] = mLinearAccelerationBuffer.map { it[1] }.average().toFloat()
                linearAcceleration[2] = mLinearAccelerationBuffer.map { it[2] }.average().toFloat()

                gravity[0] = mGravityBuffer.map { it[0] }.average().toFloat()
                gravity[1] = mGravityBuffer.map { it[1] }.average().toFloat()
                gravity[2] = mGravityBuffer.map { it[2] }.average().toFloat()

                if (mAzimuthBuffer.any()) {
                    azimuth = mAzimuthBuffer.toIntArray().average().roundToInt()
                }

                if (mPitchBuffer.any()) {
                    pitch = mPitchBuffer.toIntArray().average().roundToInt()
                }

                if (mRollBuffer.any()) {
                    roll = mRollBuffer.toIntArray().average().roundToInt()
                }

                mLinearAccelerationBuffer.clear()
                mGravityBuffer.clear()
                mAzimuthBuffer.clear()
                mPitchBuffer.clear()
                mRollBuffer.clear()
            }

            val inclination = InclinationData(
                    gravity, linearAcceleration, azimuth, pitch, roll, System.currentTimeMillis())
            callback(inclination)
        }
        catch (ex: java.lang.Exception)
        {
            Log.e("SYR", "SYR -> Unable to send inclination data because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Stop to acquiring acceleration, rotation and gravity data
     */
    override fun stopProvider()
    {
        sensorManager.unregisterListener(this)
        mProviderState = IDataProvider.ProviderState.STOPPED
        if (mTimer != null && !mTimer!!.isDisposed)
        {
            mTimer?.dispose()
            mTimer = null
        }
    }

    /**
     *  Returns the state of the inclination provider
     *
     *
     */
    override fun getProviderState(): IDataProvider.ProviderState
    {
        return mProviderState
    }

    //region process events
    /**
     * Process changed in the hardware sensors in charge of the acceleration, the gravity force
     * or the orientation of the device
     *
     * @param event: The data of the sensor event
    */
    override fun onSensorChanged(event: SensorEvent?) {
        //Log.d("SYR", "SYR -> onSensorChanged")

        when (event!!.sensor.type) {
            Sensor.TYPE_GRAVITY -> processGravity(event)
            Sensor.TYPE_LINEAR_ACCELERATION -> processAccelerometer(event)
            Sensor.TYPE_ROTATION_VECTOR -> processRotationVector(event)
        }
    }

    /**
     * Gets the linear acceleration of the mobile phone in the three axis
     *
     * @param event: Event with the acceleration data. this event provides three
     * acceleration values, one for each axis in ms2
     */
    private fun processAccelerometer(event: SensorEvent?)
    {

        try {

            val linearAcceleration = FloatArray(3)
            if (event != null) {
                linearAcceleration[0] =
                    BigDecimal(event.values[0].toDouble()).setScale(2, RoundingMode.HALF_EVEN)
                        .toFloat()
                linearAcceleration[1] =
                    BigDecimal(event.values[1].toDouble()).setScale(2, RoundingMode.HALF_EVEN)
                        .toFloat()
                linearAcceleration[2] =
                    BigDecimal(event.values[2].toDouble()).setScale(2, RoundingMode.HALF_EVEN)
                        .toFloat()

                synchronized(lock) {
                    mLinearAccelerationBuffer.add(linearAcceleration)
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("InclinationProvider", "SYR -> Unable to process the accelemrometer update: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Using the rotation vector, calculate the orientation of the device
     * The orientation holds the following values:
     *
     * [0]:Azimuth, angle of rotation about the -z axis.
     * [1]:Pitch, angle of rotation about the x axis.
     * [2]:Roll, angle of rotation about the y axis
     *
     * @param event: The data with the rotation of the device
     */
    private fun processRotationVector(event: SensorEvent?)
    {
        try
        {
            val mRotationMatrix = FloatArray(9) { 0f }
            val mOrientationRadians = FloatArray(3) { 0f }

            SensorManager.getRotationMatrixFromVector(mRotationMatrix , event!!.values)

            /**
             * getOrientation return an array of three fields with:
             * mOrientationRadians[0]:Azimuth, angle of rotation about the -z axis.
             * mOrientationRadians[1]:Pitch, angle of rotation about the x axis.
             * mOrientationRadians[2]:Roll, angle of rotation about the y axis
             *
             *  the values are radians and we want them in degrees
             */
            SensorManager.getOrientation(mRotationMatrix, mOrientationRadians)

            synchronized(lock) {
                mAzimuthBuffer.add(Math.toDegrees(mOrientationRadians[0].toDouble()).roundToInt())
                mPitchBuffer.add(Math.toDegrees(mOrientationRadians[1].toDouble()).roundToInt())
                mRollBuffer.add(Math.toDegrees(mOrientationRadians[2].toDouble()).roundToInt())
            }

            //Log.d("SYR", "SYR -> Processing rotation vector Azimuth ${mOrientation[0]} Pitch ${mOrientation[1]} Roll ${mOrientation[2]}")
        }
        catch (ex: Exception)
        {
            Log.e("InclinationProvider", "SYR -> Unable to process the rotation vector event ${ex.message}")
            ex.printStackTrace()
        }
    }

    private fun processGravity(event: SensorEvent)
    {
        try
        {
            // In this example, alpha is calculated as t / (t + dT),
            // where t is the low-pass filter's time-constant and
            // dT is the event delivery rate.

            val alpha = 0.8f

            val gravity = FloatArray(3)
            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) *
                    BigDecimal(event.values[0].toDouble()).setScale(2, RoundingMode.HALF_EVEN).toFloat()
            gravity[1] = alpha * gravity[1] + (1 - alpha) *
                    BigDecimal(event.values[1].toDouble()).setScale(2, RoundingMode.HALF_EVEN).toFloat()
            gravity[2] = alpha * gravity[2] + (1 - alpha) *
                    BigDecimal(event.values[2].toDouble()).setScale(2, RoundingMode.HALF_EVEN).toFloat()

            synchronized(lock) {
                mGravityBuffer.add((gravity))
            }

            //Log.d("SYR", "SYR -> Processing gravity ${mGravity[0]} ${mGravity[1]} ${mGravity[2]}")
        }
        catch (ex: Exception)
        {
            Log.e("InclinationProvider", "SYR -> Unable to process the rotation vector event ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion



}