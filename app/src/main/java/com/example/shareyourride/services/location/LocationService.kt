package com.example.shareyourride.services.location

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.datamodel.data.Location
import com.bvillarroya_creations.shareyourride.datamodel.dataBase.ShareYourRideRepository
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTopics
import com.bvillarroya_creations.shareyourride.messagesdefinition.MessageTypes
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.bvillarroya_creations.shareyourride.telemetry.location.LocationData
import com.bvillarroya_creations.shareyourride.telemetry.location.LocationManager
import com.example.shareyourride.services.DataConverters
import com.example.shareyourride.services.base.TelemetryServiceBase
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

/**
 * Service for location data
 */
class LocationService() : TelemetryServiceBase()
{
    override var mClassName: String = LocationService::class.java.simpleName

    /**
     * The latitude position of the device, in degrees
     */
    val latitude = MutableLiveData<Double>()

    /**
     * The longitude position in degrees of the device
     */
    val longitude = MutableLiveData<Double>()

    /**
     * The current altitude of the device, in meters
     */
    val altitude = MutableLiveData<Double>()

    /**
     * The speed of the device, in meters per second
     */
    val speed = MutableLiveData<Float>()

    /**
     * The inclination of the terrain
     */
    val terrainInclination = MutableLiveData<Int>()

    /**
     * Timer to send the update GPS events
     */
    private var updateGpsStateTimer: Disposable? = null

    //region message handlers
    init {
        this.createMessageHandler( "LocationService", listOf<String>(MessageTopics.SESSION_CONTROL, MessageTopics.GPS_DATA))
    }

    /**
     * Listen to session messages related to the session management
     *
     * @param msg: received message from the android internal queue
     */
    override fun processMessage(msg: MessageBundle)
    {
        try {

            when (msg.messageKey)
            {
                MessageTypes.GPS_STATE_REQUEST ->
                {
                    Log.d(mClassName, "SYR -> received  GPS_STATE_REQUEST, responding with " +
                            "GPS_STATE_EVNT ${providerReady?.value}")
                    notifyGpState()
                }
                else ->
                {
                    super.processMessage(msg)
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to process message ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    //region override TelemetryServiceBase
    override fun initializeManager() {
        mTelemetryManager = LocationManager(applicationContext)
        providerReady = mTelemetryManager?.providerReady
        mTelemetryManager?.configure()
    }

    /**
     * Process the telemetry data to make easy upper layers to manage it
     *
     * @param data: The telemetry data given by the provider
     */
    override fun processTelemetry(data: ITelemetryData) {
        try
        {
            val location = DataConverters.convertData(data as LocationData, mSessionId, 0)

            telemetryData = location

            latitude.postValue(location.latitude)
            longitude.postValue(location.longitude)
            altitude.postValue(location.altitude)
            speed.postValue(location.speed)
            terrainInclination.postValue(location.terrainInclination)
        }
        catch(ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to process location telemetry data ${ex.message}")
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
        try
        {
            if (telemetryData != null) {
                telemetryData!!.id.timeStamp = timeStamp

                runBlocking {
                    ShareYourRideRepository.insertLocation(telemetryData as Location)
                }
            }
        }
        catch(ex: Exception)
        {
            Log.e(mClassName, "SYR -> Unable to save location telemetry data ${ex.message}")
            ex.printStackTrace()
        }
    }

    override fun updateTelemetry()
    {
        try
        {
            if (telemetryData != null)
            {
                val message = MessageBundle(MessageTypes.GPS_DATA_EVENT, telemetryData as Location, MessageTopics.GPS_DATA)
                sendMessage(message)
            }
            else
            {
                Log.e(mClassName, "SYR -> There isn't any telemetry data to save ")
            }
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to update location telemetry data ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    /**
     * Configure the timer to update the GPS state in the view
     */
    override fun onCreate() {
        super.onCreate()
        initializeManager()
        configurePeriodicTimer()
    }

    //region private functions
    private fun notifyGpState()
    {
        try
        {
            val message = MessageBundle(MessageTypes.GPS_STATE_EVENT, providerReady?.value, MessageTopics.GPS_DATA)
            sendMessage(message)
        }
        catch (ex: java.lang.Exception)
        {
            Log.e(mClassName,"SYR -> Unable to notify the GPS state because ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Configures a periodic timer to send the GPS state to upper layers
     */
    private fun configurePeriodicTimer()
    {
        try
        {
            updateGpsStateTimer = Observable.interval(1000, 10000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe {
                notifyGpState()
            }
        }
        catch (ex: java.lang.Exception)
        {
            Log.e(mClassName,"SYR -> Unable to configure a periodic timer to send telemetry messages because ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion
}