/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.viewmodels.settings

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.configuration.SettingPreferencesGetter
import com.example.shareyourride.configuration.SettingPreferencesIds
import com.example.shareyourride.services.session.SummaryTelemetryType
import com.example.shareyourride.services.session.TelemetryType
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

    /**
     * Throttle to not invoke to many changes about the configuration
     */
    private val subject: BehaviorSubject<Boolean> = BehaviorSubject.create()
    //region mutable live data
    /**
     * Camera properties
     */
    var cameraId = ""
    var cameraName = ""
    var cameraSsidName = ""
    var cameraConnectionType = ""
    var cameraPassword = ""
    var cameraIp = ""
    var cameraProtocol = ""
    var cameraPath = ""

    /**
     * This is a flag used to control the wifi camera changes that a camera client must observer
     */
    val cameraConfigChangedFlag = MutableLiveData<Boolean>()

    /**
     * Activity properties
     */
    var activityKind = ""
    var activityName = ""
    var speedMetric = false
    var gforceMetric = false
    var leanAngleMetric = false
    var inclinationMetric = false
    var altitudeMetric = false
    var distanceMetric = false

    /**
     * List of configured activities
     */
    val telemetryList  = mutableListOf<TelemetryType>()

    /**
     * Summary telemetry list given at the end of the session
     */
    val summaryTelemetryList  = mutableListOf<SummaryTelemetryType>()



    /**
     * This is a flag used to control the activity changes that a camera client must observer
     */
    val activityDataChangedFlag = MutableLiveData<Boolean>()

    /**
     * Metric Ids
     */
    var unitSystem = ""

    /**
     * For future use
     *
    private var temperatureMetric = false
    private var windMetric = false
    private var pressureMetric = false
    private var speedUnit = ""
    private var temperatureUnit = ""
    private var windSpeedUnit = ""
    */
    //endregion

    init {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        Log.i("SettingsViewModel", "SYR -> INITIAL LOAD")
        var index = 0
        application.resources.getStringArray(R.array.activity_kind_values).forEach {
            if (application.resources.getStringArray(R.array.activity_kind_entries).count() > index) {
                activitiesHasMap[it] = application.resources.getStringArray(R.array.activity_kind_entries)[index]
                index++
            }
        }

        //Debounce change of the configuration in order to minimize events to upper layers
        val observable: Observable<Boolean>? = subject.debounce(1, TimeUnit.SECONDS)?.observeOn(AndroidSchedulers.mainThread())
        observable?.subscribe {
            composePreferences()
        }

        composePreferences()
    }

    /**
     * Handles changes in the configuration
     */
    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, keyChanged: String?)
    {
        try
        {
            //Log.d("SettingsViewModel", "SYR -> Handling changes in the configuration - $keyChanged, new value ${settingsGetter.getStringOption(SettingPreferencesIds.CameraId)}")
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
        //Camera Ids
        cameraId             = settingsGetter.getStringOption(SettingPreferencesIds.CameraId)
        cameraName           = settingsGetter.getStringOption(SettingPreferencesIds.CameraName)
        cameraSsidName       = settingsGetter.getStringOption(SettingPreferencesIds.CameraSsidName)
        cameraConnectionType = settingsGetter.getStringOption(SettingPreferencesIds.CameraConnectionType)
        cameraPassword       = settingsGetter.getStringOption(SettingPreferencesIds.CameraPassword)
        cameraIp             = settingsGetter.getStringOption(SettingPreferencesIds.CameraIp)
        cameraProtocol       = settingsGetter.getStringOption(SettingPreferencesIds.CameraProtocol)
        cameraPath           = settingsGetter.getStringOption(SettingPreferencesIds.CameraPath)
        //Activity Ids
        activityKind         = settingsGetter.getStringOption(SettingPreferencesIds.ActivityKind)
        speedMetric          = settingsGetter.getBooleanOption(SettingPreferencesIds.SpeedMetric)
        gforceMetric         = settingsGetter.getBooleanOption(SettingPreferencesIds.GforceMetric)
        leanAngleMetric      = settingsGetter.getBooleanOption(SettingPreferencesIds.LeanAngleMetric)
        inclinationMetric    = settingsGetter.getBooleanOption(SettingPreferencesIds.InclinationMetric)
        altitudeMetric       = settingsGetter.getBooleanOption(SettingPreferencesIds.AltitudeMetric)
        distanceMetric       = settingsGetter.getBooleanOption(SettingPreferencesIds.DistanceMetric)
        //Metric Ids
        unitSystem           = settingsGetter.getStringOption(SettingPreferencesIds.UnitSystem)

        if (activitiesHasMap.containsKey(activityKind))
        {
            activityName = activitiesHasMap[activityKind]!!
        }
        else
        {
            Log.e("SettingsViewModel","SYR -> Unable to get the activity name")
        }

        /**
         * For future use
         *
        temperatureMetric = settingsGetter.getBooleanOption(SettingPreferencesIds.TemperatureMetric)
        pressureMetric    = settingsGetter.getBooleanOption(SettingPreferencesIds.PressureMetric)
        windMetric        = settingsGetter.getBooleanOption(SettingPreferencesIds.WindMetric)
        speedUnit         = settingsGetter.getStringOption(SettingPreferencesIds.SpeedMetric)
        temperatureUnit   = settingsGetter.getStringOption(SettingPreferencesIds.TemperatureUnit)
        windSpeedUnit     = settingsGetter.getStringOption(SettingPreferencesIds.WindSpeedUnit)
        */
        composeTelemetryList()

        cameraConfigChangedFlag.postValue(true)
        activityDataChangedFlag.postValue(true)
    }

    /**
     *
     */
    private fun composeTelemetryList()
    {
        telemetryList.clear()
        summaryTelemetryList.clear()

        summaryTelemetryList.add(SummaryTelemetryType.Duration)

        if (distanceMetric)
        {
            telemetryList.add(TelemetryType.Distance)
            summaryTelemetryList.add(SummaryTelemetryType.Distance)
        }

        if (speedMetric)
        {
            telemetryList.add(TelemetryType.Speed)
            summaryTelemetryList.add(SummaryTelemetryType.MaxSpeed)
            summaryTelemetryList.add(SummaryTelemetryType.AverageMaxSpeed)
        }

        if (gforceMetric)
        {
            telemetryList.add(TelemetryType.Acceleration)
            summaryTelemetryList.add(SummaryTelemetryType.MaxAcceleration)
        }

        if (leanAngleMetric)
        {
            telemetryList.add(TelemetryType.LeanAngle)
            summaryTelemetryList.add(SummaryTelemetryType.MaxLeftLeanAngle)
            summaryTelemetryList.add(SummaryTelemetryType.MaxRightLeanAngle)
        }

        if (inclinationMetric)
        {
            telemetryList.add(TelemetryType.TerrainInclination)
            summaryTelemetryList.add(SummaryTelemetryType.MaxDownhillTerrainInclination)
            summaryTelemetryList.add(SummaryTelemetryType.MaxUphillTerrainInclination)
            summaryTelemetryList.add(SummaryTelemetryType.AverageTerrainInclination)
        }

        if (altitudeMetric)
        {
            telemetryList.add(TelemetryType.Altitude)
            summaryTelemetryList.add(SummaryTelemetryType.MaxAltitude)
            summaryTelemetryList.add(SummaryTelemetryType.MinAltitude)
        }

        /**
         * for future use
         *
        if (temperatureMetric)
        {
            telemetryList.add(getApplication<Application>().getString(R.string.temperature))
        }

        if (windMetric)
        {
            telemetryList.add(getApplication<Application>().getString(R.string.wind))
        }

        if (pressureMetric)
        {
            telemetryList.add(getApplication<Application>().getString(R.string.pressure))
        }
        */
    }


}