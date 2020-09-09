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
            configurePreferenceChangeListeners()
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

            val listPreference: ListPreference? = findPreference(getString(R.string.unit_system)) as ListPreference?
            if (listPreference?.value == "custom") {
                configureCustom()
            }
        }
        catch (ex: Exception)
        {
            Log.e("UnitsFragment", "SYR -> Failure onCreatePreferences: ${ex.message}")
            ex.printStackTrace()
        }
    }
    //endregion

    //region Configuration management
    /**
     * Configure the listeners that are going to manage changes in the preference window
     */
    private fun configurePreferenceChangeListeners()
    {
        try {
            val listPreference: ListPreference? = findPreference(getString(R.string.unit_system)) as ListPreference?

            listPreference?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                manageUnitSystemChanged(newValue as String)
                true
            }
        }
        catch (ex: Exception)
        {
            Log.e("UnitsFragment", "SYR -> Failure at configuring list changes manager: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Process the change of the selector of unit systems
     *
     * @param: newValue: The selected value as string
     */
    private fun manageUnitSystemChanged(newValue: String)
    {
        when (newValue) {
            "metric" -> {
                configureMetric()
            }
            "imperial" -> {
                configureImperial()
            }
            "custom" -> {
                configureCustom()
            }
        }
    }

    /**
     * Configure the imperial system
     * Disable the custom controls
     */
    private fun configureImperial()
    {
        try
        {
            val speed: SwitchPreference? = findPreference(getString(R.string.speed_unit)) as SwitchPreference?
            val temperature: SwitchPreference? = findPreference(getString(R.string.temperature_unit)) as SwitchPreference?
            val wind: SwitchPreference? = findPreference(getString(R.string.wind_speed_unit)) as SwitchPreference?

            manageSwitchPreferenceChanged(true, speed)
            manageSwitchPreferenceChanged(true, temperature)
            manageSwitchPreferenceChanged(true, wind)

            speed?.isEnabled = false
            temperature?.isEnabled = false
            wind?.isEnabled = false
        }
        catch (ex: Exception)
        {
            Log.e("UnitsFragment", "SYR -> Failure at managing imperial units: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Configure the metric system
     * Disable the custom controls
     */
    private fun configureMetric()
    {
        try {
            val speed: SwitchPreference? = findPreference(getString(R.string.speed_unit)) as SwitchPreference?
            val temperature: SwitchPreference? = findPreference(getString(R.string.temperature_unit)) as SwitchPreference?
            val wind: SwitchPreference? = findPreference(getString(R.string.wind_speed_unit)) as SwitchPreference?

            manageSwitchPreferenceChanged(false, speed)
            manageSwitchPreferenceChanged(false, temperature)
            manageSwitchPreferenceChanged(false, wind)

            speed?.isEnabled = false
            temperature?.isEnabled = false
            wind?.isEnabled = false
        }
        catch (ex: Exception)
        {
            Log.e("UnitsFragment", "SYR -> Failure at managing metric units: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Configure the custom system
     * Enable the custom controls
     */
    private fun configureCustom()
    {
        try
        {
            val speed: SwitchPreference? = findPreference(getString(R.string.speed_unit)) as SwitchPreference?
            val temperature: SwitchPreference? = findPreference(getString(R.string.temperature_unit)) as SwitchPreference?
            val wind: SwitchPreference? = findPreference(getString(R.string.wind_speed_unit)) as SwitchPreference?

            speed?.isEnabled = true
            temperature?.isEnabled = true
            wind?.isEnabled = true
        }
        catch (ex: Exception)
        {
            Log.e("UnitsFragment", "SYR -> Failure at managing custom units: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Updates the summary of the changed preference
     *
     * @param newValue: The value to set
     * @param switchPreference: The switch control to update
     */
    private fun manageSwitchPreferenceChanged(newValue: Boolean, switchPreference: SwitchPreference?)
    {
        switchPreference?.isChecked = newValue
    }
    //endregion
}