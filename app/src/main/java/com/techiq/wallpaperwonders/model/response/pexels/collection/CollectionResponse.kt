package com.techiq.wallpaperwonders.model.response.pexels.collection

data class CollectionResponse(
    val collections: List<CollectionItem>,
    val next_page: String,
    val page: Int,
    val per_page: Int,
    val prev_page: String,
    val total_results: Int
)