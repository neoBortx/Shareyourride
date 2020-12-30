/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.bvillarroya_creations.shareyourride.messenger

/**
 * Represent the bundle of data to send inside of a message
 * This class make easy the action of send and receive message making
 * transparent the way of handling data of the android message handler
 */
class MessageBundle ()
{

    /**
     * The identifier of the message
     * This ID will rule the content of the messageData field
     */
    var messageKey: String = ""

    /**
     * This is a string used to filter messages at send time
     * Message with a filter value will be sent just to handlers that contains that
     * filter in their filter list
     */
    var messageFilter: String = ""

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
     * @param filter: keyword to send this message to clients that are listening to this
     * this kind of messages
     */
    constructor(key: String, data: Any?, filter: String) : this() {
        messageKey = key
        messageData = MessageBundleData()
        messageFilter = filter

        if (data != null)
        {
            messageData.type = data::class
            messageData.data = data
        }
        else
        {
            messageData.type = String::class
            messageData.data = ""
        }
    }

    /**
     * Constructor that requires a MessageBundleData
     *
     * @param key: the type of message
     * @param data: the content to send
     * @param filter: keyword to send this message to handlers that are listening to this
     * this kind of messages
     *
     */
    constructor(key: String, data: MessageBundleData, filter: String) : this() {
        messageKey = key
        messageFilter = filter
        messageData = data
    }
}