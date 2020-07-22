package com.bvillarroya_creations.shareyourride.telemetry.location

import android.content.Context
import com.bvillarroya_creations.shareyourride.telemetry.base.ClientManager
import com.bvillarroya_creations.shareyourride.telemetry.constants.TelemetryConstants
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.IDataProvider


class LocationManager(context: Context) : ClientManager(context) {

    //region client manager properties
    override val telemetryEventType = TelemetryConstants.Companion.TelemetryEventType.Location

    override var mDataProvider: IDataProvider? = LocationProvider(context)
    //endregion

}