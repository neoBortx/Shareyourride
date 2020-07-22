package com.bvillarroya_creations.shareyourride.messenger

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import java.lang.Exception
import java.lang.ref.WeakReference

/**
 * This class receive a message from the android message queue
 * Has to be instantiated by a class that implements IMessageHandlerClient
 * IMessageHandlerClient must implement the logic of the received message
 *
 * The reason to make the message reception in this way is to avoid memory leaks:
 * This Handler class should be static or leaks might occur (anonymous android.os.Handler)
 * https://stackoverflow.com/questions/52025220/how-to-use-handler-and-handlemessage-in-kotlin
 * https://developer.android.com/reference/java/lang/ref/WeakReference
 */
class MessageHandler(private val messageClient: WeakReference<IMessageHandlerClient>) : Handler() {

    companion object{
        /**
         * List of all message handlers that are attached to the system
         */
        internal var mListOfHandlers: MutableList<MessageHandler> = mutableListOf()
    }

    /**
     * The unique identifier of the message handler
     */
    private var mHandlerId: String = ""

    /**
     * Add the current handler to the list of handlers that are listening messages
     *
     * @param id: The unique identifier of the message handler
     */
    fun attachHandler(id: String)
    {
        try {
            mHandlerId = id
            if (!mListOfHandlers.any { it.mHandlerId ==  mHandlerId})
            {
                Log.d("SYR", "SYR -> Attaching queue handler $mHandlerId")
                mListOfHandlers.add(this)
            }
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to remove handler ${ex.message} - ${ex.stackTrace}")
        }

    }

    /**
     * Remove the message handler to the list of handlers
     */
    fun removeHandler()
    {
        try {
            if (mListOfHandlers.any { it.mHandlerId ==  mHandlerId})
            {
                Log.d("SYR", "SYR -> Removing queue handler $mHandlerId")
                mListOfHandlers.remove(this)
            }
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to remove handler ${ex.message} - ${ex.stackTrace}")
        }
    }

    /**
     * convert the message object of the message queue to a easy readable object form upper layers
     * forward the message to the client
     *
     * Use the weak Reference object to process messages
     * @param msg: The message to handle
     */
    override fun handleMessage(msg: Message) {

        try {
            val messageBundleData = msg.data.getParcelable<MessageBundleData>(MessengerConstants.MessageDataKey)
            val messageKey = msg.data.getString(MessengerConstants.MessageTypeKey)

            if(messageKey != null) {
                messageClient.get()?.processMessage(MessageBundle(messageKey, messageBundleData))
            }
            else
            {
                Log.e("SYR", "SYR -> Unable to handle message from Messenger Queue because the message type is null")
            }
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to handle message ${ex.message} - ${ex.stackTrace}")
        }
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
            val msg = Message()
            val bundle = Bundle()
            bundle.putString(MessengerConstants.MessageTypeKey,message.messageKey)
            bundle.putParcelable(MessengerConstants.MessageDataKey,message.messageData)

            msg.data = bundle
            mListOfHandlers.filter { it.mHandlerId != mHandlerId}.forEach{ it.dispatchMessage(msg) }
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to send message ${ex.message} - ${ex.stackTrace}")
        }
    }
}