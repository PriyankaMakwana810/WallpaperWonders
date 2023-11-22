package com.techiq.wallpaperwonders.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.databinding.RowVideoBinding
import com.techiq.wallpaperwonders.interfaces.OnItemClickedListener
import com.techiq.wallpaperwonders.utils.GlideUtils
import java.io.File
import java.net.URLConnection

class DownloadedWallpapersAdapter(
    glideUtils: GlideUtils,
    private val dataSet: Array<File>,
) : RecyclerView.Adapter<DownloadedWallpapersAdapter.MyViewHolder?>() {
    lateinit var onItemClickedListener: OnItemClickedListener
    var glideUtils: GlideUtils

    init {
        this.glideUtils = glideUtils
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val mBinder: RowVideoBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_video,
            parent,
            false
        )
        return MyViewHolder(mBinder)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
        val file = dataSet[position].absolutePath
        if (isImageFile(file)) {
            viewHolder.mBinder.videoIndicator.visibility = View.GONE
            glideUtils.loadImageFromFile(dataSet[position], viewHolder.mBinder.ivVideo)
        } else {
            viewHolder.mBinder.videoIndicator.visibility = View.VISIBLE
            glideUtils.loadImageFromFile(dataSet[position], viewHolder.mBinder.ivVideo)
        }
    }

    private fun isImageFile(path: String?): Boolean {
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType != null && mimeType.startsWith("image")
    }

    inner class MyViewHolder internal constructor(mBinder: RowVideoBinding) :
        RecyclerView.ViewHolder(mBinder.root), View.OnClickListener {
        var mBinder: RowVideoBinding

        init {
            this.mBinder = mBinder
            mBinder.ivVideo.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.ivVideo ->if (onItemClickedListener != null) onItemClickedListener.onItemClicked(
                    adapterPosition
                )
            }
        }
    }

    fun setOnItemClickListener(onItemClickedListener: OnItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener
    }
}