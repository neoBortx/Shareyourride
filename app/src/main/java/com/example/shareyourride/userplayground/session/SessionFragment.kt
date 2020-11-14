package com.example.shareyourride.userplayground.session

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.viewmodels.SettingsViewModel
import com.example.shareyourride.viewmodels.userplayground.InclinationViewModel
import com.example.shareyourride.viewmodels.userplayground.LocationViewModel
import com.example.shareyourride.viewmodels.userplayground.SessionViewModel
import com.example.shareyourride.wifi.WifiViewModel
import kotlinx.android.synthetic.main.fragment_session.*


class SessionFragment : Fragment() {

    //region view models
    /**
     * View model that manage setting changes
     */
    private val settingsViewModel: SettingsViewModel by viewModels({ requireParentFragment() })

    /**
     * View model that holds the current state of the wifi and can be used to command the WIFI system
     */
    private val wifiViewModel: WifiViewModel by viewModels({ requireParentFragment() })

    /**
     * View model that holds the current location and the state of the GPS state
     */
    private val locationViewModel: LocationViewModel by viewModels({ requireParentFragment() })

    /**
     * View model that holds the current location and the state of the GPS state
     */
    private val inclinationViewModel: InclinationViewModel by viewModels({ requireParentFragment() })

    /**
     * View model that manages session changes, holds the current state of the session and all commands
     * to manage the user session
     */
    private val sessionViewModel: SessionViewModel by viewModels({ requireParentFragment() })
    //endregion

    //region recycler view
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter:  RecyclerView.Adapter<*>
    private lateinit var viewManager:  RecyclerView.LayoutManager
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_session, container, false)

        (view?.findViewById(R.id.text_activity_kind_value_session) as TextView).text = settingsViewModel.activityName
        (view.findViewById(R.id.stop_activity_button) as Button).setOnClickListener(stopActivityButton)

        if (context != null)
        {
            configureLocationChanges()
            configureWifiChanges()
        }
        else
        {
            Log.e("SessionFragment","Unable to initialize observers, context is null")
        }

        viewManager = GridLayoutManager(context,2)
        viewAdapter = TelemetryRecyclerViewAdapter(settingsViewModel.telemetryList.toTypedArray(), locationViewModel, inclinationViewModel)

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_session)?.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            //MiddleDivideritemDecoration.ALL means both vertical and horizontal dividers
            //You can also use MiddleDividerItemDecoration.VERTICAL / MiddleDividerItemDecoration.HORIZONTAL if you just want horizontal / vertical dividers
            addItemDecoration(MiddleDividerItemDecoration(requireContext(), MiddleDividerItemDecoration.ALL))

            // use a grid layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }!!

        return view
    }



    //region configure observers
    private fun configureWifiChanges()
    {
        try {

            wifiViewModel.wifiConnected.observe(viewLifecycleOwner, Observer {state ->
                Log.d("SessionFragment", "SYR -> processing changes in wifi wifiConnectionObserver, new state = $state")
                val colorId = if (state) R.color.colorProviderOk else R.color.colorProviderError
                ImageViewCompat.setImageTintList(wifi_state_img_session, context?.getColor(colorId)?.let { ColorStateList.valueOf(it) })
            })
        }
        catch(ex: Exception)
        {
            Log.e("SessionFragment", "SYR -> Unable to create Wifi observers because: ${ex.message}")
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
                val colorId = if (state) R.color.colorProviderOk else R.color.colorProviderError
                ImageViewCompat.setImageTintList(gps_state_img_session, context?.getColor(colorId)?.let { ColorStateList.valueOf(it) })
            })
        }
        catch(ex: Exception)
        {
            Log.e("SessionFragment", "SYR -> Unable to create location observers because: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    /**
     * Manages clicks in the start activity button
     */
    private val stopActivityButton = View.OnClickListener {

        Log.i("SessionFragment", "SYR -> Processing stop activity click event")
        sessionViewModel.showFinishActivityDialog(requireContext())
    }
}