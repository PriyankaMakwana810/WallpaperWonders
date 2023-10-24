package com.tridhya.basesetupnew.repository.Signin

import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.tridhya.basesetupnew.Service.ApiClient
import com.tridhya.basesetupnew.Service.ApiState
import com.tridhya.basesetupnew.Service.NetworkConstants
import com.tridhya.basesetupnew.Service.NetworkConstants.getApiStateResponseStatus
import com.tridhya.basesetupnew.Service.ResponseState
import com.tridhya.basesetupnew.request.LoginRequest
import com.tridhya.basesetupnew.utils.Constant
import com.tridhya.basesetupnew.utils.PrefUtils
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class SigninRepository @Inject constructor(
    @Named(Constant.API_CLIENT) private val apiClient: ApiClient,
    @Named(Constant.SHARED_COMMON) private val prefUtils: PrefUtils,
) {
    suspend fun userSignin(
        parent: View?,
        username: String,
        password: String,
        isSuccessMessageShow: Boolean,
        isFailureMessageShow: Boolean,
        loginRequest: LoginRequest
    ): ApiState {
        // Log.d("TAG", "userSignin: "+username+"pass"+password)
        val responseData: ResponseState?
        if (Constant.isNetWork(parent!!.context)) {
            val response = apiClient.signin(username = username, password = password,loginRequest)
            val responseBody = response.body()
            Log.d(
                "TAG", "userSignin: " + Gson().toJson(response.raw().headers)
            )
            val responseMessage = response.message() ?: NetworkConstants.ErrorMsg.SOMETHING_WENT_WRONG
            responseData =
                ResponseState(
                    apiStatus = response.code(),
                    message = response.body()?.message,
                    response = response as Response<Any>,
                    responseBody = responseBody,
                    parentView = parent,
                    isFailureMessageShow = isFailureMessageShow,
                    isSuccessMessageShow = isSuccessMessageShow

                )
            // Log.d("TAG", "userSignin: "+ response..toString())
        } else
            responseData =
                ResponseState(
                    parentView = parent,
                    isNetworkAvailable = false
                )
        return getApiStateResponseStatus(

            responseData, prefUtils
        )
    }

}