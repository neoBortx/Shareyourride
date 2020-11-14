package com.bvillarroya_creations.shareyourride.datamodel.dataBase

import android.content.Context
import android.util.Log
import androidx.room.InvalidationTracker
import androidx.room.Room
import com.bvillarroya_creations.shareyourride.datamodel.dao.*
import com.bvillarroya_creations.shareyourride.datamodel.data.*

class DataBaseManager {
    companion object {

        /**
         * Internal object with the data base reference
         */
        private var m_instance: ShareYourRideDatabase? = null

        private var sessionDao: SessionDao? = null
        private var sessionTelemetryDao: SessionTelemetryDao? = null
        private var videoDao: VideoDao? = null
        private var environmentDao: EnvironmentDao? = null
        private var inclinationDao: InclinationDao? = null
        private var locationDao: LocationDao? = null

        /***
         * Build an instance of the data base. Use a singleton strategy to avoid create multiple instances
         *
         * @param context: Activity context
         * @return The data instance of the data base
         */
        fun buildDataBase(context: Context): ShareYourRideDatabase?
        {
            if (m_instance == null)
            {
                Log.i("SYR", "Building the data base")
                m_instance = Room.databaseBuilder(context,
                    ShareYourRideDatabase::class.java,
                    ShareYourRideDatabase.DATABASE_NAME)
                    .fallbackToDestructiveMigration().build()

                sessionDao = m_instance!!.sessionDao()
                sessionTelemetryDao = m_instance!!.sessionTelemetryDao()
                videoDao = m_instance!!.videoDao()
                environmentDao = m_instance!!.environmentDao()
                inclinationDao = m_instance!!.inclinationDao()
                locationDao = m_instance!!.locationDao()
            }
            return m_instance
        }

        //region observers

        fun setObserver(observer: InvalidationTracker.Observer)
        {
            if (m_instance != null)
            {
                Log.i("SYR", "Setting observer in the data base")
                m_instance!!.invalidationTracker.addObserver(observer)
            }
        }

        fun removeObserver(observer: InvalidationTracker.Observer)
        {
            if (m_instance != null)
            {
                Log.i("SYR", "removing observer of the data base")
                m_instance!!.invalidationTracker.removeObserver(observer)
            }
        }

        //endregion

        //region insert data
        /**
         * Insert the given session in the data base
         * @param session: The session to insert
         */
        fun insertSession(session: Session)
        {
            if (sessionDao != null)
            {
                sessionDao!!.addSession(session)
                Log.d("SYR", "SYR -> Inserted session $session")
            }
            else
            {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to insert session")
            }
        }

        /**
         * Insert the given session telemetry in the data base
         * @param sessionTelemetry: The session telemetry to insert
         */
        fun insertSessionTelemetry(sessionTelemetry: SessionTelemetry)
        {
            if (sessionTelemetryDao != null)
            {
                sessionTelemetryDao!!.addSessionTelemetry(sessionTelemetry)
                Log.d("SYR", "SYR -> Inserted session telemetry $sessionTelemetry")
            }
            else
            {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to insert session telemetry")
            }
        }

        /**
         * Insert the given video in the data base
         * @param video: The video to insert
         */
        fun insertVideo(video: Video)
        {
            if (videoDao != null)
            {
                Log.d("SYR", "Inserting video frame row: ${video.id.sessionId}, ${video.id.timeStamp}")
                videoDao!!.addVideo(video)
            }
            else
            {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to insert video")
            }
        }

        /**
         * Insert the given location in the data base
         * @param location: The location to insert
         */
        fun insertLocation(location: Location)
        {
            if (locationDao != null)
            {
                locationDao!!.addLocation(location)
            }
            else
            {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to insert location")
            }
        }

        /**
         * Insert the given environment in the data base
         * @param environment: The environment to insert
         */
        fun insertEnvironment(environment: Environment)
        {
            if (environmentDao != null)
            {
                Log.d("SYR", "SYR -> Inserting environment data in session: ${environment.id.sessionId}, ${environment.id.timeStamp}")
                environmentDao!!.addEnvironment(environment)
            }
            else
            {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to insert environment")
            }
        }

        /**
         * Insert the given inclination in the data base
         * @param inclination: The inclination to insert
         */
        fun insertInclination(inclination: Inclination)
        {
            if (inclinationDao != null)
            {
                  inclinationDao!!.addInclination(inclination)
            }
            else
            {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to insert inclination")
            }
        }

        //endregion

        //region update data
        /**
         * Update the given session in the data base
         * @param session: The session to insert
         */
        fun updateSession(session: Session)
        {
            if (sessionDao != null)
            {
                sessionDao!!.updateSession(session)
                Log.d("SYR", "SYR -> Updated session $session")
            }
            else
            {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to update session")
            }
        }

        /**
         * Update the given session telemetry in the data base
         * @param sessionTelemetry: The session telemetry to update
         */
        fun updateSessionTelemetry(sessionTelemetry: SessionTelemetry)
        {
            if (sessionTelemetryDao != null)
            {
                sessionTelemetryDao!!.updateSessionTelemetry(sessionTelemetry)
                Log.d("SYR", "SYR -> Updated session telemetry $sessionTelemetry")
            }
            else
            {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to update session")
            }
        }
        //endregion

        //region get data
        /**
         * Get the list of signs of the given date stored in the data base
         */
        fun getSessions(): List<Session>
        {
            return if (sessionDao != null) {
                sessionDao!!.getSessionList()
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to retrieve the list of sessions")
                listOf()
            }
        }

        /**
         * Get the list of the telemetry configured for the given session ID
         *
         * @param sessionId: The identifier of the session
         *
         * @return The telemetry configuration
         */
        fun getSessionTelemetry(sessionId: String): SessionTelemetry?
        {
            return if (sessionTelemetryDao != null) {
                sessionTelemetryDao!!.getSessionTelemetry(sessionId)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to retrieve the list of sessions")
                null
            }
        }

        /**
         * Get the list of environments related to the given session and frame ide
         * @param sessionId: the identifier of the session
         * @param videoFrame: The identifier of the frame
         */
        fun getEnvironments(sessionId: String, videoFrame: Int): List<Environment>
        {
            return if (environmentDao != null) {
                environmentDao!!.getEnvironmentList(sessionId,videoFrame)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to retrieve the list of environments")
                listOf()
            }
        }

        /**
         * Get the list of inclinations related to the given session and frame ide
         * @param sessionId: the identifier of the session
         * @param videoFrame: The identifier of the frame
         */
        fun getInclinations(sessionId: String, videoFrame: Int): List<Inclination>
        {
            return if (inclinationDao != null) {
                inclinationDao!!.getInclinationList(sessionId,videoFrame)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to retrieve the list of inclinations")
                listOf()
            }
        }

        /**
         * Get the list of locations related to the given session and frame ide
         * @param sessionId: the identifier of the session
         * @param videoFrame: The identifier of the frame
         */
        fun getLocations(sessionId: String, videoFrame: Int): List<Location>
        {
            return if (locationDao != null) {
                locationDao!!.getLocationList(sessionId,videoFrame)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to retrieve the list of locations")
                listOf()
            }
        }

        /**
         * Get the list of locations related to the given session and frame ide
         * @param sessionId: the identifier of the session
         * @param timeStamp: The identifier of the frame
         */
        fun getVideo(sessionId: String, timeStamp: Long): Video?
        {
            return if (videoDao != null) {
                videoDao!!.getVideoFrame(sessionId,timeStamp)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to retrieve the video frame")
                null
            }
        }
        //endregion

        //region session summary
        /**
         * Get the maximum speed of the mobile phone during the session
         */
        fun getMaxSpeed(sessionId: String): Float{
            return if (locationDao != null) {
                locationDao!!.getMaxSpeed(sessionId)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to the max speed")
                0F
            }
        }

        /**
         * Get the average speed of the mobile phone during the session
         */
        fun getAverageMaxSpeed(sessionId: String): Float{
            return if (locationDao != null) {
                locationDao!!.getAverageMaxSpeed(sessionId)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to the average speed")
                0F
            }
        }

        /**
         * Get the total distance of the session
         */
        fun getDistance(sessionId: String): Long{
            return if (locationDao != null) {
                locationDao!!.getDistance(sessionId)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to the distance")
                0
            }
        }

        /**
         * Get the maximum acceleration detected during the session
         */
        fun getMaxAcceleration(sessionId: String): Float{
            return if (inclinationDao != null) {
                inclinationDao!!.getMaxAcceleration(sessionId)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to the max acceleration")
                0F
            }
        }

        /**
         * Get the maximum acceleration direction detected during the session
         */
        fun getMaxAccelerationDirection(sessionId: String): Int{
            return if (inclinationDao != null) {
                inclinationDao!!.getMaxAccelerationDirection(sessionId)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to the max acceleration direction")
                0
            }
        }


        /**
         * Get the maximum lean angle in the left side detected during session
         */
        fun getMaxLeftLeanAngle(sessionId: String): Int{
            return if (inclinationDao != null) {
                inclinationDao!!.getMaxLeftLeanAngle(sessionId)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to the max left angle")
                0
            }
        }

        /**
         * The maximum lean angle in the right side detected during session
         */
        fun getMaxRightLeanAngle(sessionId: String): Int{
            return if (inclinationDao != null) {
                inclinationDao!!.getMaxRightLeanAngle(sessionId)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to the max right angle")
                0
            }
        }

        /**
         * Get the maximum altitude detected during the session
         */
        fun getMaxAltitude(sessionId: String): Double{
            return if (locationDao != null) {
                locationDao!!.getMaxAltitude(sessionId)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to the max altitude")
                0.0
            }
        }

        /**
         * Get the minimum altitude detected during the session
         */
        fun getMinAltitude(sessionId: String): Double{
            return if (locationDao != null) {
                locationDao!!.getMinAltitude(sessionId)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to the minimum altitude")
                0.0
            }
        }

        /**
         * Get the maximum terrain inclination in Uphill
         */
        fun getMaxUphillTerrainInclination(sessionId: String): Int{
            return if (locationDao != null) {
                locationDao!!.getMaxUphillTerrainInclination(sessionId)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to the maximum uphill terrain inclination")
                0
            }
        }

        /**
         * Get the maximum terrain inclination in Downhill
         */
        fun getMaxDownhillTerrainInclination(sessionId: String): Int{
            return if (locationDao != null) {
                locationDao!!.getMaxDownhillTerrainInclination(sessionId)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to the maximum downhill terrain inclination")
                0
            }
        }

        /**
         * Get the average terrain inclination
         */
        fun getAverageTerrainInclination(sessionId: String): Int{
            return if (locationDao != null) {
                locationDao!!.getAverageTerrainInclination(sessionId)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to the average terrain inclination")
                0
            }
        }
        //endregion

        //region delete data
        /**
         * Update the given session in the data base
         * @param session: The session to insert
         */
        fun deleteSession(session: Session)
        {
            if (sessionDao != null)
            {
                sessionDao!!.deleteSession(session)
                Log.d("SYR", "SYR -> Deleted session $session")
            }
            else
            {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to delete session")
            }
        }
        //endregion
    }
}