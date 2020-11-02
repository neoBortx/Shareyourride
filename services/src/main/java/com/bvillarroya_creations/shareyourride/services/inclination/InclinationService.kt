package com.bvillarroya_creations.shareyourride.services.inclination

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.datamodel.data.Inclination
import com.bvillarroya_creations.shareyourride.datamodel.dataBase.ShareYourRideRepository
import com.bvillarroya_creations.shareyourride.telemetry.inclination.InclinationData
import com.bvillarroya_creations.shareyourride.telemetry.inclination.InclinationManager
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryManager
import com.bvillarroya_creations.shareyourride.services.DataConverters
import com.bvillarroya_creations.shareyourride.services.base.TelemetryServiceBase
import com.bvillarroya_creations.shareyourride.services.environment.EnvironmentService
import kotlinx.coroutines.runBlocking

/**
 * Service for tilt data
 */
class InclinationService(): TelemetryServiceBase() {

    override var mTelemetryManager: ITelemetryManager = InclinationManager(application)

    override var mClassName: String = EnvironmentService::class.java.simpleName

    //Region properties
    val accelerationX = MutableLiveData<Float>()

    val accelerationY = MutableLiveData<Float>()

    val accelerationZ = MutableLiveData<Float>()

    val gravityX = MutableLiveData<Float>()

    val gravityY = MutableLiveData<Float>()

    val gravityZ = MutableLiveData<Float>()

    val rotationVector = MutableLiveData<IntArray>()

    val rotationX = MutableLiveData<Int>()

    val rotationY = MutableLiveData<Int>()

    val rotationZ = MutableLiveData<Int>()
    //endregion

    //region ServiceBase

    /**
     * Process the telemetry data to make easy upper layers to manage it
     *
     * @param data: The telemetry data given by the provider
     */
    override fun processTelemetry(data: ITelemetryData) {
        try {
            val inclination = DataConverters.convertData(data as InclinationData, mSessionId, 0)
            telemetryData = inclination

            rotationVector.value = inclination.orientationVector
            rotationX.value = inclination.orientationVector[0]
            rotationY.value = inclination.orientationVector[1]
            rotationZ.value = inclination.orientationVector[2]

            gravityX.value = inclination.gravity[0]
            gravityY.value = inclination.gravity[1]
            gravityZ.value = inclination.gravity[2]

            accelerationX.value = inclination.acceleration[0]
            accelerationY.value = inclination.acceleration[1]
            accelerationZ.value = inclination.acceleration[2]
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to process inclination telemetry data ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Saves the current telemetry in the data base
     *
     * @param timeStamp: The time stamp that will be used to index the telemetry
     * and join all different kinds of telemetry
     */
    override fun saveTelemetry(timeStamp: Long) {
        try {
            telemetryData!!.id.timeStamp = timeStamp

            runBlocking {
                ShareYourRideRepository.insertInclination(telemetryData as Inclination)
            }
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to save location telemetry data ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion
}