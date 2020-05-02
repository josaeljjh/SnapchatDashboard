package com.jjhdev.snapchatdashboard.fragment

/**
 * Created by Josaél Hernández on 5/2/20.
 * Contact : josaeljjh@gmail.com
 */


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.jjhdev.snapchatdashboard.MainActivity
import com.jjhdev.snapchatdashboard.R
import kotlinx.android.synthetic.main.fragment_central.*


/**
 * Fragment to manage the central page of the 5 pages application navigation (top, center, bottom, left, right).
 */
class CentralFragment : Fragment() {
    var pager: ViewPager? = null
    var mCenterActivity: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pager = container as ViewPager?
        mCenterActivity = activity as MainActivity?
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_central, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnARMessaging.setOnClickListener { pager!!.currentItem = 0 }
        btnAugs.setOnClickListener { pager!!.currentItem = 2 }
        btnProfile.setOnClickListener { mCenterActivity!!.mVerticalPager?.snapToPage(0) }
        btnDiscover.setOnClickListener { mCenterActivity!!.mVerticalPager?.snapToPage(2) }

    }
}
