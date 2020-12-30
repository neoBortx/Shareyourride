/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.userplayground.gyroscopes

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.services.session.SessionState
import com.example.shareyourride.viewmodels.settings.SettingsViewModel
import com.example.shareyourride.viewmodels.userplayground.LocationViewModel
import com.example.shareyourride.viewmodels.userplayground.SessionViewModel
import com.example.shareyourride.viewmodels.cameraWifi.WifiViewModel
import kotlinx.android.synthetic.main.fragment_gyroscope_calibration.*

/**
 * Fragment that rules the gyroscopes calibration window
 */
class GyroscopeCalibrationFragment : Fragment()
{
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
    //endregion

    //region configure observers
    private fun configureWifiChanges()
    {
        try {

            wifiViewModel.wifiConnected.observe(viewLifecycleOwner, Observer {state ->
                Log.d("GyroscopeCalibrationFragment", "SYR -> processing changes in wifi wifiConnectionObserver, new state = $state")
                setImageColor(state, wifi_state_img_gyroscopes)
            })
        }
        catch(ex: Exception)
        {
            Log.e("GyroscopeCalibrationFragment", "SYR -> Unable to create Wifi observers because: ${ex.message}")
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
            locationViewModel.gpsState.observe(viewLifecycleOwner, Observer { state ->
                setImageColor(state, gps_state_img_gyroscopes)
            })
        }
        catch(ex: Exception)
        {
            Log.e("GyroscopeCalibrationFragment", "SYR -> Unable to create location observers because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Create an observer to manage changes in the GPS state
     * Just change the color of the icon and enable or disable the start activity button
     */
    private fun configureSessionChanges()
    {
        try
        {
            sessionViewModel.sessionData.observe(viewLifecycleOwner, Observer { state ->

                if (state.state == SessionState.SensorsCalibrated)
                {
                    updateView(requireView())
                }
            })
        }
        catch(ex: Exception)
        {
            Log.e("GyroscopeCalibrationFragment", "SYR -> Unable to create session state observers because: ${ex.message}")
            ex.printStackTrace()
        }
    }


    private fun setImageColor(state: Boolean?, imageView: ImageView)
    {
        val colorId = if (state != null && state) R.color.colorProviderOk else R.color.colorProviderError
        ImageViewCompat.setImageTintList(imageView, context?.getColor(colorId)?.let { ColorStateList.valueOf(it) })
    }

    /**
     * Manages clicks in the start activity button
     */
    private val continueActivityButtonListener = OnClickListener {
        Log.i("GyroscopeCalibrationFragment", "SYR -> Processing continue button clicked")
        sessionViewModel.continueSession()
    }

    /**
     * Manages clicks in the start activity button
     */
    private val retryCalibrationButtonListener = OnClickListener {
        Log.i("GyroscopeCalibrationFragment", "SYR -> Processing retry calibration button clicked")
        sessionViewModel.retryCalibration()
    }
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
         val view = inflater.inflate(R.layout.fragment_gyroscope_calibration, container, false)

        (view?.findViewById(R.id.text_activity_kind_value_gyroscopes) as TextView).text = settingsViewModel.activityName

        (view.findViewById(R.id.continue_activity_button) as Button).setOnClickListener(continueActivityButtonListener)
        (view.findViewById(R.id.retry_calibration_button) as Button).setOnClickListener(retryCalibrationButtonListener)

        configureWifiChanges()
        configureLocationChanges()
        configureSessionChanges()

        setImageColor(locationViewModel.gpsState.value,view.findViewById(R.id. gps_state_img_gyroscopes) as ImageView)
        setImageColor(wifiViewModel.wifiConnected.value, view.findViewById(R.id. wifi_state_img_gyroscopes) as ImageView)

        updateView(view)

        return view
    }

    private fun updateView(view: View)
    {
        try
        {

            (view.findViewById(R.id.continue_activity_button) as Button).isEnabled = isCalibrationProcessFinished()

            (view.findViewById(R.id.retry_calibration_button) as Button).isEnabled = getCalibrationEndWithError()
            (view.findViewById(R.id.retry_calibration_button) as Button).visibility = if (getCalibrationEndWithError()) VISIBLE else GONE

            (view.findViewById(R.id.progressBar_calibrating) as ProgressBar).visibility = if (!isCalibrationProcessFinished()) VISIBLE else INVISIBLE

            when {
                getCalibrationEndSuccessfully() -> {
                    (view.findViewById(R.id.calibration_state_text_view) as TextView).text = getString(R.string.calibration_success_notification)
                }
                getCalibrationEndWithError() -> {
                    (view.findViewById(R.id.calibration_state_text_view) as TextView).text = getString(R.string.calibration_error_notification)
                }
                else -> {
                    (view.findViewById(R.id.calibration_state_text_view) as TextView).text = getString(R.string.calibrating)
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("GyroscopeCalibrationFragment", "SYR -> Unable to get the calibration state ${ex.message}")
            ex.printStackTrace()
        }
    }

    private fun isCalibrationProcessFinished(): Boolean
    {
        return sessionViewModel.sessionData.value?.state == SessionState.SensorsCalibrated
    }


    /**
     * Calculates if the calibrations has finished and the result is successful
     *
     * @return True if the calibration  has finished with error
     *         false otherwise
     */
    private fun getCalibrationEndWithError(): Boolean
    {
        try
        {
            return (isCalibrationProcessFinished())
                    && (sessionViewModel.sessionData.value != null)
                    && (!sessionViewModel.sessionData.value!!.sensorCalibrated)
        }
        catch (ex: Exception)
        {
            Log.e("GyroscopeCalibrationFragment", "SYR -> Unable to get the calibration state ${ex.message}")
            ex.printStackTrace()
        }
        return false
    }

    /**
     * Calculates if the calibrations has finished and the result is successful
     *
     * @return True if the calibration  has finished successfully
     *         false otherwise
     */
    private fun getCalibrationEndSuccessfully(): Boolean
    {
        try
        {
            return (isCalibrationProcessFinished())
                    && (sessionViewModel.sessionData.value != null)
                    && (sessionViewModel.sessionData.value!!.sensorCalibrated)
        }
        catch (ex: Exception)
        {
            Log.e("GyroscopeCalibrationFragment", "SYR -> Unable to get the calibration state ${ex.message}")
            ex.printStackTrace()
        }
        return false
    }
}