/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.messagesdefinition.dataBundles

/**
 * Holds the result of the calibration process
 */
class InclinationCalibrationData(
    /**
     * True: the device can be calibrated
     * False: the device can't be calibrated
     */
    val result: Boolean,

    /**
     * Angle of rotation about the y axis
     */
    val roll: Int,

    /**
     * Angle of rotation about the x axis.
     */
    val pitch: Int,

    /**
     * Angle of rotation about the -z axis.
     */
    val azimuth: Int,

    /**
     * the acceleration of the mobile phone in three axis
     */
    val linealAcceleration: FloatArray


)