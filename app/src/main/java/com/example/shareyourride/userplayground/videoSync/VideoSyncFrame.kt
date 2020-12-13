package com.example.shareyourride.userplayground.videoSync

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.viewmodels.cameraWifi.WifiViewModel
import com.example.shareyourride.viewmodels.settings.SettingsViewModel
import com.example.shareyourride.viewmodels.userplayground.LocationViewModel
import com.example.shareyourride.viewmodels.userplayground.SessionViewModel
import com.example.shareyourride.viewmodels.userplayground.VideoViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.video_sync_fragment.*
import java.util.concurrent.TimeUnit

class VideoSyncFrame : Fragment() {

    //region view models
    /**
     * View model that manage setting changes
     */
    private val settingsViewModel: SettingsViewModel by viewModels({ requireActivity() })

    /**
     * View model that holds the current state of the wifi and can be used to command the WIFI system
     */
    private val wifiViewModel: WifiViewModel by viewModels({ requireActivity() })

    /**
     * View model that holds the current location and the state of the GPS state
     */
    private val locationViewModel: LocationViewModel by viewModels({ requireActivity() })

    /**
     * View model that manages session changes, holds the current state of the session and all commands
     * to manage the user session
     */
    private val sessionViewModel: SessionViewModel by viewModels({ requireActivity() })

    /**
     * View model that manages session changes, holds the current state of the session and all commands
     * to manage the user session
     */
    private val videoViewModel: VideoViewModel by viewModels({ requireActivity() })
    //endregion


    /**
     * Create an observer to manage changes in the GPS state
     * Just change the color of the icon and enable or disable the start activity button
     */
    private fun configureLocationChanges()
    {
        try
        {
            locationViewModel.gpsState.observe(viewLifecycleOwner, Observer {state ->
                val colorId = if (state) R.color.colorProviderOk else R.color.colorProviderError
                ImageViewCompat.setImageTintList(gps_state_img_video_synchronization, context?.getColor(colorId)?.let { ColorStateList.valueOf(it) })
            })
        }
        catch(ex: Exception)
        {
            Log.e("VideoSyncFrame", "SYR -> Unable to create location observers because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Create an observer to manage changes in the GPS state
     * Just change the color of the icon and enable or disable the start activity button
     */
    private fun configureVideoStateChanges()
    {
        try
        {
            videoViewModel.videoState.observe(viewLifecycleOwner, Observer {state ->

                try
                {
                    if (state != null)
                    {
                        Log.d("VideoSyncFrame", "SYR -> Handling change in the video state connected: $state")

                        val colorId = if (state) R.color.colorProviderOk else R.color.colorProviderError

                        ImageViewCompat.setImageTintList(video_state_img_video_synchronization, context?.getColor(colorId)?.let { ColorStateList.valueOf(it) })
                    }
                }
                catch (ex: java.lang.Exception)
                {
                    ex.printStackTrace()
                }
            })
        }
        catch(ex: Exception)
        {
            Log.e("VideoSyncFrame", "SYR -> Unable to create video state observer because: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Create an observer to manage changes in the GPS state
     * Just change the color of the icon and enable or disable the start activity button
     */
    private fun configureFrameObserver()
    {
        try
        {
            videoViewModel.bitmapFrameToShow.observe(viewLifecycleOwner, Observer {image ->
                image_view_video_synchronization.setImageBitmap(image)
            })

            videoViewModel.leanAngleForSynchronization.observe(viewLifecycleOwner, Observer {angle ->
                try
                {
                    if (videoViewModel.synchronizationDelay.value != null && angle!= null && angle_indicator != null)
                    {
                        Completable.timer(videoViewModel.synchronizationDelay.value!!.toLong(),
                                          TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe {
                            if (angle_indicator != null) {
                                angle_indicator.text = angle.toString()
                            }
                        }
                    }
                }
                catch (ex: java.lang.Exception)
                {
                    ex.printStackTrace()
                }
            })

            videoViewModel.synchronizationDelay.observe(viewLifecycleOwner, Observer {delay ->
                try
                {
                    if (delay != null)
                    {
                        configured_delay_text_view.text= delay.toString() + " ms"
                    }
                }
                catch (ex: java.lang.Exception)
                {
                    ex.printStackTrace()
                }
            })
        }
        catch(ex: Exception)
        {
            Log.e("VideoSyncFrame", "SYR -> Unable to create location observers because: ${ex.message}")
            ex.printStackTrace()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.video_sync_fragment, container, false)

        (view?.findViewById(R.id.text_activity_kind_value_video_synchronization) as TextView).text = settingsViewModel.activityName
        (view.findViewById(R.id.continue_video_synchronization) as Button).setOnClickListener(continueActivityButtonCommand)
        (view.findViewById(R.id.increase_delay_button) as ImageButton).setOnClickListener(increaseDelayButtonCommand)
        (view.findViewById(R.id.decrease_delay_button) as ImageButton).setOnClickListener(decreaseDelayButtonCommand)



        (view.findViewById(R.id.configured_delay_text_view) as TextView).text= "0 ms"

        if (context != null)
        {
            configureLocationChanges()
            configureVideoStateChanges()
            configureFrameObserver()
        }
        else
        {
            Log.e("VideoSyncFrame","Unable to initialize observers, context is null")
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    //region buttons commands
    /**
     * Manages clicks in the start activity button
     */
    private val continueActivityButtonCommand = View.OnClickListener {

        Log.i("VideoSyncFrame", "SYR -> Processing continue button clicked")
        sessionViewModel.continueSession()
    }

    /**
     * Manages clicks in the start activity button
     */
    private val increaseDelayButtonCommand = View.OnClickListener {

        Log.i("VideoSyncFrame", "SYR -> Processing increase delay button clicked")
        videoViewModel.increaseDelay()
    }


    /**
     * Manages clicks in the start activity button
     */
    private val decreaseDelayButtonCommand = View.OnClickListener {

        Log.i("VideoSyncFrame", "SYR -> Processing decrease delay button clicked")
        videoViewModel.decreaseDelay()
    }


    //endregion

}