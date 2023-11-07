package com.techiq.wallpaperwonders.model.response.pexels.videos

data class VideoFile(
    val file_type: String,
    val height: Int,
    val id: Int,
    val link: String,
    val quality: String,
    val width: Int
)

data class User(
    val id: Int,
    val name: String,
    val url: String
)