package com.techiq.wallpaperwonders.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.design.signin.SigninActivity
import com.techiq.wallpaperwonders.utils.Constant
import com.techiq.wallpaperwonders.utils.Constant.INTENT_LOGIN_PASSWORD
import com.techiq.wallpaperwonders.utils.Constant.INTENT_LOGIN_USER_NAME
import com.techiq.wallpaperwonders.utils.Constant.SHARED_COMMON
import com.techiq.wallpaperwonders.utils.Constant.logE
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

    private fun initBaseComponants() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBaseComponants()

    }

    fun logout(
        isDataClearOnly: Boolean? = false, userName: String? = null, password: String? = null,
    ) {
        sharedPref.logout()

        logE(tagName, "logout: normal logout")
        if (isDataClearOnly == null || isDataClearOnly == false) {
            val intent = Intent(this, SigninActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(Constant.LOGOUT_SCREEN, Constant.LOGOUT)

            if (userName != null && password != null) {
                intent.putExtra(INTENT_LOGIN_USER_NAME, userName)
                intent.putExtra(INTENT_LOGIN_PASSWORD, password)
            }
            startActivity(intent)
            finishAffinity()
        }
    }

    open fun onBackPress() {
        finish()
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

    fun pushFragment(fragment: Fragment, addToBackStack: Boolean) {
        false.pushFragment(fragment, addToBackStack)
    }

    private fun Boolean.pushFragment(fragment: Fragment, addToBackStack: Boolean) {
        val manager: FragmentManager = supportFragmentManager
        if (this && manager.backStackEntryCount > 0) {
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
