package com.bvillarroya_creations.shareyourride.telemetry.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
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
import kotlin.math.pow
import kotlin.math.sqrt


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

    /**
     * The last known longitude (to calculate terrain inclination and the distance)
     */
    private var lastLongitude: Double = 0.0

    /**
     * The last known latitude (to calculate terrain inclination and the distance)
     */
    private var lastLatitude: Double = 0.0

    /**
     *The last known altitude (to calculate terrain inclination and the distance)
     */
    private var lastAltitude: Double = 0.0

    /**
     * The distance of the activity in meters
     */
    private var accumulatedDistance: Long = 0
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
            Log.e("LoctaionProvider", "SYR -> Unable to configure to location provider ${ex.message}")
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
                clearValues()
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
     * Remove the handler of the location updates
     */
    override fun stopProvider() {
        try
        {
            clearValues()
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
        this.createMessageHandler( "LocationProvider", listOf<String>(TelemetryMessageTopics.TELEMETRY_DATA))
    }

    override lateinit var messageHandler: MessageHandler

    /**
     * No message required
     */
    override fun processMessage(msg: MessageBundle) {
        Log.d("LocationProvider", "SYR -> Skipping message")
    }
    //endregion

    //region private functions
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
                override fun onLocationResult(locationResult: LocationResult?)
                {
                    locationResult ?: return
                    for (location in locationResult.locations) {

                        val (distance , terrainInclination) = calculateTerrainInclination(newlong =  location.longitude, newLat = location.latitude, newAlt = location.altitude)

                        accumulatedDistance += distance

                        val locationData = LocationData(
                                location.latitude,
                                location.longitude,
                                location.altitude,
                                location.speed,
                                location.bearing,
                                terrainInclination,
                                accumulatedDistance,
                                location.time
                        )

                        //Log.d("SYR","SYR -> Location obtained lat: ${location.latitude} lon: ${location.longitude} " +
                        //        "altitude: ${location.altitude} speed: ${location.speed} bearing ${location.bearing}")

                        callback(locationData)

                        val message = MessageBundle(TelemetryMessageTypes.LOCATION_DATA, locationData,TelemetryMessageTopics.TELEMETRY_DATA)

                        sendMessage(message)
                    }
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("LocationProvider", "SYR -> Unable to create location callback: ${ex.message}")
            ex.printStackTrace()
        }

    }

    /**
     * Calculates the inclination of the slope of the terrain in percentage
     * To do that, use the last known point and compare the altitude and the distance
     * between these two points. Then calculate in percentage the inclination.
     *
     * @param newlong: The new longitude obtained
     * @param newLat: The new Latitude obtained
     * @param newAlt: The new Altitude obtained
     *
     * @return Pair with the distance (first value) and inclination percentage (second value)
     */
    private fun calculateTerrainInclination(newlong: Double, newLat: Double, newAlt: Double): Pair<Long,Int>
    {
        var percentage = 0
        var distance: Long = 0

        try
        {
            if (newlong == 0.0 || newLat == 0.0)
            {
                Log.e("LocationProvider", "SYR -> Skipping terrain inclination calculation because new data is incomplete, long = $newlong, lat = $newLat, alt = $newAlt")
                return Pair(distance, percentage)
            }

            if (lastLongitude != 0.0 && lastLatitude != 0.0)
            {

                val oldLocation = Location("oldLocation")
                oldLocation.latitude = lastLatitude
                oldLocation.longitude = lastLongitude
                oldLocation.altitude = lastAltitude

                val newLocation = Location("newLocation")
                newLocation.latitude = newLat
                newLocation.longitude = newlong
                newLocation.altitude = newAlt


                //Get the distance of two points in meters in the X and Y axis
                val distanceX: Double = oldLocation.distanceTo(newLocation).toDouble()
                val distanceY = newAlt - lastAltitude

                //Get the non decimal part of the number
                percentage = (((distanceY / distanceX) * 100).toInt())

                //Pythagoras  theorem to acquire the distance between two points
                distance = sqrt(distanceX.pow(2.0) + distanceY.pow(2.0)).toLong()
            }
            else
            {
                Log.d("LocationProvider", "SYR -> Skipping terrain inclination calculation because we don't have all values to calculate it,"
                        + " long = $lastLongitude, lat = $lastLatitude, alt = $lastAltitude")
            }

            lastLongitude = newlong
            lastLatitude = newLat
            lastAltitude = newAlt
        }
        catch(ex: Exception)
        {
            Log.e("LocationProvider", "SYR -> Unable to terrain inclination because: ${ex.message}")
            ex.printStackTrace()
        }

        return Pair(distance, percentage)
    }

    private fun clearValues()
    {
        accumulatedDistance = 0
        lastLatitude = 0.0
        lastLatitude = 0.0
        lastAltitude = 0.0
    }
    //endregion

}
