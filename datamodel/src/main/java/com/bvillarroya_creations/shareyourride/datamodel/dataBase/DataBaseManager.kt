package com.bvillarroya_creations.shareyourride.datamodel.dataBase

import android.content.Context
import android.util.Log
import androidx.room.InvalidationTracker
import androidx.room.Room
import com.bvillarroya_creations.shareyourride.datamodel.dao.*
import com.bvillarroya_creations.shareyourride.datamodel.data.*
import java.lang.Exception

internal class DataBaseManager {
    companion object {

        /**
         * Internal object with the data base reference
         */
        private var m_instance: ShareYourRideDatabase? = null

        private var sessionDao: SessionDao? = null
        private var sessionTelemetryDao: SessionTelemetryDao? = null
        private var videoDao: VideoDao? = null
        private var videoFrameDao: VideoFrameDao? = null
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
            try
            {
                if (m_instance == null)
                {
                    Log.i("DataBaseManager", "Building the data base")
                    m_instance = Room.databaseBuilder(context,
                                                      ShareYourRideDatabase::class.java,
                                                      ShareYourRideDatabase.DATABASE_NAME)
                        .fallbackToDestructiveMigration().build()

                    sessionDao = m_instance!!.sessionDao()
                    sessionTelemetryDao = m_instance!!.sessionTelemetryDao()
                    videoDao = m_instance!!.videoDao()
                    videoFrameDao = m_instance!!.videoFrameDao()
                    inclinationDao = m_instance!!.inclinationDao()
                    locationDao = m_instance!!.locationDao()
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }
            return m_instance
        }

        //region observers

        fun setObserver(observer: InvalidationTracker.Observer)
        {
            try
            {
                if (m_instance != null) {
                    Log.i("DataBaseManager", "Setting observer in the data base")
                    m_instance!!.invalidationTracker.addObserver(observer)
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }
        }

        fun removeObserver(observer: InvalidationTracker.Observer)
        {
            try
            {
                if (m_instance != null)
                {
                    Log.i("DataBaseManager", "removing observer of the data base")
                    m_instance!!.invalidationTracker.removeObserver(observer)
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
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
            try
            {
                if (sessionDao != null)
                {
                    sessionDao!!.addSession(session)
                    Log.d("DataBaseManager", "SYR -> Inserted session $session")
                }
                else
                {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to insert session")
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }
        }

        /**
         * Insert the given session telemetry in the data base
         * @param sessionTelemetry: The session telemetry to insert
         */
        fun insertSessionTelemetry(sessionTelemetry: SessionTelemetry)
        {
            try
            {
                if (sessionTelemetryDao != null)
                {
                    sessionTelemetryDao!!.addSessionTelemetry(sessionTelemetry)
                }
                else
                {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to insert session telemetry")
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }
        }

        /**
         * Insert the given video in the data base
         * @param video: The video to insert
         */
        fun insertVideo(video: Video)
        {
            try
            {
                if (videoDao != null)
                {
                    videoDao!!.addVideo(video)
                }
                else
                {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to insert video")
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }
        }

        /**
         * Insert the given video in the data base
         * @param videoFrame: The video frame to insert
         */
        fun insertVideoFrame(videoFrame: VideoFrame)
        {
            try
            {
                if (videoFrameDao != null)
                {
                    videoFrameDao!!.addVideoFrame(videoFrame)
                }
                else
                {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to insert video frame")
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }
        }

        /**
         * Insert the given location in the data base
         * @param location: The location to insert
         */
        fun insertLocation(location: Location)
        {
            try
            {
                if (locationDao != null)
                {
                    locationDao!!.addLocation(location)
                }
                else
                {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to insert location")
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }
        }

        /**
         * Insert the given inclination in the data base
         * @param inclination: The inclination to insert
         */
        fun insertInclination(inclination: Inclination)
        {
            try
            {
                if (inclinationDao != null)
                {
                      inclinationDao!!.addInclination(inclination)
                }
                else
                {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to insert inclination")
                }
            }
            catch (ex: Exception)
            {
                Log.e("DataBaseManager", "SYR -> Unable to insert inclination $inclination due ${ex.message}")

                ex.printStackTrace()
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
            try
            {
                if (sessionDao != null)
                {
                    sessionDao!!.updateSession(session)
                    Log.d("DataBaseManager", "SYR -> Updated session $session")
                }
                else
                {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to update session")
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }
        }

        /**
         * Update the given video in the data base
         * @param video: The video to insert
         */
        fun updateVideo(video: Video)
        {
            try
            {
                if (videoDao != null)
                {
                    videoDao!!.updateVideo(video)
                    Log.d("DataBaseManager", "SYR -> Updated video $video")
                }
                else
                {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to update session")
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }
        }
        //endregion

        //region get data
        /**
         * Get the list of signs of the given date stored in the data base
         */
        fun getSession(sessionId: String): Session?
        {
            try
            {
                return if (sessionDao != null) {
                    sessionDao!!.getSession(sessionId)
                } else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to retrieve the session data")
                    null
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

            return null
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
            try
            {
                return if (sessionTelemetryDao != null) {
                    sessionTelemetryDao!!.getSessionTelemetry(sessionId)
                } else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to retrieve the list of sessions")
                    null
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }
            return null
        }

        /**
         * Get the list of inclinations related to the given session and frame ide
         * @param sessionId: the identifier of the session
         * @param timestamp: sync timecode
         */
        fun getInclination(sessionId: String, timestamp: Long): Inclination?
        {
            try
            {
                return if (inclinationDao != null) {
                    inclinationDao!!.getInclination(sessionId,timestamp)
                } else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to retrieve the list of inclinations")
                    null
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

            return null
        }

        /**
         * Get the location of the given session and time code
         * @param sessionId: the identifier of the session
         * @param timeStamp: The timecode of the location
         */
        fun getLocation(sessionId: String, timeStamp: Long): Location?
        {
            try
            {
                return if (locationDao != null) {
                    locationDao!!.getLocation(sessionId,timeStamp)
                } else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to retrieve the list of locations")
                    null
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

            return null
        }

        /**
         * Get video information related to the given session
         * @param sessionId: the identifier of the session
         */
        fun getVideo(sessionId: String): Video?
        {
            try
            {
                return if (videoDao != null) {
                    videoDao!!.getVideo(sessionId)
                } else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to retrieve the video information")
                    null
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

            return null
        }

        /**
         * Get video list related to the given session
         * @param sessionId: the identifier of the session
         */
        fun getVideoFrameList(sessionId: String): List<VideoFrame>
        {
            try
            {
                return if (videoFrameDao != null) {
                    videoFrameDao!!.getVideoFrameList(sessionId)
                } else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to retrieve the list of video frames")
                    listOf()
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

            return listOf()
        }
        //endregion

        //region session summary
        /**
         * Get the maximum speed of the mobile phone during the session
         */
        fun getMaxSpeed(sessionId: String): Float{

            try {
                return if (locationDao != null) {
                    locationDao!!.getMaxSpeed(sessionId)
                }
                else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to the max speed")
                    0F
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

            return 0F
        }

        /**
         * Get the average speed of the mobile phone during the session
         */
        fun getAverageMaxSpeed(sessionId: String): Float{

            try
            {
                return if (locationDao != null) {
                    locationDao!!.getAverageMaxSpeed(sessionId)
                } else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to the average speed")
                    0F
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

            return 0F
        }

        /**
         * Get the total distance of the session
         */
        fun getDistance(sessionId: String): Long{

            try {
                return if (locationDao != null) {
                    locationDao!!.getDistance(sessionId)
                }
                else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to the distance")
                    0
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

            return 0
        }

        /**
         * Get the maximum acceleration detected during the session
         */
        fun getMaxAcceleration(sessionId: String): Float{

            try {
                return if (inclinationDao != null) {
                    inclinationDao!!.getMaxAcceleration(sessionId)
                }
                else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to the max acceleration")
                    0F
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

            return 0F
        }

        /**
         * Get the maximum acceleration direction detected during the session
         */
        fun getMaxAccelerationDirection(sessionId: String): Int{
            try {
                return if (inclinationDao != null) {
                    inclinationDao!!.getMaxAccelerationDirection(sessionId)
                }
                else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to the max acceleration direction")
                    0
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

            return 0
        }


        /**
         * Get the maximum lean angle in the left side detected during session
         */
        fun getMaxLeftLeanAngle(sessionId: String): Int{
            try {
                return if (inclinationDao != null) {
                    inclinationDao!!.getMaxLeftLeanAngle(sessionId)
                }
                else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to the max left angle")
                    0
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

            return 0
        }

        /**
         * The maximum lean angle in the right side detected during session
         */
        fun getMaxRightLeanAngle(sessionId: String): Int{
            try {
                return if (inclinationDao != null) {
                    inclinationDao!!.getMaxRightLeanAngle(sessionId)
                }
                else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to the max right angle")
                    0
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

            return 0
        }

        /**
         * Get the maximum altitude detected during the session
         */
        fun getMaxAltitude(sessionId: String): Double{
            try {
                return if (locationDao != null) {
                    locationDao!!.getMaxAltitude(sessionId)
                }
                else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to the max altitude")
                    0.0
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

            return 0.0
        }

        /**
         * Get the minimum altitude detected during the session
         */
        fun getMinAltitude(sessionId: String): Double{
            try {
                return if (locationDao != null) {
                    locationDao!!.getMinAltitude(sessionId)
                }
                else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to the minimum altitude")
                    0.0
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

            return 0.0
        }

        /**
         * Get the maximum terrain inclination in Uphill
         */
        fun getMaxUphillTerrainInclination(sessionId: String): Int{
            try {
                return if (locationDao != null) {
                    locationDao!!.getMaxUphillTerrainInclination(sessionId)
                }
                else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to the maximum uphill terrain inclination")
                    0
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

            return 0
        }

        /**
         * Get the maximum terrain inclination in Downhill
         */
        fun getMaxDownhillTerrainInclination(sessionId: String): Int{
            try {
                return if (locationDao != null) {
                    locationDao!!.getMaxDownhillTerrainInclination(sessionId)
                }
                else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to the maximum downhill terrain inclination")
                    0
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

            return 0
        }

        /**
         * Get the average terrain inclination
         */
        fun getAverageTerrainInclination(sessionId: String): Int{
            try {
                return if (locationDao != null) {
                    locationDao!!.getAverageTerrainInclination(sessionId)
                }
                else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to the average terrain inclination")
                    0
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }

            return 0
        }
        //endregion

        //region delete data
        /**
         * Update the given session in the data base
         * @param session: The session to insert
         */
        fun deleteSession(session: Session)
        {
            try {
                if (sessionDao != null) {
                    sessionDao!!.deleteSession(session)
                    Log.d("DataBaseManager", "SYR -> Deleted session $session")
                }
                else {
                    Log.e("DataBaseManager", "SYR -> Data base is not initialized yet, unable to delete session")
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }
        }
        //endregion
    }
}