package com.example.shareyourride.viewmodels

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.shareyourride.configuration.SettingPreferencesGetter
import com.example.shareyourride.configuration.SettingPreferencesIds
import java.lang.Exception

/**
 * View model used to notify changes in the application settings
 */
class SettingsViewModel(application: Application)  : AndroidViewModel(application),SharedPreferences.OnSharedPreferenceChangeListener
{
    private var settingsGetter = SettingPreferencesGetter(application.applicationContext)

    //region mutable live data
    /**
     * Camera Ids
     */
    val cameraId = MutableLiveData<String>()
    val cameraName = MutableLiveData<String>()
    val cameraSsidName = MutableLiveData<String>()
    val cameraConnectionType = MutableLiveData<String>()
    val cameraPassword = MutableLiveData<String>()
    val cameraIp = MutableLiveData<String>()
    val cameraProtocol = MutableLiveData<String>()
    val cameraPath = MutableLiveData<String>()

    /**
     * Activity Ids
     */
    val activityKind = MutableLiveData<String>()
    val speedMetric = MutableLiveData<String>()
    val gforceMetric = MutableLiveData<String>()
    val leanAngleMetric = MutableLiveData<String>()
    val temperatureMetric = MutableLiveData<String>()
    val windMetric = MutableLiveData<String>()
    val pressureMetric = MutableLiveData<String>()
    val heartRateMetric = MutableLiveData<String>()
    val inclinationMetric = MutableLiveData<String>()
    val altitudeMetric = MutableLiveData<String>()

    /**
     * Metric Ids
     */
    val unitSystem = MutableLiveData<String>()
    val speedUnit = MutableLiveData<String>()
    val temperatureUnit = MutableLiveData<String>()
    val windSpeedUnit = MutableLiveData<String>()

    /**
     * User params
     */
    val profileImage = MutableLiveData<String>()
    val profileName = MutableLiveData<String>()

    val custom = MutableLiveData<String>()
    //endregion

    init {
        composePreferences()
    }

    /**
     * Handles changes in the configuration
     */
    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, keyChanged: String?)
    {
        try
        {
            Log.i("SettingsViewModel", "SYR -> Handling changes in thh configuration - $keyChanged, new value ${settingsGetter.getStringOption(SettingPreferencesIds.CameraId)}")
            composePreferences()
        }
        catch (ex: Exception)
        {
            Log.e("SettingsViewModel", "SYR -> Unable to process changes in teh configuration error: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Update all live data with the new configuration values
     */
    private fun composePreferences()
    {
        /**
         * Camera Ids
         */
         cameraId.value = settingsGetter.getStringOption(SettingPreferencesIds.CameraId)
         cameraName.value = settingsGetter.getStringOption(SettingPreferencesIds.CameraName)
         cameraSsidName.value = settingsGetter.getStringOption(SettingPreferencesIds.CameraSsidName)
         cameraConnectionType.value = settingsGetter.getStringOption(SettingPreferencesIds.CameraConnectionType)
         cameraPassword.value = settingsGetter.getStringOption(SettingPreferencesIds.CameraPassword)
         cameraIp.value = settingsGetter.getStringOption(SettingPreferencesIds.CameraIp)
         cameraProtocol.value = settingsGetter.getStringOption(SettingPreferencesIds.CameraProtocol)
         cameraPath.value = settingsGetter.getStringOption(SettingPreferencesIds.CameraPath)

        /**
         * Activity Ids
         */
         activityKind.value = settingsGetter.getStringOption(SettingPreferencesIds.ActivityKind)
         speedMetric.value = settingsGetter.getStringOption(SettingPreferencesIds.SpeedMetric)
         gforceMetric.value = settingsGetter.getStringOption(SettingPreferencesIds.GforceMetric)
         leanAngleMetric.value = settingsGetter.getStringOption(SettingPreferencesIds.LeanAngleMetric)
         temperatureMetric.value = settingsGetter.getStringOption(SettingPreferencesIds.TemperatureMetric)
         windMetric.value = settingsGetter.getStringOption(SettingPreferencesIds.WindMetric)
         pressureMetric.value = settingsGetter.getStringOption(SettingPreferencesIds.PressureMetric)
         heartRateMetric.value = settingsGetter.getStringOption(SettingPreferencesIds.HeartRateMetric)
         inclinationMetric.value = settingsGetter.getStringOption(SettingPreferencesIds.InclinationMetric)
         altitudeMetric.value = settingsGetter.getStringOption(SettingPreferencesIds.AltitudeMetric)

        /**
         * Metric Ids
         */
         unitSystem.value = settingsGetter.getStringOption(SettingPreferencesIds.UnitSystem)
         speedUnit.value = settingsGetter.getStringOption(SettingPreferencesIds.SpeedMetric)
         temperatureUnit.value = settingsGetter.getStringOption(SettingPreferencesIds.TemperatureUnit)
         windSpeedUnit.value = settingsGetter.getStringOption(SettingPreferencesIds.WindSpeedUnit)

        /**
         * User params
         */
         profileImage.value = settingsGetter.getStringOption(SettingPreferencesIds.ProfileImage)
         profileName.value = settingsGetter.getStringOption(SettingPreferencesIds.ProfileName)

    }

}