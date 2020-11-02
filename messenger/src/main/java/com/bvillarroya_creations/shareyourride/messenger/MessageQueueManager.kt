package com.bvillarroya_creations.shareyourride.messenger

import android.os.Message
import android.util.Log
import java.util.*

/**
 * This class manages the queue system build for internal app messaging
 */
internal class MessageQueueManager {

    companion object{
        /**
         * List of all message handlers that are attached to the system
         */
        private val mListOfHandlers: MutableMap<Int,MessageHandler> = mutableMapOf()

        private val instanceId = UUID.randomUUID().toString().hashCode()

        /**
         * Add the given handler to the list of handlers that are listening messages
         * this handler won't have a filter list so it will receive all messages and will
         * send messages to all handlers
         *
         * @param messageHandler: The message handler to attach
         */
        fun attachHandler(messageHandler: MessageHandler)
        {
            try
            {
                if (!mListOfHandlers.containsKey(messageHandler.handlerId))
                {
                    Log.d("MessageQueueManager", "SYR -> Instance $instanceId, attaching queue handler ${messageHandler.handlerName} - ${messageHandler.handlerId}")
                    mListOfHandlers.set(messageHandler.handlerId,messageHandler)
                }
                else
                {
                    Log.e("MessageQueueManager", "SYR -> Instance $instanceId, the given handler is already attached to the queue system")
                }
            }
            catch (ex: Exception)
            {
                Log.e("MessageQueueManager", "SYR -> Instance $instanceId, , unable to remove handler ${ex.message}")
                ex.printStackTrace()
            }
        }

        /**
         * Remove the given message handler to the list of handlers
         *
         * @param messageHandler: Message handler to remove
         */
        fun removeHandler(messageHandler: MessageHandler)
        {
            try {
                if (mListOfHandlers.containsKey(messageHandler.handlerId))
                {
                    Log.d("MessageQueueManager", "SYR -> Instance $instanceId, removing queue handler ${messageHandler.handlerId}")
                    mListOfHandlers.remove(messageHandler.handlerId)
                }
                else
                {
                    Log.e("MessageQueueManager", "SYR -> Instance $instanceId, the given handler is not attached to the queue system")
                }
            }
            catch (ex: Exception)
            {
                Log.e("MessageQueueManager", "SYR -> Instance $instanceId, unable to remove handler ${ex.message}")
                ex.printStackTrace()
            }
        }


        /**
         * Send the given data using the message key identifier
         * and the data
         *
         * The message is sent to all message handlers attached to our custom message system
         *
         * @param message: the message to send
         * @param id: The identifier of the message handler
         * @param filter: Keyword used to send this message to handlers that are listening that kind
         * of message. Leave this field empty if you want to send this message to all handlers
         */
        fun sendMessage(id: Int, message: Message, filter: String = "")
        {
            try
            {
                if (filter.isEmpty())
                {
                    sendMessagesWithoutFilter(id, message)
                }
                else
                {
                    sendMessagesWithFilter(id, message,filter)
                }
            }
            catch (ex: Exception)
            {
                Log.e("MessageQueueManager", "SYR -> Instance $instanceId, unable to send message ${ex.message}")
                ex.printStackTrace()
            }
        }

        /**
         * Send the given data using the message key identifier
         * and the data
         *
         * The message is sent to all message handlers attached to our custom message system
         *
         * @param message: the message to send
         * @param id: The identifier of the message handler
         */
        private fun sendMessagesWithoutFilter(id: Int,message: Message)
        {
            try {
                mListOfHandlers
                    .filter { it.key != id}
                    .forEach{
                        it.value.dispatchMessage(message)
                    }
            }
            catch (ex: Exception)
            {
                Log.e("MessageQueueManager", "SYR -> Instance $instanceId, Unable to send message ${ex.message}")
                ex.printStackTrace()
            }
        }

        /**
         * Send the given data using the message key identifier
         * and the data
         *
         * The message is sent only to handlers that doesn't have any filter keyword or contains
         * the filter keyword in their list of filters
         *
         * @param message: the message to send
         * @param id: The identifier of the message handler
         * @param filter: Keyword used to send this message to handlers that are listening that kind
         * of message
         */
        private fun sendMessagesWithFilter(id: Int, message: Message, filter: String)
        {
            try {

                val filterHasCode = filter.hashCode()

                mListOfHandlers
                    .filter { it.key != id }
                    .filter{!it.value.mFilterListInt.any() ||it.value.mFilterListInt.contains(filterHasCode)}
                    .forEach{
                        it.value.dispatchMessage(message)
                    }
            }
            catch (ex: Exception)
            {
                Log.e("MessageQueueManager", "SYR -> Instance $instanceId, unable to send message ${ex.message}")
                ex.printStackTrace()
            }
        }
    }
}