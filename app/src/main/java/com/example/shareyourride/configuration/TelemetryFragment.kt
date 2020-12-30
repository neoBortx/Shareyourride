/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.configuration

import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.bvillarroya_creations.shareyourride.R

/**
 * Class that contains the logic related to the configuration of the metrics to capture in the session
 * User can choose to configure several sports with preconfigured values or a custom configuration
 *
 * @remarks: This configuration affects to telemetry data providers that are going to be used in the session
 * also the data stores in the data base and the data shown in the video
 *
 * Suppress unused because the code analyzer doesn't detect the use of this class in the SettingsActivity
 */
@Suppress("unused")
class TelemetryFragment : PreferenceFragmentCompat() {

    //region overrides
    override fun onCreate(savedInstanceState: Bundle?) {
        try
        {
            super.onCreate(savedInstanceState)

            configurePreferenceChangeListeners()
        }
        catch (ex: Exception)
        {
            Log.e("TelemetryFragment", "SYR -> Failure onCreate: ${ex.message}")
            ex.printStackTrace()
        }
    }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        try
        {
            setPreferencesFromResource(R.xml.telemetry_preferences, rootKey)

            val getter = context?.let { SettingPreferencesGetter(it) }

            if (getter!= null && getter.getStringOption(SettingPreferencesIds.ActivityKind) == "custom")
            {
                configureCustom()
            }
        }
        catch (ex: Exception)
        {
            Log.e("TelemetryFragment", "SYR -> Failure onCreatePreferences: ${ex.message}")
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
            val listPreference: ListPreference? = findPreference(getString(R.string.activity_kind)) as ListPreference?

            listPreference?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                manageActivityChanged(newValue as String)
                true
            }
        }
        catch (ex: Exception)
        {
            Log.e("TelemetryFragment", "SYR -> Failure at configuring list changes handlers: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Process the change of the selector of activity kind
     *
     * @param: newValue: The selected value as string
     */
    private fun manageActivityChanged(newValue: String)
    {
        try {

            when (newValue) {
                "custom" -> {
                    configureCustom()
                }
                "motorbike" -> {
                    configureValues(speed = true, force = true, lean = true, temperature = true, wind = false, pressure = false, heart = false, inclination = false, altitude = false, distance = true)
                }
                "bike" -> {
                    configureValues(speed = true, force = false, lean = false, temperature = true, wind = true, pressure = false, heart = true, inclination = true, altitude = true, distance = true)
                }
                "car" -> {
                    configureValues(speed = true, force = true, lean = false, temperature = true, wind = false, pressure = false, heart = false, inclination = false, altitude = false, distance = true)
                }
                "snowboard" -> {
                    configureValues(speed = true, force = false, lean = false, temperature = true, wind = false, pressure = false, heart = false, inclination = true, altitude = true, distance = true)
                }
                "longboard" -> {
                    configureValues(speed = true, force = false, lean = false, temperature = false, wind = false, pressure = false, heart = false, inclination = true, altitude = true, distance = true)
                }
                "skydiving" -> {
                    configureValues(speed = true, force = false, lean = false, temperature = true, wind = true, pressure = true, heart = false, inclination = false, altitude = true, distance = false)
                }
                "sailing" -> {
                    configureValues(speed = false, force = false, lean = false, temperature = true, wind = true, pressure = true,
                                    heart = false, inclination = false, altitude = false, distance =  true)
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("TelemetryFragment", "SYR -> Failure at managing activity kind changes: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Configure the imperial system
     * Disable the custom controls
     */
    private fun configureCustom()
    {
        try {
            val speedPref: SwitchPreference? = findPreference(getString(R.string.speed_metric)) as SwitchPreference?
            val forcePref: SwitchPreference? = findPreference(getString(R.string.gforce_metric)) as SwitchPreference?
            val leanPref: SwitchPreference? = findPreference(getString(R.string.lean_angle_metric)) as SwitchPreference?
            val temperaturePref: SwitchPreference? = findPreference(getString(R.string.temperature_metric)) as SwitchPreference?
            val windPref: SwitchPreference? = findPreference(getString(R.string.wind_metric)) as SwitchPreference?
            val pressurePref: SwitchPreference? = findPreference(getString(R.string.pressure_metric)) as SwitchPreference?
            val heartPref: SwitchPreference? = findPreference(getString(R.string.heart_rate_metric)) as SwitchPreference?
            val inclinationPref: SwitchPreference? = findPreference(getString(R.string.inclination_metric)) as SwitchPreference?
            val altitudePref: SwitchPreference? = findPreference(getString(R.string.altitude_metric)) as SwitchPreference?
            val distancePref: SwitchPreference? = findPreference(getString(R.string.distance_metric)) as SwitchPreference?

            speedPref?.isEnabled = true
            forcePref?.isEnabled = true
            leanPref?.isEnabled = true
            temperaturePref?.isEnabled = true
            windPref?.isEnabled = true
            pressurePref?.isEnabled = true
            heartPref?.isEnabled = true
            inclinationPref?.isEnabled = true
            altitudePref?.isEnabled = true
            distancePref?.isEnabled = true
        }
        catch (ex: Exception)
        {
            Log.e("TelemetryFragment", "SYR -> Failure at enabling custom controls: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Configure the metric switch preferences with the given values
     *
     * @param speed
     * @param force
     * @param lean
     * @param temperature
     * @param wind
     * @param pressure
     * @param heart
     * @param inclination
     * @param altitude
     * @param distance
     *
     */
    private fun configureValues(speed: Boolean, force: Boolean, lean: Boolean, temperature: Boolean, wind: Boolean,
                                pressure: Boolean, heart: Boolean, inclination: Boolean, altitude: Boolean, distance: Boolean)
    {
        try {
            val speedPref: SwitchPreference? = findPreference(getString(R.string.speed_metric)) as SwitchPreference?
            val forcePref: SwitchPreference? = findPreference(getString(R.string.gforce_metric)) as SwitchPreference?
            val leanPref: SwitchPreference? = findPreference(getString(R.string.lean_angle_metric)) as SwitchPreference?
            val temperaturePref: SwitchPreference? = findPreference(getString(R.string.temperature_metric)) as SwitchPreference?
            val windPref: SwitchPreference? = findPreference(getString(R.string.wind_metric)) as SwitchPreference?
            val pressurePref: SwitchPreference? = findPreference(getString(R.string.pressure_metric)) as SwitchPreference?
            val heartPref: SwitchPreference? = findPreference(getString(R.string.heart_rate_metric)) as SwitchPreference?
            val inclinationPref: SwitchPreference? = findPreference(getString(R.string.inclination_metric)) as SwitchPreference?
            val altitudePref: SwitchPreference? = findPreference(getString(R.string.altitude_metric)) as SwitchPreference?
            val distancePref: SwitchPreference? = findPreference(getString(R.string.distance_metric)) as SwitchPreference?

            manageSwitchPreferenceChanged(speed, speedPref)
            manageSwitchPreferenceChanged(force, forcePref)
            manageSwitchPreferenceChanged(lean, leanPref)
            manageSwitchPreferenceChanged(temperature, temperaturePref)
            manageSwitchPreferenceChanged(wind, windPref)
            manageSwitchPreferenceChanged(pressure, pressurePref)
            manageSwitchPreferenceChanged(heart, heartPref)
            manageSwitchPreferenceChanged(inclination, inclinationPref)
            manageSwitchPreferenceChanged(altitude, altitudePref)
            manageSwitchPreferenceChanged(distance, distancePref)

            speedPref?.isEnabled = false
            forcePref?.isEnabled = false
            leanPref?.isEnabled = false
            temperaturePref?.isEnabled = false
            windPref?.isEnabled = false
            pressurePref?.isEnabled = false
            heartPref?.isEnabled = false
            inclinationPref?.isEnabled = false
            altitudePref?.isEnabled = false
            distancePref?.isEnabled = false
        }
        catch (ex: Exception)
        {
            Log.e("TelemetryFragment", "SYR -> Failure at configuring custom controls: ${ex.message}")
            ex.printStackTrace()
        }
    }


    /**
     * Updates the value of the changed preference
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