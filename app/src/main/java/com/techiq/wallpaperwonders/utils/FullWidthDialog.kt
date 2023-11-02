package com.techiq.wallpaperwonders.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager

class FullWidthDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the current window and set its layout params
        val window = window
        val lp = window?.attributes
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        lp?.height = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = lp
    }
}