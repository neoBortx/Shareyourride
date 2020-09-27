package com.bvillarroya_creations.shareyourride.telemetry.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import com.bvillarroya_creations.shareyourride.messenger.IMessageHandlerClient
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.messenger.MessageHandler
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.IDataProvider
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.bvillarroya_creations.shareyourride.telemetry.messages.TelemetryMessageTopics
import com.bvillarroya_creations.shareyourride.telemetry.messages.TelemetryMessageTypes
import com.google.android.gms.location.*


/**
 * Class in charge of manage the location services
 */
class LocationProvider(private val context: Context): IDataProvider, IMessageHandlerClient {

    //region variables
    /**
     * Function that handles the location updates
     */
    private lateinit var mLocationCallback: LocationCallback

    /**
     * The state of the location provider
     */
    private var mProviderState: IDataProvider.ProviderState =   IDataProvider.ProviderState.STOPPED

    /**
     * The client to connect with the location services
     */
    private val mFusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * To request location updates
     */
    private var mLocationRequest: LocationRequest? = null
    //endregion

    //region IDataProvider
    /**
     * Configure Location services with the priority and the pooling interval
     * PRIORITY_BALANCED_POWER_ACCURACY to keep the location working in second plane
     * Pooling interval of 10 locations per second (100 millis)
     * https://developer.android.com/guide/topics/location/battery
     */
    override fun configureProvider() {
        try {
            //Get updates each one second
            //
            mLocationRequest = LocationRequest.create()?.apply {
                interval = 100
                fastestInterval = 100
                priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            }

            if (mLocationRequest != null) {
                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest!!)

                val client: SettingsClient = LocationServices.getSettingsClient(context)
                client.checkLocationSettings(builder.build())
            }

        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to configure to location provider ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Set the handler that is going gto precess changes in the location
     * @param callback: Function that is going to process location updates
     */
    @SuppressLint("MissingPermission")
    override fun subscribeProvider(callback: (ITelemetryData) -> Unit) {
        try {

            if (mLocationRequest != null)
            {
                createLocationCallback(callback)
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,Looper.getMainLooper())
            }
            else{
                Log.e("SYR", "SYR -> Unable to subscribe to location provider, mLocationRequest is null")
            }
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to subscriber to location provider ${ex.message}")
            ex.printStackTrace()
        }

    }

    /**
     * Create the call back function called by the location services to process location updates
     * This call callback function will forward the location data to upper layers
     *
     * @param callback: Function that is going to process location updates
     */
    private fun createLocationCallback(callback: (ITelemetryData) -> Unit)
    {
        try {
            mLocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {

                    locationResult ?: return
                    for (location in locationResult.locations) {

                        val locationData = LocationData(
                            location.latitude,
                            location.longitude,
                            location.altitude,
                            location.speed,
                            location.bearing,
                            location.time
                        )

                        //Log.d("SYR","SYR -> Location obtained lat: ${location.latitude} lon: ${location.longitude} " +
                        //        "altitude: ${location.altitude} speed: ${location.speed} bearing ${location.bearing}")

                        callback(locationData)

                        val message = MessageBundle(TelemetryMessageTypes.LOCATION_DATA, locationData)

                        sendMessage(message)
                    }
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to create location callback: ${ex.message}")
            ex.printStackTrace()
        }

    }

    /**
     * Remove the handler of the location updates
     */
    override fun stopProvider() {
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to stop location provers: ${ex.message}")
            ex.printStackTrace()
        }

        mProviderState = IDataProvider.ProviderState.STOPPED
    }

    /**
     * Returns the state of the Location provider
     */
    override fun getProviderState(): IDataProvider.ProviderState
    {
        return mProviderState
    }
    //endregion

    //region IMessageHandlerClient implementation
    init {
        this.createMessageHandler( listOf<String>(TelemetryMessageTopics.TELEMETRY_DATA))
    }

    override lateinit var messageHandler: MessageHandler

    /**
     * No message required
     */
    override fun processMessage(msg: MessageBundle) {
        Log.d("SYR", "SYR -> Skipping message")
    }
    //endregion

}
