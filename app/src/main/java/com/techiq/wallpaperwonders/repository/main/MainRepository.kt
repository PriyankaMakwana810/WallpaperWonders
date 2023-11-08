package com.techiq.wallpaperwonders.repository.main

import android.view.View
import com.google.gson.Gson
import com.techiq.wallpaperwonders.service.ApiClient
import com.techiq.wallpaperwonders.service.ApiState
import com.techiq.wallpaperwonders.service.NetworkConstants
import com.techiq.wallpaperwonders.service.NetworkConstants.getApiStateResponseStatus
import com.techiq.wallpaperwonders.service.ResponseState
import com.techiq.wallpaperwonders.utils.Constant
import com.techiq.wallpaperwonders.utils.Constants
import com.techiq.wallpaperwonders.utils.PrefUtils
import javax.inject.Inject
import javax.inject.Named

class MainRepository @Inject constructor(
    @Named(Constant.GSON) private val gson: Gson,
    @Named(Constant.API_CLIENT) private val apiClient: ApiClient,
    @Named(Constant.SHARED_COMMON) private val prefUtils: PrefUtils,
) {
    suspend fun getImagesPixabay(
        parent: View?,
        isSuccessMessageShow: Boolean,
        isFailureMessageShow: Boolean,
        key: String?,
        category: String?,
        pretty: Boolean?,
        page: Int?,
        orientation: String?,
        per_page: Int?,
        image_type: String?,
        q: String?,
        safesearch: Boolean?,
        order: String?,
    ): ApiState {
        val responseData: ResponseState?
        if (Constant.isNetWork(parent!!.context)) {
            val response = apiClient.getImagesPixabay(
                key = key,
                category = category,
                pretty = pretty,
                page = page,
                orientation = orientation,
                per_page = per_page,
                image_type = image_type,
                q = q,
                safesearch = safesearch,
                order = order
            )
            val responseBody = response.body()

            val responseMessage =
                response.message() ?: NetworkConstants.ErrorMsg.SOMETHING_WENT_WRONG
            responseData =
                ResponseState(
                    apiStatus = response.code(),
                    message = response.body(),
                    response = response,
                    responseBody = responseBody,
                    parentView = parent,
                    isFailureMessageShow = isFailureMessageShow,
                    isSuccessMessageShow = isSuccessMessageShow
                )
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

    suspend fun getCollectionPexels(
        parent: View?,
        isSuccessMessageShow: Boolean,
        isFailureMessageShow: Boolean,
        authKey: String? = Constants.AUTHORIZATION_KEY,
        pretty: Boolean?,
        page: Int?,
        per_page: Int?,
    ): ApiState {
        val responseData: ResponseState?
        if (Constant.isNetWork(parent!!.context)) {
            val response = apiClient.getCollectionPexels(
                authKey, pretty, page, per_page
            )
            val responseBody = response.body()

            val responseMessage =
                response.message() ?: NetworkConstants.ErrorMsg.SOMETHING_WENT_WRONG
            responseData =
                ResponseState(
                    apiStatus = response.code(),
                    message = response.body(),
                    response = response,
                    responseBody = responseBody,
                    parentView = parent,
                    isFailureMessageShow = isFailureMessageShow,
                    isSuccessMessageShow = isSuccessMessageShow
                )
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