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
import com.jjhdev.snapchatdashboard.R


/**
 * Fragment to manage the top page of the 5 pages application navigation (top, center, bottom, left, right).
 */
class TopFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_top, container, false)
    }
}