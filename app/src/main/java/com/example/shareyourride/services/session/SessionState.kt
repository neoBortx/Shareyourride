package com.example.shareyourride.services.session

import com.google.android.gms.common.stats.StatsEvent

/**
 * Different states that the session can have
 */
enum class SessionState(val value: Int)
{
    /**
     * No supported state
     */
    Unknown(0),

    /**
     * Session is not running
     */
    Stopped(1),

    /**
     * Calibrating gyroscopes window is shown and the procedure to calibrate them is started
     */
    CalibratingSensors(2),

    /**
     * Sensors are calibrated and the app is waiting to the user to press continue
     */
    SensorsCalibrated(3),

    /**
     * Session running
     */
    Started(4),

    /**
     * The video of the session is being created
     */
    CreatingVideo(5),

    /**
     * Session is finished
     */
    Finished(6);

    companion object {
        fun fromInt(value: Int) = SessionState.values().first { it.value == value }
    }
}