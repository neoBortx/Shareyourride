package com.example.shareyourride.services.session

/**
 * Session context sendt between layers
 */
class SessionData {

    var state: SessionState = SessionState.Unknown
    var sensorCalibrated: Boolean = false
}