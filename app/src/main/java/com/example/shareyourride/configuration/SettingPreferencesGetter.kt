package com.example.shareyourride.configuration

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.bvillarroya_creations.shareyourride.R


/**
 * Enumeration of all supported preferences that the user can configure in the settings window
 */
enum class SettingPreferencesIds{

    /**
     * Camera Ids
     */
    CameraKind,
    CameraName,
    CameraSsidName,
    CameraConnectionType,
    CameraPassword,
    CameraIp,

    /**
     * Activity Ids
     */
    ActivityKind,
    SpeedMetric,
    GforceMetric,
    LeanAngleMetric,
    TemperatureMetric,
    WindMetric,
    PressureMetric,
    HeartRateMetric,
    InclinationMetric,
    AltitudeMetric,

    /**
     * Metric Ids
     */
    UnitSystem,
    SpeedUnit,
    TemperatureUnit,
    WindSpeedUnit,

    Custom,
}


/**
 * Class to access the shared preference system in an easier way
 */
class SettingPreferencesGetter(context: Context) {
    private var idToString: HashMap<SettingPreferencesIds, String> = HashMap()

    init {
        idToString[SettingPreferencesIds.CameraKind] = context.getString(R.string.camera_kind)
        idToString[SettingPreferencesIds.CameraName] = context.getString(R.string.camera_name)
        idToString[SettingPreferencesIds.CameraSsidName] = context.getString(R.string.camera_ssidName)
        idToString[SettingPreferencesIds.CameraConnectionType] = context.getString(R.string.camera_connection_type)
        idToString[SettingPreferencesIds.CameraPassword] = context.getString(R.string.camera_password)
        idToString[SettingPreferencesIds.CameraIp] = context.getString(R.string.camera_ip)

        idToString[SettingPreferencesIds.ActivityKind] = context.getString(R.string.activity_kind)
        idToString[SettingPreferencesIds.SpeedMetric] = context.getString(R.string.speed_metric)
        idToString[SettingPreferencesIds.GforceMetric] = context.getString(R.string.gforce_metric)
        idToString[SettingPreferencesIds.LeanAngleMetric] = context.getString(R.string.lean_angle_metric)
        idToString[SettingPreferencesIds.TemperatureMetric] = context.getString(R.string.temperature_metric)
        idToString[SettingPreferencesIds.WindMetric] = context.getString(R.string.wind_metric)
        idToString[SettingPreferencesIds.PressureMetric] = context.getString(R.string.pressure_metric)
        idToString[SettingPreferencesIds.HeartRateMetric] = context.getString(R.string.heart_rate_metric)
        idToString[SettingPreferencesIds.InclinationMetric] = context.getString(R.string.inclination_metric)
        idToString[SettingPreferencesIds.AltitudeMetric] = context.getString(R.string.altitude_metric)


        idToString[SettingPreferencesIds.UnitSystem] = context.getString(R.string.unit_system)
        idToString[SettingPreferencesIds.SpeedUnit] = context.getString(R.string.speed_unit)
        idToString[SettingPreferencesIds.TemperatureUnit] = context.getString(R.string.temperature_unit)
        idToString[SettingPreferencesIds.WindSpeedUnit] = context.getString(R.string.wind_speed_unit)

        idToString[SettingPreferencesIds.Custom] = context.getString(R.string.custom)

    }

    /**
     * Preference manager instance
     */
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    /**
     * Returns a boolean option
     *
     * @param key: Key that identifies the option, values are defined in preferences.xml
     * @return The option related to the given key
     */
    fun getBooleanOption(key: SettingPreferencesIds): Boolean
    {
        return if (idToString.containsKey(key)) {
            prefs.getBoolean(idToString[key], false)
        }
        else {
            Log.e("SettingPreferences", "GIS -> Key $key not supported")
            false
        }
    }

    /**
     * Returns a string option
     *
     * @param key: Key that identifies the option, values are defined in preferences.xml
     * @return The option related to the given key
     */
    fun getStringOption(key: SettingPreferencesIds): String
    {
        return if (idToString.containsKey(key)) {
            prefs.getString(idToString[key], "") ?: ""
        }
        else {
            Log.e("SettingPreferences", "GIS -> Key $key not supported")
            ""
        }
    }

    /**
     * Returns a int option
     *
     * @param key: Key that identifies the option, values are defined in preferences.xml
     * @return The option related to the given key
     */
    fun getIntOption(key: SettingPreferencesIds): Int
    {
        return if (idToString.containsKey(key)) {
            prefs.getInt(idToString[key], 0)
        }
        else {
            Log.e("SettingPreferences", "GIS -> Key $key not supported")
            0
        }
    }
}