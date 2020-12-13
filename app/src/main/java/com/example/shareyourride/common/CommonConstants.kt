package com.example.shareyourride.common

import android.content.Context
import com.bvillarroya_creations.shareyourride.R
import com.example.shareyourride.configuration.SettingPreferencesGetter
import com.example.shareyourride.configuration.SettingPreferencesIds

class CommonConstants {

    //region constants
    companion object
    {
        /**
         * To convert meters/second to kilometers/hour
         */
        private const val KILOMETERS_PER_HOUR_CONVERTER = 3.6

        /**
         * To convert meters/second to miles/hour
         */
        private const val MILES_PER_HOUR_CONVERTER = 2.23694

        /**
         * Value to multiply to convert from meters to feet
         */
        private const val FOOT_CONVERTER = 3.28084

        /**
         * To convert from meters to meters (do nothing)
         */
        private const val METER_CONVERTER = 1.0

        /**
         * Value to multiply to convert from meters/seconds^2 to feet/seconds^2
         */
        private const val FOOT_PER_SECOND2_CONVERTER = 3.28084

        /**
         *
         */
        private const val METERS_PER_SECOND2_CONVERTER = 1.0

        /**
         *
         */
        private const val DISTANCE_CHANGE_UNITS_KILOMETERS = 1000.0

        /**
         *
         */
        private const val DISTANCE_CHANGE_UNITS_MILES = 1609.34

        /**
         * The acceleration of the gravity in the earth
         */
        const val GRAVITY_ACCELERATION = 9.80665

        /**
         * Minimum managed video delay increments
         */
        const val VIDEO_DELAY_SEGMENT: Int = 100


        fun getSpeedText(context: Context): String
        {
            return if (SettingPreferencesGetter(context).getStringOption(SettingPreferencesIds.UnitSystem) == "imperial")
            {
                context.getString(R.string.miles_hour_unit)
            }
            else
            {
                context.getString(R.string.kilometers_hour_unit)
            }
        }

        fun getShortDistanceText(context: Context): String
        {
            return if (SettingPreferencesGetter(context).getStringOption(SettingPreferencesIds.UnitSystem) == "imperial")
            {
                context.getString(R.string.feet_unit)
            }
            else
            {
                context.getString(R.string.meters_unit)
            }
        }

        fun getLongDistanceText(context: Context): String
        {
            return if (SettingPreferencesGetter(context).getStringOption(SettingPreferencesIds.UnitSystem) == "imperial")
            {
                context.getString(R.string.miles_unit)
            }
            else
            {
                context.getString(R.string.kilometers_unit)
            }
        }

        fun getAccelerationText(context: Context): String
        {
            /*return if (SettingPreferencesGetter(context).getStringOption(SettingPreferencesIds.UnitSystem) == "imperial")
            {
                context.getString(R.string.feet_per_seconds_unit)
            }
            else
            {
                context.getString(R.string.meters_per_seconds_unit)
            }*/
            
            return context.getString(R.string.gravity_force)
        }


        fun getSpeedConverter(context: Context): Double
        {
            return if (SettingPreferencesGetter(context).getStringOption(SettingPreferencesIds.UnitSystem) == "imperial")
            {
                MILES_PER_HOUR_CONVERTER
            }
            else
            {
                KILOMETERS_PER_HOUR_CONVERTER
            }
        }

        fun getAccelerationConverter(context: Context): Double
        {
            return if (SettingPreferencesGetter(context).getStringOption(SettingPreferencesIds.UnitSystem) == "imperial")
            {
                FOOT_PER_SECOND2_CONVERTER
            }
            else
            {
                METERS_PER_SECOND2_CONVERTER
            }
        }

        fun getShortDistanceConverter(context: Context): Double
        {
            return if (SettingPreferencesGetter(context).getStringOption(SettingPreferencesIds.UnitSystem) == "imperial")
            {
                FOOT_CONVERTER
            }
            else
            {
                METER_CONVERTER
            }
        }

        fun getLongDistanceConverter(context: Context): Double
        {
            return if (SettingPreferencesGetter(context).getStringOption(SettingPreferencesIds.UnitSystem) == "imperial")
            {
                DISTANCE_CHANGE_UNITS_MILES
            }
            else
            {
                DISTANCE_CHANGE_UNITS_KILOMETERS
            }
        }

    }
    //endregion


}