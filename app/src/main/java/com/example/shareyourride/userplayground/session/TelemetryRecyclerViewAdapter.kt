package com.example.shareyourride.userplayground.session

import android.content.res.ColorStateList
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
import com.example.shareyourride.services.session.TelemetryType
import com.example.shareyourride.viewmodels.userplayground.InclinationViewModel
import com.example.shareyourride.viewmodels.userplayground.LocationViewModel
import java.text.DecimalFormat

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

        fun bind(telemetry: TelemetryType) {
            itemView.findViewById<ImageView>(R.id.telemetry_icon).setImageResource(TelemetryIconConverter().getIcon(telemetry))
            ImageViewCompat.setImageTintList(itemView.findViewById(R.id.telemetry_icon), itemView.context?.getColor(R.color.colorPrimary)?.let { ColorStateList.valueOf(it) })


            when (telemetry)
            {
                TelemetryType.Altitude -> {
                    itemView.findViewById<TextView>(R.id.telemetry_value).text = locationViewModel.altitude.value.toString().format() + itemView.context.getString(R.string.meters_unit)
                    locationViewModel.altitude.observe(itemView.context as LifecycleOwner, Observer {
                        itemView.findViewById<TextView>(R.id.telemetry_value).text = locationViewModel.altitude.value.toString() + itemView.context.getString(R.string.meters_unit)
                    })
                }
                TelemetryType.Distance -> {
                    if(locationViewModel.distance.value!! < 1000)
                    {
                        itemView.findViewById<TextView>(R.id.telemetry_value).text = locationViewModel.distance.value.toString() + itemView.context.getString(R.string.meters_unit)
                    }
                    else
                    {
                        val distance = locationViewModel.distance.value!!.toDouble()/1000
                        itemView.findViewById<TextView>(R.id.telemetry_value).text = distance.toString() + itemView.context.getString(R.string.kilometers_unit)
                    }
                    locationViewModel.distance.observe(itemView.context as LifecycleOwner, Observer {
                        if(locationViewModel.distance.value!! < 1000)
                        {
                            itemView.findViewById<TextView>(R.id.telemetry_value).text = locationViewModel.distance.value.toString() + itemView.context.getString(R.string.meters_unit)
                        }
                        else
                        {
                            val distance = locationViewModel.distance.value!!.toDouble()/1000
                            itemView.findViewById<TextView>(R.id.telemetry_value).text = distance.toString() + itemView.context.getString(R.string.kilometers_unit)
                        }
                    })
                }
                TelemetryType.TerrainInclination -> {
                    itemView.findViewById<TextView>(R.id.telemetry_value).text = locationViewModel.terrainInclination.value.toString()  + itemView.context.getString(R.string.percentage)
                    locationViewModel.terrainInclination.observe(itemView.context as LifecycleOwner, Observer {
                        itemView.findViewById<TextView>(R.id.telemetry_value).text = locationViewModel.terrainInclination.value.toString() + itemView.context.getString(R.string.percentage)
                    })
                }
                TelemetryType.Speed -> {
                    itemView.findViewById<TextView>(R.id.telemetry_value).text = locationViewModel.speed.value.toString() + itemView.context.getString(R.string.kilometers_hour_unit)
                    locationViewModel.speed.observe(itemView.context as LifecycleOwner, Observer {
                        itemView.findViewById<TextView>(R.id.telemetry_value).text = locationViewModel.speed.value.toString() + itemView.context.getString(R.string.kilometers_hour_unit)
                    })
                }
                TelemetryType.Acceleration -> {
                    itemView.findViewById<TextView>(R.id.telemetry_value).text = DecimalFormat("##.#").format(inclinationViewModel.acceleration.value).toString() + itemView.context.getString(R.string.meters_per_seconds_unit)
                    inclinationViewModel.acceleration.observe(itemView.context as LifecycleOwner, Observer {
                        itemView.findViewById<TextView>(R.id.telemetry_value).text = DecimalFormat("##.#").format(inclinationViewModel.acceleration.value).toString() + itemView.context.getString(R.string.meters_per_seconds_unit)
                    })

                    itemView.findViewById<ImageView>(R.id.direction_icon).visibility = View.VISIBLE
                    itemView.findViewById<ImageView>(R.id.direction_icon).setImageResource(TelemetryDirectionIconConverter().getDirectionIcon(inclinationViewModel.accelerationDirection.value))
                    inclinationViewModel.accelerationDirection.observe(itemView.context as LifecycleOwner, Observer {
                        itemView.findViewById<ImageView>(R.id.direction_icon).setImageResource(TelemetryDirectionIconConverter().getDirectionIcon(inclinationViewModel.accelerationDirection.value))
                        ImageViewCompat.setImageTintList(itemView.findViewById(R.id.direction_icon), itemView.context?.getColor(R.color.colorPrimary)?.let { ColorStateList.valueOf(it) })
                    })
                }
                TelemetryType.LeanAngle -> {
                    itemView.findViewById<TextView>(R.id.telemetry_value).text = inclinationViewModel.leanAngle.value.toString() + itemView.context.getString(R.string.degrees)
                    inclinationViewModel.leanAngle.observe(itemView.context as LifecycleOwner, Observer {
                        itemView.findViewById<TextView>(R.id.telemetry_value).text = inclinationViewModel.leanAngle.value.toString() + itemView.context.getString(R.string.degrees)
                    })

                    itemView.findViewById<ImageView>(R.id.direction_icon).visibility = View.VISIBLE
                    itemView.findViewById<ImageView>(R.id.direction_icon).setImageResource(TelemetryDirectionIconConverter().getDirectionIcon(inclinationViewModel.leanDirection.value))
                    inclinationViewModel.leanDirection.observe(itemView.context as LifecycleOwner, Observer {
                        itemView.findViewById<ImageView>(R.id.direction_icon).setImageResource(TelemetryDirectionIconConverter().getDirectionIcon(inclinationViewModel.leanDirection.value))
                        ImageViewCompat.setImageTintList(itemView.findViewById(R.id.direction_icon), itemView.context?.getColor(R.color.colorPrimary)?.let { ColorStateList.valueOf(it) })
                    })
                }
            }

        }

    }
}