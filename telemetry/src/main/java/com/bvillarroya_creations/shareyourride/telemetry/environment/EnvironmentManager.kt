package com.bvillarroya_creations.shareyourride.telemetry.environment

import android.content.Context
import com.bvillarroya_creations.shareyourride.telemetry.base.ClientManager
import com.bvillarroya_creations.shareyourride.telemetry.constants.TelemetryConstants
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.IDataProvider

class EnvironmentManager(context: Context): ClientManager(context) {

    //region client manager properties
    override val telemetryEventType = TelemetryConstants.Companion.TelemetryEventType.Environment

    override var mDataProvider: IDataProvider? = EnvironmentProvider(context)
    //endregion
}