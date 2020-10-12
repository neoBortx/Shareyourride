package com.example.shareyourride.userplayground.session

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bvillarroya_creations.shareyourride.R
import com.bvillarroya_creations.shareyourride.databinding.FragmentSessionBinding
import com.example.shareyourride.configuration.SettingPreferencesGetter
import com.example.shareyourride.configuration.SettingPreferencesIds
import com.example.shareyourride.video.RemoteVideoConfiguration
import com.example.shareyourride.video.rtsp.RtspVideoClient
import com.example.shareyourride.viewmodels.userplayground.SessionViewModel
import com.example.shareyourride.viewmodels.userplayground.VideoViewModel

class SessionFragment : Fragment() {

    private val sessionViewModel: SessionViewModel  by viewModels()
    private val viewModel: VideoViewModel  by viewModels()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        inflater.inflate(R.layout.fragment_session, container, false)
        val binding: FragmentSessionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_session, container, false)

        val view: View = binding.root

        //viewModel.connect()
        //client.startPlaying()

        return view
    }
}