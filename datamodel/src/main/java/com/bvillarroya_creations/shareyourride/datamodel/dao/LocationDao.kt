package com.bvillarroya_creations.shareyourride.datamodel.dao


import androidx.room.*
import com.bvillarroya_creations.shareyourride.datamodel.data.Location

/**
 * Database access object used to operate the data stored in the data base LocationsDataBase
 */
@Dao
interface LocationDao {

    /**
     * Insert a new Location into the collection
     * If the Location already existing, rollback and return an error
     *
     * @param Location: the new Location to add
     * @return The Location identifier
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addLocation(Location: Location): Long

    /**
     * Update an existing Location with the given one
     *
     * @param Location: the new Location to update
     */
    @Update
    fun updateLocation(Location: Location)

    /**
     * Delete the given Location from the database
     *
     * @param Location: the new Location to delete
     */
    @Delete
    fun deleteLocation(Location: Location)

    /**
     * Get the whole Location collection from the database
     *
     * @return: The list of Location
     */
    @Query("SELECT * FROM Location Where sessionId like :session and videoId like :videoFrame")
    fun getLocationList(session: Int, videoFrame: Int): List<Location>
}