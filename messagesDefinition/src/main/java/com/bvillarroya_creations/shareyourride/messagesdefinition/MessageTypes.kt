package com.bvillarroya_creations.shareyourride.messagesdefinition

/**
 * This is a list of all kind of messages that can be sent through the message queue system
 * NOTE: These fields only inform about the sent message, they aren't used to filter
 */
class MessageTypes {
    companion object {
        /**
         * Notify that the session has been started
         *
         * Belongs to topic SESSION_COMMANDS
         *
         * @remarks this message contains a string with the session id
         */
        const val START_SESSION = "startSession"

        /**
         * Notify that the session has been stopped
         *
         * Belongs to topic SESSION_COMMANDS
         */
        const val STOP_SESSION = "stopSession"

        /**
         * Message send to telemetry view models in order to force them to save the current
         * telemetry data in the room data base
         *
         * Belongs to topic SESSION_COMMANDS
         *
         * @remarks this message contains a long with the timestamp to use to index the telemetry
         * data in the data base
         */
        const val SAVE_TELEMETRY = "saveTelemetry"
    }
}