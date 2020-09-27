package com.example.shareyourride.camera

import android.content.Context
import android.util.Log
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
                                ""),
                        "rtsp",
                        "",
                        ""
                )
        )

        supportedCamerasList.add(
                CameraConnectionData(
                        "xiaomi_1",
                        "Xiaomi Mi Action camera 4k",
                        WifiConnectionData(
                                "MiCam_",
                                ConnectionType.WPA2,
                                ""),
                        "rtsp",
                        "192.168.42.1",
                        "live"
                )
        )
    }

    /**
     * Function that search the camera configured by the user and returns a CameraConnectionData
     * with the stored data
     *
     * @param context: Required to access a shared preferences
     */
    fun getSavedCameraData(context: Context): CameraConnectionData?
    {
        try {
            val preferences = SettingPreferencesGetter(context)

            val cameraId = preferences.getStringOption(SettingPreferencesIds.CameraId)

            val index = supportedCamerasList.indexOfFirst{ it.cameraId == cameraId }

            if (index >= 0)
            {
                val connectionData = WifiConnectionData(
                        preferences.getStringOption(SettingPreferencesIds.CameraSsidName),
                        enumValueOf(preferences.getStringOption(SettingPreferencesIds.CameraConnectionType)),
                        preferences.getStringOption(SettingPreferencesIds.CameraPassword))

                return CameraConnectionData(
                        cameraId,
                        preferences.getStringOption(SettingPreferencesIds.CameraName),
                        connectionData,
                        "rtsp",
                        preferences.getStringOption(SettingPreferencesIds.CameraPath),
                        preferences.getStringOption(SettingPreferencesIds.CameraIp))
            }
            else
            {
                Log.e("SupportCamera", "SYR -> Saved camara id $cameraId not supported")
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