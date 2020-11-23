package com.bvillarroya_creations.shareyourride.datamodel.converters

import androidx.room.TypeConverter
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class FloatArrayToStringConverter {
    @TypeConverter
    fun fromFloatArray(values: FloatArray?): String
    {
        try
        {
            val jsonArray = JSONArray(listOf(values))
            return jsonArray.toString() ?: ""
        }
        catch (e: JSONException)
        {
            e.printStackTrace()
        }

        return ""
    }

    @TypeConverter
    fun fromString(values: String?): FloatArray
    {
        try
        {
            val jsonArray = JSONArray(values)
            val floatArray = FloatArray(jsonArray.length())
            for (i in 0 until jsonArray.length())
            {
                floatArray[i] = jsonArray.getString(i).toFloat()
            }
            return floatArray
        }
        catch (e: JSONException)
        {
            e.printStackTrace()
        }
        return FloatArray(3)
    }
}
