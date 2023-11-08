package com.techiq.wallpaperwonders.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.databinding.RowNavigationDrawerSubmenuBinding
import com.techiq.wallpaperwonders.interfaces.OnItemClickedListener

class NavigationDrawerExpandableMenuAdapter(
    private val mContext: Context,
    private val dataSet: List<String>,
) :
    RecyclerView.Adapter<NavigationDrawerExpandableMenuAdapter.ItemViewHolder?>() {
    private var onItemClickedListener: OnItemClickedListener? = null
    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val mBinder: RowNavigationDrawerSubmenuBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.row_navigation_drawer_submenu,
            parent,
            false
        )
        return ItemViewHolder(mBinder)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.mBinder.tvItem.text = dataSet[position]
        if (selectedPosition == position) {
            holder.mBinder.tvItem.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.colorSecondaryLight
                )
            )
        } else {
            holder.mBinder.tvItem.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    android.R.color.transparent
                )
            )
        }
        holder.mBinder.tvItem.setTextColor(
            ContextCompat.getColor(
                mContext, R.color.white
            )
        )
    }


    override fun getItemCount(): Int {
        return dataSet.size
    }

    inner class ItemViewHolder(mBinder: RowNavigationDrawerSubmenuBinding) :
        RecyclerView.ViewHolder(mBinder.root), View.OnClickListener {
        var mBinder: RowNavigationDrawerSubmenuBinding

        init {
            this.mBinder = mBinder
            mBinder.tvItem.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            when (view.id) {
                R.id.tvItem -> {
                    if (onItemClickedListener != null) onItemClickedListener!!.onItemClicked(
                        adapterPosition
                    )
                    selectedPosition = adapterPosition
                    notifyDataSetChanged()
                }
            }
        }
    }
    fun setOnItemClickListener(onItemClickedListener: OnItemClickedListener?) {
        this.onItemClickedListener = onItemClickedListener
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}