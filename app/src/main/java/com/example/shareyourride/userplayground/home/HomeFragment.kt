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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bvillarroya_creations.shareyourride.R
import com.bvillarroya_creations.shareyourride.R.color
import com.bvillarroya_creations.shareyourride.R.layout
import com.example.shareyourride.viewmodels.settings.SettingsViewModel
import com.example.shareyourride.viewmodels.userplayground.LocationViewModel
import com.example.shareyourride.viewmodels.userplayground.SessionViewModel
import com.example.shareyourride.viewmodels.userplayground.VideoViewModel
import com.example.shareyourride.viewmodels.cameraWifi.WifiViewModel
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment()  {

    //region view models
    /**
     * View model that manage setting changes
     */
    private val settingsViewModel: SettingsViewModel by viewModels({ requireActivity() })

    /**
     * View model that holds the current state of the wifi and can be used to command the WIFI system
     */
    private val wifiViewModel: WifiViewModel by viewModels({ requireActivity() })

    /**
     * View model that holds the current location and the state of the GPS state
     */
    private val locationViewModel: LocationViewModel by viewModels({ requireActivity() })

    /**
     * View model that manages session changes, holds the current state of the session and all commands
     * to manage the user session
     */
    private val sessionViewModel: SessionViewModel by viewModels({ requireActivity() })

    /**
     * View model that holds the current location and the state of the GPS state
     */
    private val videoViewModel: VideoViewModel by viewModels({ requireActivity() })
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(layout.fragment_home, container, false)

        (view.findViewById(R.id.start_activity_button) as Button).setOnClickListener(startActivityButton)

        if (context != null)
        {
            configureLocationChanges()
            configureSettingObservers()
            configureVideoStateChanges()
        }
        else
        {
            Log.e("HomeFragment","Unable to initialize observers, context is null")
        }

        wifiViewModel.connectToWifi(requireActivity())
        locationViewModel.getGpsState()
        videoViewModel.changeVideoServer()
        videoViewModel.getVideoState()

        return view
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
     * Create an observer to manage changes in the GPS state
     * Just change the color of the icon and enable or disable the start activity button
     */
    private fun configureVideoStateChanges()
    {
        try
        {
            videoViewModel.videoState.observe(viewLifecycleOwner, Observer {state ->
                Log.d("HomeFragment", "SYR -> Handling change in the video state")
                val colorId = if (state) color.colorProviderOk else color.colorProviderError
                ImageViewCompat.setImageTintList(video_state_img, context?.getColor(colorId)?.let { ColorStateList.valueOf(it) })
            })
        }
        catch(ex: Exception)
        {
            Log.e("HomeFragment", "SYR -> Unable to create video state observer because: ${ex.message}")
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
                wifiViewModel.changeWifiNetwork(requireActivity())
                videoViewModel.changeVideoServer()
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