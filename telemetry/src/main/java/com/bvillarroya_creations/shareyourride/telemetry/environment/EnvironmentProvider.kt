package com.bvillarroya_creations.shareyourride.telemetry.environment

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorManager
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.IDataProvider
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData


class EnvironmentProvider(val context: Context): IDataProvider {

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

    /*
        Set the handler that is going gto precess changes in the location
     */
    override fun subscribeProvider(callback: (ITelemetryData) -> Unit)
    {

    }


    /*
        Remove the handler of the location updates
     */
    override fun stopProvider()
    {

    }

    /*
        returns the state of the Loctaion provider
     */
    override fun getProviderState(): IDataProvider.ProviderState
    {
        return mProviderState
    }



}