package com.bvillarroya_creations.shareyourride.datamodel.dataBase

import android.content.Context
import com.bvillarroya_creations.shareyourride.datamodel.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class ShareYourRideRepository {

    companion object {

        suspend fun buildDataBase(context: Context)
        {
            withContext(Dispatchers.Default) {
                DataBaseManager.buildDataBase(context)
            }
        }

        //region get data
        /*
            Get the full list of sessions
         */
        suspend fun getSessions(): List<Session> {
            return withContext(Dispatchers.Default) {
                DataBaseManager.getSessions()
            }
        }

        /*
            Get teh list of bodies of the given session and the video frame
         */
        suspend fun getBody(sessionId: Int, videoId: Int): List<Body> {
            return withContext(Dispatchers.Default) {
                DataBaseManager.getBodies(sessionId, videoId)
            }
        }

        /*
            Get teh list of bodies of the given session and the video frame
        */
        suspend fun getEnvironments(sessionId: Int, videoId: Int): List<Environment> {
            return withContext(Dispatchers.Default) {
                DataBaseManager.getEnvironments(sessionId, videoId)
            }
        }

        /*
            Get teh list of bodies of the given session and the video frame
        */
        suspend fun getLocations(sessionId: Int, videoId: Int): List<Location> {
            return withContext(Dispatchers.Default) {
                DataBaseManager.getLoctaions(sessionId, videoId)
            }
        }

        /*
            Get teh list of bodies of the given session and the video frame
        */
        suspend fun getInclinations(sessionId: Int, videoId: Int): List<Inclination> {
            return withContext(Dispatchers.Default) {
                DataBaseManager.getInclinations(sessionId, videoId)
            }
        }

        /*
            Get teh list of bodies of the given session and the video frame
        */
        suspend fun getVideo(sessionId: Int, videoId: Long): Video? {
            return withContext(Dispatchers.Default) {
                DataBaseManager.getVideo(sessionId, videoId)
            }
        }
        //endregion

        //region session data
        /*
            Insert a given session in the data base
         */
        suspend fun insertSession(session: Session) {
            return withContext(Dispatchers.Default) {
                DataBaseManager.insertSession(session)
            }
        }

        /*
            Insert a given session in the data base
         */
        suspend fun updateSession(session: Session) {
            return withContext(Dispatchers.Default) {
                DataBaseManager.updateSession(session)
            }
        }
        //endregion

        /*
            Insert a given video frame in the data base
        */
        suspend fun insertVideo(video: Video) {
            return GlobalScope.async {
                DataBaseManager.insertVideo(video)
            }.await()
        }

        /*
            Insert a given session in the data base
         */
        suspend fun insertbody(body: Body) {
            return withContext(Dispatchers.Default) {
                DataBaseManager.insertBody(body)
            }
        }

        /*
            Insert a given session in the data base
         */
        suspend fun insertEnvironment(environment: Environment) {
            return withContext(Dispatchers.Default) {
                DataBaseManager.insertEnvironment(environment)
            }
        }

        /*
            Insert a given session in the data base
         */
        suspend fun insertInclination(inclination: Inclination) {
            return withContext(Dispatchers.Default) {
                DataBaseManager.insertInclination(inclination)
            }
        }

        /*
            Insert a given session in the data base
         */
        suspend fun insertLocation(location: Location) {
            return withContext(Dispatchers.Default) {
                DataBaseManager.insertLocation(location)
            }
        }
        //endregion
    }

}