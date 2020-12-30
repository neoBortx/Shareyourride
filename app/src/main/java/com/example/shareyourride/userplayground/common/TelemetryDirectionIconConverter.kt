/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.userplayground.common

import android.util.Log
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.viewmodels.userplayground.InclinationViewModel
import java.util.*
import kotlin.math.absoluteValue

enum class AccelerationDirection(val value: Int) {
    /**
     * Unknown force
     */
    Unknown(0),

    /**
     * NO forces at all
     */
    None(1),

    /**
     * Negative force in the x axis
     */
    Left(2),

    /**
     * Positive forces in the y axis and negative in the x axis
     */
    FrontLeft(3),

    /**
     * Positive force in the y axis
     */
    Front(4),

    /**
     * Positive force in the y and x axis
     */
    FrontRight(5),

    /**
     * Positive force in the x axis
     */
    Right(6),

    /**
     * Positive force in the x axis and negative in the y axis
     */
    BackRight(7),
    /**
     * Negative force in the y axis
     */
    Back(8),

    /**
     * Negative forces in the x and y axis
     */
    BackLeft(9);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}
//endregion

/**
 * Gets the icon associate to a direction
 */
class TelemetryDirectionIconConverter() {

    companion object
    {
        /**
         * Calculate the direction of the acceleration
         *
         * @param x: force in the x axis
         * @param y: Force in the u axis
         *
         * @return The acceleration direction
         */
        fun getAccelerationDirection(x: Float, y :Float): AccelerationDirection
        {
            /**
             * If absolute value is less than 0.2 (error correction factor)
             */
            val xAxisZero = x.absoluteValue < 0.2
            val xAxisNegative = x <= 0.2
            val xAxisPositive = x >= 0.2

            /**
             * If absolute value is less than 0.2 (error correction factor)
             */
            val yAxisZero = y.absoluteValue < 0.2
            val yAxisNegative = y <= 0.2
            val yAxisPositive = y >= 0.2

            if (yAxisZero && xAxisZero)
            {
                return AccelerationDirection.None
            }
            else if (yAxisZero && xAxisNegative)
            {
                return AccelerationDirection.Left
            }
            else if (yAxisPositive && xAxisNegative)
            {
                return AccelerationDirection.FrontLeft
            }
            else if (yAxisPositive && xAxisZero)
            {
                return AccelerationDirection.Front
            }
            else if (yAxisPositive && xAxisPositive)
            {
                return AccelerationDirection.FrontRight
            }
            else if (yAxisZero && xAxisPositive)
            {
                return AccelerationDirection.Right
            }
            else if (yAxisNegative && xAxisPositive)
            {
                return AccelerationDirection.BackRight
            }
            else if (yAxisNegative && xAxisZero)
            {
                return AccelerationDirection.Back
            }
            else if (yAxisNegative && xAxisNegative)
            {
                return AccelerationDirection.BackLeft
            }
            else
            {
                return AccelerationDirection.None
            }
        }
    }

    private var telemetryToLeanIcon: EnumMap<InclinationViewModel.LeanDirection, Int> = EnumMap(InclinationViewModel.LeanDirection::class.java)
    private var telemetryToAccelerationIcon: EnumMap<AccelerationDirection, Int> = EnumMap(AccelerationDirection::class.java)

    init {
        try {
            telemetryToLeanIcon[InclinationViewModel.LeanDirection.Right] = R.drawable.right
            telemetryToLeanIcon[InclinationViewModel.LeanDirection.Left] = R.drawable.left

            telemetryToAccelerationIcon[AccelerationDirection.Right] = R.drawable.right
            telemetryToAccelerationIcon[AccelerationDirection.Left] = R.drawable.left
            telemetryToAccelerationIcon[AccelerationDirection.Front] = R.drawable.front
            telemetryToAccelerationIcon[AccelerationDirection.Back] = R.drawable.back

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
    fun getDirectionIcon(accelerationDirection: AccelerationDirection?): Int
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