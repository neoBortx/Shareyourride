/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.telemetry.inclination

import android.content.Context
import com.bvillarroya_creations.shareyourride.telemetry.base.ClientManager
import com.bvillarroya_creations.shareyourride.telemetry.constants.TelemetryConstants
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.IDataProvider

class InclinationManager(context: Context): ClientManager(context) {

    //region client manager properties
    override val telemetryEventType = TelemetryConstants.Companion.TelemetryEventType.Inclination

    override var mDataProvider: IDataProvider? = InclinationProvider(context)


    //endregion
}