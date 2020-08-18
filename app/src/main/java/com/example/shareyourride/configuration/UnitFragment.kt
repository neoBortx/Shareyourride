package com.example.shareyourride.configuration

import android.os.Bundle
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
        super.onCreate(savedInstanceState)
        configurePreferenceChangeListeners()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.units_preferences, rootKey)
    }
    //endregion

    //region Configuration management
    /**
     * Configure the listeners that are going to manage changes in the preference window
     */
    private fun configurePreferenceChangeListeners()
    {
        val listPreference: ListPreference? = findPreference("unit_system") as ListPreference?

        listPreference?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            manageUnitSystemChanged(newValue as String)
            true
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
        val speed: SwitchPreference? = findPreference("speed_unit") as SwitchPreference?
        val temperature: SwitchPreference? = findPreference("temperature_unit") as SwitchPreference?
        val wind: SwitchPreference? = findPreference("wind_speed_unit") as SwitchPreference?

        manageSwitchPreferenceChanged(true, speed)
        manageSwitchPreferenceChanged(true, temperature)
        manageSwitchPreferenceChanged(true, wind)

        speed?.isEnabled = false
        temperature?.isEnabled = false
        wind?.isEnabled = false

    }

    /**
     * Configure the metric system
     * Disable the custom controls
     */
    private fun configureMetric()
    {
        val speed: SwitchPreference? = findPreference("speed_unit") as SwitchPreference?
        val temperature: SwitchPreference? = findPreference("temperature_unit") as SwitchPreference?
        val wind: SwitchPreference? = findPreference("wind_speed_unit") as SwitchPreference?

        manageSwitchPreferenceChanged(false, speed)
        manageSwitchPreferenceChanged(false, temperature)
        manageSwitchPreferenceChanged(false, wind)

        speed?.isEnabled = false
        temperature?.isEnabled = false
        wind?.isEnabled = false
    }

    /**
     * Configure the custom system
     * Enable the custom controls
     */
    private fun configureCustom()
    {
        val speed: SwitchPreference? = findPreference("speed_unit") as SwitchPreference?
        val temperature: SwitchPreference? = findPreference("temperature_unit") as SwitchPreference?
        val wind: SwitchPreference? = findPreference("wind_speed_unit") as SwitchPreference?

        speed?.isEnabled = true
        temperature?.isEnabled = true
        wind?.isEnabled = true
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