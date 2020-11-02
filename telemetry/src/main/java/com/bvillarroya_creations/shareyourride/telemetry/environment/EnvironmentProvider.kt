package com.bvillarroya_creations.shareyourride.telemetry.environment

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bvillarroya_creations.shareyourride.messenger.IMessageHandlerClient
import com.bvillarroya_creations.shareyourride.messenger.MessageBundle
import com.bvillarroya_creations.shareyourride.messenger.MessageHandler
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.IDataProvider
import com.bvillarroya_creations.shareyourride.telemetry.interfaces.ITelemetryData
import com.bvillarroya_creations.shareyourride.telemetry.location.LocationData
import com.bvillarroya_creations.shareyourride.telemetry.messages.TelemetryMessageTopics
import com.bvillarroya_creations.shareyourride.telemetry.messages.TelemetryMessageTypes
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class EnvironmentProvider(val context: Context): IDataProvider, IMessageHandlerClient {

    //region constants
    companion object {
        /**
         * The interval to make weather request
         */
        private const val WEATHER_REQUEST_INTERVAL : Long = 600000

        /**
         * Todo make it configurable
         */
        private const val openWeatherKey = "9d75feb8d66603f27a7a44ff0cdf6bb3"
    }
    //endregion

    //region private vars

    /**
     * Used to dispose the periodic timer
     */
    private var httpRequestTimer: Disposable? = null

    /**
     * The current longitude, used to maje the weather request
     */
    private var currentLongitude: Double = 0.0

    /**
     * The current latitude, used to make the weather request
     */
    private var currentLatitude: Double = 0.0

    /**
     * Contains the call back invoked when the weather request is performed
     */
    private lateinit var callbackHandler: (ITelemetryData) -> Unit

    /**
     * Stores the state of the weather provider
     * STOPED store when the request is successful
     * SUBSCRIBED when the request fails
     */
    private var mProviderState: IDataProvider.ProviderState = IDataProvider.ProviderState.STOPPED
    //endregion

    //region IDataProvider
    /**
     *Initialize the three sensors
     */
    override fun configureProvider() = Unit

    /**
     * Set the handler that is going to precess changes in the weather
     * This function is invoked periodically,
     *
     * @param: callback: callback functionK to process weather changes
     */
    override fun subscribeProvider(callback: (ITelemetryData) -> Unit)
    {
        try {

            httpRequestTimer =
                Observable.interval(0, WEATHER_REQUEST_INTERVAL, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .subscribe {
                        makeWeatherRequest()
                    }


            callbackHandler = callback
            // Instantiate the RequestQueue.
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to get the subscribe to weather provider, exception: ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Compose and sent the request to the open maps weather server, using the current latitude and longitude
     */
    private fun makeWeatherRequest()
    {
        try {

            if (currentLatitude != 0.0 && currentLongitude != 0.0) {
                val queue = Volley.newRequestQueue(context)
                val url =
                    "https://api.openweathermap.org/data/2.5/weather?lat=$currentLatitude&lon=$currentLongitude&appid=${openWeatherKey}&units=metric"

                // Request a json response from the provided URL.
                val jsonRequest = JsonObjectRequest(Request.Method.GET, url, null,
                    Response.Listener { response ->

                        processOpenWeatherResponse(response)
                        mProviderState = IDataProvider.ProviderState.SUBSCRIBED
                        Log.d("SYR", "SYR -> Received current weather $response")
                    },
                    Response.ErrorListener { error ->
                        mProviderState = IDataProvider.ProviderState.STOPPED
                        Log.e("SYR", "SYR -> Unable to get current weather, $error")
                        error
                    }
                )

                // Add the request to the RequestQueue.
                queue.add(jsonRequest)
            }
            else
            {
                Log.e("SYR", "SYR -> Unable to make weather request to open maps because we don't known the latitude or longitude")
            }
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to get the current weather telemetry, exception: ${ex.message}")
            ex.printStackTrace()
        }
    }


    /**
     * Remove the timer that makes the periodically
     */
    override fun stopProvider()
    {
        httpRequestTimer?.dispose()
    }

    /**
     * returns the state of the weather provider
     */
    override fun getProviderState(): IDataProvider.ProviderState
    {
        return mProviderState
    }
    //endregion

    //region IMessageHandlerClient
    init {
        /**
         * Subscribe the class to telemetry data message
         */
        this.createMessageHandler( "EnvironmentProvider", listOf<String>(TelemetryMessageTopics.TELEMETRY_DATA))
    }

    /**
     * Class in charge of handle message
     */
    override lateinit var messageHandler: MessageHandler

    /**
     * Process messages related to the change of the location of the device
     * We use the location to compose the request to the open maps service
     *
     * If there isn't ant precious location known, make a request to get the first environment data
     *
     * @param msg: received message
     */
    override fun processMessage(msg: MessageBundle) {

        if (msg.messageKey == TelemetryMessageTypes.LOCATION_DATA
            && msg.messageData.type == LocationData::class)
        {
            val data =msg.messageData.data as LocationData

            if (currentLatitude == 0.0 || currentLongitude == 0.0)
            {
                Log.d("SYR", "SYR this is the first time that the location is known so make the request")
                currentLatitude = data.latitude
                currentLongitude = data.longitude

                makeWeatherRequest()
            }
            else
            {
                currentLatitude = data.latitude
                currentLongitude = data.longitude
            }


        }
    }
    //endregion

    //region tools
    /**
     * Process the received data from the query to the open weather server,
     * The content of the response is explained in https://openweathermap.org/current
     * Example:
     * {
    "coord": {
    "lon": -0.89,
    "lat": 41.65
    },
    "weather": [
    {
    "id": 801,
    "main": "Clouds",
    "description": "few clouds",
    "icon": "02d"
    }
    ],
    "base": "stations",
    "main": {
    "temp": 37.84,
    "feels_like": 36.42,
    "temp_min": 37.78,
    "temp_max": 38,
    "pressure": 1015,
    "humidity": 22
    },
    "visibility": 10000,
    "wind": {
    "speed": 3.1,
    "deg": 80
    },
    "clouds": {
    "all": 20
    },
    "dt": 1596904593,
    "sys": {
    "type": 1,
    "id": 6439,
    "country": "ES",
    "sunrise": 1596863116,
    "sunset": 1596913987
    },
    "timezone": 7200,
    "id": 6362983,
    "name": "Zaragoza",
    "cod": 200
    }
     */
    private fun processOpenWeatherResponse(response: JSONObject)
    {
        try {
            val temp: Double? = response.getJSONObject("main").optDouble("temp")
            val humidity: Int? = response.getJSONObject("main").optInt("humidity")
            val pressure: Double? = response.getJSONObject("main").optDouble("pressure")
            val windDirection: Double? = response.getJSONObject("wind").optDouble("deg")
            val windSpeed: Double? = response.getJSONObject("wind").optDouble("speed")
            val timeStamp: Long? = response.optLong("dt ")

            val environment = EnvironmentData(temp ?: 0.0, windDirection ?: 0.0, windSpeed ?: 0.0, humidity ?: 0, pressure ?: 0.0, timeStamp ?: 0)

            callbackHandler(environment)
        }
        catch (ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to retrieve data from the response: ${ex.message}")
            Log.e("SYR", "SYR -> Response: $response")
            ex.printStackTrace()
        }

    }
    //end region
}