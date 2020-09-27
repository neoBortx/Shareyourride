package com.bvillarroya_creations.shareyourride.telemetry.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bvillarroya_creations.shareyourride.telemetry.events.TelemetryEvent
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.IDataProvider
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryManager

/**
 * This is the access point or the facade to access the telemetry provider
 */
abstract class ClientManager(protected val context: Context): BroadcastReceiver(), ITelemetryManager {

    //region abstract properties
    /**
     * Class in charge of getting the data from the different system sensors
     */
    abstract var mDataProvider: IDataProvider?
    //endregion

    //region properties
    /**
     * Notifies upper layers that new data is available
     */
    override val telemetryChanged: MutableLiveData<TelemetryEvent> = MutableLiveData()
    //endregion

    //region client state
    /**
     * Flag that points if the provider is ready
     */
    override var providerReady: MutableLiveData<Boolean> = MutableLiveData()
    //endregion

    //region telemetry data handler
    /**
     * Process the telemetry that the client is managing and send it to upper layers using
     * the observable pattern
     */
    private fun processTelemetryData(data: ITelemetryData) {

        try {
            telemetryChanged.postValue(TelemetryEvent(telemetryEventType,data))
        }
        catch (ex: Exception)
        {
            Log.e("ClientManager", "SYR -> Unable to send telemetry to upper layers, exception: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    //region ITelemetryManager implementation
    /**
     * Initialize internal handlers
     */
    override fun configure() {
        context.applicationContext.registerReceiver(this, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        context.applicationContext.registerReceiver(this, IntentFilter(LocationManager.GPS_PROVIDER));
    }

    /**
     * Stop the data provider of listening telemetry events
     */
    override fun stopAcquiringData() {
        if (mDataProvider != null) {
            mDataProvider?.stopProvider()
        }
    }

    /**
     * Returns the state of the data provider
     */
    override fun getManagerState(): IDataProvider.ProviderState {
        return mDataProvider?.getProviderState() ?:  IDataProvider.ProviderState.STOPPED
    }

    /**
     * Start the provider of the data provider
     */
    override fun startAcquiringData() {
        Log.d("ClientManager", "Start AcquiringData")
        if (mDataProvider != null) {
            mDataProvider?.configureProvider()
            mDataProvider?.subscribeProvider(::processTelemetryData)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
    }
    //endregion
}
