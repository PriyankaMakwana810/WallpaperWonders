package com.techiq.wallpaperwonders.model.response.pixabay


import com.google.gson.annotations.SerializedName

data class PixabayResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: PixabayImagesResponse,
    @SerializedName("message")
    val message: String,
)