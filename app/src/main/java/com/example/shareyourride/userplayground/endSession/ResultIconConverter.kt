package com.example.shareyourride.userplayground.endSession

import android.util.Log
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.services.session.SummaryTelemetryType
import java.util.*

/**
 * Gets the icon associate to a telemetry value
 */
class ResultIconConverter() {
    private var summaryTelemetryToIcon: EnumMap<SummaryTelemetryType, Int> = EnumMap(SummaryTelemetryType::class.java)

    init {
        try
        {
            summaryTelemetryToIcon[SummaryTelemetryType.Duration] = R.drawable.time
            summaryTelemetryToIcon[SummaryTelemetryType.MaxSpeed] = R.drawable.speed
            summaryTelemetryToIcon[SummaryTelemetryType.AverageMaxSpeed] = R.drawable.speed
            summaryTelemetryToIcon[SummaryTelemetryType.MaxAcceleration] = R.drawable.aceleration
            summaryTelemetryToIcon[SummaryTelemetryType.Distance] = R.drawable.distance
            summaryTelemetryToIcon[SummaryTelemetryType.MaxRightLeanAngle] = R.drawable.screen_rotation
            summaryTelemetryToIcon[SummaryTelemetryType.MaxLeftLeanAngle] = R.drawable.screen_rotation
            summaryTelemetryToIcon[SummaryTelemetryType.AverageTerrainInclination] = R.drawable.inclination
            summaryTelemetryToIcon[SummaryTelemetryType.MaxUphillTerrainInclination] = R.drawable.inclination
            summaryTelemetryToIcon[SummaryTelemetryType.MaxDownhillTerrainInclination] = R.drawable.inclination
            summaryTelemetryToIcon[SummaryTelemetryType.MaxAltitude] = R.drawable.altitude
            summaryTelemetryToIcon[SummaryTelemetryType.MinAltitude] = R.drawable.altitude

        }
        catch (ex: java.lang.Exception) {
            Log.e("ResultIconConverter", "SYR -> Unable to fill icon dictionary because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     *
     */
    fun getIcon(summaryType: SummaryTelemetryType): Int
    {
        try {
            if (summaryTelemetryToIcon.containsKey(summaryType))
            {
                return summaryTelemetryToIcon[summaryType]!!
            }
        }
        catch (ex: java.lang.Exception) {
            Log.e("ResultIconConverter", "SYR -> Unable to get icon dictionary because: ${ex.message}")
            ex.printStackTrace()
        }

        return 0
    }
}