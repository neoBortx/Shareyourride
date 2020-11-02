package com.example.shareyourride.userplayground.home

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bvillarroya_creations.shareyourride.R
import com.bvillarroya_creations.shareyourride.R.color
import com.bvillarroya_creations.shareyourride.R.layout
import com.bvillarroya_creations.shareyourride.databinding.FragmentHomeBinding
import com.example.shareyourride.viewmodels.SettingsViewModel
import com.example.shareyourride.viewmodels.userplayground.LocationViewModel
import com.example.shareyourride.viewmodels.userplayground.SessionViewModel
import com.example.shareyourride.wifi.WifiViewModel
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment()  {

    //region view models
    /**
     * View model that manage setting changes
     */
    private val settingsViewModel: SettingsViewModel by viewModels({ requireParentFragment() })

    /**
     * View model that holds the current state of the wifi and can be used to command the WIFI system
     */
    private val wifiViewModel: WifiViewModel  by viewModels({ requireParentFragment() })

    /**
     * View model that holds the current location and the state of the GPS state
     */
    private val locationViewModel: LocationViewModel by viewModels({ requireParentFragment() })

    /**
     * View model that manages session changes, holds the current state of the session and all commands
     * to manage the user session
     */
    private val sessionViewModel: SessionViewModel by viewModels({ requireParentFragment() })


    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(layout.fragment_home, container, false)

        (view.findViewById(R.id.start_activity_button) as Button).setOnClickListener(startActivityButton)

        if (context != null)
        {
            configureLocationChanges()
            configureSettingObservers()
            configureWifiChanges()
        }
        else
        {
            Log.e("HomeFragment","Unable to initialize observers, context is null")
        }

        wifiViewModel.connectToWifi(requireActivity())
        locationViewModel.getGpsState()

        return view
    }

    //region configure observers
    private fun configureWifiChanges()
    {
        try {

            wifiViewModel.wifiConnected.observe(viewLifecycleOwner, Observer {state ->
                Log.d("HomeFragment", "SYR -> processing changes in wifi wifiConnectionObserver, new state = $state")
                val colorId = if (state) color.colorProviderOk else color.colorProviderError
                ImageViewCompat.setImageTintList(wifi_state_img, context?.getColor(colorId)?.let { ColorStateList.valueOf(it) })
            })
        }
        catch(ex: Exception)
        {
            Log.e("HomeFragment", "SYR -> Unable to create Wifi observers because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Create an observer to manage changes in the GPS state
     * Just change the color of the icon and enable or disable the start activity button
     */
    private fun configureLocationChanges()
    {
        try
        {
            locationViewModel.gpsState.observe(viewLifecycleOwner, Observer {state ->
                val colorId = if (state) color.colorProviderOk else color.colorProviderError
                ImageViewCompat.setImageTintList(gps_state_img, context?.getColor(colorId)?.let { ColorStateList.valueOf(it) })
            })
        }
        catch(ex: Exception)
        {
            Log.e("HomeFragment", "SYR -> Unable to create location observers because: ${ex.message}")
            ex.printStackTrace()
        }
    }


    /**
     * Create and configure observers related to the settings system
     */
    private fun configureSettingObservers()
    {
        try
        {
            /**
             * Manage changes in the camera configuration
             */
            settingsViewModel.cameraConfigChangedFlag.observe(viewLifecycleOwner, Observer {
                Log.i("HomeFragment","SYR -> Processing changes in the camera configuration")
                wifiViewModel.changeWifiNetwork()
            })

            /**
             * Manage changes in the activity configuration
             */
            settingsViewModel.activityDataChangedFlag.observe(viewLifecycleOwner, Observer {
                Log.i("HomeFragment","SYR -> Processing changes in the activity data")
                text_activity_kind_value.text = settingsViewModel.activityName
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, settingsViewModel.telemetryList)
                telemetry_list.adapter = adapter
            })
        }
        catch(ex: Exception)
        {
            Log.e("HomeFragment", "SYR -> Unable to create settings observers because: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    /**
     * Manages clicks in the start activity button
     */
    private val startActivityButton = View.OnClickListener {

        Log.i("HomeFragment", "SYR -> Processing start button clicked")
        sessionViewModel.startSession()
    }
}