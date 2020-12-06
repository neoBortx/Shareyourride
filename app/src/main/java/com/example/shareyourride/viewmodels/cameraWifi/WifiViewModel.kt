package com.example.shareyourride.viewmodels.cameraWifi

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.camera.SupportedCameras
import com.example.shareyourride.camera.CameraWifiClient
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

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
     * The application context
     */
    private var context = application.applicationContext

    private var lifecycleOwner: LifecycleOwner? = null

    fun initialize(lifecycleOwner: LifecycleOwner, activity: Activity)
    {
        this.lifecycleOwner = lifecycleOwner
        wifiClient.wifiEnabled.observe(lifecycleOwner, Observer {

            if (wifiClient.wifiEnabled.value!!)
            {
                connectToWifi(activity)
            }
        })

        wifiClient.wifiEnabled.observe(lifecycleOwner, Observer {
            if (wifiClient.wifiEnabled.value!!)
            {
                val observable = Observable.timer(5000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe {
                    Log.e("WifiViewModel", "SYR -> Retrying connect to WIFI")
                    connectToWifi(activity)
                }
            }
        })

    }

    /**
     * - Get a wifi client
     * - Get the camera connection data
     * - Check if the connection is established jet
     *      - Yes: Leave
     *      - No Try to connect
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

    fun changeWifiNetwork(activity: Activity )
    {
        try {
            Log.i("WifiViewModel", "SYR -> changing the wifi parameters")
            wifiClient.disconnectFromNetwork()
            connectToWifi(activity)
        }
        catch(ex: java.lang.Exception)
        {
            Log.e("WifiViewModel", "SYR -> Unable to change wifi network because: ${ex.message}")
            ex.printStackTrace()
        }
    }
}