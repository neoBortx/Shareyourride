package com.example.shareyourride.camera

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.bvillarroya_creations.shareyourride.wifi.ConnectionType
import com.bvillarroya_creations.shareyourride.wifi.WifiConnectionData
import com.example.shareyourride.configuration.SettingPreferencesGetter
import com.example.shareyourride.configuration.SettingPreferencesIds

/**
 * Manages the list of supported cameras
 */
class SupportedCameras
{

    companion object
    {
        val customId = "custom"
        val customName = "Custom"
    }

    /**
     * List of supported cameras
     */
    val supportedCamerasList = mutableListOf<CameraConnectionData>()

    /**
     * Fill the list of supported cameras, this list
     * must be moved to a database in firebase in order to avoid
     * to generate updates when we want to add the support to more cameras
     */
    init {
        supportedCamerasList.add(
                CameraConnectionData(
                        customId,
                        customName,
                        WifiConnectionData(
                                "",
                                ConnectionType.Open,
                                "",
                                ""
                        )
                )
        )

        supportedCamerasList.add(
                CameraConnectionData(
                        "xiaomi_1",
                        "Xiaomi Mi Action camera 4k",
                        WifiConnectionData(
                                "MiCam_",
                                ConnectionType.WPA2,
                                "81844614",
                                "192.168.42.1"
                        )
                )
        )
    }

    /**
     * Function that search the camera configured by the user and returns a CameraConnectionData
     * with the stored data
     *
     * @param context: Required to access a shared preferences
     */
    fun getSavedCameraData(context: Context): CameraConnectionData? {
        try {
            val preferences = SettingPreferencesGetter(context)
            val cameraId = preferences.getStringOption(SettingPreferencesIds.CameraKind)
            val index = supportedCamerasList.indexOfFirst{ it.cameraId == cameraId }
            if (index >= 0)
            {
                return supportedCamerasList[index]
            }
        }
        catch (ex: Exception)
        {
            Log.e("SupportedCameras", "SYR -> Unable to get the Camera information because: ${ex.message}")
            ex.printStackTrace()
        }

        return null
    }
}