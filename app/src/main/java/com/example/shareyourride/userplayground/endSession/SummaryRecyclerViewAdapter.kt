/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.userplayground.endSession

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bvillarroya_creations.shareyourride.R
import com.bvillarroya_creations.shareyourride.messagesdefinition.dataBundles.SessionSummaryData
import com.example.shareyourride.common.CommonConstants
import com.example.shareyourride.common.CommonConstants.Companion.GRAVITY_ACCELERATION
import com.example.shareyourride.services.session.SummaryTelemetryType
import com.example.shareyourride.viewmodels.userplayground.SessionViewModel
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class SummaryRecyclerViewAdapter(private val dataList: Array<SummaryTelemetryType>,
                                 private val sessionViewModel: SessionViewModel) : RecyclerView.Adapter<SummaryRecyclerViewAdapter.CustomViewHolder>() {

     // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_summary_telemetry_data, parent, false)
        // set the view's size, margins, padding and layout parameters

        return CustomViewHolder(view, sessionViewModel)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    // Return the size of your data set (invoked by the layout manager)
    override fun getItemCount() = dataList.size

    inner class CustomViewHolder(view: View, private val sessionViewModel: SessionViewModel) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(summaryTelemetry: SummaryTelemetryType) {

            val sessionSummary: SessionSummaryData

            if (sessionViewModel.sessionSummaryData.value != null) {
                sessionSummary= sessionViewModel.sessionSummaryData.value!!
            }
            else
            {
                return
            }

            itemView.findViewById<ImageView>(R.id.telemetry_icon_summary).setImageResource(ResultIconConverter().getIcon(summaryTelemetry))
            ImageViewCompat.setImageTintList(itemView.findViewById(R.id.telemetry_icon_summary), itemView.context.getColor(R.color.colorPrimary).let { ColorStateList.valueOf(it) })


            when (summaryTelemetry)
            {
                SummaryTelemetryType.Duration -> {
                    itemView.findViewById<TextView>(R.id.telemetry_kind_summary).text = itemView.context.getString(R.string.total)

                    itemView.findViewById<TextView>(R.id.telemetry_value_summary).text = String.format("%02d:%02d:%02d",
                                  TimeUnit.MILLISECONDS.toHours(sessionSummary.duration),
                                  TimeUnit.MILLISECONDS.toMinutes(sessionSummary.duration) -
                                          TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours( sessionSummary.duration)), // The change is in this line
                                  TimeUnit.MILLISECONDS.toSeconds(sessionSummary.duration) -
                                          TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sessionSummary.duration)))
                }

                SummaryTelemetryType.Distance -> {
                    itemView.findViewById<TextView>(R.id.telemetry_kind_summary).text = itemView.context.getString(R.string.total)

                    if(sessionSummary.distance< CommonConstants.getLongDistanceConverter(itemView.context))
                    {
                        val distance = sessionSummary.distance.times(CommonConstants.getShortDistanceConverter(itemView.context))
                        itemView.findViewById<TextView>(R.id.telemetry_value_summary).text = distance.roundToInt().toString() + CommonConstants.getShortDistanceText(itemView.context)
                    }
                    else
                    {
                        val distance = sessionSummary.distance/CommonConstants.getLongDistanceConverter(itemView.context)
                        itemView.findViewById<TextView>(R.id.telemetry_value_summary).text =  DecimalFormat("####.###").format(distance).toString() + CommonConstants.getLongDistanceText(itemView.context)
                    }
                }

                SummaryTelemetryType.AverageMaxSpeed -> {
                    itemView.findViewById<TextView>(R.id.telemetry_kind_summary).text = itemView.context.getString(R.string.average)
                    itemView.findViewById<TextView>(R.id.telemetry_value_summary).text = (sessionSummary.averageSpeed.times(CommonConstants.getSpeedConverter(itemView.context))).roundToInt().toString() + CommonConstants.getSpeedText(itemView.context)
                }
                
                SummaryTelemetryType.MaxSpeed ->  {
                    itemView.findViewById<TextView>(R.id.telemetry_kind_summary).text = itemView.context.getString(R.string.maximum)
                    itemView.findViewById<TextView>(R.id.telemetry_value_summary).text = (sessionSummary.maxSpeed.times(CommonConstants.getSpeedConverter(itemView.context))).roundToInt().toString() + CommonConstants.getSpeedText(itemView.context)
                }
                
                SummaryTelemetryType.MaxAcceleration -> {
                    itemView.findViewById<TextView>(R.id.telemetry_kind_summary).text = itemView.context.getString(R.string.maximum)
                    itemView.findViewById<TextView>(R.id.telemetry_value_summary).text =  DecimalFormat("##.#").format((sessionSummary.maxAcceleration/GRAVITY_ACCELERATION).absoluteValue).toString() + CommonConstants.getAccelerationText(itemView.context)
                }

                SummaryTelemetryType.MaxLeftLeanAngle -> {
                    itemView.findViewById<TextView>(R.id.telemetry_kind_summary).text = itemView.context.getString(R.string.maxLeft)
                    itemView.findViewById<TextView>(R.id.telemetry_value_summary).text = sessionSummary.maxLeftLeanAngle.absoluteValue.toString() + itemView.context.getString(R.string.degrees)

                    itemView.findViewById<ImageView>(R.id.direction_icon_summary).setImageResource(R.drawable.left)
                    itemView.findViewById<ImageView>(R.id.direction_icon_summary).visibility = View.VISIBLE
                    ImageViewCompat.setImageTintList(itemView.findViewById(R.id.direction_icon_summary), itemView.context.getColor(R.color.colorPrimary).let { ColorStateList.valueOf(it) })
                }

                SummaryTelemetryType.MaxRightLeanAngle -> {
                    itemView.findViewById<TextView>(R.id.telemetry_kind_summary).text = itemView.context.getString(R.string.maxRight)
                    itemView.findViewById<TextView>(R.id.telemetry_value_summary).text = sessionSummary.maxRightLeanAngle.absoluteValue.toString() + itemView.context.getString(R.string.degrees)

                    itemView.findViewById<ImageView>(R.id.direction_icon_summary).setImageResource(R.drawable.right)
                    itemView.findViewById<ImageView>(R.id.direction_icon_summary).visibility = View.VISIBLE
                    ImageViewCompat.setImageTintList(itemView.findViewById(R.id.direction_icon_summary), itemView.context.getColor(R.color.colorPrimary).let { ColorStateList.valueOf(it) })
                }
                SummaryTelemetryType.MaxAltitude -> {
                    itemView.findViewById<TextView>(R.id.telemetry_kind_summary).text = itemView.context.getString(R.string.maximum)
                    itemView.findViewById<TextView>(R.id.telemetry_value_summary).text = sessionSummary.maxAltitude.times(CommonConstants.getShortDistanceConverter(itemView.context)).roundToInt().toString() + CommonConstants.getShortDistanceText(itemView.context)

                    itemView.findViewById<ImageView>(R.id.direction_icon_summary).setImageResource(R.drawable.front)
                    itemView.findViewById<ImageView>(R.id.direction_icon_summary).visibility = View.VISIBLE
                    ImageViewCompat.setImageTintList(itemView.findViewById(R.id.direction_icon_summary), itemView.context.getColor(R.color.colorPrimary).let { ColorStateList.valueOf(it) })
                }

                SummaryTelemetryType.MinAltitude -> {
                    itemView.findViewById<TextView>(R.id.telemetry_kind_summary).text = itemView.context.getString(R.string.minimum)
                    itemView.findViewById<TextView>(R.id.telemetry_value_summary).text = sessionSummary.minAltitude.times(CommonConstants.getShortDistanceConverter(itemView.context)).roundToInt().toString() + CommonConstants.getShortDistanceText(itemView.context)

                    itemView.findViewById<ImageView>(R.id.direction_icon_summary).setImageResource(R.drawable.back)
                    itemView.findViewById<ImageView>(R.id.direction_icon_summary).visibility = View.VISIBLE
                    ImageViewCompat.setImageTintList(itemView.findViewById(R.id.direction_icon_summary), itemView.context.getColor(R.color.colorPrimary).let { ColorStateList.valueOf(it) })
                }

                SummaryTelemetryType.MaxUphillTerrainInclination -> {
                    itemView.findViewById<TextView>(R.id.telemetry_kind_summary).text = itemView.context.getString(R.string.maxUphill)
                    itemView.findViewById<TextView>(R.id.telemetry_value_summary).text = sessionSummary.maxUphillTerrainInclination.toString() + itemView.context.getString(R.string.percentage)

                    itemView.findViewById<ImageView>(R.id.direction_icon_summary).setImageResource(R.drawable.front)
                    itemView.findViewById<ImageView>(R.id.direction_icon_summary).visibility = View.VISIBLE
                    ImageViewCompat.setImageTintList(itemView.findViewById(R.id.direction_icon_summary), itemView.context.getColor(R.color.colorPrimary).let { ColorStateList.valueOf(it) })
                }

                SummaryTelemetryType.MaxDownhillTerrainInclination -> {
                    itemView.findViewById<TextView>(R.id.telemetry_kind_summary).text = itemView.context.getString(R.string.maxDownhill)
                    itemView.findViewById<TextView>(R.id.telemetry_value_summary).text = sessionSummary.maxDownhillTerrainInclination.toString() + itemView.context.getString(R.string.percentage)

                    itemView.findViewById<ImageView>(R.id.direction_icon_summary).setImageResource(R.drawable.back)
                    itemView.findViewById<ImageView>(R.id.direction_icon_summary).visibility = View.VISIBLE
                    ImageViewCompat.setImageTintList(itemView.findViewById(R.id.direction_icon_summary), itemView.context.getColor(R.color.colorPrimary).let { ColorStateList.valueOf(it) })
                }

                SummaryTelemetryType.AverageTerrainInclination -> {
                    itemView.findViewById<TextView>(R.id.telemetry_kind_summary).text = itemView.context.getString(R.string.average)
                    itemView.findViewById<TextView>(R.id.telemetry_value_summary).text = sessionSummary.averageTerrainInclination.toString() + itemView.context.getString(R.string.percentage)
                }
            }
        }
    }
}