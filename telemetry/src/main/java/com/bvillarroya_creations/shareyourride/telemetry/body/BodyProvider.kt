package com.bvillarroya_creations.shareyourride.telemetry.body

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import androidx.lifecycle.Observer
import com.bvillarroya_creations.shareyourride.bluetooth.BluetoothController
import com.bvillarroya_creations.shareyourride.messenger.IMessageHandlerClient
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.messenger.MessageHandler
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.IDataProvider
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.bvillarroya_creations.shareyourride.telemetry.messages.TelemetryMessageTopics

/**
 * Class in charge of manage the location services
 */
class BodyProvider(private val context: Context): IDataProvider, IMessageHandlerClient
{

    //region variables
    /**
     * Class that manages the connection through bluetooth
     */
    val bluetoothController = BluetoothController(context)

    /**
     * The state of the location provider
     */
    private var mProviderState: IDataProvider.ProviderState =   IDataProvider.ProviderState.STOPPED
    //endregion


    //region IDataProvider

    /**
     * Creates the observer which handle telemetryChanged
     */
    private val handleScannedDevices = Observer<MutableList<BluetoothDevice>> { event ->
        event?.forEach {
            Log.d("BodyProvider", "-------------------------------------------")
            Log.d("BodyProvider", "AAAAAAAAAAAAAAAAAAAAAAAAAAA name: ${it.name}")
            Log.d("BodyProvider", "AAAAAAAAAAAAAAAAAAAAAAAAAAA uuids: ${it.uuids}")
            Log.d("BodyProvider", "AAAAAAAAAAAAAAAAAAAAAAAAAAA type: ${it.type}")
            Log.d("BodyProvider", "AAAAAAAAAAAAAAAAAAAAAAAAAAA address: ${it.address}")
            Log.d("BodyProvider", "AAAAAAAAAAAAAAAAAAAAAAAAAAA bondState: ${it.bondState}")
        }
    }

    /**
     * Configure Location services with the priority and the pooling interval
     * Configure the location client
     */
    override fun configureProvider()
    {
        try
        {
            bluetoothController.scannedDevices.observeForever(handleScannedDevices)
            bluetoothController.scanBluetoothDevice()
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to configure to location provider ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Set the handler that is going gto precess changes in the location
     * @param callback: Function that is going to process location updates
     */
    @SuppressLint("MissingPermission")
    override fun subscribeProvider(callback: (ITelemetryData) -> Unit) {
        try {

        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to subscriber to location provider ${ex.message}")
            ex.printStackTrace()
        }

    }

    /**
     * Remove the handler of the location updates
     */
    override fun stopProvider() {
        try {

        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to stop location provers: ${ex.message}")
            ex.printStackTrace()
        }

        mProviderState = IDataProvider.ProviderState.STOPPED
    }

    /**
     * Returns the state of the Location provider
     */
    override fun getProviderState(): IDataProvider.ProviderState
    {
        return mProviderState
    }
    //endregion

    //region IMessageHandlerClient implementation
    init {
        this.createMessageHandler( listOf<String>(TelemetryMessageTopics.TELEMETRY_DATA))
    }

    override lateinit var messageHandler: MessageHandler

    /**
     * No message required
     */
    override fun processMessage(msg: MessageBundle) {
        Log.d("SYR", "SYR -> Skipping message")
    }
    //endregion

}
