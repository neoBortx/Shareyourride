package com.bvillarroya_creations.shareyourride.telemetry.location

import android.content.Context
import android.util.Log
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.IDataProvider
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task


class LocationProvider(private val context: Context): IDataProvider {

    private lateinit var mLocationCallback: LocationCallback

    private var mProviderState: IDataProvider.ProviderState =   IDataProvider.ProviderState.STOPED

    private val mFusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var mLocationRequest: LocationRequest? = null

    /*
        Initialize the provider
     */
    override fun configureProvider() {
        //Get updates each one second
        mLocationRequest = LocationRequest.create()?.apply {
            interval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    /*
        Set the handler that is going gto precess changes in the location
     */
    override fun subscribeProvider(callback: (ITelemetryData) -> Unit) {
        if (mLocationRequest != null) {
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest!!)

            val client: SettingsClient = LocationServices.getSettingsClient(context)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
            mProviderState = IDataProvider.ProviderState.SUBSCRIBED
            task.addOnSuccessListener {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...

                startObserveLocation(callback)
            }

            task.addOnFailureListener { exception ->
                Log.e("SYR", "SYR -> Unable to subscribe to location provider $exception")

            }
        }
    }

    private fun startObserveLocation(callback: (ITelemetryData) -> Unit)
    {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {

                    val locationData = LocationData(location.latitude, location.longitude, location.altitude, location.speed, location.time)

                    Log.d("SYR","Location -> obtained lat: ${location.latitude} lon: ${location.longitude} " +
                            "altitude: ${location.altitude} speed: ${location.speed} bearing ${location.bearing}")

                    callback(locationData)
                }
            }
        }
    }

    /*
        Remove the handler of the location updates
     */
    override fun stopProvider() {
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
        }
        catch (ex: Exception)
        {
            ex.printStackTrace()
        }

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
