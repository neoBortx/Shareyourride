package com.example.shareyourride.services.video

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.userplayground.common.AccelerationDirection
import kotlin.math.absoluteValue

/**
 * Selects the speedometer image
 */
class ForcePrinter(videoScale: Float, val applicationContext: Context) {

    /**
     * Map that holds the collection of images
     */
    private var forceImagesMap: MutableMap<Pair<ForceIntensity,AccelerationDirection>, Bitmap> = mutableMapOf()


    /**
     * Possible magnitudes of the force to represent in the video
     */
    enum class ForceIntensity {
        /**
         * No supported
         */
        unknown,

        /**
         * G force is less than 0.2
         */
        None,

        /**
         * G force is between 0.2 and 0.8
         */
        LessThanOne,

        /**
         * G force is between 0.8 and 1.2
         */
        One,

        /**
         * G force is higher than 1.2
         */
        MoreThanOne,
    }
    /**
     * Coordinates of the speedometer in the canvas of the whole frame
     */
    private lateinit var forceImageLocation : Rect

    init
    {
        try
        {
            Log.d("ForcePrinter", "SYR -> Creating ForcePrinter")
            forceImageLocation   = Rect(0, 0, (70 * videoScale).toInt(), (70 * videoScale).toInt())

            var bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_1)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.None,AccelerationDirection.None)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_2)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.LessThanOne,AccelerationDirection.Front)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_3)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.One,AccelerationDirection.Front)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_4)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.MoreThanOne,AccelerationDirection.Front)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_5)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.LessThanOne,AccelerationDirection.FrontRight)] =Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_6)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.One,AccelerationDirection.FrontRight)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_7)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.MoreThanOne,AccelerationDirection.FrontRight)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_8)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.LessThanOne,AccelerationDirection.Right)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_9)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.One,AccelerationDirection.Right)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_10)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.MoreThanOne,AccelerationDirection.Right)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_11)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.LessThanOne,AccelerationDirection.BackRight)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_12)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.One,AccelerationDirection.BackRight)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_13)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.MoreThanOne,AccelerationDirection.BackRight)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_14)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.LessThanOne,AccelerationDirection.Back)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_15)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.One,AccelerationDirection.Back)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_16)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.MoreThanOne,AccelerationDirection.Back)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_17)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.LessThanOne,AccelerationDirection.BackLeft)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_18)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.One,AccelerationDirection.BackLeft)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_19)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.MoreThanOne,AccelerationDirection.BackLeft)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_20)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.LessThanOne,AccelerationDirection.Left)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_21)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.One,AccelerationDirection.Left)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_22)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.MoreThanOne,AccelerationDirection.Left)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_23)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.LessThanOne,AccelerationDirection.FrontLeft)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_24)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.One,AccelerationDirection.FrontLeft)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_25)!!.toBitmap()
            forceImagesMap[Pair(ForceIntensity.MoreThanOne,AccelerationDirection.FrontLeft)] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            Log.d("ForcePrinter", "SYR -> Created ForcePrinter")
        }
        catch (ex: Exception)
        {
            Log.e("ForcePrinter", "SYR -> Unable to create ForcePrinter because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Obtains the bitmap related to the given force magnitude and direction
     */
    fun getForceRepresentation(currentForce: Float, direction: AccelerationDirection): Bitmap?
    {
        try
        {
            val magnitude = when {
                currentForce.absoluteValue < 0.2 ->
                {
                    ForceIntensity.None
                }
                currentForce.absoluteValue in 0.2..0.8 ->
                {
                    ForceIntensity.LessThanOne
                }
                currentForce.absoluteValue in 0.8..1.2 ->
                {
                    ForceIntensity.One
                }
                else -> {
                    ForceIntensity.MoreThanOne
                }
            }

            val pair = if (magnitude == ForceIntensity.None) Pair(magnitude, AccelerationDirection.None) else Pair(magnitude,direction)
            return if (forceImagesMap.containsKey(pair))
            {
                forceImagesMap[pair]
            }
            else
            {
                forceImagesMap[Pair(ForceIntensity.None,AccelerationDirection.None)]
            }
        }
        catch (ex: Exception)
        {
            Log.e("ForcePrinter", "SYR -> Unable to get the force image because: ${ex.message}")
            ex.printStackTrace()
        }

        Log.e("ForcePrinter", "SYR -> Unable to get the force image")
        return null
    }
}