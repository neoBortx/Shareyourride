package com.example.shareyourride.test

import android.Manifest
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bvillarroya_creations.shareyourride.R
import com.bvillarroya_creations.shareyourride.databinding.ContentTestctivityBinding
import com.bvillarroya_creations.shareyourride.viewmodel.inclination.InclinationViewModel
import com.bvillarroya_creations.shareyourride.viewmodel.location.LocationViewModel
import com.bvillarroya_creations.shareyourride.viewmodel.session.SessionViewModel
import com.example.shareyourride.Permissions.PermissionsManager

class TestActivity : AppCompatActivity() {

    val permissions = PermissionsManager(this)
    val sessionViewModel: SessionViewModel by viewModels()
    val inclinationViewModel: InclinationViewModel by viewModels()
    val locationViewModel: LocationViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testctivity)
        setSupportActionBar(findViewById(R.id.toolbar))

        initializePermissions()

        // Inflate view and obtain an instance of the binding class.
        val binding = DataBindingUtil.setContentView<ContentTestctivityBinding>(this, R.layout.content_testctivity)
        binding.lifecycleOwner = this
        // Assign the component to a property in the binding class.
        binding.session = sessionViewModel
        binding.location = locationViewModel
        binding.inclination = inclinationViewModel
    }


    /*
        Fill the list of permissions to request and check of
     */
    private fun initializePermissions()
    {
        permissions.addFunctionality(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.addFunctionality(Manifest.permission.ACCESS_COARSE_LOCATION)

        permissions.checkPermissions()
    }
}