package com.bvillarroya_creations.shareyourride.messenger

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import java.lang.Exception
import kotlin.reflect.KClass

class MessageBundleData(): Parcelable {

    /**
     * The type of content
     */
   lateinit var type: KClass<out Any>

    /**
     * the content of the message
     */
    lateinit var data: Any

    /**
     *
     */
    constructor(parcel: Parcel) : this() {
        try
        {
            val typeString = parcel.readString()
            if(typeString != null) {
                type = Class.forName(typeString).kotlin

                when (type) {
                    Int::class -> {
                        data = parcel.readInt()
                    }
                    String::class -> {
                        data = parcel.readString()!!
                    }
                    Double::class -> {
                        data = parcel.readDouble()
                    }
                    Float::class -> {
                        data = parcel.readFloat()
                    }
                    else -> {
                        if (typeString == "" && type is Parcelable) {
                            val content: Parcelable? = parcel.readParcelable(type.java.classLoader)
                            data = content as Any
                        } else {
                            Log.e("SYR", "SYR -> Unable to parcel the given data")
                        }

                    }
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to parcel ${ex.message} - ${ex.stackTrace}")
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        try {
            parcel.writeString(type.toString())

            when {
                type == Int::class -> {
                    parcel.writeInt(data as Int)
                }
                type == String::class -> {
                    parcel.writeString(data as String)
                }
                type == Double::class -> {
                    parcel.writeDouble(data as Double)
                }
                type == Float::class -> {
                    parcel.writeFloat(data as Float)
                }

                else -> {
                    if (data is Parcelable)
                    {
                        parcel.writeParcelable(data as Parcelable, flags)
                    }
                    else
                    {
                        Log.e("SYR", "SYR -> Unable to parcel the given data")
                    }
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to writeToParcel ${ex.message} - ${ex.stackTrace}")
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MessageBundleData> {
        override fun createFromParcel(parcel: Parcel): MessageBundleData {
            return MessageBundleData(parcel)
        }

        override fun newArray(size: Int): Array<MessageBundleData?> {
            return arrayOfNulls(size)
        }
    }


}