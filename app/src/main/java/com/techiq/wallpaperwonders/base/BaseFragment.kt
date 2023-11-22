package com.techiq.wallpaperwonders.base

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.techiq.wallpaperwonders.utils.Constant.SHARED_COMMON
import com.techiq.wallpaperwonders.utils.Constant.SHARED_GLIDE
import com.techiq.wallpaperwonders.utils.GlideUtils
import com.techiq.wallpaperwonders.utils.PrefUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
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
}