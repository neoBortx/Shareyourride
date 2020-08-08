package com.bvillarroya_creations.shareyourride.messagesdefinition

/**
 * This is the list of supported topics by the share your ride application
 * this strings are used to filter messages in the messaging queue system
 */
class MessageTopics {
    companion object{
        /**
         * For messages related to the user session
         */
        const val SESSION_COMMANDS = "sessionCommands"
    }
}