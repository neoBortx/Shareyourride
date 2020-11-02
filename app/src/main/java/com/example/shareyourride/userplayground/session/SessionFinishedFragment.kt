package com.example.shareyourride.userplayground.session

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bvillarroya_creations.shareyourride.R
import com.bvillarroya_creations.shareyourride.databinding.FragmentSessionBinding
import com.example.shareyourride.viewmodels.userplayground.SessionViewModel
import com.example.shareyourride.viewmodels.userplayground.VideoViewModel

class SessionFinishedFragment : Fragment() {

    private val sessionViewModel: SessionViewModel  by viewModels()
    private val viewModel: VideoViewModel  by viewModels()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

       val view = inflater.inflate(R.layout.fragment_session_finished, container, false)

        //viewModel.connect()
        //client.startPlaying()

        return view
    }
}