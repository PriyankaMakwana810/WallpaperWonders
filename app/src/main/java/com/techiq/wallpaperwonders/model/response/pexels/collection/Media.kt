package com.techiq.wallpaperwonders.model.response.pexels.collection

import com.techiq.wallpaperwonders.model.response.pexels.images.Src
import com.techiq.wallpaperwonders.model.response.pexels.videos.User
import com.techiq.wallpaperwonders.model.response.pexels.videos.VideoFile
import com.techiq.wallpaperwonders.model.response.pexels.videos.VideoPicture

data class Media(
    val avg_color: String,
    val duration: Int,
    val full_res: Any,
    val height: Int,
    val id: Int,
    val image: String,
    val liked: Boolean,
    val photographer: String,
    val photographer_id: Int,
    val photographer_url: String,
    val src: Src,
    val tags: List<Any>,
    val type: String,
    val url: String,
    val user: User,
    val video_files: List<VideoFile>,
    val video_pictures: List<VideoPicture>,
    val width: Int
)