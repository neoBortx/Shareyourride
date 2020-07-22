package com.bvillarroya_creations.shareyourride.messenger

/**
 * Represent the bundle of data to send inside of a message
 * This class make easy the action of send and receive message making
 * transparent the way of handling data of the android message handler
 */
class MessageBundle ()
{

    /**
     * The idenfier of the message
     * This ID will rule the content of the messageData field
     */
    var messageKey: String = ""

    /**
     * The data to send in the message
     * This object can be null
     */
    lateinit var messageData: MessageBundleData

    /**
     * Constructor that creates the MessageBundleData
     *
     * @param key: the type of message
     * @param data: the content to send
     */
    constructor(key: String, data: Any?) : this() {
        messageKey = key
        if (data != null)
        {
            messageData = MessageBundleData()
            messageData.type = data::class
            messageData.data = data
        }
    }

    /**
     * Constructor that requires a MessageBundleData
     *
     * @param key: the type of message
     * @param data: the content to send
     */
    constructor(key: String, data: MessageBundleData?) : this() {
        messageKey = key
        if (data != null)
        {
            messageData = data
        }
    }
}