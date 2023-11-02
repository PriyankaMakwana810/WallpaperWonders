package com.techiq.wallpaperwonders.Model.response.SignIn


import com.google.gson.annotations.SerializedName


data class SigninResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val data: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("ModelState")
    val modelState: ModelState
)