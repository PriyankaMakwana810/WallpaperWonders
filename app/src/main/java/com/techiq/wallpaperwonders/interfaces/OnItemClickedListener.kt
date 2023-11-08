package com.techiq.wallpaperwonders.interfaces

interface OnItemClickedListener {
    fun onItemClicked(position: Int)
    fun onSubItemClicked(position: Int, subPosition: Int)
}
