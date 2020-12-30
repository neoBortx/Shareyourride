/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.services.video

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bvillarroya_creations.shareyourride.R
import kotlin.math.absoluteValue

class LeanAnglePrinter(videoScale: Float, val applicationContext: Context) {


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

            var bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_0)!!.toBitmap()
            leanAngleImagesMap[0] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_5)!!.toBitmap()
            leanAngleImagesMap[5] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_10)!!.toBitmap()
            leanAngleImagesMap[10] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_15)!!.toBitmap()
            leanAngleImagesMap[15] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_20)!!.toBitmap()
            leanAngleImagesMap[20] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_25)!!.toBitmap()
            leanAngleImagesMap[25] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_30)!!.toBitmap()
            leanAngleImagesMap[30] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_35)!!.toBitmap()
            leanAngleImagesMap[35] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_40)!!.toBitmap()
            leanAngleImagesMap[40] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_45)!!.toBitmap()
            leanAngleImagesMap[45] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_50)!!.toBitmap()
            leanAngleImagesMap[50] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_55)!!.toBitmap()
            leanAngleImagesMap[55] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_60)!!.toBitmap()
            leanAngleImagesMap[60] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_65)!!.toBitmap()
            leanAngleImagesMap[65] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_70)!!.toBitmap()
            leanAngleImagesMap[70] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_75)!!.toBitmap()
            leanAngleImagesMap[75] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_80)!!.toBitmap()
            leanAngleImagesMap[80] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_85)!!.toBitmap()
            leanAngleImagesMap[85] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_90)!!.toBitmap()
            leanAngleImagesMap[90] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)




            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_5)!!.toBitmap()
            leanAngleNegativeImagesMap[5] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_10)!!.toBitmap()
            leanAngleNegativeImagesMap[10] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_15)!!.toBitmap()
            leanAngleNegativeImagesMap[15] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_20)!!.toBitmap()
            leanAngleNegativeImagesMap[20] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_25)!!.toBitmap()
            leanAngleNegativeImagesMap[25] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_30)!!.toBitmap()
            leanAngleNegativeImagesMap[30] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_35)!!.toBitmap()
            leanAngleNegativeImagesMap[35] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_40)!!.toBitmap()
            leanAngleNegativeImagesMap[40] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_45)!!.toBitmap()
            leanAngleNegativeImagesMap[45] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_50)!!.toBitmap()
            leanAngleNegativeImagesMap[50] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_55)!!.toBitmap()
            leanAngleNegativeImagesMap[55] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_60)!!.toBitmap()
            leanAngleNegativeImagesMap[60] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_65)!!.toBitmap()
            leanAngleNegativeImagesMap[65] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_70)!!.toBitmap()
            leanAngleNegativeImagesMap[70] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_75)!!.toBitmap()
            leanAngleNegativeImagesMap[75] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_80)!!.toBitmap()
            leanAngleNegativeImagesMap[80] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_85)!!.toBitmap()
            leanAngleNegativeImagesMap[85] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

            bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_grad_neg_90)!!.toBitmap()
            leanAngleNegativeImagesMap[90] = Bitmap.createScaledBitmap( bitmap, (bitmap.width  *videoScale).toInt(), (bitmap.height * videoScale).toInt(), true)

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