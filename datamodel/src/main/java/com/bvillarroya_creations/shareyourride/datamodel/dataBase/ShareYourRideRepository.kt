package com.bvillarroya_creations.shareyourride.datamodel.dataBase

import android.content.Context
import android.util.Log
import com.bvillarroya_creations.shareyourride.datamodel.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShareYourRideRepository {

    companion object {

        suspend fun buildDataBase(context: Context)
        {
            withContext(Dispatchers.IO) {
                DataBaseManager.buildDataBase(context)
            }
        }

        //region get data
        /**
         * Get the full list of sessions
         */
        suspend fun getSessions(): List<Session> {
            return withContext(Dispatchers.IO) {
                DataBaseManager.getSessions()
            }
        }

        /**
         * Get teh list of bodies of the given session and the video frame
        */
        suspend fun getLocations(sessionId: String, videoId: Int): List<Location> {
            return withContext(Dispatchers.IO) {
                DataBaseManager.getLocations(sessionId, videoId)
            }
        }

        /**
         *  Get the list of inclinations related to the given session
        */
        suspend fun getInclinations(sessionId: String, videoId: Int): List<Inclination> {
            return withContext(Dispatchers.IO) {
                DataBaseManager.getInclinations(sessionId, videoId)
            }
        }

        /**
         * Get the video information related to the given session ID
        */
        suspend fun getVideo(sessionId: String): Video? {
            return withContext(Dispatchers.IO) {
                DataBaseManager.getVideo(sessionId)
            }
        }

        /**
         * Get the all video frames information related to the given session ID
         */
        suspend fun getVideoFrameList(sessionId: String): List<VideoFrame> {
            return withContext(Dispatchers.IO) {
                DataBaseManager.getVideoFrameList(sessionId)
            }
        }


        /**
         * Get the maximum speed of the mobile phone during the session
         */
        suspend fun getMaxSpeed(sessionId: String): Float{
            return withContext(Dispatchers.IO)
            {
                val maxSpeed = DataBaseManager.getMaxSpeed(sessionId)
                Log.e("SessionService", "Retrieved session $sessionId maxspeed $maxSpeed")
                return@withContext maxSpeed
            }
        }

        /**
         * Get the average speed of the mobile phone during the session
         */
        suspend fun getAverageMaxSpeed(sessionId: String): Float{
            return withContext(Dispatchers.IO)
            {
                val avgSpeed = DataBaseManager.getAverageMaxSpeed(sessionId)
                Log.e("SessionService", "Retrieved session $sessionId avgSpeed $avgSpeed")
                return@withContext avgSpeed
            }
        }

        /**
         * Get the total distance of the session
         */
        suspend fun getDistance(sessionId: String): Long{
            return withContext(Dispatchers.IO)
            {
                DataBaseManager.getDistance(sessionId)
            }
        }

        /**
         * Get the maximum acceleration detected during the session
         */
        suspend fun getMaxAcceleration(sessionId: String): Float{
            return withContext(Dispatchers.IO)
            {
                val maxAcc = DataBaseManager.getMaxAcceleration(sessionId)
                Log.e("SessionService", "Retrieved msession $sessionId axAcc $maxAcc")
                return@withContext maxAcc
            }
        }

        /**
         * Get the maximum acceleration direction detected during the session
         */
        suspend fun getMaxAccelerationDirection(sessionId: String): Int{
            return withContext(Dispatchers.IO)
            {
                DataBaseManager.getMaxAccelerationDirection(sessionId)
            }
        }


        /**
         * Get the maximum lean angle in the left side detected during session
         */
        suspend fun getMaxLeftLeanAngle(sessionId: String): Int{
            return withContext(Dispatchers.IO)
            {
                val leftAngle = DataBaseManager.getMaxLeftLeanAngle(sessionId)
                Log.e("SessionService", "Retrieved session $sessionId left $leftAngle")
                return@withContext leftAngle
            }
        }

        /**
         * The maximum lean angle in the right side detected during session
         */
        suspend fun getMaxRightLeanAngle(sessionId: String): Int{
            return withContext(Dispatchers.IO)
            {
                val rightAngle = DataBaseManager.getMaxRightLeanAngle(sessionId)
                Log.e("SessionService", "Retrieved session $sessionId right  $rightAngle")
                return@withContext rightAngle
            }
        }

        /**
         * Get the maximum altitude detected during the session
         */
        suspend fun getMaxAltitude(sessionId: String): Double{
            return withContext(Dispatchers.IO)
            {
                DataBaseManager.getMaxAltitude(sessionId)
            }
        }

        /**
         * Get the minimum altitude detected during the session
         */
        suspend fun getMinAltitude(sessionId: String): Double{
            return withContext(Dispatchers.IO)
            {
                DataBaseManager.getMinAltitude(sessionId)
            }
        }

        /**
         * Get the maximum terrain inclination in Uphill
         */
        suspend fun getMaxUphillTerrainInclination(sessionId: String): Int{
            return withContext(Dispatchers.IO)
            {
                DataBaseManager.getMaxUphillTerrainInclination(sessionId)
            }
        }

        /**
         * Get the maximum terrain inclination in Downhill
         */
        suspend fun getMaxDownhillTerrainInclination(sessionId: String): Int{
            return withContext(Dispatchers.IO)
            {
                DataBaseManager.getMaxDownhillTerrainInclination(sessionId)
            }
        }

        /**
         * Get the average terrain inclination
         */
        suspend fun getAverageTerrainInclination(sessionId: String): Int{
            return withContext(Dispatchers.IO)
            {
                DataBaseManager.getAverageTerrainInclination(sessionId)
            }
        }
        //endregion

        //region session data
        /**
         * Insert a new session in the data base
         * If the session already exist, this operation will be discarded
         *
         * @param session: Session data to update
         */
        suspend fun insertSession(session: Session) {
            return withContext(Dispatchers.IO) {
                DataBaseManager.insertSession(session)
            }
        }

        /**
         * Update an existing session with new data
         *
         * @param session: Session data to update
         */
        suspend fun updateSession(session: Session) {
            return withContext(Dispatchers.IO) {
                DataBaseManager.updateSession(session)
            }
        }

        /**
         * Delete an existing session
         *
         * @param session: Session data to delete
         */
        suspend fun deleteSession(session: Session) {
            return withContext(Dispatchers.IO) {
                DataBaseManager.deleteSession(session)
            }
        }
        //endregion

        /**
         * Insert a given video in the data base
        */
        suspend fun insertVideo(video: Video) {
            return withContext(Dispatchers.IO) {
                DataBaseManager.insertVideo(video)
            }
        }

        /**
         * Update an existing video with new data
         *
         * @param video: Video data to update
         */
        suspend fun updateVideo(video: Video) {
            return withContext(Dispatchers.IO) {
                DataBaseManager.updateVideo(video)
            }
        }

        /**
         * Insert a given video frame in the data base
         */
        suspend fun insertVideoFrame(videoFrame: VideoFrame) {
            return withContext(Dispatchers.IO)  {
                DataBaseManager.insertVideoFrame(videoFrame)
            }
        }

        /**
         * Insert a given session in the data base
         */
        suspend fun insertInclination(inclination: Inclination) {
            return withContext(Dispatchers.IO) {
                DataBaseManager.insertInclination(inclination)
            }
        }

        /**
         * Insert a given session in the data base
         */
        suspend fun insertLocation(location: Location) {
            return withContext(Dispatchers.IO) {
                DataBaseManager.insertLocation(location)
            }
        }
        //endregion
    }

}