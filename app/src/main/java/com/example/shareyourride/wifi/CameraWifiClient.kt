package com.example.shareyourride.wifi

import android.content.Context
import android.net.wifi.ScanResult
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.bvillarroya_creations.ffmpegWrapper.RtspClient
import com.bvillarroya_creations.shareyourride.wifi.interfaces.WifiClient
import com.example.shareyourride.camera.CameraConnectionData

/**
 *
 */
class CameraWifiClient(private val context: Context,
                       private val cameraData: CameraConnectionData,
                       cycleOwner: LifecycleOwner
                        ): WifiClient(context,cycleOwner) {

    fun connectToCamera()
    {
        Log.d("CameraWifiClient", "SYR -> Searching for wifi connection of camera: " +
                " ${cameraData.name} - ssid: ${cameraData.connectionData.ssidName}")

        connectToNetwork(cameraData.connectionData,context)
    }

    override fun processScanFinished(scannedNetworks: List<ScanResult>) {
        scannedNetworks.forEach {
            Log.d(
                    "CameraWifiClient", "SYR -> WIFI network found ${it.SSID} - ${it.level}")
            }
    }

    override fun processScanFails() {
        Log.e("CameraWifiClient", "SYR -> Scan failed")
    }

    override fun processWifiNetworkFound(network: ScanResult?) {
        Log.d("CameraWifiClient", "SYR -> Wifi network found for ${cameraData.name} ssid: " +
                "${network?.SSID}")

        connectToNetwork(cameraData.connectionData, context)
    }

    override fun processWifiConnectionEstablished(connected: Boolean?) {
        if (connected != null)
        {
            Log.i("CameraWifiClient", "SYR -> Connected to the wifi created by ${cameraData.name} " +
                    "SSID: ${cameraData.connectionData.ssidName}")

            val rtp = RtspClient();
            rtp.getStream(/*cameraData.connectionData.gateway*/);
        }
    }


}