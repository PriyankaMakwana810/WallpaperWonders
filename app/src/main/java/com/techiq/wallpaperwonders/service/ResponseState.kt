package com.techiq.wallpaperwonders.service

import android.view.View
import retrofit2.Response

class ResponseState(
    var apiStatus: Any? = null,
    val message: Any? = null,
    val response: Response<*>? = null,
    var responseBody: Any? = null,
    val parentView: View? = null,
    val isSuccessMessageShow: Boolean? = false,
    val isFailureMessageShow: Boolean? = true,
    val isNetworkAvailable: Boolean? = true,
)