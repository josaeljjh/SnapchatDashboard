package com.jjhdev.snapchatdashboard.fragment

/**
 * Created by Josaél Hernández on 5/2/20.
 * Contact : josaeljjh@gmail.com
 */


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.jjhdev.snapchatdashboard.R
import com.jjhdev.snapchatdashboard.adapter.FragmentsClassesPagerAdapter
import com.jjhdev.snapchatdashboard.event.EventBus
import com.jjhdev.snapchatdashboard.event.PageChangedEvent
import java.util.*
/**
 * Fragment to manage the horizontal pages (left, central, right) of the 5 pages application navigation (top, center,
 * bottom, left, right).
 */
class CentralCompositeFragment : Fragment() {

    var mHorizontalPager: ViewPager? = null
    private var mCentralPageIndex = 0
    private val mPagerChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            EventBus.getInstance()?.post(PageChangedEvent(mCentralPageIndex == position))
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_composite_central, container, false)
        findViews(fragmentView)
        return fragmentView
    }

    private fun findViews(fragmentView: View) {
        mHorizontalPager = fragmentView.findViewById<View>(R.id.fragment_composite_central_pager) as ViewPager
        initViews()
    }

    private fun initViews() {
        populateHozizontalPager()
        mHorizontalPager!!.currentItem = mCentralPageIndex
        mHorizontalPager!!.setOnPageChangeListener(mPagerChangeListener)
    }

    private fun populateHozizontalPager() {
        val pages = ArrayList<Class<out Fragment>>()
        pages.add(LeftFragment::class.java)
        pages.add(CentralFragment::class.java)
        pages.add(RightFragment::class.java)
        mCentralPageIndex = pages.indexOf(CentralFragment::class.java)
        mHorizontalPager!!.adapter = activity?.let {
            FragmentsClassesPagerAdapter(
                childFragmentManager,
                it,
                pages
            )
        }
    }
}
