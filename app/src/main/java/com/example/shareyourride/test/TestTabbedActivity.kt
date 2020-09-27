package com.example.shareyourride.test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bvillarroya_creations.VideoClient
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.camera.SupportedCameras
import com.example.shareyourride.configuration.SettingPreferencesGetter
import com.example.shareyourride.configuration.SettingPreferencesIds
import com.example.shareyourride.configuration.SettingsActivity
import com.example.shareyourride.test.ui.main.SectionsPagerAdapter
import com.example.shareyourride.wifi.CameraWifiClient
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_test_tabbed.*
import java.util.*

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
                //val wifiClient = CameraWifiClient(this, cameraData,this);
                //wifiClient.connectToCamera()
            }
        }

        button_configuration.setOnClickListener {
            Log.d("TestTabbedActivity", "SYR -> Opening settings activity")
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        button_video.setOnClickListener {

            Log.d("TestTabbedActivity", "SYR -> Opening video stream")

            val videoClient = VideoClient(this)

            val setting = SettingPreferencesGetter(this)
            val protocol = setting.getStringOption(SettingPreferencesIds.CameraProtocol)
            val ip = setting.getStringOption(SettingPreferencesIds.CameraIp)
            val path = setting.getStringOption(SettingPreferencesIds.CameraPath)
            val videoStream = protocol +"://"+ ip+"/"+ path

            if (!videoClient.isVideoCapturing())
            {
                videoClient.initializeStreamClient(videoStream, "VideoTestTabbed", "mp4")
                videoClient.saveVideoStream()
            }
            else
            {
                videoClient.stopVideoCapturing()
            }
        }
    }
}