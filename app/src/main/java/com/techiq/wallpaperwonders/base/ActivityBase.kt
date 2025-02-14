package com.techiq.wallpaperwonders.base

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.utils.Constant
import com.techiq.wallpaperwonders.utils.Constant.SHARED_COMMON
import com.techiq.wallpaperwonders.utils.GlideUtils
import com.techiq.wallpaperwonders.utils.PrefUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
open class ActivityBase : DataBindingActivity() {
    private val tagName: String = javaClass.simpleName

    @Inject
    @Named(SHARED_COMMON)
    lateinit var sharedPref: PrefUtils

    @Inject
    @Named(Constant.SHARED_GLIDE)
    lateinit var glideUtils: GlideUtils

    private fun initBaseComponants() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBaseComponants()

    }

    fun showSoftKeyboard(editText: EditText?) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideSoftKeyboard() {
        try {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(window.currentFocus?.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pushFragment(fragment: Fragment) {
        pushFragment(fragment, false)
    }

    fun pushFragment(fragment: Fragment, addToBackStack: Boolean) {
        pushFragment(fragment, false, addToBackStack)
    }

    private fun pushFragment(fragment: Fragment, clearBackStack: Boolean, addToBackStack: Boolean) {
        val manager: FragmentManager = supportFragmentManager
        if (clearBackStack && manager.backStackEntryCount > 0) {
            try {
                manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val backStateName: String = fragment.javaClass.name
        val fragmentPopped: Boolean = manager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped) {
            val ft: FragmentTransaction = manager.beginTransaction()
            ft.replace(R.id.container, fragment, backStateName)
            if (addToBackStack) ft.addToBackStack(backStateName)
            ft.commit()
        }
    }


}
