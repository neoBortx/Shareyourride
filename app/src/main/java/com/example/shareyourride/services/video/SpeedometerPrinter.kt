package com.example.shareyourride.services.video

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bvillarroya_creations.shareyourride.R

/**
 * Selects the speedometer image
 */
class SpeedometerPrinter(bottom: Float, left: Float, videoScale: Float, private val maxSpeed: Float, val applicationContext: Context)
{

    /**
     * Map that holds all speedometer images
     */
    private var speedometersMap: MutableMap<Int, Bitmap> = mutableMapOf()

    /**
     * Maps that holds different tokens
     */
    private var speedTokensMap: MutableMap<Int, Int> = mutableMapOf()
    /**
     * Coordinates of the speedometer in the canvas of the whole frame
     */
    private lateinit  var speedImageLocation : Rect

    init
    {
        try
        {
            Log.d("SpeedometerPrinter", "SYR -> Creating SpeedometerPrinter")
            speedImageLocation   = Rect(0, 0, (110 * videoScale).toInt(), (35 * videoScale).toInt())
            speedometersMap[0] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_1).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[1] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_2).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[2] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_3).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[3] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_4).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[4] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_5).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[5] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_6).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[6] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_7).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[7] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_8).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[8] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_9).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[9] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_10).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[10] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_11).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[11] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_12).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[12] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_13).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[13] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_14).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[14] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_15).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[15] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_16).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[16] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_17).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[17] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_18).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            speedometersMap[18] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_19).apply {
                this?.bounds = speedImageLocation
            }!!.toBitmap()

            //Split the max speed into tokens
            val token = maxSpeed/19

            var count = 0

            while(count < 19)
            {
                speedTokensMap[count] = (count * token).toInt()
                count++
            }

            Log.d("SpeedometerPrinter", "SYR -> Created SpeedometerPrinter")
        }
        catch (ex: Exception)
        {
            Log.e("SpeedometerPrinter", "SYR -> Unable to create SpeedometerPrinter because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    fun getSpeedometer(currentSpeed: Float): Bitmap?
    {
        try
        {
            var count = 18
            while(count >= 0)
            {
                if (currentSpeed >= speedTokensMap[count]!!)
                {
                    return speedometersMap[count]
                }
                else
                {
                    count--
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("SpeedometerPrinter", "SYR -> Unable to get the speedometer image because: ${ex.message}")
            ex.printStackTrace()
        }

        Log.e("SpeedometerPrinter", "SYR -> Unable to get the speedometer image")
        return null
    }
}