package com.example.shareyourride.userplayground.session

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.viewmodels.userplayground.VideoViewModel

class VideoSyncFrame : Fragment() {

    companion object {
        fun newInstance() = VideoSyncFrame()
    }

    private val viewModel: VideoViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.video_sync_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}