package com.jjhdev.snapchatdashboard

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import com.jjhdev.snapchatdashboard.event.EventBus
import com.jjhdev.snapchatdashboard.event.PageChangedEvent
import com.jjhdev.snapchatdashboard.view.VerticalPager
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var CENTRAL_PAGE_INDEX:Int = 1
var mVerticalPager: VerticalPager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViews()
    }

    private fun findViews() {
        initViews()
    }

    private fun initViews() {
        mVerticalPager = verticalPager
        snapPageWhenLayoutIsReady(mVerticalPager!!, CENTRAL_PAGE_INDEX)
    }

    private fun snapPageWhenLayoutIsReady(pageView: View, page: Int) {
        pageView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mVerticalPager!!.snapToPage(page, VerticalPager.PAGE_SNAP_DURATION_INSTANT)
                pageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        EventBus.getInstance()?.register(this)
    }

    override fun onPause() {
        EventBus.getInstance()?.unregister(this)
        super.onPause()
    }
}
