package com.bvillarroya_creations.shareyourride.wifi.interfaces

import android.content.Context
import android.net.wifi.ScanResult
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.bvillarroya_creations.shareyourride.wifi.WifiConnectionData
import com.bvillarroya_creations.shareyourride.wifi.WifiConnectionManager
import com.bvillarroya_creations.shareyourride.wifi.WifiScanner
import java.lang.Exception

/**
 * Interface that must be implemented for classes that want to use the WIFI library
 */
abstract class WifiClient(private val context: Context,private val cycleOwner: LifecycleOwner) {

    //region wifi managers
    /**
     *
     */
    private val wifiScanner = WifiScanner(context)

    private val wifiConnectionManager = WifiConnectionManager(context, ::processWifiConnectionEstablished)
    //endregion

    //region private properties
    /**
     * The SSID identifier that is searched in searchWifiNetwork
     */
    private var ssidToSearch : String = ""

    /**
     * The data of the wifi network that the API is trying to connect in that moment in function connectToNetwork
     */
    private var connectionData: WifiConnectionData? = null
    //endregion

    //region observers
    private val scanFinishedObserver = Observer<List<ScanResult>> { items ->
        stopScanning()
        processScanFinished(items)
    }

    //Observer to process the end due an error of the wifi scan
    private val scanFailsObserver = Observer<Boolean> {
        Log.e("WifiClient", "SYR -> Scanning procedure for Wifi networks has failed")
        stopScanning()
        processScanFails()
    }

    private val scanForNetworkFinishedObserver = Observer<List<ScanResult>> { items ->
        stopScanning()
        val index = items.indexOfFirst { it.SSID.contains(ssidToSearch) }
        if (index >= 0)
        {
            Log.i("WifiClient", "SYR -> Found network with SSID: ${items[index].SSID} ")
            processWifiNetworkFound(items[index])
        }
        else {
            Log.e("WifiClient", "SYR -> There aren't any network available with SSID: $ssidToSearch")
            processWifiNetworkFound(null)
        }
    }

    //Observer to process the end due an error of the wifi scan
    private val scanForNetworkFailsObserver = Observer<Boolean> {
        stopScanning()
        Log.e("WifiClient", "SYR -> Error while scanning for networks with SSID: $ssidToSearch")
        processWifiNetworkFound(null)
    }

    //look for a wifi network that contains part of the given SSID
    private val scanFinishedWhileConnectingObserver = Observer<List<ScanResult>> { items ->
        stopScanning()
        if (connectionData != null) {
            val index = items.indexOfFirst { it.SSID.contains(connectionData!!.ssidName) }
            if (index >= 0) {
                Log.i("WifiClient", "SYR -> Trying to connect to network with SSID: ${items[index].SSID}")

                wifiConnectionManager.connectToNetwork(connectionData!!, items[index].SSID)
            }
            else {
                Log.e("WifiClient", "SYR -> SSID ${connectionData!!.ssidName} not found, unable to connect to wfi")
                processWifiConnectionEstablished(null)
            }
        }
        else
        {
            Log.e("WifiClient", "SYR -> connectionData is null, unable to complete the connection procedure")
        }
    }

    //Observer to process the end due an error of the wifi scan
    private val scanFailsWhileConnectingObserver = Observer<Boolean> { _ ->
        stopScanning()
        processWifiConnectionEstablished(null)
    }
    //endregion

    //region public functions
    /**
     * Start scanning for all available Wifi networks
     * - When the scan finishes, the call back method processScanFinished is invoked
     *   with the list of the discovered networks
     * - When the scan fails, the call back method processScanFails is invoked
     */
    fun startScanning()
    {
        Log.d("WifiClient", "Start scanning WIFI networks")

        try {
            //Observer to process the end of the wifi scan
            wifiScanner.scanFinished.observe(cycleOwner, scanFinishedObserver)
            wifiScanner.scanFails.observe(cycleOwner, scanFailsObserver)

            wifiScanner.startScanning()
        }
        catch (ex: Exception)
        {
            Log.e("WifiClient", "SYR -> Exception while start scanning for wifi networks")
            ex.printStackTrace()
        }
    }

    /**
     * Unsubscribe to scanFinished and scan fail events
     */
    fun stopScanning()
    {
        try {

            Log.d("WifiClient", "SYR -> Stop scanning WIFI networks")

            wifiScanner.scanFinished.removeObservers(cycleOwner);
            wifiScanner.scanFails.removeObservers(cycleOwner);
        }
        catch (ex: Exception)
        {
            Log.e("WifiClient", "SYR -> Exception while stopping scanning for wifi networks")
            ex.printStackTrace()
        }
    }

    /**
     * Function to search a wifi network with the same SSID
     */
    fun searchWifiNetwork(ssid: String)
    {

        Log.d("WifiClient", "Start scanning WIFI networks with SSID: $ssid")

        try {
            ssidToSearch = ssid

            //Observer to process the end of the wifi scan
            wifiScanner.scanFinished.observe(cycleOwner, scanForNetworkFinishedObserver)
            wifiScanner.scanFails.observe(cycleOwner, scanForNetworkFailsObserver)

            wifiScanner.startScanning()
        }
        catch (ex: Exception)
        {
            Log.e("WifiClient", "SYR -> Exception while scanning for networks with SSID: $ssid")
            ex.printStackTrace()
        }
    }

    /**
     * Looks for a WIFI network that has or contains the given SSID
     * Then, using the connection parameters, will try to connect to the
     * Wifi network.
     *
     * When the connection is established the call back method processWifiConnectionEstablished is
     * invoked.
     * If the attempt fails, the same call back method is triggered with a null value
     *
     * @param connection: Pre configured data required to discover and connect to a WIFI network
     * @param context: context that holds the execution of this method
     */
    fun connectToNetwork(connection: WifiConnectionData,context: Context)
    {
        try {
            connectionData = connection

            wifiScanner.scanFinished.observe(cycleOwner, scanFinishedWhileConnectingObserver)
            wifiScanner.scanFails.observe(cycleOwner, scanFailsWhileConnectingObserver)

            wifiScanner.startScanning()
        }
        catch (ex: Exception)
        {
            Log.e("WifiClient", "SYR -> Exception while scanning for networks with SSID: ${connection.ssidName}")
            ex.printStackTrace()
        }
    }

    //endregion

    //region abstract functions
    /**
     * Method invoked when the scan of the wifi networks works
     * @param scannedNetworks: a list of networks retrieved with the scanning tool
     */
    abstract fun processScanFinished(scannedNetworks: List<ScanResult>)

    /**
     * Method invoked when the scan of the wifi networks fails
     */
    abstract fun processScanFails()

    /**
     * Method invoked when the searchWifiNetwork function finished
     * This callback function will receive the data of the network
     * or a null value if the network is not found
     *
     * @param network: the found network or null value if the network is not present
     */
    abstract fun processWifiNetworkFound(network: ScanResult?)

    /**
     * Method invoked when the connectToNetwork function finished
     * This callback function will receive the data of the network
     * or a null value if the network is not found
     *
     * @param connected: the found network or null value if the network is not present
     */
    abstract fun processWifiConnectionEstablished(connected: Boolean?)
    //endregion

}