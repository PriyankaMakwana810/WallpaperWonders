package com.techiq.wallpaperwonders.base

import android.content.Intent
import android.os.Bundle
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


}
