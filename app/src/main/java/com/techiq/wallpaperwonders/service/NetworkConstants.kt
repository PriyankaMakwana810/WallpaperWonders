package com.techiq.wallpaperwonders.service

import android.util.Log
import com.techiq.wallpaperwonders.utils.Constant
import com.techiq.wallpaperwonders.utils.PrefUtils
import org.json.JSONObject

object NetworkConstants {
    private const val TRANSLATE_DATA = "TranslateData"
    private fun getMessageFromApi(key: String, preferences: PrefUtils): String =
        if (preferences.getString(TRANSLATE_DATA).isNotEmpty() && JSONObject(
                preferences.getString(
                    TRANSLATE_DATA
                )
            ).has(key)
        ) JSONObject(preferences.getString(TRANSLATE_DATA)).optString(key)
        else key

    object ApiUrl {
        const val HOME_URL = "https://www.google.com/"
        const val LOGIN_URL = "https://linxsystems.flowgear.net/"

        //  const val SIGNIN_URL = "https://dummyjson.com/"
        const val SIGNIN_URL = "http://restapi.adequateshop.com/api/authaccount/"
        private var pixabayURL: String = "http://pixabay.com/"
        private var pexelsURL: String = "https://api.pexels.com/"


    }

    object ApiCode {
        const val SUCCESS_CODE = 200
        const val INTERNAL_ERROR_CODE = 500
        const val ERROR_CODE_CREDENTIAL = 400
        const val FAILURE_CODE_422 = 422
        const val UN_AUTHORIZE = 401
        const val FAILURE_CODE_202 = 202
        const val QUEUE_CODE = 201
        const val NO_INTERNET = 203
        const val UN_CONDITIONAL_EXCEPTION = 501
        const val SESSION_TIMEOUT_EXCEPTION = 502
        const val SESSION_EXCEPTION = 503
    }

    object ErrorMsg {
        const val SOMETHING_WENT_WRONG = "Something went wrong"
        const val NO_NETWORK = "Please check your Internet Connection"
        const val REQUEST_PARAMETER_MISSING = "Some data is missing"
    }

    fun getApiStateResponseStatus(
        responseState: ResponseState?, prefUtils: PrefUtils, funListener: (Int, Any?) -> Any?,
    ): ApiState {
        responseState.apply {
            try {
                when (this) {
                    null -> {
                        return ApiState(
                            localStatus = Status.ERROR, localError = ErrorMsg.SOMETHING_WENT_WRONG
                        )
                    }

                    else -> {
                        if (isNetworkAvailable == false) {
                            Constant.smallToastWithContext(
                                parentView!!.context, ErrorMsg.NO_NETWORK
                            )
                            return ApiState(
                                localError = ErrorMsg.NO_NETWORK, localStatus = Status.ERROR
                            )

                        }
                        when (apiStatus) {
                            ApiCode.SUCCESS_CODE -> {
                                if (isNetworkAvailable == true) {
                                    Constant.logI("TAG", "getApiStateResponseStatus: delete")
                                    // funListener.invoke(Constant.DELETE_DATA_IN_DATABASE, null)
                                    responseBody?.let {
                                        Constant.logI("TAG", "getApiStateResponseStatus: insert")
                                    }
                                }
                                if (parentView != null && isSuccessMessageShow == true) Constant.smallToastWithContext(
                                    parentView.context,
                                    getMessageFromApi(message.toString(), prefUtils),
                                )
                                return ApiState(
                                    Status.SUCCESS,
                                    getMessageFromApi(message.toString(), prefUtils),
                                    responseBody
                                )
                            }

                            ApiCode.QUEUE_CODE -> {
                                if (parentView != null && isSuccessMessageShow == true) Constant.smallToastWithContext(
                                    parentView.context,
                                    getMessageFromApi(message.toString(), prefUtils),
                                )
                                return ApiState(
                                    Status.QUEUE,
                                    getMessageFromApi(message.toString(), prefUtils),
                                    responseBody
                                )
                            }

                            ApiCode.FAILURE_CODE_202 -> {
                                if (parentView != null && isFailureMessageShow == true) Constant.smallToastWithContext(
                                    parentView.context,
                                    getMessageFromApi(message.toString(), prefUtils),

                                    )
                                return ApiState(
                                    Status.FAIL,
                                    getMessageFromApi(message.toString(), prefUtils),
                                    responseBody
                                )
                            }

                            ApiCode.UN_AUTHORIZE -> {
                                if (parentView != null && isFailureMessageShow == true) Constant.smallToastWithContext(
                                    parentView.context,
                                    getMessageFromApi(
                                        response?.message().toString(), prefUtils
                                    ),
                                )
                                return ApiState(
                                    Status.UNAUTHORISED, getMessageFromApi(
                                        response?.message().toString(), prefUtils
                                    ), responseBody
                                )
                            }

                            ApiCode.FAILURE_CODE_422 -> {
                                if (parentView != null && isFailureMessageShow == true) Constant.smallToastWithContext(
                                    parentView.context,
                                    getMessageFromApi(
                                        response?.message().toString(), prefUtils
                                    ),
                                )
                                return ApiState(
                                    Status.FAIL, getMessageFromApi(
                                        response?.message().toString(), prefUtils
                                    ), responseBody
                                )
                            }

                            else -> {
                                if (parentView != null && isFailureMessageShow == true) Constant.smallToastWithContext(
                                    parentView.context,
                                    getMessageFromApi(message.toString(), prefUtils)
                                )
                                return ApiState(
                                    Status.ERROR,
                                    getMessageFromApi(message.toString(), prefUtils),
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.localizedMessage?.let { Constant.logE("catch Error", it) }
                return ApiState(
                    localStatus = Status.ERROR, localError = ErrorMsg.SOMETHING_WENT_WRONG
                )
            }
        }
    }


    fun getApiStateResponseStatus(
        responseState: ResponseState?, prefUtils: PrefUtils,
    ): ApiState {
        responseState.apply {
            when (this) {

                null -> {
                    return ApiState(
                        localStatus = Status.ERROR, localError = ErrorMsg.SOMETHING_WENT_WRONG
                    )
                }

                else -> {
                    if (isNetworkAvailable == false) {
                        Constant.smallToastWithContext(
                            parentView!!.context, ErrorMsg.NO_NETWORK,
                        )
                        return ApiState(
                            localError = ErrorMsg.NO_NETWORK, localStatus = Status.ERROR
                        )
                    }
                    when (apiStatus) {
                        Log.d(
                            "TAG",
                            "getApiStateResponseStatus: " + "apistatus" + apiStatus + "resbody" + responseBody
                        ),


                        ApiCode.SUCCESS_CODE,
                        -> {
                            if (parentView != null && isSuccessMessageShow == true) Constant.smallToastWithContext(
                                parentView.context,
                                getMessageFromApi(message.toString(), prefUtils),

                                )
                            return ApiState(
                                Status.SUCCESS,
                                getMessageFromApi(message.toString(), prefUtils),
                                responseBody
                            )
                        }

                        ApiCode.QUEUE_CODE -> {
                            if (parentView != null && isSuccessMessageShow == true) Constant.smallToastWithContext(
                                parentView.context,
                                getMessageFromApi(message.toString(), prefUtils),

                                )
                            return ApiState(
                                Status.QUEUE,
                                getMessageFromApi(message.toString(), prefUtils),
                                responseBody
                            )
                        }

                        ApiCode.ERROR_CODE_CREDENTIAL -> {
                            if (parentView != null && isFailureMessageShow == true) Constant.smallToastWithContext(
                                parentView.context,
                                getMessageFromApi("Invalid credential", prefUtils),

                                )
                            return ApiState(
                                Status.FAIL,
                                getMessageFromApi(message.toString(), prefUtils),
                                responseBody
                            )
                        }

                        ApiCode.FAILURE_CODE_202 -> {
                            if (parentView != null && isFailureMessageShow == true) Constant.smallToastWithContext(
                                parentView.context,
                                getMessageFromApi(message.toString(), prefUtils),

                                )
                            return ApiState(
                                Status.FAIL,
                                getMessageFromApi(message.toString(), prefUtils),
                                responseBody
                            )
                        }

                        ApiCode.UN_AUTHORIZE -> {
                            if (parentView != null && isFailureMessageShow == true) Constant.smallToastWithContext(
                                parentView.context,
                                getMessageFromApi(
                                    response?.message().toString(), prefUtils
                                ),
                            )
                            return ApiState(
                                Status.UNAUTHORISED, getMessageFromApi(
                                    response?.message().toString(), prefUtils
                                ), responseBody
                            )
                        }

                        ApiCode.FAILURE_CODE_422 -> {
                            if (parentView != null && isFailureMessageShow == true) Constant.smallToastWithContext(
                                parentView.context,
                                getMessageFromApi(
                                    response?.message().toString(), prefUtils
                                ),
                            )
                            return ApiState(
                                Status.FAIL, getMessageFromApi(
                                    response?.message().toString(), prefUtils
                                ), responseBody
                            )
                        }

                        else -> {
                            if (parentView != null && isFailureMessageShow == true) Constant.smallToastWithContext(
                                parentView.context,
                                getMessageFromApi(message.toString(), prefUtils),
                            )
                            return ApiState(
                                Status.ERROR,
                                getMessageFromApi(message.toString(), prefUtils),
                            )
                        }
                    }
                }
            }
        }
    }
}