/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.userplayground

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.configuration.SettingsActivity
import com.example.shareyourride.permissions.PermissionsManager
import com.example.shareyourride.services.common.ServiceNotificationBuilder
import com.example.shareyourride.services.inclination.InclinationService
import com.example.shareyourride.services.location.LocationService
import com.example.shareyourride.services.session.SessionService
import com.example.shareyourride.services.session.SessionState
import com.example.shareyourride.services.video.VideoComposerService
import com.example.shareyourride.services.video.VideoService
import com.example.shareyourride.viewmodels.cameraWifi.WifiViewModel
import com.example.shareyourride.viewmodels.settings.SettingsViewModel
import com.example.shareyourride.viewmodels.userplayground.InclinationViewModel
import com.example.shareyourride.viewmodels.userplayground.LocationViewModel
import com.example.shareyourride.viewmodels.userplayground.SessionViewModel
import com.example.shareyourride.viewmodels.userplayground.VideoViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(),BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val permissions = PermissionsManager(this)

    //region view models
    /**
     * View model that manage setting changes
     */
    private val settingsViewModel: SettingsViewModel by viewModels()

    /**
     * View model that holds the current state of the wifi and can be used to command the WIFI system
     */
    private val wifiViewModel: WifiViewModel by viewModels()

    /**
     * View model that holds the current location and the state of the GPS state
     */
    private val locationViewModel: LocationViewModel by viewModels()

    /**
     * View model that holds the current location and the state of the GPS state
     */
    private val inclination: InclinationViewModel by viewModels()

    /**
     * View model that manages session changes, holds the current state of the session and all commands
     * to manage the user session
     */
    private val sessionViewModel: SessionViewModel by viewModels()

    /**
     * View model that gets the information about the state of the video and acts as facade to control
     * the video service
     */
    private val viewModel: VideoViewModel by viewModels()
    //endregion

    //region navigation controls
    /**
     *  Control related to the navigation between fragments
     */
    private var navController: NavController? = null
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        initializeServices()
        super.onCreate(savedInstanceState)
        setTheme(R.style.NoActionBar)
        setContentView(R.layout.activity_main)

        initializePermissions()

        bottom_navigation.setOnNavigationItemSelectedListener(this)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        configureSessionStateChangesListener()

        sessionViewModel.getSessionState()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun configureSessionStateChangesListener()
    {
        try
        {
            sessionViewModel.sessionData.observe(this, Observer {sessionData ->

                when (sessionData.state)
                {
                    SessionState.SynchronizingVideo ->
                    {
                        disableNavigationControls()
                        navController?.navigate(R.id.nav_video_synchronization_fragment)
                    }
                    SessionState.CalibratingSensors ->
                    {
                        disableNavigationControls()
                        navController?.navigate(R.id.nav_gyroscope_calibration_fragment)
                    }
                    SessionState.SensorsCalibrated ->
                    {
                        disableNavigationControls()
                        navController?.navigate(R.id.nav_gyroscope_calibration_fragment)
                    }
                    SessionState.Finished ->
                    {
                        enableNavigationControls()
                        navController?.navigate(R.id.nav_activity_finished)
                    }
                    SessionState.Started ->
                    {
                        disableNavigationControls()
                        navController?.navigate(R.id.nav_activity_started_fragment)
                    }
                    SessionState.Stopped ->
                    {
                        enableNavigationControls()
                        navController?.navigate(R.id.nav_home_fragment)
                    }
                    SessionState.CreatingVideo ->
                    {
                        disableNavigationControls()
                        navController?.navigate(R.id.nav_activity_finished)
                    }
                    else ->
                    {
                        Log.e("MainActivity", "SYR -> Unable to process session state ${sessionData.state}, not supported")
                    }
                }

                notifySessionState(sessionData.state)
            })
        }
        catch(ex: Exception)
        {
            Log.e("HomeFragment", "SYR -> Unable to create location observers because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    private fun notifySessionState(sessionState: SessionState)
    {
        try
        {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val pendingIntent: PendingIntent =
                Intent(this, MainActivity::class.java).let { notificationIntent ->
                    PendingIntent.getActivity(this, 0, notificationIntent, 0)
                }

            val notification = ServiceNotificationBuilder.getNotification(this, pendingIntent, sessionState)
            notificationManager.notify(ServiceNotificationBuilder.NOTIFICATION_ID, notification)
        }
        catch(ex: Exception)
        {
            Log.e("HomeFragment", "SYR -> Unable to create location observers because: ${ex.message}")
            ex.printStackTrace()
        }
    }


    //region navigation control
    /**
     * Handles the click of an element of the navigation menu
     * @param item: The pressed item
     * @return true
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        try {
            when (item.itemId)
            {
                R.id.nav_settings_home -> {
                    processHomeButton()
                }
                R.id.nav_settings_button -> {
                    processSettingsButton()
                }
                R.id.nav_gallery_button -> {
                    processGalleryButton()
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("MainActivity ", "SYR -> Unable to process navigation item selected ${ex.message}")
            ex.printStackTrace()
        }
        return true
    }

    /**
     * Open the settings window
     */
    private fun processSettingsButton()
    {
        Log.i("MainActivity","SYR -> User has press the settings button")
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun processHomeButton()
    {
        Log.i("MainActivity","SYR -> User has press the home button")
        navController?.navigate(R.id.nav_home_fragment)
    }

    /**
     * Open the gallery fragment with the navigation controller
     */
    private fun processGalleryButton()
    {
        Log.i("MainActivity","SYR -> User has press the show gallery button")
        val intent: Intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.type = "video/*"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
    //endregion

    //region private function
    /**
     * Fill the list of permissions to request and check of
     * */
    private fun initializePermissions()
    {
        permissions.addFunctionality(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.addFunctionality(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.addFunctionality(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        permissions.addFunctionality(Manifest.permission.CHANGE_WIFI_STATE)
        permissions.addFunctionality(Manifest.permission.ACCESS_WIFI_STATE)
        permissions.addFunctionality(Manifest.permission.CHANGE_NETWORK_STATE)
        permissions.addFunctionality(Manifest.permission.ACCESS_NETWORK_STATE)
        permissions.addFunctionality(Manifest.permission.INTERNET)
        permissions.addFunctionality(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissions.addFunctionality(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        permissions.checkPermissions()
    }

    /**
     * Starts app services
     */
    private fun initializeServices()
    {
        try {
            Log.i("MainActivity", "SYR -> Starting services")
            val si = Intent(this, SessionService::class.java)
            startForegroundService(si)

            val li = Intent(this, LocationService::class.java)
            startForegroundService(li)

            val ii = Intent(this, InclinationService::class.java)
            startForegroundService(ii)

            val vi = Intent(this, VideoService::class.java)
            startForegroundService(vi)

            val vci = Intent(this, VideoComposerService::class.java)
            startForegroundService(vci)
        }
        catch(ex: Exception)
        {
            Log.e("MainActivity", "SYR -> Unable to initialize services because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Gets the key down event, just for navigation back actions
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //do your stuff
            return navigateBackDispatcher()
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * handles the back button event
     *
     * @return true if the back button event has to be managed by the system
     *         false if the back button event has been managed by the app
     */
    private fun navigateBackDispatcher(): Boolean
    {
        var mustBeManagedByTheSystem = false
        try
        {
            when (sessionViewModel.sessionData.value?.state) {
                SessionState.Unknown -> {
                    //do nothing, let the system do its magic
                    Log.i("MainActivity", "SYR -> User has pressed the return button when the app is in Unknown state, let the system to rule the navigation ")
                    mustBeManagedByTheSystem = true
                }
                SessionState.Stopped -> {
                    //do nothing, let the system do its magic
                    Log.i("MainActivity", "SYR -> User has pressed the return button when the app is in Stopped state, let the system to rule the navigation ")
                    mustBeManagedByTheSystem = true
                }
                SessionState.SynchronizingVideo ->
                {
                    //Navigate to home
                    sessionViewModel.cancelSession()
                    navController?.navigate(R.id.nav_home_fragment)
                    Log.i("MainActivity", "SYR -> User has pressed the return button when the app is in SynchronizingVideo state, navigate to home fragment ")
                }
                SessionState.CalibratingSensors -> {
                    //Navigate to home
                    sessionViewModel.cancelSession()
                    navController?.navigate(R.id.nav_home_fragment)
                    Log.i("MainActivity", "SYR -> User has pressed the return button when the app is in CalibrationSensors state, navigate to home fragment ")
                }
                SessionState.SensorsCalibrated -> {
                    //Navigate to home
                    Log.i("MainActivity", "SYR -> User has pressed the return button when the app is in SensorsCalibrated state, navigate to home fragment ")
                    sessionViewModel.cancelSession()
                    navController?.navigate(R.id.nav_home_fragment)
                }
                SessionState.Started -> {
                    Log.i("MainActivity", "SYR -> User has pressed the return button when the app is in CreatingVideo state, show the fisnish activity dialog")
                    sessionViewModel.showFinishActivityDialog(this)
                }
                SessionState.CreatingVideo -> {
                    Log.i("MainActivity", "SYR -> User has pressed the return button when the app is in CreatingVideo state, block the action ")
                    Toast.makeText(applicationContext, getString(R.string.creating_video_notification), Toast.LENGTH_SHORT).show()
                }
                SessionState.Finished -> {
                    //Navigate to home
                    Log.i("MainActivity", "SYR -> User has pressed the return button when the app is in Finished state, navigate to home fragment")
                    navController?.navigate(R.id.nav_home_fragment)
                }
                null ->
                {
                    Log.i("MainActivity", "SYR -> User has pressed the return button when the app is in Finished state, navigate to home fragment")
                }
            }

            if (sessionViewModel.sessionData.value != null)
            {
                notifySessionState(sessionViewModel.sessionData.value!!.state)
            }

        }
        catch(ex: Exception)
        {
            Log.e("MainActivity", "SYR -> Unable to handle back button action because: ${ex.message}")
            ex.printStackTrace()
        }

        return mustBeManagedByTheSystem
    }

    /**
     * Disable buttons of the tool bar
     * This buttons only must be enabled when the session is in state stopped or finished
     */
    private fun disableNavigationControls()
    {
        bottom_navigation.menu.forEach { it.isEnabled = false }
    }

    /**
     * Enable buttons of the tool bar
     * This buttons only must be enabled when the session is in state stopped or finished
     */
    private fun enableNavigationControls()
    {
        bottom_navigation.menu.forEach { it.isEnabled = true }
    }
    //endregion

}