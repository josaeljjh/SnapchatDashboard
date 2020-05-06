package com.jjhdev.snapchatdashboard.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

/**
 * Created by Josaél Hernández on 5/2/20.
 * Contact : josaeljjh@gmail.com
 */

class SmartViewPager(
    context: Context?,
    attrs: AttributeSet?
) :
    ViewPager(context!!, attrs),GestureDetector.OnGestureListener {

    private var mGestureDetector: GestureDetector = GestureDetector(context, this)
    private var mIsLockOnHorizontalAxis = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // decide if horizontal axis is locked already or we need to check the scrolling direction
        if (!mIsLockOnHorizontalAxis) mIsLockOnHorizontalAxis = mGestureDetector.onTouchEvent(event)
        // release the lock when finger is up
        if (event.action == MotionEvent.ACTION_UP) mIsLockOnHorizontalAxis = false
        parent.requestDisallowInterceptTouchEvent(mIsLockOnHorizontalAxis)
        return super.onTouchEvent(event)
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return abs(distanceX) > abs(distanceY)
    }
    override fun onShowPress(event: MotionEvent?) {}
    override fun onSingleTapUp(event: MotionEvent?): Boolean {
        // release the lock when finger is up
        if (event!!.action == MotionEvent.ACTION_UP) mIsLockOnHorizontalAxis = false
        return mIsLockOnHorizontalAxis
    }
    override fun onDown(event: MotionEvent?): Boolean {
        // release the lock when finger is up
        if (event!!.action == MotionEvent.ACTION_UP) mIsLockOnHorizontalAxis = false
        return mIsLockOnHorizontalAxis
    }
    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float) = true
    override fun onLongPress(e: MotionEvent?) {}
}
