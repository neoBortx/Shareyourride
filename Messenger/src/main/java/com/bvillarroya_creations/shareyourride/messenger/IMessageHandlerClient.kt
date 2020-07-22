package com.bvillarroya_creations.shareyourride.messenger

import java.lang.ref.WeakReference

/**
 * Generic interface used to consume message of the android message queue
 * this implementation allow us to communicate view models easy
 */
interface IMessageHandlerClient {

    companion object{
        /**
         * Singleton pattern because all viewmodels must share the shame message handler
         * in order to communicate them
         */
        private var mMessageHandlerInstance: MessageHandler? = null
    }
    /**
     *  Weak reference of the message handler client.
     *  This reference will be passed to the Message Handler class
     *  and prevent memory leaks, because the message handler will remains in the
     *  device memory until all messages of the queue are consumed, so some times
     *  the activity or the view model can't be deleted although the view has been destroyed.
     */
    val mMessageClient : WeakReference<IMessageHandlerClient>
        get() = WeakReference(this)

    /**
     * Handler that consume the message queue
     */
    val mMessageHandler: MessageHandler
        get() = MessageHandler(mMessageClient)
    /*{
            if (mMessageHandlerInstance == null)
            {
                mMessageHandlerInstance = MessageHandler(mMessageClient)
            }

            return mMessageHandlerInstance!!
        }*/

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