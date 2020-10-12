package com.example.shareyourride.userplayground.home

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.bvillarroya_creations.shareyourride.R
import com.bvillarroya_creations.shareyourride.R.color
import com.bvillarroya_creations.shareyourride.R.layout
import com.bvillarroya_creations.shareyourride.databinding.FragmentHomeBinding
import com.bvillarroya_creations.shareyourride.viewmodel.location.LocationViewModel
import com.bvillarroya_creations.shareyourride.viewmodel.session.SessionViewModel
import com.example.shareyourride.video.statemcahine.VideoClientState
import com.example.shareyourride.viewmodels.SettingsViewModel
import com.example.shareyourride.viewmodels.userplayground.VideoViewModel
import com.example.shareyourride.wifi.WifiViewModel
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment()  {

    //region view models
    /**
     *
     */
    private val settingsViewModel: SettingsViewModel by viewModels()

    /**
     * View model that handles the GPS data
     */
    private val locationViewModel: LocationViewModel by viewModels()

    /**
     *
     */
    private val wifiViewModel: WifiViewModel  by viewModels()

    /**
     *
     */
    private val sessionViewModel: SessionViewModel by viewModels()

    /*


     */
    private val videoViewModel: VideoViewModel by viewModels()
    //endregion

    //region observers
    /**
     * Observer to manage changes in the location
     */
    private val locationStateObserver = Observer<Boolean> { newState ->

        if (newState)
        {
            ImageViewCompat.setImageTintList(gps_state_img, context?.getColor(color.colorProviderOk)?.let { ColorStateList.valueOf(it) })
        }
        else
        {
            ImageViewCompat.setImageTintList(gps_state_img, context?.getColor(color.colorProviderError)?.let { ColorStateList.valueOf(it) })
        }
    }

    /**
     * Observer to manage changes in the wifi state
     */
    private val wifiConnectionObserver = Observer<Boolean> { newState ->

        if (newState)
        {
            Log.d("SYR", "HomeFragment -> processing changes in wifi wifiConnectionObserver, new state ok")
            ImageViewCompat.setImageTintList(wifi_state_img, context?.getColor(color.colorProviderOk)?.let { ColorStateList.valueOf(it) })

            if (videoViewModel.clientState.value == VideoClientState.Disconnected
                || videoViewModel.clientState.value == VideoClientState.None)
            {
                videoViewModel.connect()
            }
        }
        else
        {
            Log.d("SYR", "HomeFragment -> processing changes in wifi wifiConnectionObserver, new state nok")
            ImageViewCompat.setImageTintList(wifi_state_img, context?.getColor(color.colorProviderError)?.let { ColorStateList.valueOf(it) })
            videoViewModel.disconnect()
        }
    }

    /**
     * Observer to manage changes in the wifi state
     */
    private val wifiStateObserver = Observer<Boolean> { newState ->

        if (newState)
        {
            Log.d("SYR", "HomeFragment -> processing changes in wifi wifiStateObserver state ok")
            /*ImageViewCompat.setImageTintList(wifi_state_img, context?.getColor(color.colorProviderOk)?.let { ColorStateList.valueOf(it) })

            if (videoViewModel.clientState.value == VideoClientState.Disconnected
                || videoViewModel.clientState.value == VideoClientState.None)
            {
                createClient()
            }*/
        }
        else
        {
            Log.e("SYR", "HomeFragment -> processing changes in wifi wifiStateObserver state nok")
            videoViewModel.disconnect()
        }
    }

    /**
     * Observer that manages the state of the wifi connection
     */
    private val cameraValuesChangedObserver = Observer<Boolean> {
        wifiViewModel.changeWifiNetwork()
    }

    /**
     * Observer that manages the state of the list of telemetry values
     */
    private val listConfigurationObserver =  Observer<List<String>> { newState ->

        if (settingsViewModel.configuredTelemetryList.value != null)
        {
            Log.i("SYR","HomeFragment -> Processing changes in the list of telemetry  elements ${settingsViewModel.configuredTelemetryList.value!!})")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                                       settingsViewModel.configuredTelemetryList.value!!)
            telemetry_list.adapter = adapter
        }
        else
        {
            Log.e("SYR", "HomeFragment -> preference data is null")
        }
    }

    private val activityKindChangedObserver =  Observer<String> { newState ->

        if (settingsViewModel.activityName.value != null)
        {
            Log.i("SYR","HomeFragment -> Processing changes in the activity name: ${settingsViewModel.activityName.value!!})")
            text_activity_kind_value.text = settingsViewModel.activityName.value
        }
        else
        {
            Log.e("SYR", "HomeFragment -> preference data is null")
        }
    }

    private val startActivityButton = View.OnClickListener {

        Log.d("SYR", "HomeFragment -> Processing start button clicked")
        val navHostFragment = this.activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if(settingsViewModel.leanAngleMetric.value != null && settingsViewModel.leanAngleMetric.value!!)
        {
            Log.i("SYR", "HomeFragment -> Opening gyroscope calibration activity")
            navController.navigate(R.id.nav_gyroscope_calibration_fragment)
        }
        else
        {
            Log.i("SYR", "HomeFragment -> Opening start activity activity")
            navController.navigate(R.id.nav_activity_started_fragment)
        }
    }
    //endregion
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        inflater.inflate(R.layout.fragment_home, container, false)

        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
                inflater, layout.fragment_home, container, false)
        
        val view: View = binding.root

        binding.location = locationViewModel
        binding.settings = settingsViewModel
        binding.session = sessionViewModel
        binding.video = videoViewModel

        locationViewModel.providerReady.observe(viewLifecycleOwner, locationStateObserver)

        (binding.root.findViewById(R.id.start_activity_button) as Button).setOnClickListener(startActivityButton)

        if (context != null)
        {
            wifiViewModel.wifiConnected.observe(viewLifecycleOwner, wifiConnectionObserver)
            wifiViewModel.wifiEnabled.observe(viewLifecycleOwner, wifiStateObserver)
            wifiViewModel.openSettingsActivity.observe(viewLifecycleOwner, wifiStateObserver)

            settingsViewModel.cameraConfigChangedFlag.observe(viewLifecycleOwner, cameraValuesChangedObserver)
            settingsViewModel.configuredTelemetryList.observe(viewLifecycleOwner,listConfigurationObserver)
            settingsViewModel.activityName.observe(viewLifecycleOwner, activityKindChangedObserver)
        }
        else
        {
            Log.e("HomeFragment","CONTEXT IS NULLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL")
        }

        val textureView: TextureView = binding.root.findViewById(R.id.video_placeholder_home)
        videoViewModel.configureClient(textureView)

        wifiViewModel.connectToWifi(requireActivity())

        return view
    }
}