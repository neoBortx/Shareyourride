package com.bvillarroya_creations.shareyourride.telemetry.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.bvillarroya_creations.shareyourride.telemetry.base.ClientManager
import com.bvillarroya_creations.shareyourride.telemetry.constants.TelemetryConstants
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.IDataProvider


class LocationManager(context: Context) : ClientManager(context) {

    //region client manager properties
    override val telemetryEventType = TelemetryConstants.Companion.TelemetryEventType.Location

    override var mDataProvider: IDataProvider? = LocationProvider(context)
    //endregion

    /**
     *
     */
    private var stringSignal = false
}