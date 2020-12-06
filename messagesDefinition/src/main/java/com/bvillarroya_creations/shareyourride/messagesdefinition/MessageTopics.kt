package com.bvillarroya_creations.shareyourride.messagesdefinition

/**
 * This is the list of supported topics by the share your ride application
 * this strings are used by the queue manager to filter messages and send them
 * only to clients that are attached to this topic
 */
class MessageTopics {
    companion object{
        /**
         * For messages related to the user session
         *
         * - START_SESSION
         * - END_SESSION
         * - CONTINUE_SESSION
         * - DISCARD_SESSION
         * - SESSION_STATE_REQUEST
         * - SESSION_STATE_EVENT
         *
         */
        const val SESSION_COMMANDS = "sessionCommands"

        /**
         * Messages related to the control of the session
         *
         * - START_ACQUIRING_DATA
         * - STOP_ACQUIRING_DATA
         * - SAVE_TELEMETRY
         * - UPDATE_TELEMETRY
         */
        const val SESSION_CONTROL = "sessionControl"

        /**
         * Messages related to the GPS system
         *
         * - GPS_STATE_REQUEST
         * - GPS_STATE_EVENT
         * - GPS_DATA_EVENT
         */
        const val GPS_DATA = "gpsData"

        /**
         * Messages related to the inclination data
         *
         * - INCLINATION_DATA_EVENT
         * - INCLINATION_CALIBRATION_START
         * - INCLINATION_CALIBRATION_END
         */
        const val INCLINATION_DATA = "inclinationData"

        /**
         * Messages related to the inclination control
         *
         * - INCLINATION_CALIBRATION_START
         * - INCLINATION_CALIBRATION_END
         */
        const val INCLINATION_CONTROL = "inclinationControl"

        /**
         * Messages related to the video flow
         *
         * - VIDEO_STATE_REQUEST
         * - VIDEO_STATE_EVENT
         * - VIDEO_DISCARD_COMMAND
         */
        const val VIDEO_DATA = "videoData"

        /**
         * Messages related to the video creation flow
         *
         * - VIDEO_CREATION_COMMAND
         * - VIDEO_CREATION_DATA
         */
        const val VIDEO_CREATION_DATA = "videoCreationData"
    }
}