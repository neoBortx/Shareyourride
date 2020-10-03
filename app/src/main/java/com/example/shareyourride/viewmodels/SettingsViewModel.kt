package com.example.shareyourride.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.configuration.SettingPreferencesGetter
import com.example.shareyourride.configuration.SettingPreferencesIds
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

/**
 * View model used to notify changes in the application settings
 */
class SettingsViewModel(application: Application)  : AndroidViewModel(application),SharedPreferences.OnSharedPreferenceChangeListener
{
    private var settingsGetter = SettingPreferencesGetter(application.applicationContext)

    private var activitiesHasMap: HashMap<String, String> = HashMap()

    val subject: BehaviorSubject<Boolean> = BehaviorSubject.create()
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
     * This is a flag used to control the wifi camera changes that a camera client must observer
     */
    val cameraConfigChangedFlag = MutableLiveData<Boolean>()

    /**
     * Activity Ids
     */
    val activityKind = MutableLiveData<String>()
    val activityName = MutableLiveData<String>()
    val speedMetric = MutableLiveData<Boolean>()
    val gforceMetric = MutableLiveData<Boolean>()
    val leanAngleMetric = MutableLiveData<Boolean>()
    val temperatureMetric = MutableLiveData<Boolean>()
    val windMetric = MutableLiveData<Boolean>()
    val pressureMetric = MutableLiveData<Boolean>()
    val heartRateMetric = MutableLiveData<Boolean>()
    val inclinationMetric = MutableLiveData<Boolean>()
    val altitudeMetric = MutableLiveData<Boolean>()

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

    val configuredTelemetryList = MutableLiveData<List<String>>()
    //endregion

    init {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        Log.i("SettingsViewModel", "SYR -> INITIAL LOAD")
        composePreferences()

        var index = 0
        application.resources.getStringArray(R.array.activity_kind_values).forEach {
            if (application.resources.getStringArray(R.array.activity_kind_entries).count() > index) {
                activitiesHasMap[it] = application.resources.getStringArray(R.array.activity_kind_entries)[index]
                index++
            }
        }

        val observable: Observable<Boolean>? = subject.debounce(3, TimeUnit.SECONDS)?.observeOn(AndroidSchedulers.mainThread())
        val subscribe = observable?.subscribe {
            composePreferences()
        }
    }

    /**
     * Handles changes in the configuration
     */
    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, keyChanged: String?)
    {
        try
        {
            Log.i("SettingsViewModel", "SYR -> Handling changes in the configuration - $keyChanged, new value ${settingsGetter.getStringOption(SettingPreferencesIds.CameraId)}")
            if (keyChanged != null && settingsGetter.checkIfIdIsManaged(keyChanged)) {
                subject.onNext(false)
            }
        }
        catch (ex: Exception)
        {
            Log.e("SettingsViewModel", "SYR -> Unable to process changes in the configuration error: ${ex.message}")
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
        cameraConfigChangedFlag.value = true;

        /**
         * Activity Ids
         */
        activityKind.value = settingsGetter.getStringOption(SettingPreferencesIds.ActivityKind)

         if (activityKind.value != null
             && activitiesHasMap.containsKey(activityKind.value!!))
         {
             activityName.value = activitiesHasMap[activityKind.value!!]
         }
        else
         {
             Log.e("SettingsViewModel","SYR -> Unable to get the activity name")
         }

         activityName.value = activitiesHasMap[activityKind.value!!]
         speedMetric.value = settingsGetter.getBooleanOption(SettingPreferencesIds.SpeedMetric)
         gforceMetric.value = settingsGetter.getBooleanOption(SettingPreferencesIds.GforceMetric)
         leanAngleMetric.value = settingsGetter.getBooleanOption(SettingPreferencesIds.LeanAngleMetric)
         temperatureMetric.value = settingsGetter.getBooleanOption(SettingPreferencesIds.TemperatureMetric)
         windMetric.value = settingsGetter.getBooleanOption(SettingPreferencesIds.WindMetric)
         pressureMetric.value = settingsGetter.getBooleanOption(SettingPreferencesIds.PressureMetric)
         heartRateMetric.value = settingsGetter.getBooleanOption(SettingPreferencesIds.HeartRateMetric)
         inclinationMetric.value = settingsGetter.getBooleanOption(SettingPreferencesIds.InclinationMetric)
         altitudeMetric.value = settingsGetter.getBooleanOption(SettingPreferencesIds.AltitudeMetric)

        /**
         * Metric Ids
         */
         unitSystem.value = settingsGetter.getStringOption(SettingPreferencesIds.UnitSystem)
         //speedUnit.value = settingsGetter.getStringOption(SettingPreferencesIds.SpeedMetric)
        //temperatureUnit.value = settingsGetter.getStringOption(SettingPreferencesIds.TemperatureUnit)
        //windSpeedUnit.value = settingsGetter.getStringOption(SettingPreferencesIds.WindSpeedUnit)

        /**
         * User params
         */
         profileImage.value = settingsGetter.getStringOption(SettingPreferencesIds.ProfileImage)
         profileName.value = settingsGetter.getStringOption(SettingPreferencesIds.ProfileName)

        composeTelemetryList()
    }

    /**
     *
     */
    private fun composeTelemetryList()
    {
        val telemetryList = mutableListOf<String>()
        if (speedMetric.value!!)
        {
            telemetryList.add(getApplication<Application>().getString(R.string.speed))
        }

        if (gforceMetric.value!!)
        {
            telemetryList.add(getApplication<Application>().getString(R.string.gforce))
        }

        if (leanAngleMetric.value!!)
        {
            telemetryList.add(getApplication<Application>().getString(R.string.lean_angle))
        }

        if (temperatureMetric.value!!)
        {
            telemetryList.add(getApplication<Application>().getString(R.string.temperature))
        }

        if (windMetric.value!!)
        {
            telemetryList.add(getApplication<Application>().getString(R.string.wind))
        }

        if (pressureMetric.value!!)
        {
            telemetryList.add(getApplication<Application>().getString(R.string.pressure))
        }

        if (inclinationMetric.value!!)
        {
            telemetryList.add(getApplication<Application>().getString(R.string.inclination_terrain))
        }

        if (altitudeMetric.value!!)
        {
            telemetryList.add(getApplication<Application>().getString(R.string.altitude))
        }

        configuredTelemetryList.postValue(telemetryList)
    }


}