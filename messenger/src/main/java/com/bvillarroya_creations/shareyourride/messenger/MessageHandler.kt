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

    /**
     * The unique identifier of the message handler
     * Use an integer to speed-up uperations
     */
    var handlerId: Int = 0

    /**
     * List of keyword that are used to filter messages
     */
    private val mFilterListString = mutableListOf<String>()

    /**
     * List of keyword converted to hash to filter messages
     * This is the list used, because int comparison is faster that
     * the int comparison
     */
    internal val mFilterListInt = mutableListOf<Int>()

    /**
     * Add the current handler to the list of handlers that are listening messages
     * This handler will have a list of keywords used to filter messages at send time
     * A handler with filter keyword only will send messages to handlers that contains at least
     * one similar keyword
     *
     * @param id: The unique identifier of the message handler
     * @param filterKeyWordList: the list of keyword used to filter messages and sent and receive time
     */
    internal fun attachHandler(id: Int, filterKeyWordList: List<String> = mutableListOf())
    {
        handlerId = id

        if (filterKeyWordList.any())
        {
            mFilterListString.addAll(filterKeyWordList)
            filterKeyWordList.forEach { mFilterListInt.add(it.hashCode()) }
        }

        MessageQueueManager.attachHandler(this)
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
            Log.e("SYR", "SYR -> Unable to handle message ${ex.message}")
            ex.printStackTrace()
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
    internal fun sendMessage(message: MessageBundle)
    {
        try {
            val msg = Message()
            val bundle = Bundle()
            bundle.putString(MessengerConstants.MessageTypeKey,message.messageKey)
            bundle.putParcelable(MessengerConstants.MessageDataKey,message.messageData)

            msg.data = bundle
            MessageQueueManager.sendMessage(handlerId,msg,message.messageFilter)
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to send message ${ex.message}")
            ex.printStackTrace()
        }
    }
}