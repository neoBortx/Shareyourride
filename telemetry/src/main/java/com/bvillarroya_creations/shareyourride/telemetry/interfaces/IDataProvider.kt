package com.bvillarroya_creations.shareyourride.telemetry.interfaces

/**
 * Common interface for all telemetry providers
 */
interface IDataProvider {

    /**
     * The provider state
     */
    enum class ProviderState{
        /**
         * The provider isn't working
         */
        STOPPED,

        /**
         * The provider is working
         */
        SUBSCRIBED,
    }

    /**
     * To configure all related stuff that the provider requires to work
     */
    fun configureProvider()

    /**
     * Start the provider,when the provider gets new data, it will invoke the callback
     * function given here
     *
     * @param callback: callback function to process new telemetry data
     *
     */
    fun subscribeProvider(callback: (ITelemetryData) -> Unit)

    /**
     * Stops the provider
     */
    fun stopProvider()

    /**
     * To known the current state of the provider
     * @return the current state of the provider
     */
    fun getProviderState(): ProviderState
}