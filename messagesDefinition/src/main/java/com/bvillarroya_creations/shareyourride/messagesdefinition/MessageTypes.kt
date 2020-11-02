package com.bvillarroya_creations.shareyourride.messagesdefinition

/**
 * This is a list of all kind of messages that can be sent through the message queue system
 * NOTE: These fields only inform about the sent message, clients must implement the logic
 * using this parameter
 */
class MessageTypes {
    companion object {

        //region session SESSION_COMMANDS
        /**
         * Notify that the session has been started
         *
         * Belongs to topic SESSION_COMMANDS
         *
         * @remarks this message contains a string with the session id
         *
         * View model -> Service
         */
        const val START_SESSION = "startSession"

        /**
         * Notify that the session has been stopped
         *
         * Belongs to topic SESSION_COMMANDS
         * VIew model -> Service
         */
        const val END_SESSION = "stopSession"

        /**
         * Notify that the session has been stopped and discarded
         *
         * Belongs to topic SESSION_COMMANDS
         * View model -> Service
         */
        const val DISCARD_SESSION = "discardSession"

        /**
         * Notify that the session has been stopped before start recording
         *
         * Belongs to topic SESSION_COMMANDS
         * View model -> Service
         */
        const val CANCEL_SESSION = "cancelSession"

        /**
         * Notify that the user has pressed the continue button after calibrating sensors
         *
         * Belongs to topic SESSION_COMMANDS
         * View model -> Service
         */
        const val CONTINUE_SESSION = "continueSession"

        /**
         * Notify that the user has pressed the retry calibration button
         *
         * Belongs to topic SESSION_COMMANDS
         * View model -> Service
         */
        const val RETRY_CALIBRATION = "retryCalibration"


        /**
         * Request the state of the session
         *
         * Belongs to topic SESSION_COMMANDS
         * VIew model -> Service
         */
        const val SESSION_STATE_REQUEST = "sessionStateRequest"

        /**
         * Notify the state session has been started
         *
         * Belongs to topic SESSION_COMMANDS
         *
         * @remarks this message contains a enum with the session state
         * service -> view model
         */
        const val SESSION_STATE_EVENT = "sessionStateEvent"
        //endregion

        //region SESSION_CONTROL
        /**
         * Message to command services to start acquiring data from the sensors
         *
         * Belongs to topic SESSION_CONTROL
         *
         * @remarks this message contains the id of the session
         *
         * SESSION -> The rest of services
         */
        const val START_ACQUIRING_DATA = "startAcquiringData"

        /**
         * Message to command services to stops acquiring data from the sensors
         *
         * Belongs to topic SESSION_CONTROL
         *
         * @remarks this message contains the id of the session
         *
         * SESSION -> The rest of services
         */
        const val STOP_ACQUIRING_DATA = "stopAcquiringData"

        /**
         * Message send to telemetry view models in order to force them to save the current
         * telemetry data in the room data base
         *
         * Belongs to topic SESSION_CONTROL
         *
         * @remarks this message contains a long with the timestamp to use to index the telemetry
         * data in the data base
         *
         * SESSION -> The rest of services
         */
        const val SAVE_TELEMETRY = "saveTelemetry"

        /**
         * Message send to telemetry services in order to command them to send their telemetry
         * to the view
         *
         * Belongs to topic SESSION_COMMANDS
         *
         * @remarks this message is empty
         *
         * SESSION -> The rest of services
         */
        const val UPDATE_TELEMETRY = "updateTelemetry"
        //endregion

        //region GPS_DATA
        /**
         * Notifies the state of the GPS, if it is running or not
         *
         * Belongs to topic GPS_DATA
         * @remarks this message contains a boolean true if the service is running or false if not
         *
         * View -> service
         */
        const val GPS_STATE_REQUEST = "gpsStateRequest"

        /**
         * Notifies the state of the GPS, if it is running or not
         *
         * Belongs to topic GPS_DATA
         * @remarks this message contains a boolean true if the service is running or false if not
         *
         * Service -> View
         */
        const val GPS_STATE_EVENT = "gpsStateEvent"

        /**
         * Notifies the data of the GPS
         *
         * Belongs to topic GPS_DATA
         * @remarks this message contains a Location object
         *
         * Service -> View
         */
        const val GPS_DATA_EVENT = "gpsDataEvent"
        //endregion

        //region VIDEO_DATA
        /**
         * Notifies the state of the GPS, if it is running or not
         *
         * Belongs to topic VIDEO_DATA
         * @remarks this message contains a boolean true if the service is running or false if not
         *
         * View -> video service
         */
        const val VIDEO_STATE_REQUEST = "videoStateRequest"

        /**
         * Notifies the state of the video, if it is a stream available or not
         *
         * Belongs to topic VIDEO_DATA
         * @remarks this message contains a boolean true if the video stream is available or false if not
         *
         * Video service -> View
         */
        const val VIDEO_STATE_EVENT = "videoStateEvent"

        /**
         * Notifies the state of the video, if it is a stream available or not
         *
         * Belongs to topic VIDEO_DATA
         * @remarks this message contains a boolean true if the video stream is available or false if not
         *
         * Session service to video service
         */
        const val VIDEO_DISCARD_COMMAND = "videoDiscardCommand"
        //endregion

        //region INCLINATION_DATA
        /**
         * Notifies the current values obtained by the gyroscopes and accelerometers
         *
         * belongs to topic INCLINATION_DATA
         *
         * inclination service -> view
         */
        const val INCLINATION_DATA_EVENT = "inclinationDataEvent"

        /**
         * Command to the inclination service to start the sensor calibration
         *
         * belongs to topic INCLINATION_DATA
         *
         * view -> Inclination service
         */
        const val INCLINATION_CALIBRATION_START = "inclinationCalibrationStart"

        /**
         * Notifies that the synchronization service has finished
         *
         * belongs to topic INCLINATION_DATA
         *
         * Inclination service -> service session
         *
         * @remarks This message contains the object InclinationCalibrationData
         */
        const val INCLINATION_CALIBRATION_END = "inclinationCalibrationEnd"
        //endregion
    }
}