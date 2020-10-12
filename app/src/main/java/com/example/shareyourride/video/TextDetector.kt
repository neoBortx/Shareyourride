package com.example.shareyourride.video

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition

/**
 *
 */
class TextDetector(private val context: Context, private val detectedTextCallback : (Long) -> Unit)
{


    /**
     *  Look for into the given image the text passed as parameter
     *  If the text is found, a callback function will be triggered
     *  in the main thread.
     *
     * @param bitmap: Image to process
     * @param controlText: piece o text to search in the given image
     * @param timeStamp: The timestamp when the frame is captured. It will be returned in the callback function
     */
    fun detectControlTextInImage(bitmap: Bitmap, controlText: String, timeStamp: Long) {

        try {
                TextRecognition.getClient().process(getVisionImageFromFrame(bitmap))
                .addOnSuccessListener {detectedText ->

                    Log.e("TextDetector","SYR -> Detected text:  ${detectedText.text}")
                    if (detectedText.text.contains(controlText))
                    {
                        //execute the call back in the main thread
                        val mainHandler = Handler(context.mainLooper)
                        mainHandler.post {  detectedTextCallback(timeStamp) }
                    }
                }
                .addOnFailureListener { ex ->
                    Log.e("TextDetector","SYR -> unable to process image, error_ ${ex.message}")
                    ex.printStackTrace()
                }
        }
        catch(ex: Exception)
        {

            ex.printStackTrace()
        }


    }

    private fun getVisionImageFromFrame(bitmap : Bitmap) : InputImage{
        return InputImage.fromBitmap(bitmap,0)
    }
}