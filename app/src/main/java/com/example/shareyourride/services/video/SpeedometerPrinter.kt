package com.example.shareyourride.services.video

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ScaleDrawable
import android.graphics.drawable.VectorDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bvillarroya_creations.shareyourride.R

/**
 * Selects the speedometer image
 */
class SpeedometerPrinter(videoScale: Float, maxSpeed: Float, val applicationContext: Context)
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
            Log.d("SpeedometerPrinter", "SYR -> Creating SpeedometerPrinter video, scale ${videoScale}")

            var bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_1)!!.toBitmap()
            var height = if ((bitmap.height * videoScale).toInt() < 1 ) 1 else (bitmap.height * videoScale).toInt()
            speedometersMap[0] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), height, true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_2)!!.toBitmap()
            height = if ((bitmap.height * videoScale).toInt() < 1 ) 1 else (bitmap.height * videoScale).toInt()
            speedometersMap[1] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), height, true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_3)!!.toBitmap()
            height = if ((bitmap.height * videoScale).toInt() < 1 ) 1 else (bitmap.height * videoScale).toInt()
            speedometersMap[2] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), height, true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_4)!!.toBitmap()
            height = if ((bitmap.height * videoScale).toInt() < 1 ) 1 else (bitmap.height * videoScale).toInt()
            speedometersMap[3] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), height, true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_5)!!.toBitmap()
            speedometersMap[4] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_6)!!.toBitmap()
            speedometersMap[5] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_7)!!.toBitmap()
            speedometersMap[6] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_8)!!.toBitmap()
            speedometersMap[7] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_9)!!.toBitmap()
            speedometersMap[8] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_10)!!.toBitmap()
            speedometersMap[9] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_11)!!.toBitmap()
            speedometersMap[10] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_12)!!.toBitmap()
            speedometersMap[11] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_13)!!.toBitmap()
            speedometersMap[12] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_14)!!.toBitmap()
            speedometersMap[13] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_15)!!.toBitmap()
            speedometersMap[14] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_16)!!.toBitmap()
            speedometersMap[15] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_17)!!.toBitmap()
            speedometersMap[16] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_18)!!.toBitmap()
            speedometersMap[17] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_speedometer_19)!!.toBitmap()
            speedometersMap[18] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)


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