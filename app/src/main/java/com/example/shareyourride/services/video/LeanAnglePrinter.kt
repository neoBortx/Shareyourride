package com.example.shareyourride.services.video

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bvillarroya_creations.shareyourride.R
import kotlin.math.absoluteValue

class LeanAnglePrinter(bottom: Float, left: Float, private val videoScale: Float, val applicationContext: Context) {


    /**
     * Map that holds the collection of images
     */
    private var leanAngleImagesMap: MutableMap<Int, Bitmap> = mutableMapOf()

    /**
     * Map that holds the collection of images
     */
    private var leanAngleNegativeImagesMap: MutableMap<Int, Bitmap> = mutableMapOf()


    /**
     * Coordinates of the canvas that hols the angle transporter and the lean angle pointer in the canvas of the whole frame
     */
    private lateinit var leanAngleLocation : Rect

    init
    {
        try
        {
            Log.d("LeanAnglePrinter", "SYR -> Creating LeanAnglePrinter")
            leanAngleLocation   = Rect(0, 0, (50 * videoScale).toInt(), (50 * videoScale).toInt())

            leanAngleImagesMap[0] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_0).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[5] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_5).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[10] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_10).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[15] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_15).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[20] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_20).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[25] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_25).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[30] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_30).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[35] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_35).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[40] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_40).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[45] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_45).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[50] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_50).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[55] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_55).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[60] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_60).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[65] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_65).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[70] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_70).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[75] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_75).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[80] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_80).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[85] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_85).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleImagesMap[90] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_90).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleNegativeImagesMap[5] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_5).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleNegativeImagesMap[10] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_10).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleNegativeImagesMap[15] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_15).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleNegativeImagesMap[20] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_20).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleNegativeImagesMap[25] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_25).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleNegativeImagesMap[30] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_30).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()
            leanAngleNegativeImagesMap[35] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_35).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleNegativeImagesMap[40] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_40).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleNegativeImagesMap[45] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_45).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleNegativeImagesMap[50] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_50).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleNegativeImagesMap[55] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_55).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleNegativeImagesMap[60] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_60).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleNegativeImagesMap[65] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_65).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleNegativeImagesMap[70] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_70).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleNegativeImagesMap[75] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_75).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleNegativeImagesMap[80] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_80).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleNegativeImagesMap[85] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_85).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            leanAngleNegativeImagesMap[90] = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_90).apply {
                this?.bounds = leanAngleLocation
            }!!.toBitmap()

            Log.d("LeanAnglePrinter", "SYR -> Created LeanAnglePrinter")
        }
        catch (ex: Exception)
        {
            Log.e("LeanAnglePrinter", "SYR -> Unable to create LeanAnglePrinter because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Generates an image representing the given angle
     *
     * @param angle: The angle to show
     *
     * @return a drawable object to insert in the
     */
    fun getAngle(angle: Int): Bitmap?
    {
        try
        {

            //If the angle is lower than 10, select the 0 angle image
            //else take the approximate representation of the angle, 5 by 5
            return when {
                angle.absoluteValue <= 5 ->
                {
                    leanAngleImagesMap[0]
                }
                angle > 0 ->
                {
                    leanAngleImagesMap.toList().reversed().first { it.first <= angle.absoluteValue}.second
                }
                angle < 0 ->
                {
                    leanAngleNegativeImagesMap.toList().reversed().first { it.first <= angle.absoluteValue}.second
                }
                else ->
                {
                    null
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("LeanAnglePrinter", "SYR -> Unable to generate angle ${ex.message}")
            ex.printStackTrace()
        }

        Log.e("LeanAnglePrinter", "SYR -> Unable to get the LeanAnglePrinter image")
        return null
    }

}