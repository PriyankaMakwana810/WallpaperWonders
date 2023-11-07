package com.techiq.wallpaperwonders.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.techiq.wallpaperwonders.R
import java.io.File
@GlideModule
class GlideUtils(private val context: Context) {
    
    fun loadCircleImage(image: String?, imageView: ImageView?) {
        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .priority(Priority.NORMAL)
        Glide.with(context)
            .load(image)
            .apply(options)
            .apply(RequestOptions.circleCropTransform())
            .into(imageView!!)
    }

    fun loadImage(image: String?, imageView: ImageView?) {
        val options: RequestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .priority(Priority.NORMAL)
        Glide.with(context)
            .load(image)
            .apply(options)
            .into(imageView!!)
    }

    fun loadImageFromFile(file: File?, imageView: ImageView?) {
        val options: RequestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .priority(Priority.NORMAL)
        Glide.with(context)
            .load(file)
            .apply(options)
            .into(imageView!!)
    }
}