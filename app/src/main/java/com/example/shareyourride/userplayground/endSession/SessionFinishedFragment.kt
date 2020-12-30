/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.userplayground.endSession

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bvillarroya_creations.shareyourride.R
import com.bvillarroya_creations.shareyourride.messagesdefinition.dataBundles.VideoState
import com.example.shareyourride.userplayground.session.MiddleDividerItemDecoration
import com.example.shareyourride.viewmodels.settings.SettingsViewModel
import com.example.shareyourride.viewmodels.userplayground.InclinationViewModel
import com.example.shareyourride.viewmodels.userplayground.LocationViewModel
import com.example.shareyourride.viewmodels.userplayground.SessionViewModel
import com.example.shareyourride.viewmodels.userplayground.VideoViewModel
import com.example.shareyourride.viewmodels.cameraWifi.WifiViewModel
import kotlinx.android.synthetic.main.fragment_session_finished.*

class SessionFinishedFragment : Fragment() {

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
     * View model that holds the current location and the state of the GPS state
     */
    private val inclinationViewModel: InclinationViewModel by viewModels({ requireActivity() })

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

    //region recycler view
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter:  RecyclerView.Adapter<*>
    private lateinit var viewManager:  RecyclerView.LayoutManager
    //endregion

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

       val view = inflater.inflate(R.layout.fragment_session_finished, container, false)

        (view?.findViewById(R.id.text_activity_kind_value_end) as TextView).text = settingsViewModel.activityName


        viewManager = GridLayoutManager(context,2)
        viewAdapter = SummaryRecyclerViewAdapter(settingsViewModel.summaryTelemetryList.toTypedArray(), sessionViewModel)

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_summary)

        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(MiddleDividerItemDecoration(requireContext(), MiddleDividerItemDecoration.ALL))
        // use a grid layout manager
        recyclerView.layoutManager = viewManager
        // specify an viewAdapter (see also next example)
        recyclerView.adapter = viewAdapter

        sessionViewModel.sessionSummaryData.observe(viewLifecycleOwner, Observer {
            recyclerView.adapter!!.notifyDataSetChanged()
        })

        videoViewModel.creationPercentage.observe(viewLifecycleOwner, Observer {
            processVideoCreationProgress(it)
        })

        videoViewModel.creationState.observe(viewLifecycleOwner, Observer {
            processVideoCreationState(it)
        })

        sessionViewModel.requestSummaryData()

        return view
    }

    /**
     * Updates the progress bar
     */
    private fun processVideoCreationProgress(progress: Int)
    {
        progressBar_end.progress = progress
    }

    private fun processVideoCreationState(state: VideoState)
    {
        when(state)
        {
            VideoState.Unknown ->
            {
                Log.e("SessionFinishedFragment", "SYR -> No supported video creation state")
            }
            VideoState.Composing ->
            {
                video_creation_state_text.text = context?.getText(R.string.creating_video_please_wait)
            }
            VideoState.Finished ->
            {
                video_creation_state_text.text = context?.getText(R.string.video_created)
            }
            VideoState.Failed ->
            {
                video_creation_state_text.text = context?.getText(R.string.video_failed)
            }
        }
    }
}