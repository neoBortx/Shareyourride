package com.example.shareyourride.wifi

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.camera.SupportedCameras

/**
 * Wifi to manage the WIFI connection
 */
class WifiViewModel(application: Application) : AndroidViewModel(application)
{

    /**
     * In charge of manage the wifi connection
     */
   private var wifiClient : CameraWifiClient = CameraWifiClient(application.applicationContext)

    /**
     * Used to notify upper layers that the state of the connection
     */
    var wifiConnected : MutableLiveData<Boolean> = wifiClient.wifiConnected

    /**
     * Used to notify upper layers that the state of the connection
     */
    var wifiEnabled : MutableLiveData<Boolean> = wifiClient.wifiEnabled

    /**
     * Flag to open the setting activity
     */
    var openSettingsActivity : MutableLiveData<Boolean> = wifiClient.wifiEnabled

    private var context = application.applicationContext

    /**
     * - Get a wifi client
     * - Get the camera connection data
     * - Check if the connection is established jey
     *      - Yes: Leave
     *      - N. Try to connect
     */
    fun connectToWifi(activity: Activity )
    {
        try
        {
            val cameras = SupportedCameras(getApplication())
            val cameraData = cameras.getSavedCameraData(context)

            if (cameraData != null)
            {
                wifiClient.configureClient(cameraData)
                Log.i("WifiViewModel", "SYR -> Stablising connection to camera ${cameraData.name}")

                if (wifiClient.wifiEnabled.value!!)
                {
                    if (wifiClient.wifiConnected.value!!)
                    {
                        Log.i("WifiViewModel", "SYR -> Connection with ${cameraData.name} -${cameraData.connectionData.ssidName} already established")
                    }
                    else
                    {
                        Log.i("WifiViewModel", "SYR -> Proceeding to connect with ${cameraData.name} -${cameraData.connectionData.ssidName}")
                        wifiClient.disconnectFromNetwork()
                        wifiClient.connectToNetwork()
                    }
                }
                else
                {
                    Log.i("WifiViewModel", "SYR -> Enabling WIFI because the device is disconnected")
                    wifiClient.enableWifi(activity)
                }
            }
            else
            {
                Log.e("WifiViewModel", "SYR -> No camera data configured")
                Toast.makeText(context, context.getString(R.string.configure_cam), Toast.LENGTH_LONG).show()

            }
        }
        catch (ex: Exception)
        {
            Log.e("WifiViewModel", "SYR -> Unable to connect to wifi: ${ex.message}")
            ex.printStackTrace()
        }
    }

    fun changeWifiNetwork()
    {
        try {
            Log.i("WifiViewModel", "SYR -> changing the wifi parameters")
            wifiClient.disconnectFromNetwork()

            wifiClient.connectToNetwork()
        }
        catch(ex: java.lang.Exception)
        {

        }
    }
}