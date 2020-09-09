package com.example.shareyourride.test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bvillarroya_creations.ffmpegWrapper.RtspClient
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.camera.SupportedCameras
import com.example.shareyourride.configuration.SettingPreferencesGetter
import com.example.shareyourride.configuration.SettingPreferencesIds
import com.example.shareyourride.configuration.SettingsActivity
import com.example.shareyourride.test.ui.main.SectionsPagerAdapter
import com.example.shareyourride.wifi.CameraWifiClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_test_tabbed.*

class TestTabbedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_tabbed)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)


        button_start.setOnClickListener {

            val cameras = SupportedCameras()
            val cameraData= cameras.getSavedCameraData(this)
            if (cameraData != null)
            {
                val wifiClient = CameraWifiClient(this, cameraData,this);
                wifiClient.connectToCamera()
            }
        }

        button_configuration.setOnClickListener {
            Log.d("TestTabbedActivity", "SYR -> Opening settings activity")
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        button_video.setOnClickListener {
            Log.d("TestTabbedActivity", "SYR -> Opening video stream")
            val rtsp = RtspClient()
            val setting = SettingPreferencesGetter(this)
            rtsp.getStream(/*setting.getStringOption(SettingPreferencesIds.CameraIp)*/)
        }
    }
}