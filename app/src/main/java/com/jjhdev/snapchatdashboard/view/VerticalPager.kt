package com.jjhdev.snapchatdashboard.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import java.util.*

/**
 * Created by Josaél Hernández on 5/2/20.
 * Contact : josaeljjh@gmail.com
 */

class VerticalPager @JvmOverloads constructor(context: Context, attrs: AttributeSet?, defStyle: Int = 0) : ViewGroup(context, attrs, defStyle) {

    /**
     * Used to inflate the Workspace from XML.
     *
     * @param context
     * The application's context.
     * @param attrs
     * The attribtues set containing the Workspace's customization values.
     * @param defStyle
     * Unused.
     */
    /**
     * Used to inflate the Workspace from XML.
     *
     * @param context
     * The application's context.
     * @param attrs
     * The attribtues set containing the Workspace's customization values.
     */
    init {
        init(context)
    }

    var isPagingEnabled = true
    var pageHeight = 0
        private set
    private var getMeasuredHeight = 0
    private var mFirstLayout = true
    private var mCurrentPage = 0
    private var mNextPage =
        INVALID_SCREEN
    private var mScroller: Scroller? = null
    private var mVelocityTracker: VelocityTracker? = null
    private var mTouchSlop = 0
    private var mMaximumVelocity = 0
    private var mLastMotionY = 0f
    private var mLastMotionX = 0f
    private var mTouchState =
        TOUCH_STATE_REST
    private var mAllowLongPress = false
    private val mListeners: MutableSet<OnScrollListener> =
        HashSet()

    /**
     * Initializes various states for this workspace.
     */
    private fun init(context: Context) {
        mScroller = Scroller(getContext(), DecelerateInterpolator())
        mCurrentPage = 0
        val configuration = ViewConfiguration.get(getContext())
        mTouchSlop = configuration.scaledTouchSlop
        mMaximumVelocity = configuration.scaledMaximumFlingVelocity
    }

    /**
     * Returns the index of the currently displayed page.
     *
     * @return The index of the currently displayed page.
     */
    /**
     * Sets the current page.
     *
     * @param currentPage
     */
    var currentPage: Int
        get() = mCurrentPage
        set(currentPage) {
            mCurrentPage = Math.max(0, Math.min(currentPage, childCount))
            scrollTo(getScrollYForPage(mCurrentPage), 0)
            invalidate()
        }

    // public void setPageHeight(int pageHeight) {
    // this.pageHeightSpec = pageHeight;
    // }
    /**
     * Gets the value that getScrollX() should return if the specified page is the current page (and no other scrolling
     * is occurring). Use this to pass a value to scrollTo(), for example.
     *
     * @param whichPage
     * @return
     */
    private fun getScrollYForPage(whichPage: Int): Int {
        var height = 0
        for (i in 0 until whichPage) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                height += child.height
            }
        }
        return height - pageHeightPadding()
    }

    override fun computeScroll() {
        if (mScroller!!.computeScrollOffset()) {
            scrollTo(mScroller!!.currX, mScroller!!.currY)
            postInvalidate()
        } else if (mNextPage != INVALID_SCREEN) {
            mCurrentPage = mNextPage
            mNextPage = INVALID_SCREEN
            clearChildrenCache()
        }
    }

    override fun dispatchDraw(canvas: Canvas) {

        // ViewGroup.dispatchDraw() supports many features we don't need:
        // clip to padding, layout animation, animation listener, disappearing
        // children, etc. The following implementation attempts to fast-track
        // the drawing dispatch by drawing only what we know needs to be drawn.
        val drawingTime = drawingTime
        // todo be smarter about which children need drawing
        val count = childCount
        for (i in 0 until count) {
            drawChild(canvas, getChildAt(i), drawingTime)
        }
        for (mListener in mListeners) {
            val adjustedScrollY = scrollY + pageHeightPadding()
            mListener.onScroll(adjustedScrollY)
            if (adjustedScrollY % pageHeight == 0) {
                mListener.onViewScrollFinished(adjustedScrollY / pageHeight)
            }
        }
    }

    fun pageHeightPadding(): Int {
        return (measuredHeight - pageHeight) / 2
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        pageHeight = measuredHeight
        val count = childCount
        for (i in 0 until count) {
            // getChildAt(i).measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
            // MeasureSpec.EXACTLY),
            // MeasureSpec.makeMeasureSpec(pageHeight,
            // MeasureSpec.UNSPECIFIED));
            getChildAt(i).measure(MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(pageHeight, MeasureSpec.EXACTLY)
            )
        }

        if (mFirstLayout) {
            scrollTo(getScrollYForPage(mCurrentPage), 0)
            mFirstLayout = false
        }
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        getMeasuredHeight = 0
        val count = childCount
        var height: Int
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                height =
                    pageHeight * Math.ceil(
                        child.measuredHeight.toDouble() / pageHeight.toDouble()
                    ).toInt()
                height = Math.max(pageHeight, height)
                child.layout(0, getMeasuredHeight, right - left, getMeasuredHeight + height)
                getMeasuredHeight += height
            }
        }
    }

    override fun requestChildRectangleOnScreen(
        child: View,
        rectangle: Rect,
        immediate: Boolean
    ): Boolean {
        val screen = indexOfChild(child)
        return if (screen != mCurrentPage || !mScroller!!.isFinished) {
            true
        } else false
    }

    override fun onRequestFocusInDescendants(
        direction: Int,
        previouslyFocusedRect: Rect
    ): Boolean {
        val focusableScreen: Int
        focusableScreen = if (mNextPage != INVALID_SCREEN) {
            mNextPage
        } else {
            mCurrentPage
        }
        getChildAt(focusableScreen).requestFocus(direction, previouslyFocusedRect)
        return false
    }

    override fun dispatchUnhandledMove(
        focused: View,
        direction: Int
    ): Boolean {
        if (direction == View.FOCUS_LEFT) {
            if (currentPage > 0) {
                snapToPage(currentPage - 1)
                return true
            }
        } else if (direction == View.FOCUS_RIGHT) {
            if (currentPage < childCount - 1) {
                snapToPage(currentPage + 1)
                return true
            }
        }
        return super.dispatchUnhandledMove(focused, direction)
    }

    override fun addFocusables(
        views: ArrayList<View>,
        direction: Int
    ) {
        getChildAt(mCurrentPage).addFocusables(views, direction)
        if (direction == View.FOCUS_LEFT) {
            if (mCurrentPage > 0) {
                getChildAt(mCurrentPage - 1).addFocusables(views, direction)
            }
        } else if (direction == View.FOCUS_RIGHT) {
            if (mCurrentPage < childCount - 1) {
                getChildAt(mCurrentPage + 1).addFocusables(views, direction)
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isPagingEnabled) return false

        // Log.d(TAG, "onInterceptTouchEvent::action=" + ev.getAction());

        /*
		 * This method JUST determines whether we want to intercept the motion. If we return true, onTouchEvent will be
		 * called and we do the actual scrolling there.
		 */

        /*
		 * Shortcut the most recurring case: the user is in the dragging state and he is moving his finger. We want to
		 * intercept this motion.
		 */
        val action = ev.action
        if (action == MotionEvent.ACTION_MOVE && mTouchState != TOUCH_STATE_REST) {
            // Log.d(TAG, "onInterceptTouchEvent::shortcut=true");
            return true
        }
        val y = ev.y
        val x = ev.x
        when (action) {
            MotionEvent.ACTION_MOVE ->            /*
			 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check whether the user has moved
			 * far enough from his original down touch.
			 */if (mTouchState == TOUCH_STATE_REST) {
                checkStartScroll(x, y)
            }
            MotionEvent.ACTION_DOWN -> {
                // Remember location of down touch
                mLastMotionX = x
                mLastMotionY = y
                mAllowLongPress = true

                /*
			 * If being flinged and user touches the screen, initiate drag; otherwise don't. mScroller.isFinished should
			 * be false when being flinged.
			 */mTouchState =
                    if (mScroller!!.isFinished) TOUCH_STATE_REST else TOUCH_STATE_SCROLLING
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                // Release the drag
                clearChildrenCache()
                mTouchState =
                    TOUCH_STATE_REST
            }
        }

        /*
		 * The only time we want to intercept motion events is if we are in the drag mode.
		 */return mTouchState != TOUCH_STATE_REST
    }

    private fun checkStartScroll(x: Float, y: Float) {
        /*
		 * Locally do absolute value. mLastMotionX is set to the y value of the down event.
		 */
        val xDiff = Math.abs(x - mLastMotionX).toInt()
        val yDiff = Math.abs(y - mLastMotionY).toInt()
        val xMoved = xDiff > mTouchSlop
        val yMoved = yDiff > mTouchSlop
        if (xMoved || yMoved) {
            if (yMoved) {
                // Scroll if the user moved far enough along the X axis
                mTouchState =
                    TOUCH_STATE_SCROLLING
                enableChildrenCache()
            }
            // Either way, cancel any pending longpress
            if (mAllowLongPress) {
                mAllowLongPress = false
                // Try canceling the long press. It could also have been
                // scheduled
                // by a distant descendant, so use the mAllowLongPress flag to
                // block
                // everything
                val currentScreen = getChildAt(mCurrentPage)
                currentScreen.cancelLongPress()
            }
        }
    }

    fun enableChildrenCache() {
        setChildrenDrawingCacheEnabled(true)
        isChildrenDrawnWithCacheEnabled = true
    }

    fun clearChildrenCache() {
        isChildrenDrawnWithCacheEnabled = false
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (!isPagingEnabled) return false
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(ev)
        val action = ev.action
        val x = ev.x
        val y = ev.y
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                /*
			 * If being flinged and user touches, stop the fling. isFinished will be false if being flinged.
			 */if (!mScroller!!.isFinished) {
                    mScroller!!.abortAnimation()
                }

                // Remember where the motion event started
                mLastMotionY = y
            }
            MotionEvent.ACTION_MOVE -> if (mTouchState == TOUCH_STATE_REST) {
                checkStartScroll(y, x)
            } else if (mTouchState == TOUCH_STATE_SCROLLING) {
                // Scroll to follow the motion event
                var deltaY = (mLastMotionY - y).toInt()
                mLastMotionY = y

                // Apply friction to scrolling past boundaries.
                val count = childCount
                if (scrollY < 0 || scrollY + pageHeight > getChildAt(count - 1).bottom) {
                    deltaY /= 2
                }
                scrollBy(0, deltaY)
            }
            MotionEvent.ACTION_UP -> {
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    val velocityTracker = mVelocityTracker
                    velocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                    val velocityY = velocityTracker.yVelocity.toInt()
                    val count = childCount

                    // check scrolling past first or last page?
                    if (scrollY < 0) {
                        snapToPage(0)
                    } else if (scrollY > getMeasuredHeight - pageHeight) {
                        snapToPage(
                            count - 1,
                            BOTTOM,
                            PAGE_SNAP_DURATION_DEFAULT
                        )
                    } else {
                        var i = 0
                        while (i < count) {
                            val child = getChildAt(i)
                            if (child.top < scrollY && child.bottom > scrollY + pageHeight) {
                                // we're inside a page, fling that bitch
                                mNextPage = i
                                mScroller!!.fling(
                                    scrollX, scrollY, 0, -velocityY, 0, 0, child.top,
                                    child.bottom - height
                                )
                                invalidate()
                                break
                            } else if (child.bottom > scrollY && child.bottom < scrollY + height) {
                                // stuck in between pages, oh snap!
                                if (velocityY < -SNAP_VELOCITY) {
                                    snapToPage(i + 1)
                                } else if (velocityY > SNAP_VELOCITY) {
                                    snapToPage(
                                        i,
                                        BOTTOM,
                                        PAGE_SNAP_DURATION_DEFAULT
                                    )
                                } else if (scrollY + pageHeight / 2 > child.bottom) {
                                    snapToPage(i + 1)
                                } else {
                                    snapToPage(
                                        i,
                                        BOTTOM,
                                        PAGE_SNAP_DURATION_DEFAULT
                                    )
                                }
                                break
                            }
                            i++
                        }
                    }
                    if (mVelocityTracker != null) {
                        mVelocityTracker!!.recycle()
                        mVelocityTracker = null
                    }
                }
                mTouchState =
                    TOUCH_STATE_REST
            }
            MotionEvent.ACTION_CANCEL -> mTouchState =
                TOUCH_STATE_REST
        }
        return true
    }

    private fun snapToPage(whichPage: Int, where: Int, duration: Int) {
        enableChildrenCache()
        val changingPages = whichPage != mCurrentPage
        mNextPage = whichPage
        val focusedChild = focusedChild
        if (focusedChild != null && changingPages && focusedChild === getChildAt(mCurrentPage)) {
            focusedChild.clearFocus()
        }
        val delta: Int
        delta = if (getChildAt(whichPage).height <= pageHeight || where == TOP) {
            getChildAt(whichPage).top - scrollY
        } else {
            getChildAt(whichPage).bottom - pageHeight - scrollY
        }
        mScroller!!.startScroll(0, scrollY, 0, delta, duration)
        invalidate()
    }

    /**
     * Snap pager to the specified page with the default [VerticalPager.PAGE_SNAP_DURATION_DEFAULT] duration.
     *
     * @param whichPage
     * Zero based index of the page.
     */
    fun snapToPage(whichPage: Int) {
        snapToPage(
            whichPage,
            TOP,
            PAGE_SNAP_DURATION_DEFAULT
        )
    }

    /**
     * Snap pager to the specified page.
     *
     * @param whichPage
     * Zero based index of the page.
     * @param duration
     * Duration in milliseconds of scrolling to the chosen page.
     */
    fun snapToPage(whichPage: Int, duration: Int) {
        snapToPage(
            whichPage,
            TOP,
            duration
        )
    }

    override fun onSaveInstanceState(): Parcelable? {
        val state =
            SavedState(super.onSaveInstanceState())
        state.currentScreen = mCurrentPage
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState =
            state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        if (savedState.currentScreen != INVALID_SCREEN) {
            mCurrentPage = savedState.currentScreen
        }
    }

    fun scrollUp() {
        if (mNextPage == INVALID_SCREEN && mCurrentPage > 0 && mScroller!!.isFinished) {
            snapToPage(mCurrentPage - 1)
        }
    }

    fun scrollDown() {
        if (mNextPage == INVALID_SCREEN && mCurrentPage < childCount - 1 && mScroller!!.isFinished) {
            snapToPage(mCurrentPage + 1)
        }
    }

    fun getScreenForView(v: View?): Int {
        val result = -1
        if (v != null) {
            val vp = v.parent
            val count = childCount
            for (i in 0 until count) {
                if (vp === getChildAt(i)) {
                    return i
                }
            }
        }
        return result
    }

    /**
     * @return True is long presses are still allowed for the current touch
     */
    fun allowLongPress(): Boolean {
        return mAllowLongPress
    }

    class SavedState : BaseSavedState {
        var currentScreen = -1

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            currentScreen = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(currentScreen)
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState?> =
                object :
                    Parcelable.Creator<SavedState?> {
                    override fun createFromParcel(`in`: Parcel): SavedState? {
                        return SavedState(`in`)
                    }

                    override fun newArray(size: Int): Array<SavedState?> {
                        return arrayOfNulls(
                            size
                        )
                    }
                }
        }
    }

    fun addOnScrollListener(listener: OnScrollListener) {
        mListeners.add(listener)
    }

    fun removeOnScrollListener(listener: OnScrollListener) {
        mListeners.remove(listener)
    }

    /**
     * Implement to receive events on scroll position and page snaps.
     */
    interface OnScrollListener {
        /**
         * Receives the current scroll X value. This value will be adjusted to assume the left edge of the first page
         * has a scroll position of 0. Note that values less than 0 and greater than the right edge of the last page are
         * possible due to touch events scrolling beyond the edges.
         *
         * @param scrollX
         * Scroll X value
         */
        fun onScroll(scrollX: Int)

        /**
         * Invoked when scrolling is finished (settled on a page, centered).
         *
         * @param currentPage
         * The current page
         */
        fun onViewScrollFinished(currentPage: Int)
    }

    companion object {
        /**
         * Default page snap duration in milliseconds.
         */
        const val PAGE_SNAP_DURATION_DEFAULT = 300

        /**
         * Instant page snap duration in milliseconds.
         */
        const val PAGE_SNAP_DURATION_INSTANT = 1
        const val TAG = "VerticalPager"
        private const val INVALID_SCREEN = -1
        const val SPEC_UNDEFINED = -1
        private const val TOP = 0
        private const val BOTTOM = 1

        /**
         * The velocity at which a fling gesture will cause us to snap to the next screen
         */
        private const val SNAP_VELOCITY = 1000
        private const val TOUCH_STATE_REST = 0
        private const val TOUCH_STATE_SCROLLING = 1
    }

}