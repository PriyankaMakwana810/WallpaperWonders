package com.techiq.wallpaperwonders.model

data class PexelsModel(
    val menu: String,
    val subMenu: List<String>? = null,
    val subId: List<String>? = null,
)
