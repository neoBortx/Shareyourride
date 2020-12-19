package com.example.shareyourride.services.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.util.Log
import androidx.core.app.ActivityCompat
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
     * Timer to send the update GPS events
     */
    private var updateGpsStateTimer: Disposable? = null

    /**
     * Points if the accuracy mode is enabled, in that mode the telemetry data is used to calculate
     * if the received GPS data is good enough to start a new session
     */
    private var accuracyModeEnabled = false

    /**
     * Flag that points if the accuracy data is admissible
     */
    private var accuracyAdmissible = false

    /**
     *
     */
    var gpsEnabled = false

    private var mLocationManager : android.location.LocationManager? = null

    /**
     * Function that handles changes the GPS state
     */
    private lateinit var mGnssStatusCallback: GnssStatus.Callback
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
                            "GPS_STATE_EVENT ${providerReady}")
                    notifyGpState()
                }
                MessageTypes.GPS_START_ACQUIRING_ACCURACY ->
                {
                    Log.d(mClassName, "SYR -> received  GPS_START_ACQUIRING_ACCURACY, starting the accuracyMode")
                    setAccuracyCalculationMode()
                }
                MessageTypes.START_ACQUIRING_DATA ->
                {
                    Log.d(mClassName, "SYR -> received  START_ACQUIRING_DATA message")
                    if (accuracyModeEnabled) {
                        accuracyModeEnabled = false
                    }
                    super.processMessage(msg)
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
            val locationData = data as LocationData
            val location = DataConverters.convertData(locationData, mSessionId, 0)

            if (accuracyModeEnabled) {
                if (locationData.accuracy > 68) {
                    Log.d(mClassName, "SYR -> The received accuracy is good enough ${locationData.accuracy}")
                    accuracyAdmissible = true
                    notifyGpState()
                }
                else {
                    Log.d(mClassName, "SYR -> The received accuracy is to low ${locationData.accuracy}")
                    accuracyAdmissible = false
                    telemetryData = location
                }
            }
            else
            {
                telemetryData = location
            }
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
                telemetryData!!.id.sessionId = mSessionId
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
    private fun setAccuracyCalculationMode()
    {
        try
        {
            accuracyModeEnabled = true
            initializeTelemetry()

        }
        catch (ex: java.lang.Exception)
        {
            Log.e(mClassName,"SYR -> Unable to set accuracy mode because ${ex.message}")
            ex.printStackTrace()
        }
    }

    private fun notifyGpState()
    {
        try
        {
            val message = MessageBundle(MessageTypes.GPS_STATE_EVENT, gpsEnabled && accuracyAdmissible, MessageTopics.GPS_DATA)
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        mLocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as  android.location.LocationManager?
        if (mLocationManager != null)
        {
            gpsEnabled = mLocationManager!!.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER )
        }

        Log.i("LocationManager", "SYR -> Creating  mGnssStatusCallback")
        mGnssStatusCallback = object : GnssStatus.Callback()
        {
            override fun onFirstFix(ttffMillis: Int) {
                Log.i("LocationManager", "SYR -> GPS onFirstFix")
                super.onFirstFix(ttffMillis)
            }

            override fun onStarted()
            {
                Log.i("LocationManager", "SYR -> GPS started")
                gpsEnabled = true
            }

            override fun onStopped()
            {
                Log.d("LocationManager", "SYR ->  GPS stopped")
                gpsEnabled = false
            }

            override fun onSatelliteStatusChanged(status: GnssStatus)
            {
                Log.d("LocationProvider", "SYR ->  GPS started count : ${status.satelliteCount}")
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            Log.i("LocationManager", "SYR -> GPS permissions granted attaching mGnssStatusCallback")
            mLocationManager?.registerGnssStatusCallback(mGnssStatusCallback)
        }
        else
        {
            Log.d("LocationManager", "SYR -> GPS permissions denied unable to attach mGnssStatusCallback")
        }

        setAccuracyCalculationMode()

        return super.onStartCommand(intent, flags, startId)
    }
    //endregion
}