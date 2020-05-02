package com.jjhdev.snapchatdashboard.view

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Created by Josaél Hernández on 5/2/20.
 * Contact : josaeljjh@gmail.com
 */

class SmartViewPager(
    context: Context?,
    attrs: AttributeSet?
) :
    ViewPager(context!!, attrs) {

    private val mGestureDetector: GestureDetector
    private var mIsLockOnHorizontalAxis = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // decide if horizontal axis is locked already or we need to check the scrolling direction
        if (!mIsLockOnHorizontalAxis) mIsLockOnHorizontalAxis = mGestureDetector.onTouchEvent(event)

        // release the lock when finger is up
        if (event.action == MotionEvent.ACTION_UP) mIsLockOnHorizontalAxis = false
        parent.requestDisallowInterceptTouchEvent(mIsLockOnHorizontalAxis)
        return super.onTouchEvent(event)
    }

    private inner class XScrollDetector : SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            return Math.abs(distanceX) > Math.abs(distanceY)
        }
    }

    init {
        mGestureDetector = GestureDetector(context, XScrollDetector())
    }
}
