package com.techiq.wallpaperwonders.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.databinding.RowFooterLoadingBinding
import com.techiq.wallpaperwonders.databinding.RowImageBinding
import com.techiq.wallpaperwonders.interfaces.LoadMoreListener
import com.techiq.wallpaperwonders.interfaces.OnItemClickedListener
import com.techiq.wallpaperwonders.model.response.pixabay.Hit
import com.techiq.wallpaperwonders.utils.GlideUtils
import com.techiq.wallpaperwonders.utils.OnScrollListener

class ImagesAdapter(
    glideUtils: GlideUtils,
    private val dataSet: List<Any?>,
    recyclerView: RecyclerView,
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var isLoading = false
    private val visibleThreshold = 1
    private var lastVisibleItem = 0
    private var totalItemCount = 0
    private lateinit var mLoadMoreListener: LoadMoreListener
    private val ROW_ITEM = 0
    private val ROW_PROG = 1
    var onItemClickedListener: OnItemClickedListener? = null
    private val glideUtils: GlideUtils

    init {
        this.glideUtils = glideUtils
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : OnScrollListener(linearLayoutManager) {
            override fun loadMore() {
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = linearLayoutManager.itemCount
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    if (mLoadMoreListener != null) {
                        mLoadMoreListener.onLoadMore()
                    }
                    isLoading = true
                }
            }

        })
    }

    fun setLoaded() {
        isLoading = false
    }

    fun setLoadMoreListener(mLoadMoreListener: LoadMoreListener?) {
        this.mLoadMoreListener = mLoadMoreListener!!
    }

    override fun getItemViewType(position: Int): Int {
        if (dataSet[position] == null) {
            return ROW_PROG
        }
        return ROW_ITEM
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_PROG -> {
                val mBinder: RowFooterLoadingBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.row_footer_loading,
                    parent,
                    false
                )
                ProgressViewHolder(mBinder)
            }

            ROW_ITEM -> {
                val mBinder: RowImageBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.row_image,
                    parent,
                    false
                )
                MyViewHolder(mBinder)
            }

            else -> {
                val mBinder: RowImageBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.row_image,
                    parent,
                    false
                )
                MyViewHolder(mBinder)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is MyViewHolder) {
            val previewURL =
                (dataSet[position] as Hit?)!!.webformatURL!!.replace("_640", "_340")


            glideUtils.loadImage(previewURL, (viewHolder).mBinder.ivImage)
        }
    }

    private inner class MyViewHolder(mBinder: RowImageBinding) :
        RecyclerView.ViewHolder(mBinder.root), View.OnClickListener {
        var mBinder: RowImageBinding

        init {
            this.mBinder = mBinder
            mBinder.ivImage.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.ivImage -> onItemClickedListener?.onItemClicked(adapterPosition)
            }
        }
    }

    private inner class ProgressViewHolder(mBinder: RowFooterLoadingBinding) :
        RecyclerView.ViewHolder(mBinder.root) {
        var mBinder: RowFooterLoadingBinding

        init {
            this.mBinder = mBinder
        }
    }

    fun setOnItemClickListener(onItemClickedListener: OnItemClickedListener?) {
        this.onItemClickedListener = onItemClickedListener
    }

}