package com.bvillarroya_creations.shareyourride.telemetry.inclination

import android.app.Activity
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Half.EPSILON
import android.util.Log
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.IDataProvider
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import java.util.concurrent.TimeUnit
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class InclinationProvider(private val context: Context): IDataProvider, SensorEventListener {

    //region sensor properties
    /*
        Sensor in charge of manage the rotation of the mobile phone
     */
    private var mSensorManagerTilt: Sensor? = null

    /*
        Sensor in charge of manage the g-force of each axis in the mobile phone
     */
    private var mSensorManagerGravity: Sensor? = null

    /*
        Sensor in charge of manage the acceleration of each axis of the mobile phone
     */
    private var mSensorManagerAcceleration: Sensor? = null

    /*
        The android sensor manager
     */
    private lateinit var sensorManager: SensorManager

    /*
        Store the current state of the telemetry data provider
     */
    private var mProviderState: IDataProvider.ProviderState = IDataProvider.ProviderState.STOPED

    //endregion

    //region telemetry properties
    private var mGgravity = FloatArray(3) { 0f }

    private var mLinearAcceleration = FloatArray(3) { 0f }


    private var mRotationMatrix = FloatArray(9) { 0f }
    //endregion
    /*
        Initialize the three sensors
     */
    override fun configureProvider() {
        sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager

        mSensorManagerGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

        // Use the accelerometer.
        mSensorManagerTilt = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        mSensorManagerAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    /*
        One of the three sensors have detected a change in their state
     */
    override fun onSensorChanged(event: SensorEvent?) {
        when (event!!.sensor.type) {
            Sensor.TYPE_GRAVITY -> processGravity(event)
            Sensor.TYPE_GYROSCOPE -> processGyroscope(event)
            Sensor.TYPE_ACCELEROMETER -> processAccelerometer(event)
        }
    }

    private fun processGravity(event: SensorEvent)
    {
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        val alpha = 0.8f

        // Isolate the force of gravity with the low-pass filter.
        mGgravity[0] = alpha * mGgravity[0] + (1 - alpha) * event.values[0]
        mGgravity[1] = alpha * mGgravity[1] + (1 - alpha) * event.values[1]
        mGgravity[2] = alpha * mGgravity[2] + (1 - alpha) * event.values[2]
    }

    private var giroscopeTimestamp: Float = 0f

    private fun processGyroscope(event: SensorEvent?)
    {
        // Create a constant to convert nanoseconds to seconds.
        val nS2S = 1.0f / 1000000000.0f
        val deltaRotationVector = FloatArray(4) { 0f }

        // This timestep's delta rotation to be multiplied by the current rotation
        // after computing it from the gyro sample data.
        if (giroscopeTimestamp != 0f && event != null) {
            val dT = (event.timestamp - giroscopeTimestamp) * nS2S
            // Axis of the rotation sample, not normalized yet.
            var axisX: Float = event.values[0]
            var axisY: Float = event.values[1]
            var axisZ: Float = event.values[2]

            // Calculate the angular speed of the sample
            val omegaMagnitude: Float = sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ)

            // Normalize the rotation vector if it's big enough to get the axis
            // (that is, EPSILON should represent your maximum allowable margin of error)
            if (omegaMagnitude > EPSILON) {
                axisX /= omegaMagnitude
                axisY /= omegaMagnitude
                axisZ /= omegaMagnitude
            }

            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the timestep
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.
            val thetaOverTwo: Float = omegaMagnitude * dT / 2.0f
            val sinThetaOverTwo: Float = sin(thetaOverTwo)
            val cosThetaOverTwo: Float = cos(thetaOverTwo)
            deltaRotationVector[0] = sinThetaOverTwo * axisX
            deltaRotationVector[1] = sinThetaOverTwo * axisY
            deltaRotationVector[2] = sinThetaOverTwo * axisZ
            deltaRotationVector[3] = cosThetaOverTwo
        }
        giroscopeTimestamp = event?.timestamp?.toFloat() ?: 0f
        val deltaRotationMatrix = FloatArray(9) { 0f }
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector)
        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        var i = 0
        deltaRotationVector.forEach {
            mRotationMatrix[i] = mRotationMatrix[i] * it
            i++
        }
        //mRotationMatrix = mRotationMatrix * deltaRotationMatrix;
    }

    private fun processAccelerometer(event: SensorEvent?)
    {
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        val alpha = 0.8f

        val gravity = FloatArray(3) { 0f }


        if (event != null) {
            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

            // Remove the gravity contribution with the high-pass filter.
            mLinearAcceleration[0] = event.values[0] - gravity[0]
            mLinearAcceleration[1] = event.values[1] - gravity[1]
            mLinearAcceleration[2] = event.values[2] - gravity[2]
        }
    }

    /*
        Set the handler that is going gto precess changes in the location
     */
    override fun subscribeProvider(callback: (ITelemetryData) -> Unit)
    {
        Log.d("SYR" , "SYR-> Subscribing to inclination provider")
        if (mSensorManagerGravity != null) {
            sensorManager.registerListener(
                this,
                mSensorManagerGravity,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            mProviderState = IDataProvider.ProviderState.SUBSCRIBED
        }
        if (mSensorManagerTilt != null) {
            sensorManager.registerListener(
                this,
                mSensorManagerTilt,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            mProviderState = IDataProvider.ProviderState.SUBSCRIBED
        }

        if (mSensorManagerAcceleration != null)
        {
            sensorManager.registerListener(
                this,
                mSensorManagerAcceleration,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            mProviderState = IDataProvider.ProviderState.SUBSCRIBED
        }

        io.reactivex.rxjava3.core.Observable.interval(100L, TimeUnit.MILLISECONDS)
            .timeInterval()
            .subscribe { calculateValues(callback)
                Log.d("tag", "&&&& on timer") }
    }

    private fun calculateValues(callback: (ITelemetryData) -> Unit)
    {
        val inclination = InclinationData(mGgravity,mLinearAcceleration,mRotationMatrix, System.currentTimeMillis() )
        callback(inclination)

    }

    /*
        Remove the handler of the location updates
     */
    override fun stopProvider()
    {
        sensorManager.unregisterListener(this)
        mProviderState = IDataProvider.ProviderState.STOPED
    }

    /*
        returns the state of the Loctaion provider
     */
    override fun getProviderState(): IDataProvider.ProviderState
    {
        return mProviderState
    }



}