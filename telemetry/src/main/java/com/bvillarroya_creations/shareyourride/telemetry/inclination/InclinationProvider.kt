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
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class InclinationProvider(private val context: Context): IDataProvider, SensorEventListener {

    //region sensor properties
    /*
        Sensor in charge of manage the rotation of the mobile phone
     */
    private var mSensorManagerRotationVector: Sensor? = null

    /*
        Sensor in charge of manage the g-force of each axis in the mobile phone
     */
    private var mSensorManagerGravity: Sensor? = null

    /*
        Sensor in charge of manage the acceleration of each axis of the mobile phone
     */
    private var mSensorManagerLinearAcceleration: Sensor? = null

    /*
        The android sensor manager
     */
    private lateinit var sensorManager: SensorManager

    /*
        Store the current state of the telemetry data provider
     */
    private var mProviderState: IDataProvider.ProviderState = IDataProvider.ProviderState.STOPPED

    //endregion

    /**
     * The gravity that is affected to the device
     */
    private var mGravity = FloatArray(3) { 0f }

    /**
     * The linear acceleration of the device
     */
    private var mLinearAcceleration = FloatArray(3) { 0f }

    /**
     * The rotation ongles of the device
     */
    private var mOrientation = IntArray(3) { 0 }
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
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.e("SYR", "SYR -> Accuracy Changed")
    }

    /**
     * Subscribe to listen gravity, acceleration and orientation
     *
     * @param callback: function that will process sensor changed
     */
    override fun subscribeProvider(callback: (ITelemetryData) -> Unit)
    {
        try {
            Log.d("SYR", "SYR-> Subscribing to inclination provider")
            if (mSensorManagerGravity != null) {
                Log.d("SYR", "SYR-> Subscribing to mSensorManagerGravity provider")
                sensorManager.registerListener(
                    this,
                    mSensorManagerGravity,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
                mProviderState = IDataProvider.ProviderState.SUBSCRIBED
            }
            if (mSensorManagerRotationVector != null) {
                Log.d("SYR", "SYR-> Subscribing to mSensorManagerTilt provider")
                sensorManager.registerListener(
                    this,
                    mSensorManagerRotationVector,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
                mProviderState = IDataProvider.ProviderState.SUBSCRIBED
            }

            if (mSensorManagerLinearAcceleration != null) {
                Log.d("SYR", "SYR-> Subscribing to mSensorManagerAcceleration provider")
                sensorManager.registerListener(
                    this,
                    mSensorManagerLinearAcceleration,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
                mProviderState = IDataProvider.ProviderState.SUBSCRIBED
            }

            io.reactivex.rxjava3.core.Observable.interval(1000L, TimeUnit.MILLISECONDS)
                .timeInterval()
                .subscribe { notifyValues(callback) }
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
        val inclination = InclinationData(mGravity,mLinearAcceleration, mOrientation, System.currentTimeMillis() )
        callback(inclination)
    }

    /**
     * Stop to acquiring acceleration, rotation and gravity data
     */
    override fun stopProvider()
    {
        sensorManager.unregisterListener(this)
        mProviderState = IDataProvider.ProviderState.STOPPED
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
            //Sensor.TYPE_GYROSCOPE -> processGyroscope(event)
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

            if (event != null) {
                // Isolate the force of gravity with the low-pass filter.
                // Remove the gravity contribution with the high-pass filter.
                mLinearAcceleration[0] =
                    BigDecimal(event.values[0].toDouble()).setScale(2, RoundingMode.HALF_EVEN)
                        .toFloat()
                mLinearAcceleration[1] =
                    BigDecimal(event.values[1].toDouble()).setScale(2, RoundingMode.HALF_EVEN)
                        .toFloat()
                mLinearAcceleration[2] =
                    BigDecimal(event.values[2].toDouble()).setScale(2, RoundingMode.HALF_EVEN)
                        .toFloat()

                //Log.d("SYR", "SYR -> Processing acceleration ${mLinearAcceleration[0]} ${mLinearAcceleration[1]} ${mLinearAcceleration[2]}")
            }
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to process the accelemrometer update: ${ex.message}")
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

            //get degrees values and order them in x,y,z
            mOrientation[0] = Math.toDegrees(mOrientationRadians[2].toDouble()).roundToInt()
            mOrientation[1] = Math.toDegrees(mOrientationRadians[1].toDouble()).roundToInt()
            mOrientation[2] = Math.toDegrees(mOrientationRadians[0].toDouble()).roundToInt()

            //Log.d("SYR", "SYR -> Processing rotation vector Azimuth ${mOrientation[0]} Pitch ${mOrientation[1]} Roll ${mOrientation[2]}")
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to process the rotation vector event ${ex.message}")
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

            // Isolate the force of gravity with the low-pass filter.
            mGravity[0] = alpha * mGravity[0] + (1 - alpha) *
                    BigDecimal(event.values[0].toDouble()).setScale(2, RoundingMode.HALF_EVEN).toFloat()
            mGravity[1] = alpha * mGravity[1] + (1 - alpha) *
                    BigDecimal(event.values[1].toDouble()).setScale(2, RoundingMode.HALF_EVEN).toFloat()
            mGravity[2] = alpha * mGravity[2] + (1 - alpha) *
                    BigDecimal(event.values[2].toDouble()).setScale(2, RoundingMode.HALF_EVEN).toFloat()

            //Log.d("SYR", "SYR -> Processing gravity ${mGravity[0]} ${mGravity[1]} ${mGravity[2]}")
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to process the rotation vector event ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion



}