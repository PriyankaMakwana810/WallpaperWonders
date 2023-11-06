package com.techiq.wallpaperwonders.interfaces

interface OnItemClickedListener {
    fun onItemClicked(position: Int)
}

interface OnSubItemClickedListener {
    fun onSubItemClicked(position: Int, subPosition: Int)

}