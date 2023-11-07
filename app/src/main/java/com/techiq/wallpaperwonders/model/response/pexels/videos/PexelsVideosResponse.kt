package com.techiq.wallpaperwonders.model.response.pexels.videos

data class PexelsVideosResponse(
    val page: Int,
    val per_page: Int,
    val total_results: Int,
    val url: String,
    val videos: List<Video>
)
data class Video(
    val duration: Int,
    val height: Int,
    val id: Int,
    val image: String,
    val url: String,
    val user: User,
    val video_files: List<VideoFile>,
    val video_pictures: List<VideoPicture>,
    val width: Int
)
data class VideoPicture(
    val id: Int,
    val nr: Int,
    val picture: String
)

