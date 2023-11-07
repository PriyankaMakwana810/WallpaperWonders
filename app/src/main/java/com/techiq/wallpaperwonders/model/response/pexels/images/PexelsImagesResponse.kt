package com.techiq.wallpaperwonders.model.response.pexels.images

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PexelsImagesResponse {
    @SerializedName("page")
    @Expose
    var page: Int? = null

    @SerializedName("per_page")
    @Expose
    var per_page: Int? = null

    @SerializedName("photos")
    @Expose
    var photos: List<PexelsPhotos>? = null
}