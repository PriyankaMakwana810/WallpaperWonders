package com.techiq.wallpaperwonders.model.response.pixabay

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PixabayImagesResponse {
    @SerializedName("totalHits")
    var totalHits: Int? = null

    @SerializedName("hits")
    var hits: List<Hit>? = null

    @SerializedName("total")
    var total: Int? = null
}
