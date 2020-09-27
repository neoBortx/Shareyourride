package com.example.shareyourride.configuration

import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.bvillarroya_creations.shareyourride.R
import com.bvillarroya_creations.shareyourride.wifi.ConnectionType

/**
 * Class that contains the logic related to the configuration of the user profile
 *
 * @remarks: This configuration affects to the main menu info, and the watermark in the video
 *
 * Suppress unused because the code analyzer doesn't detect the use of this class in the SettingsActivity
 */
@Suppress("unused")
class UserFragment : PreferenceFragmentCompat() {

    //region overrides
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            configureListPreferences()
        }
        catch (ex: Exception)
        {
            Log.e("CameraFragment", "SYR -> Failure onCreate: ${ex.message}")
            ex.printStackTrace()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.user_preferences, rootKey)
    }
    //endregion


    //region Configuration management
    /**
     * Configure the listeners that are going to manage changes in the preference window
     */
    private fun configureListPreferences()
    {
        try {
            val listPreference: ListPreference? = findPreference(getString(R.string.camera_id)) as ListPreference?
            val cameraSecurity: ListPreference? = findPreference(getString(R.string.camera_connection_type)) as ListPreference?

            cameraSecurity?.entries = ConnectionType.values().map { it.toString() }.toTypedArray()
            cameraSecurity?.entryValues = ConnectionType.values().map { it.toString() }.toTypedArray()
            cameraSecurity?.setDefaultValue(ConnectionType.Open)

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
     * Process the change of the selector of the kind of camera
     *
     * @param: newValue: The selected value as string
     */
    private fun manageCameraChanged(newValue: String)
    {
        try
        {

        }
        catch (ex: Exception)
        {
            Log.e("CameraFragment", "SYR -> Failure at managing changes in the camera selection: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion
}