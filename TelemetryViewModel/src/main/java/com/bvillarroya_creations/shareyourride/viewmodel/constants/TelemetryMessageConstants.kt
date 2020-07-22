package com.bvillarroya_creations.shareyourride.viewmodel.constants

/**
 * Collection of messages that are sent between view models
 */
class TelemetryMessageConstants {

    companion object {
        public enum class MessageType {
            /**
             * No supported value
             */
            None,

            /**
             * Notify the telemetry view models that the session is about to start
             * Must contains the identifier of the session
             */
            StartSession,

            /**
             * Notify the telemetry view models that the session is about to stop
             * Must contains the identfier of the session
             */
            StopSession,
        }
    }
}