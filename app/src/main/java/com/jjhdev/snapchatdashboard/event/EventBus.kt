package com.jjhdev.snapchatdashboard.event

import com.squareup.otto.Bus

/**
 * Created by Josaél Hernández on 5/2/20.
 * Contact : josaeljjh@gmail.com
 */


/**
 * Provides a singleton instance for the application event bus. For the sake of simplicity it's done via the singleton,
 * however, some dependency injection directly into interested classes.
 */
object EventBus {

    private val sBus = Bus()
    fun getInstance(): Bus? {
        return sBus
    }
    private fun EventBus() {
        // do nothing here, EventBus is just a keeper for real a Bus instance
    }
}