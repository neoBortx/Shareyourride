package com.example.shareyourride.userplayground.session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.viewmodels.userplayground.SessionViewModel

class SessionFragment : Fragment() {

    private lateinit var sessionViewModel: SessionViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sessionViewModel = ViewModelProviders.of(this).get(SessionViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_activity_started, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        sessionViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}