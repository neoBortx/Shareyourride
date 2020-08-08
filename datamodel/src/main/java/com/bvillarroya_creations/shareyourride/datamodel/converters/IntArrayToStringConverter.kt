package com.bvillarroya_creations.shareyourride.datamodel.converters

import androidx.room.TypeConverter
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class IntArrayToStringConverter {
    @TypeConverter
    fun fromIntArray(values: IntArray?): String {
        val jsonArray = JSONArray(listOf(values))
        return jsonArray.toString()
    }

    @TypeConverter
    fun fromString(values: String?): IntArray {
        try {
            val jsonArray = JSONArray(values)
            val floatArray = IntArray(jsonArray.length())
            for (i in 0 until jsonArray.length()) {
                floatArray[i] = jsonArray.getString(i).toInt()
            }
            return floatArray
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return IntArray(3)
    }
}
