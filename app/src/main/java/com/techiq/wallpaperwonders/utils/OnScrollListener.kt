package com.techiq.wallpaperwonders.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by RajeshKushvaha on 27-04-17
 */
abstract class OnScrollListener(linearLayoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {
    private var isLast = false
    private val visibleThreshold = 1
    private var lastVisibleItem = 0
    private var totalItemCount = 0
    private val linearLayoutManager: LinearLayoutManager

    init {
        this.linearLayoutManager = linearLayoutManager
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        recyclerView.let { super.onScrolled(it, dx, dy) }
        totalItemCount = linearLayoutManager.itemCount
        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
        if (!isLast && totalItemCount <= lastVisibleItem + visibleThreshold) {
            loadMore()
            isLast = true
        }
    }

    fun setIsLast(isLast: Boolean) {
        this.isLast = isLast
    }

    abstract fun loadMore()
}