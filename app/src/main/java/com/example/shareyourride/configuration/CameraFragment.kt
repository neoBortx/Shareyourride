/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.configuration

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.bvillarroya_creations.shareyourride.R
import com.bvillarroya_creations.shareyourride.wifi.ConnectionType
import com.example.shareyourride.camera.SupportedCameras

/**
 * Class that contains the logic related to the configuration of the camera connection
 * A list of supported cameras are available, they come with a preconfigured values.
 * Also, the user can add his custom configuration.
 *
 * @remarks: This configuration affects to camera connection, so it is used with the WIFI library
 *
 * Suppress unused because the code analyzer doesn't detect the use of this class in the SettingsActivity
 */
@Suppress("unused")
class CameraFragment : PreferenceFragmentCompat() {

    //region overrides
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            configureListPreferences()
            configureAddressValidation()
        }
        catch (ex: Exception)
        {
            Log.e("CameraFragment", "SYR -> Failure onCreate: ${ex.message}")
            ex.printStackTrace()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.camera_preferences, rootKey)

        try {
            val listPreference: ListPreference? = findPreference(getString(R.string.camera_id)) as ListPreference?
            if (listPreference?.value == SupportedCameras.customId) {
                enableCustom()
            }
            else {
                disableCustom()
            }
        }
        catch (ex: Exception)
        {
            Log.e("CameraFragment", "SYR -> Failure onCreatePreferences: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion


    //region Configuration management
    /**
     * Configure the listeners that are going to manage changes in the preference window
     */
    private fun configureListPreferences()
    {
        try
        {
            val supportedCameras = SupportedCameras(requireContext())

            val listPreference: ListPreference? = findPreference(getString(R.string.camera_id)) as ListPreference?
            val cameraSecurity: ListPreference? = findPreference(getString(R.string.camera_connection_type)) as ListPreference?

            cameraSecurity?.entries = ConnectionType.values().map { it.toString() }.toTypedArray()
            cameraSecurity?.entryValues = ConnectionType.values().map { it.toString() }.toTypedArray()
            cameraSecurity?.setDefaultValue(ConnectionType.Open)

            listPreference?.entries = supportedCameras.supportedCamerasList.map { it.name }.toTypedArray()
            listPreference?.entryValues = supportedCameras.supportedCamerasList.map { it.cameraId }.toTypedArray()
            listPreference?.setDefaultValue(supportedCameras.supportedCamerasList.first())

            listPreference?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                manageCameraChanged(newValue as String)
                true
            }
        }
        catch (ex: Exception)
        {
            Log.e("CameraFragment", "SYR -> Failure at configuring selection list: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Configure the listener and the validator that handle changes in the camera ip field
     *
     * The validator checks that the ip address inserted by the user matches with the format of a
     * IPv4 address x.x.x.x while x can be between 0 and 255.
     */
    private fun configureAddressValidation()
    {
        try {
            val cameraIp: EditTextPreference? = findPreference("camera_ip") as EditTextPreference?

            cameraIp?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                val pattern = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$"

                val match = (newValue as String).matches(pattern.toRegex())

                if (!match)
                {
                    Toast.makeText(activity, "Wrong IP format", Toast.LENGTH_SHORT).show()
                }

                match
            }
        }
        catch (ex: Exception)
        {
            Log.e("CameraFragment", "SYR -> Failure in the Ip address validation: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Process the change of the selector of the kind of camera
     *
     * @param: newValue: The selected value as string
     */
    private fun manageCameraChanged(newValue: String)
    {
        try
        {
            if (newValue == SupportedCameras.customId)
            {
                enableCustom()
            }
            else
            {
                configurePredefinedParams(newValue)
            }
        }
        catch (ex: Exception)
        {
            Log.e("CameraFragment", "SYR -> Failure at managing changes in the camera selection: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Enable Custom settings controls
     */
    private fun enableCustom()
    {
        try
        {
            val cameraName: EditTextPreference? = findPreference(getString(R.string.camera_name)) as EditTextPreference?
            val cameraSsid: EditTextPreference? = findPreference(getString(R.string.camera_ssidName)) as EditTextPreference?
            val cameraSecurity: ListPreference? = findPreference(getString(R.string.camera_connection_type)) as ListPreference?
            val cameraPassword: EditTextPreference? = findPreference(getString(R.string.camera_password)) as EditTextPreference?
            val cameraIp: EditTextPreference? = findPreference(getString(R.string.camera_ip)) as EditTextPreference?
            val cameraPort: EditTextPreference? = findPreference(getString(R.string.camera_port)) as EditTextPreference?
            val cameraPath: EditTextPreference? = findPreference(getString(R.string.camera_path)) as EditTextPreference?
            val cameraProtocol: EditTextPreference? = findPreference(getString(R.string.camera_protocol)) as EditTextPreference?

            cameraName?.isEnabled = true
            cameraSsid?.isEnabled = true
            cameraSecurity?.isEnabled = true
            cameraPassword?.isEnabled = true
            cameraIp?.isEnabled = true
            cameraPort?.isEnabled = true
            cameraPath?.isEnabled = true
            //only rtsp supported a this moment
            cameraProtocol?.isEnabled = false
            cameraProtocol?.text = "rtsp"
        }
        catch (ex: Exception)
        {
            Log.e("CameraFragment", "SYR -> Failure at enabling custom controls: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Disable Custom settings controls
     */
    private fun disableCustom()
    {
        try
        {
            val cameraName: EditTextPreference? = findPreference(getString(R.string.camera_name)) as EditTextPreference?
            val cameraSsid: EditTextPreference? = findPreference(getString(R.string.camera_ssidName)) as EditTextPreference?
            val cameraSecurity: ListPreference? = findPreference(getString(R.string.camera_connection_type)) as ListPreference?
            val cameraPassword: EditTextPreference? = findPreference(getString(R.string.camera_password)) as EditTextPreference?
            val cameraIp: EditTextPreference? = findPreference(getString(R.string.camera_ip)) as EditTextPreference?
            val cameraPort: EditTextPreference? = findPreference(getString(R.string.camera_port)) as EditTextPreference?
            val cameraPath: EditTextPreference? = findPreference(getString(R.string.camera_path)) as EditTextPreference?
            val cameraProtocol: EditTextPreference? = findPreference(getString(R.string.camera_protocol)) as EditTextPreference?

            cameraName?.isEnabled = false
            cameraSsid?.isEnabled = false
            cameraSecurity?.isEnabled = false
            cameraPassword?.isEnabled = true
            cameraIp?.isEnabled = false
            cameraPort?.isEnabled = false
            cameraPath?.isEnabled = false
            cameraProtocol?.isEnabled = false
        }
        catch (ex: Exception)
        {
            Log.e("CameraFragment", "SYR -> Failure at disabling custom controls: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Configure the wifi connection using the preconfigured configuration of the selected camera
     */
    private fun configurePredefinedParams(cameraId: String)
    {
        try
        {
            val supportedCameras = SupportedCameras(requireContext())

            disableCustom()

            val cameraName: EditTextPreference? = findPreference(getString(R.string.camera_name)) as EditTextPreference?
            val cameraSsid: EditTextPreference? = findPreference(getString(R.string.camera_ssidName)) as EditTextPreference?
            val cameraSecurity: ListPreference? = findPreference(getString(R.string.camera_connection_type)) as ListPreference?
            val cameraPassword: EditTextPreference? = findPreference(getString(R.string.camera_password)) as EditTextPreference?
            val cameraIp: EditTextPreference? = findPreference(getString(R.string.camera_ip)) as EditTextPreference?
            val cameraPort: EditTextPreference? = findPreference(getString(R.string.camera_port)) as EditTextPreference?
            val cameraPath: EditTextPreference? = findPreference(getString(R.string.camera_path)) as EditTextPreference?
            val cameraProtocol: EditTextPreference? = findPreference(getString(R.string.camera_protocol)) as EditTextPreference?

            val cameraInfo = supportedCameras.supportedCamerasList.first { it.cameraId == cameraId}

            cameraName?.text = cameraInfo.name
            cameraSsid?.text = cameraInfo.connectionData.ssidName
            cameraSecurity?.value = cameraInfo.connectionData.connectionType.toString()
            cameraPassword?.text = cameraInfo.connectionData.password
            cameraIp?.text = cameraInfo.videoServerIp
            cameraPort?.text = cameraInfo.videoServerPort
            cameraPath?.text = cameraInfo.videoName
            cameraProtocol?.text = cameraInfo.networkProtocol
        }
        catch (ex: Exception)
        {
            Log.e("CameraFragment", "SYR -> Failure at configuring predefined camera controls: ${ex.message}")
            ex.printStackTrace()
        }
    }

    //endregion
}