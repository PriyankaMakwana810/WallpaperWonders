package com.techiq.wallpaperwonders.model.response.pixabay

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Hit {
    @SerializedName("row_type")
    @Expose
    var row_type: String? = null

    @SerializedName("largeImageURL")
    @Expose
    var largeImageURL: String? = null

    @SerializedName("webformatHeight")
    @Expose
    var webformatHeight: Int? = null

    @SerializedName("webformatWidth")
    @Expose
    var webformatWidth: Int? = null

    @SerializedName("likes")
    @Expose
    var likes: Int? = null

    @SerializedName("imageWidth")
    @Expose
    var imageWidth: Int? = null

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("views")
    @Expose
    var views: Int? = null

    @SerializedName("comments")
    @Expose
    var comments: Int? = null

    @SerializedName("pageURL")
    @Expose
    var pageURL: String? = null

    @SerializedName("imageHeight")
    @Expose
    var imageHeight: Int? = null

    @SerializedName("webformatURL")
    @Expose
    var webformatURL: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("previewHeight")
    @Expose
    var previewHeight: Int? = null

    @SerializedName("tags")
    @Expose
    var tags: String? = null

    @SerializedName("downloads")
    @Expose
    var downloads: Int? = null

    @SerializedName("user")
    @Expose
    var user: String? = null

    @SerializedName("favorites")
    @Expose
    var favorites: Int? = null

    @SerializedName("imageSize")
    @Expose
    var imageSize: Int? = null

    @SerializedName("previewWidth")
    @Expose
    var previewWidth: Int? = null

    @SerializedName("userImageURL")
    @Expose
    var userImageURL: String? = null

    @SerializedName("previewURL")
    @Expose
    var previewURL: String? = null

    @SerializedName("fullHDURL")
    @Expose
    var fullHDURL: String? = null
}