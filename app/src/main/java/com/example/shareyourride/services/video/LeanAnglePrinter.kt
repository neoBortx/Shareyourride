package com.example.shareyourride.services.video

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.ScaleDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import com.bvillarroya_creations.shareyourride.R

class LeanAnglePrinter(bottom: Float, left: Float, private val videoScale: Float, val applicationContext: Context) {

    /**
     * Image that points the lean angle inside the transporter
     */
    private lateinit  var leanAnglePointer : Bitmap

    /**
     * Image with the angle transporters to represent the inclination in a familiar way
     */
    private lateinit var leanAngleTransporter : Drawable

    /**
     * Coordinates of the canvas that hols the angle transporter and the lean angle pointer in the canvas of the whole frame
     */
    private lateinit var leanAngleCanvasLocation : Rect

    /**
     * Coordinates of transporter image inside the canvas
     */
    private lateinit var leanAngleTransporterLocation : Rect

    /**
     * The size of the canvas that holds the angle transporter and the angle pointer
     */
    private var innerCanvasSize : Rect = Rect(left.toInt(), (bottom - 30 * videoScale).toInt(), (left + 140 * videoScale).toInt(), (bottom - 40 * videoScale).toInt())

    /**
     * Used to rotate lean angle pointer
     */
    private var matrix = Matrix()

    /**
     * Pivot point to rotate lean angle in the x axis
     */
    private var pivotPointX : Float = 0F

    /**
     * Pivot point to rotate lean angle in the y axis
     */
    private var pivotPointY : Float = 0F

    init
    {
        try
        {
            Log.d("LeanAnglePrinter", "SYR -> Creating LeanAnglePrinter")

            leanAngleCanvasLocation      = Rect(0, (bottom - 30 * videoScale).toInt(), (140 * videoScale).toInt(), (bottom - 40 * videoScale).toInt())
            leanAngleTransporterLocation = Rect(0, 0, (150 * videoScale).toInt(), (81 * videoScale).toInt())

            val drawabler =  ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_pointer).apply {
            }!!

            //AAAAAAAA

            leanAngleTransporter = ContextCompat.getDrawable(applicationContext, R.drawable.ic_lean_angle_transporter).apply {
                this?.bounds = leanAngleTransporterLocation
            }!!

            pivotPointX = (70 * videoScale)
            pivotPointY = (5 * videoScale)

            Log.d("LeanAnglePrinter", "SYR -> Created LeanAnglePrinter")
        }
        catch (ex: Exception)
        {
            Log.e("LeanAnglePrinter", "SYR -> Unable to create Lean angle printer because: ${ex.message}")
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
            val bitmap = Bitmap.createBitmap((150 * videoScale).toInt(), (81 * videoScale).toInt(), Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)




            val auxBitmap = Bitmap.createBitmap((150 * videoScale).toInt(), (81 * videoScale).toInt(), Bitmap.Config.ARGB_8888)
            val auxCanvas = Canvas(auxBitmap)


            auxCanvas.drawBitmap(leanAnglePointer,matrix,null)

            auxCanvas.save()

            leanAngleTransporter.draw(canvas)
            canvas.drawBitmap(auxBitmap,(67) *videoScale ,0F,null)
            canvas.save()

            return bitmap

        }
        catch (ex: Exception)
        {
            Log.e("LeanAnglePrinter", "SYR -> Unable to generate angle ${ex.message}")
            ex.printStackTrace()
        }

        Log.e("SpeedometerPrinter", "SYR -> Unable to get the LeanAnglePrinter image")
        return null

    }

}