package com.example.shareyourride.userplayground.session

import android.util.Log
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.services.session.TelemetryType

/**
 * Gets the icon associate to a telemetry value
 */
class TelemetryIconConverter() {
    private var telemetryToIcon: HashMap<TelemetryType, Int> = HashMap()

    init {
        try {
            telemetryToIcon[TelemetryType.Speed] = R.drawable.speed
            telemetryToIcon[TelemetryType.Acceleration] = R.drawable.aceleration
            telemetryToIcon[TelemetryType.Distance] = R.drawable.distance
            telemetryToIcon[TelemetryType.LeanAngle] = R.drawable.screen_rotation
            telemetryToIcon[TelemetryType.TerrainInclination] = R.drawable.inclination
            telemetryToIcon[TelemetryType.Altitude] = R.drawable.altitude

        }
        catch (ex: java.lang.Exception) {
            Log.e("TelemetryIconConverter", "SYR -> Unable to fill icon dictionary because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     *
     */
    fun getIcon(telemetryType: TelemetryType): Int
    {
        try {
            if (telemetryToIcon.containsKey(telemetryType))
            {
                return telemetryToIcon[telemetryType]!!
            }
        }
        catch (ex: java.lang.Exception) {
            Log.e("TelemetryIconConverter", "SYR -> Unable to get icon dictionary because: ${ex.message}")
            ex.printStackTrace()
        }

        return 0
    }
}