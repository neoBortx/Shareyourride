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
class ForcePrinter(bottom: Float, left: Float, videoScale: Float, val applicationContext: Context) {

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
            forceImagesMap[Pair(ForceIntensity.None,AccelerationDirection.None)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_1).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.LessThanOne,AccelerationDirection.Front)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_2).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.One,AccelerationDirection.Front)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_3).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.MoreThanOne,AccelerationDirection.Front)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_4).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.LessThanOne,AccelerationDirection.FrontRight)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_5).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.One,AccelerationDirection.FrontRight)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_6).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.MoreThanOne,AccelerationDirection.FrontRight)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_7).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.LessThanOne,AccelerationDirection.Right)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_8).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.One,AccelerationDirection.Right)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_9).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.MoreThanOne,AccelerationDirection.Right)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_10).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.LessThanOne,AccelerationDirection.BackRight)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_11).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.One,AccelerationDirection.BackRight)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_12).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.MoreThanOne,AccelerationDirection.BackRight)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_13).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.LessThanOne,AccelerationDirection.Back)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_14).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.One,AccelerationDirection.Back)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_15).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.MoreThanOne,AccelerationDirection.Back)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_16).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.LessThanOne,AccelerationDirection.BackLeft)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_17).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.One,AccelerationDirection.BackLeft)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_18).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.MoreThanOne,AccelerationDirection.BackLeft)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_19).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.LessThanOne,AccelerationDirection.Left)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_20).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.One,AccelerationDirection.Left)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_21).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.MoreThanOne,AccelerationDirection.Left)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_22).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.LessThanOne,AccelerationDirection.FrontLeft)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_23).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.One,AccelerationDirection.FrontLeft)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_24).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

            forceImagesMap[Pair(ForceIntensity.MoreThanOne,AccelerationDirection.FrontLeft)] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_fuerza_24).apply {
                this?.bounds =forceImageLocation
            }!!.toBitmap()

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