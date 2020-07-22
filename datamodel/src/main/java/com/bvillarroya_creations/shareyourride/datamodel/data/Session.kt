package com.bvillarroya_creations.shareyourride.datamodel.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
Stores the current session information
 */

@Entity(tableName = "session")
data class Session(

    /*
        Row unique identifier. Also identifies the session. Generate this field with a GUID generator
     */
    @PrimaryKey
    var id: String = "",

    /*
        Name given by the user
     */
    var name: String = "",

    /*
        Date of creation (time stamp)
     */
    var initTimeStamp: Long = 0,

    /*
        Date of the end of the session (time stamp)
     */
    var endTimeStamp: Long = 0
)