package com.example.shareyourride.userplayground.session

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.common.CommonConstants
import com.example.shareyourride.services.session.TelemetryType
import com.example.shareyourride.userplayground.common.TelemetryDirectionIconConverter
import com.example.shareyourride.viewmodels.userplayground.InclinationViewModel
import com.example.shareyourride.viewmodels.userplayground.LocationViewModel
import java.text.DecimalFormat
import kotlin.math.roundToInt

class TelemetryRecyclerViewAdapter(private val dataList: Array<TelemetryType>,
                                   private val locationViewModel: LocationViewModel,
                                   private val inclinationViewModel: InclinationViewModel) : RecyclerView.Adapter<TelemetryRecyclerViewAdapter.CustomViewHolder>() {

     // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_telemetry_data, parent, false)
        // set the view's size, margins, padding and layout parameters

        return CustomViewHolder(view, locationViewModel, inclinationViewModel)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    // Return the size of your data set (invoked by the layout manager)
    override fun getItemCount() = dataList.size

    inner class CustomViewHolder(view: View, private val locationViewModel: LocationViewModel, private val inclinationViewModel: InclinationViewModel) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(telemetry: TelemetryType) {
            itemView.findViewById<ImageView>(R.id.telemetry_icon).setImageResource(TelemetryIconConverter().getIcon(telemetry))
            ImageViewCompat.setImageTintList(itemView.findViewById(R.id.telemetry_icon), itemView.context?.getColor(R.color.colorPrimary)?.let { ColorStateList.valueOf(it) })

            val telemetryValue = itemView.findViewById<TextView>(R.id.telemetry_value)
            val directionIcon = itemView.findViewById<ImageView>(R.id.direction_icon)

            when (telemetry)
            {
                TelemetryType.Altitude -> {
                    telemetryValue.text = (locationViewModel.altitude.value?.times(CommonConstants.getShortDistanceConverter(itemView.context)))?.roundToInt().toString() + CommonConstants.getShortDistanceText(itemView.context)
                    locationViewModel.altitude.observe(itemView.context as LifecycleOwner, Observer {
                        telemetryValue.text = (locationViewModel.altitude.value?.times(CommonConstants.getShortDistanceConverter(itemView.context)))?.roundToInt().toString() + CommonConstants.getShortDistanceText(itemView.context)
                    })
                }
                TelemetryType.Distance -> {

                    if(locationViewModel.distance.value!! < CommonConstants.getLongDistanceConverter(itemView.context))
                    {
                        val distance = locationViewModel.distance.value?.times(CommonConstants.getShortDistanceConverter(itemView.context))?: 0.0
                        telemetryValue.text = distance.roundToInt().toString() + CommonConstants.getShortDistanceText(itemView.context)
                    }
                    else
                    {
                        val distance = locationViewModel.distance.value!!.toDouble()/CommonConstants.getLongDistanceConverter(itemView.context)
                        telemetryValue.text = DecimalFormat("####.###").format(distance).toString() + CommonConstants.getLongDistanceText(itemView.context)
                    }
                    locationViewModel.distance.observe(itemView.context as LifecycleOwner, Observer {


                        if(locationViewModel.distance.value!! < CommonConstants.getLongDistanceConverter(itemView.context))
                        {
                            val distance = locationViewModel.distance.value?.times(CommonConstants.getShortDistanceConverter(itemView.context))?: 0.0
                            telemetryValue.text = distance.roundToInt().toString() + CommonConstants.getShortDistanceText(itemView.context)
                        }
                        else
                        {
                            val distance = locationViewModel.distance.value!!.toDouble()/CommonConstants.getLongDistanceConverter(itemView.context)
                            telemetryValue.text = DecimalFormat("####.###").format(distance).toString() + CommonConstants.getLongDistanceText(itemView.context)
                        }
                    })
                }

                TelemetryType.TerrainInclination -> {
                    telemetryValue.text = locationViewModel.terrainInclination.value.toString()  + itemView.context.getString(R.string.percentage)
                    locationViewModel.terrainInclination.observe(itemView.context as LifecycleOwner, Observer {
                        telemetryValue.text = locationViewModel.terrainInclination.value.toString() + itemView.context.getString(R.string.percentage)
                    })
                }

                TelemetryType.Speed -> {
                    telemetryValue.text = locationViewModel.speed.value?.times(CommonConstants.getSpeedConverter(itemView.context))?.roundToInt().toString() + CommonConstants.getSpeedText(itemView.context)
                    locationViewModel.speed.observe(itemView.context as LifecycleOwner, Observer {
                        telemetryValue.text = locationViewModel.speed.value?.times(CommonConstants.getSpeedConverter(itemView.context))?.roundToInt().toString() + CommonConstants.getSpeedText(itemView.context)
                    })
                }
                TelemetryType.Acceleration -> {
                    telemetryValue.text = DecimalFormat("##.#").format(inclinationViewModel.acceleration.value?.times(CommonConstants.getAccelerationConverter(itemView.context))).toString() + CommonConstants.getAccelerationText(itemView.context)
                    inclinationViewModel.acceleration.observe(itemView.context as LifecycleOwner, Observer {
                        telemetryValue.text = DecimalFormat("##.#").format(inclinationViewModel.acceleration.value?.times(CommonConstants.getAccelerationConverter(itemView.context))).toString() + CommonConstants.getAccelerationText(itemView.context)
                    })

                    directionIcon.visibility = View.VISIBLE
                    directionIcon.setImageResource(TelemetryDirectionIconConverter().getDirectionIcon(inclinationViewModel.accelerationDirection.value))
                    inclinationViewModel.accelerationDirection.observe(itemView.context as LifecycleOwner, Observer {
                        directionIcon.setImageResource(TelemetryDirectionIconConverter().getDirectionIcon(inclinationViewModel.accelerationDirection.value))
                        ImageViewCompat.setImageTintList(directionIcon, itemView.context?.getColor(R.color.colorPrimary)?.let { ColorStateList.valueOf(it) })
                    })
                }
                TelemetryType.LeanAngle -> {
                    telemetryValue.text = inclinationViewModel.leanAngle.value.toString() + itemView.context.getString(R.string.degrees)
                    inclinationViewModel.leanAngle.observe(itemView.context as LifecycleOwner, Observer {
                        telemetryValue.text = inclinationViewModel.leanAngle.value.toString() + itemView.context.getString(R.string.degrees)
                    })

                    directionIcon.visibility = View.VISIBLE
                    directionIcon.setImageResource(TelemetryDirectionIconConverter().getDirectionIcon(inclinationViewModel.leanDirection.value))
                    inclinationViewModel.leanDirection.observe(itemView.context as LifecycleOwner, Observer {
                        directionIcon.setImageResource(TelemetryDirectionIconConverter().getDirectionIcon(inclinationViewModel.leanDirection.value))
                        ImageViewCompat.setImageTintList(directionIcon, itemView.context?.getColor(R.color.colorPrimary)?.let { ColorStateList.valueOf(it) })
                    })
                }
            }

        }

    }
}