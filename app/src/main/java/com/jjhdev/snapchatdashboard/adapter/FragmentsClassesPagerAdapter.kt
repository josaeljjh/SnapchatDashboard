package com.jjhdev.snapchatdashboard.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by Josaél Hernández on 5/2/20.
 * Contact : josaeljjh@gmail.com
 */

class FragmentsClassesPagerAdapter(
    fragmentManager: FragmentManager?, context: Context,
    pages: List<Class<out Fragment>>
) : FragmentPagerAdapter(fragmentManager!!) {

    private val mPagesClasses: List<Class<out Fragment>> = pages
    private val mContext: Context = context
    override fun getItem(posiiton: Int): Fragment {
        return Fragment.instantiate(
            mContext,
            mPagesClasses[posiiton].name
        )
    }
    override fun getCount(): Int {
        return mPagesClasses.size
    }

}