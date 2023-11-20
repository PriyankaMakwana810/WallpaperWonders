package com.techiq.wallpaperwonders.base

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.utils.Constant.SHARED_COMMON
import com.techiq.wallpaperwonders.utils.Constant.SHARED_GLIDE
import com.techiq.wallpaperwonders.utils.GlideUtils
import com.techiq.wallpaperwonders.utils.PrefUtils
import javax.inject.Inject
import javax.inject.Named


open class BaseFragment : Fragment() {
    lateinit var mContext: Context
    lateinit var mActivity: Activity

    @Inject
    @Named(SHARED_GLIDE)
    lateinit var glideUtils: GlideUtils

    @Inject
    @Named(SHARED_COMMON)
    lateinit var sharedPref: PrefUtils

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = context as Activity
    }

    fun setupToolbarWithMenu(view: View, title: String? = null, icon: Int = R.drawable.v_ic_menu) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(icon)
            val tvTitle = toolbar.findViewById<TextView>(R.id.tvTitle)
            if (title != null) tvTitle.text = title
        }
        toolbar.setNavigationOnClickListener {
            //(activity as MainActivity).openDrawer()
        }
    }

    @JvmOverloads
    fun setUpToolbarWithBackArrow(strTitle: String? = null, isBackArrow: Boolean = true) {
        val toolbar = view?.findViewById<Toolbar>(R.id.toolbar)
        val title = toolbar?.findViewById<TextView>(R.id.tvTitle)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(isBackArrow)
            actionBar.setHomeAsUpIndicator(R.drawable.v_ic_back_arrow)
            if (strTitle != null) title?.text = strTitle
        }
    }
}