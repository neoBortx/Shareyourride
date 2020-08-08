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
        private var videoDao: VideoDao? = null
        private var bodyDao: BodyDao? = null
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
                videoDao = m_instance!!.videoDao()
                bodyDao = m_instance!!.bodyDao()
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
                Log.d("SYR", "SYR -> Inserting location in session: ${location.id.sessionId}, ${location.id.timeStamp}")
                locationDao!!.addLocation(location)
            }
            else
            {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to insert location")
            }
        }

        /**
         * Insert the given body in the data base
         * @param body: The body to insert
         */
        fun insertBody(body: Body)
        {
            if (bodyDao != null)
            {
                Log.d("SYR", "SYR -> Inserting body data in session: ${body.id.sessionId}, ${body.id.timeStamp}")
                bodyDao!!.addBody(body)
            }
            else
            {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to insert body")
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
                Log.d("SYR", "SYR -> Inserting inclination data in session: ${inclination.id.sessionId}," +
                        "rx ${inclination.orientationVector[0]} ry ${inclination.orientationVector[1]} rz ${inclination.orientationVector[2]} " +
                        "gx ${inclination.gravity[0]} gy ${inclination.gravity[1]} gz ${inclination.gravity[2]} " +
                        "ax ${inclination.acceleration[0]} ay ${inclination.acceleration[1]} az ${inclination.acceleration[2]}")
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
         * Get the list of bodies related to the given session and frame ide
         * @param session: the identifier of the session
         * @param videoFrame: The identifier of the frame
         */
        fun getBodies(session: Int, videoFrame: Int): List<Body>
        {
            return if (bodyDao != null) {
                bodyDao!!.getBodyList(session,videoFrame)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to retrieve the list of bodies")
                listOf()
            }
        }

        /**
         * Get the list of environments related to the given session and frame ide
         * @param session: the identifier of the session
         * @param videoFrame: The identifier of the frame
         */
        fun getEnvironments(session: Int, videoFrame: Int): List<Environment>
        {
            return if (environmentDao != null) {
                environmentDao!!.getEnvironmentList(session,videoFrame)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to retrieve the list of environments")
                listOf()
            }
        }

        /**
         * Get the list of inclinations related to the given session and frame ide
         * @param session: the identifier of the session
         * @param videoFrame: The identifier of the frame
         */
        fun getInclinations(session: Int, videoFrame: Int): List<Inclination>
        {
            return if (inclinationDao != null) {
                inclinationDao!!.getInclinationList(session,videoFrame)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to retrieve the list of inclinations")
                listOf()
            }
        }

        /**
         * Get the list of locations related to the given session and frame ide
         * @param session: the identifier of the session
         * @param videoFrame: The identifier of the frame
         */
        fun getLoctaions(session: Int, videoFrame: Int): List<Location>
        {
            return if (locationDao != null) {
                locationDao!!.getLocationList(session,videoFrame)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to retrieve the list of locations")
                listOf()
            }
        }

        /**
         * Get the list of locations related to the given session and frame ide
         * @param session: the identifier of the session
         * @param timeStamp: The identifier of the frame
         */
        fun getVideo(session: Int, timeStamp: Long): Video?
        {
            return if (videoDao != null) {
                videoDao!!.getVideoFrame(session,timeStamp)
            } else {
                Log.e("SYR", "SYR -> Data base is not initialized yet, unable to retrieve the video frame")
                null
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