package com.example.shareyourride.userplayground

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.configuration.SettingsActivity
import com.example.shareyourride.login.LoginActivity
import com.example.shareyourride.permissions.PermissionsManager
import com.example.shareyourride.viewmodels.login.UserManagementViewModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val permissions = PermissionsManager(this)

    //region view models
    /**
     * View model that handles the user information, hides the login provider
     * in this layer
     */
    private val userManagementViewModel: UserManagementViewModel by viewModels()
    //endregion

    private fun configureToolBar()
    {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Share your ride"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)

        val toggle = ActionBarDrawerToggle(
                this, main_drawer_layout, toolbar, R.string.app_name,
                R.string.close)

        main_drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializePermissions()

        configureToolBar()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }
        main_nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * Handles the click of an element of the navigation menu
     * @param item: The pressed item
     * @return true
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        try {
            when (item.itemId) {

                R.id.nav_settings_button -> {
                    processSettingsButton()
                }
                R.id.nav_gallery_button -> {
                    processGalleryButton()
                }
                R.id.nav_logout_button -> {
                    processLogOutButton()
                }
                R.id.nav_delete_account -> {
                    processDeleteAccountButton()
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("MainActivity ", "SYR -> Unable to process navigation item selected ${ex.message}")
            ex.printStackTrace()
        }

        main_drawer_layout.closeDrawer(GravityCompat.START)
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

    /**
     * Open the gallery fragment with the navigation controller
     */
    private fun processGalleryButton()
    {
        Log.i("MainActivity","SYR -> User has press the show gallery button")
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.nav_gallery_fragment)
    }

    /**
     * logout the current session and show the login activity
     */
    private fun processLogOutButton()
    {
        Log.i("MainActivity","SYR -> User has press the log out button")
        userManagementViewModel.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finishActivity(0)
    }

    /**
     * Remove the user account and show the login activity
     */
    private fun processDeleteAccountButton()
    {
        Log.i("MainActivity","SYR -> User has press the delete account button")
        userManagementViewModel.delete()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finishActivity(0)
    }

    /*
Fill the list of permissions to request and check of
*/
    private fun initializePermissions()
    {
        permissions.addFunctionality(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.addFunctionality(Manifest.permission.ACCESS_COARSE_LOCATION.toString())
        permissions.addFunctionality(Manifest.permission.CHANGE_WIFI_STATE)
        permissions.addFunctionality(Manifest.permission.ACCESS_WIFI_STATE)
        permissions.addFunctionality(Manifest.permission.CHANGE_NETWORK_STATE)
        permissions.addFunctionality(Manifest.permission.ACCESS_NETWORK_STATE)
        permissions.addFunctionality(Manifest.permission.INTERNET)

        permissions.checkPermissions()
    }

}