/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.common

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import java.io.File

class MediaStoreUtils(val context: Context) {

    private val resolver: ContentResolver = context.contentResolver
    private var contentValues : ContentValues? = null

    private var fileUri: Uri? = null

    /**
     *
     */
    fun openVideoDescriptor(sessionName: String, format: String): File?
    {

        try {
            contentValues = ContentValues().apply {
                val folderName = "Movies/Share your ride"
                //put(MediaStore.Images.Media.MIME_TYPE, MimeUtils.guessMimeTypeFromExtension(getExtension(fileName)))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "$folderName/")
                    put(MediaStore.Images.Media.DISPLAY_NAME, "$sessionName.$format")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

            }

            val collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            fileUri = context.contentResolver.insert(collection, contentValues)
        }
        catch(ex: Exception)
        {
            Log.e("MediaStoreUtils", "SYR -> Unable to open file descriptor to file $sessionName, because: ${ex.message}")
            ex.printStackTrace()
        }

        Log.d("MediaStoreUtils", "SYR -> Created file with path ${fileUri?.path ?: ""} for video")

        return if (fileUri != null) {
            File(fileUri!!.path!!)
        }
        else {
            null
        }

}

        fun closeVideoDescriptor()
        {
            try {

                contentValues?.apply {
                    put(MediaStore.Images.Media.IS_PENDING, 0)
                }

                resolver.update(fileUri!!, contentValues, null, null);
            }
            catch(ex: Exception)
            {
                Log.e("MediaStoreUtils", "SYR -> Unable to close file descriptor, because: ${ex.message}")
                ex.printStackTrace()
            }

        }

    /**
     * Retun the name of the full path of the file
     */
    private fun getRealPathFromURI(): String {
        var cursor: Cursor? = null
        return try {
            cursor = context.contentResolver.query(fileUri!!, null, null, null, null)
            if (cursor != null)
            {
                val nameIndex: Int = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            }
            else
            {
                ""
            }
        }
        finally {
            if (cursor != null) {
                cursor.close()
            }
            else
            {
                ""
            }
        }
    }

}