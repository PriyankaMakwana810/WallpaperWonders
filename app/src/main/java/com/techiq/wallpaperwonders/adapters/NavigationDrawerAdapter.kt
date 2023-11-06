package com.techiq.wallpaperwonders.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.databinding.RowNavigationDrawerBinding
import com.techiq.wallpaperwonders.interfaces.OnItemClickedListener
import com.techiq.wallpaperwonders.model.PexelsModel
import com.techiq.wallpaperwonders.utils.Constants

class NavigationDrawerAdapter(private val mContext: Context, private val dataSet: List<Any>) :
    RecyclerView.Adapter<NavigationDrawerAdapter.ItemViewHolder?>() {
    private var onItemClickedListener: OnItemClickedListener? = null
    var selectedPosition = 1

    //    var childAdapter: NavigationDrawerExpandableMenuAdapter? = null2
//    var listNavigationSubItems: MutableList<String> = ArrayList()
    var poweredBy = Constants.POWERED_BY_PIXABAY


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val mBinder: RowNavigationDrawerBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.row_navigation_drawer,
            parent,
            false
        )
//        poweredBy = sessionManager?.getDataByKey(Constants.POWERED_BY, 0)!!
        return ItemViewHolder(mBinder)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (poweredBy == Constants.POWERED_BY_PIXABAY) {
            holder.mBinder.tvItem.text = dataSet[position] as CharSequence?
        } else {
            val item = dataSet[position] as PexelsModel
            holder.mBinder.tvItem.text = item.menu
        }
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
        if (position == 0) holder.mBinder.tvItem.setTextColor(
            ContextCompat.getColor(
                mContext,
                R.color.colorPrimary
            )
        ) else holder.mBinder.tvItem.setTextColor(
            ContextCompat.getColor(
                mContext, R.color.black
            )
        )
    }


    override fun getItemCount(): Int {
        return dataSet.size
    }

    inner class ItemViewHolder(mBinder: RowNavigationDrawerBinding) :
        RecyclerView.ViewHolder(mBinder.root), View.OnClickListener {
        var mBinder: RowNavigationDrawerBinding

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
                    /*if (poweredBy == Constants.POWERED_BY_PIXABAY) {
                        if (onItemClickedListener != null) onItemClickedListener!!.onItemClicked(
                            adapterPosition
                        )
                        selectedPosition = adapterPosition
                        notifyDataSetChanged()
                    } else {
                        when (adapterPosition) {
                            0 -> {
                                if (onItemClickedListener != null) onItemClickedListener!!.onItemClicked(
                                    adapterPosition
                                )
                                selectedPosition = adapterPosition
                                notifyDataSetChanged()
                            }

                            1, 2, 3 -> {
                                if (!mBinder.rvSubMenu.isVisible) {
                                    mBinder.rvSubMenu.visibility = View.VISIBLE
                                    val item = dataSet[adapterPosition] as PexelsModel
                                    childAdapter = item.subMenu?.let {
                                        NavigationDrawerExpandableMenuAdapter(mContext, it)
                                    }
                                    childAdapter!!.setOnItemClickListener(object :
                                        OnItemClickedListener {
                                        override fun onItemClicked(position: Int) {
                                            if (onItemClickedListener != null) onItemClickedListener!!.onSubItemClicked(
                                                adapterPosition, position
                                            )
                                        }

                                        override fun onSubItemClicked(
                                            position: Int,
                                            subPosition: Int,
                                        ) {
                                        }
                                    })
                                    val layoutManager = LinearLayoutManager(mContext)
                                    mBinder.rvSubMenu.layoutManager = layoutManager
                                    mBinder.rvSubMenu.adapter = childAdapter
                                    selectedPosition = adapterPosition
                                    notifyDataSetChanged()
                                } else {
                                    mBinder.rvSubMenu.visibility = View.GONE
                                    listNavigationSubItems.clear()
                                }
                            }

                            else -> {
//                                setWallpaper(adapterPosition)
                                if (onItemClickedListener != null) onItemClickedListener!!.onItemClicked(
                                    adapterPosition
                                )
                                selectedPosition = adapterPosition
                                notifyDataSetChanged()
                            }
                        }
                    }*/
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