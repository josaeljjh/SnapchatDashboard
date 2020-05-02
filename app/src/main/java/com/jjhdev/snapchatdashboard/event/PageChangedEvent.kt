package com.jjhdev.snapchatdashboard.event

/**
 * Created by Josaél Hernández on 5/2/20.
 * Contact : josaeljjh@gmail.com
 */

/**
 * Dispatched when the current selected page of the application navigation changed. E.g. user swipes from the center
 * page to the left page.
 */
class PageChangedEvent(hasVerticalNeighbors: Boolean) {

    private var mHasVerticalNeighbors = true

    fun hasVerticalNeighbors(): Boolean {
        return mHasVerticalNeighbors
    }
    init {
        mHasVerticalNeighbors = hasVerticalNeighbors
    }
}