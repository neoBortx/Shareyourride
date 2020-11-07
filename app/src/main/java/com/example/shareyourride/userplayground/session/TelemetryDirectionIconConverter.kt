package com.example.shareyourride.userplayground.session

import android.util.Log
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.viewmodels.userplayground.InclinationViewModel

/**
 * Gets the icon associate to a direction
 */
class TelemetryDirectionIconConverter() {
    private var telemetryToLeanIcon: HashMap<InclinationViewModel.LeanDirection, Int> = HashMap()
    private var telemetryToAccelerationIcon: HashMap<InclinationViewModel.AccelerationDirection, Int> = HashMap()

    init {
        try {
            telemetryToLeanIcon[InclinationViewModel.LeanDirection.Right] = R.drawable.right
            telemetryToLeanIcon[InclinationViewModel.LeanDirection.Left] = R.drawable.left

            telemetryToAccelerationIcon[InclinationViewModel.AccelerationDirection.Right] = R.drawable.right
            telemetryToAccelerationIcon[InclinationViewModel.AccelerationDirection.Left] = R.drawable.left
            telemetryToAccelerationIcon[InclinationViewModel.AccelerationDirection.Front] = R.drawable.front
            telemetryToAccelerationIcon[InclinationViewModel.AccelerationDirection.Back] = R.drawable.back

        }
        catch (ex: java.lang.Exception) {
            Log.e("TelemetryIconConverter", "SYR -> Unable to fill icon dictionary because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Gets the icon that represent the direction of the lean angle of the given value
     * @param leanDirection: The direction of the lean angle
     *
     * @return the drawable id of the icon that represent the direction of the lean angle
     */
    fun getDirectionIcon(leanDirection: InclinationViewModel.LeanDirection?): Int
    {
        try {
            if (telemetryToLeanIcon.containsKey(leanDirection))
            {
                return telemetryToLeanIcon[leanDirection]!!
            }
        }
        catch (ex: java.lang.Exception) {
            Log.e("TelemetryIconConverter", "SYR -> Unable to get lean icon dictionary because: ${ex.message}")
            ex.printStackTrace()
        }

        return 0
    }

    /**
     * Gets the icon that represent the direction of the acceleration of the given value
     * @param accelerationDirection: The direction of the acceleration force
     *
     * @return the drawable id of the icon that represent the direction of the acceleration
     */
    fun getDirectionIcon(accelerationDirection: InclinationViewModel.AccelerationDirection?): Int
    {
        try {
            if (telemetryToAccelerationIcon.containsKey(accelerationDirection))
            {
                return telemetryToAccelerationIcon[accelerationDirection]!!
            }
        }
        catch (ex: java.lang.Exception) {
            Log.e("TelemetryIconConverter", "SYR -> Unable to get acceleration icon dictionary because: ${ex.message}")
            ex.printStackTrace()
        }

        return 0
    }


}