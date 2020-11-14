package com.example.shareyourride.configuration

import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.bvillarroya_creations.shareyourride.R

/**
 * Class that contains the logic related to the configuration of the metric units
 * User can choose to configure the imperial or metric systems, also we allow them to
 * configure a mix of both systems
 *
 * @remarks: This configuration doesn't affect to the way that the data is produced and stored
 * this is only used in the view and the video generation
 *
 * Suppress unused because the code analyzer doesn't detect the use of this class in the SettingsActivity
 */
@Suppress("unused")
class UnitsFragment : PreferenceFragmentCompat() {

    //region overrides
    override fun onCreate(savedInstanceState: Bundle?) {
        try
        {
            super.onCreate(savedInstanceState)
        }
        catch (ex: Exception)
        {
            Log.e("UnitsFragment", "SYR -> Failure onCreate: ${ex.message}")
            ex.printStackTrace()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        try
        {
            setPreferencesFromResource(R.xml.units_preferences, rootKey)
        }
        catch (ex: Exception)
        {
            Log.e("UnitsFragment", "SYR -> Failure onCreatePreferences: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    //region Configuration management
    //endregion
}