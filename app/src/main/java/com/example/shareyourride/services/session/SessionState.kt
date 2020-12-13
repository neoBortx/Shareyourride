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
     * Thw window to synchronize the video with the telemetry has been shown and the system
     * is waiting to the user to click in next button
     */
    SynchronizingVideo(2),
    /**
     * Calibrating gyroscopes window is shown and the procedure to calibrate them is started
     */
    CalibratingSensors(3),

    /**
     * Sensors are calibrated and the app is waiting to the user to press continue
     */
    SensorsCalibrated(4),

    /**
     * Session running
     */
    Started(5),

    /**
     * The video of the session is being created
     */
    CreatingVideo(6),

    /**
     * Session is finished
     */
    Finished(7);

    companion object {
        fun fromInt(value: Int) = SessionState.values().first { it.value == value }
    }
}