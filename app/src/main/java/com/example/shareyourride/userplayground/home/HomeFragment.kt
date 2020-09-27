package com.example.shareyourride.userplayground.home

import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bvillarroya_creations.shareyourride.R
import com.bvillarroya_creations.shareyourride.R.color
import com.bvillarroya_creations.shareyourride.R.layout
import com.bvillarroya_creations.shareyourride.databinding.FragmentHomeBinding
import com.bvillarroya_creations.shareyourride.viewmodel.location.LocationViewModel
import com.example.shareyourride.viewmodels.SettingsViewModel
import com.example.shareyourride.viewmodels.userplayground.HomeViewModel
import com.example.shareyourride.wifi.WifiViewModel
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    /**
     * Local view model
     */
    private lateinit var homeViewModel: HomeViewModel

    /**
     * View model that handles the GPS data
     */
    private val locationViewModel: LocationViewModel by activityViewModels()

    /**
     *
     */
    private val wifiViewModel: WifiViewModel  by activityViewModels()

    /**
     *
     */
    private val settingsViewModel: SettingsViewModel by activityViewModels()

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
            ImageViewCompat.setImageTintList(wifi_state_img, context?.getColor(color.colorProviderOk)?.let { ColorStateList.valueOf(it) })
        }
        else
        {
            ImageViewCompat.setImageTintList(wifi_state_img, context?.getColor(color.colorProviderError)?.let { ColorStateList.valueOf(it) })
        }
    }

    /**
     * Observer to manage changes in the wifi state
     */
    private val wifiStateObserver = Observer<Boolean> { newState ->

        if (newState)
        {
            ImageViewCompat.setImageTintList(wifi_state_img, context?.getColor(color.colorProviderOk)?.let { ColorStateList.valueOf(it) })
        }
        else
        {
            Log.e("SYR", "HomeFragment -> wifi device has been disabled")
        }
    }

    private val cameravaluesChangedObserver = Observer<String> {

        wifiViewModel.changeWifiNetwork()

    }
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
                inflater, layout.fragment_home, container, false)
        
        val view: View = binding.root
        //here data must be an instance of the class MarsDataProvider
        //here data must be an instance of the class MarsDataProvider
        binding.location = locationViewModel

        locationViewModel.providerReady.observe(viewLifecycleOwner, locationStateObserver)

        if (context != null)
        {
            wifiViewModel.wifiConnected.observe(viewLifecycleOwner, wifiConnectionObserver)
            wifiViewModel.wifiEnabled.observe(viewLifecycleOwner, wifiStateObserver)
            wifiViewModel.openSettingsActivity.observe(viewLifecycleOwner, wifiStateObserver)

            settingsViewModel.cameraIp.observe(viewLifecycleOwner,cameravaluesChangedObserver)
            settingsViewModel.cameraPassword.observe(viewLifecycleOwner,cameravaluesChangedObserver)
            settingsViewModel.cameraPath.observe(viewLifecycleOwner,cameravaluesChangedObserver)
            settingsViewModel.cameraSsidName.observe(viewLifecycleOwner,cameravaluesChangedObserver)
            settingsViewModel.cameraConnectionType .observe(viewLifecycleOwner,cameravaluesChangedObserver)
        }

        wifiViewModel.connectToWifi(requireActivity())

        return view
    }
}