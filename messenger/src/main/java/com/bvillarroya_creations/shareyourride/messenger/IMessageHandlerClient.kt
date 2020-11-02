package com.bvillarroya_creations.shareyourride.messenger

import android.util.Log
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.*

/**
 * Generic interface used to consume message of the android message queue
 * this implementation allow us to communicate independent modules between them
 *
 * Is a good practice to not add dependencies between modules to maje the application scalable
 * and easy modifiable. Taking this in mind, I develop an easy to use internal message broker, that
 * allow different parts to interchange data and command actions without adding any dependency
 * between then.
 */
interface IMessageHandlerClient {

    /**
     * Handler that consume the message queue
     */
    var messageHandler: MessageHandler

    /**
     * Create and Add a new message handler to the list of handlers that are listening messages
     * This handler will have a list of keywords used to filter messages at send time
     * A handler with filter keyword only will send messages to handlers that contains at least
     * one similar keyword
     *
     * @param name: The name of the handler, for login and debugging purposes
     * @param filterKeyWordList: the list of keyword used to filter messages and sent and receive time
     */
    fun createMessageHandler(name: String, filterKeyWordList: List<String> = mutableListOf())
    {
        /**
         *  Weak reference of the message handler client.
         *  This reference will be passed to the Message Handler class
         *  and prevent memory leaks, because the message handler will remains in the
         *  device memory until all messages of the queue are consumed, so some times
         *  the activity or the view model can't be deleted although the view has been destroyed.
         */
        messageHandler = MessageHandler( WeakReference(this))
        messageHandler.attachHandler(UUID.randomUUID().toString().hashCode(),name, filterKeyWordList)
    }

    /**
     * Send the given data using the message key identifier
     * and the data
     *
     * The message is sent to all message handlers attached to our custom message system
     *
     * @param message: Bundle with the message type, the message data and the message data type
     */
    fun sendMessage(message: MessageBundle)
    {
        try {
            messageHandler.sendMessage(message)
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to send the message ${ex.message}")
            ex.printStackTrace()
        }

    }

    /**
     * Remove the message handler to the list of handlers
     */
    fun removeHandler()
    {
        try {
            MessageQueueManager.removeHandler(messageHandler)
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to remove handler ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Process the given message
     * The recommended body of the message is the following:
     *
     * First field: MessageType
     * Second field: Content
     *
     * Types of messages must be defined in other module in order to leave this
     * module generic and reuse it in other projects
     * the content of the message depends on the message type
     *
     * @param msg: The received message
     */
    fun processMessage(msg: MessageBundle)
}