package com.bvillarroya_creations.shareyourride.telemetry.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bvillarroya_creations.shareyourride.telemetry.R
import com.bvillarroya_creations.shareyourride.telemetry.base.ClientManager
import com.bvillarroya_creations.shareyourride.telemetry.constants.TelemetryConstants
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.IDataProvider


class LocationManager(context: Context) : ClientManager(context) {

    //region client manager properties
    override val telemetryEventType = TelemetryConstants.Companion.TelemetryEventType.Location

    override var mDataProvider: IDataProvider? = LocationProvider(context)
    //endregion

    private var mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

    /**
     * Function that handles changes the GPS state
     */
    private lateinit var mGnssStatusCallback: GnssStatus.Callback


    init {

        if (mLocationManager != null)
        {

            if ( mLocationManager!!.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                providerReady?.postValue(true)
            }
            else
            {
                providerReady?.postValue(false)
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
                    providerReady?.postValue(true)
                }

                override fun onStopped()
                {
                    Log.d("LocationManager", "SYR ->  GPS stopped")
                    providerReady?.postValue(false)
                }

                override fun onSatelliteStatusChanged(status: GnssStatus)
                {
                    Log.d("LocationProvider", "SYR ->  GPS started count : ${status.satelliteCount}")
                }
            }

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                Log.i("LocationManager", "SYR -> GPS permissions granted attaching mGnssStatusCallback")
                mLocationManager?.registerGnssStatusCallback(mGnssStatusCallback)
            }
            else
            {
                Log.d("LocationManager", "SYR -> GPS permissions denied unable to attach mGnssStatusCallback")
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?)
    {
        if (mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            providerReady?.postValue(true)
        }
        else {
            providerReady?.postValue(false)
            Toast.makeText(context, context?.getString(R.string.switch_on_wifi), Toast.LENGTH_LONG).show()
        }
    }

}