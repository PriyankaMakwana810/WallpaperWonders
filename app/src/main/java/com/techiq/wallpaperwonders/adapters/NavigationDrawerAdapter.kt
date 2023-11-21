package com.techiq.wallpaperwonders.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.databinding.RowNavigationDrawerBinding
import com.techiq.wallpaperwonders.interfaces.OnItemClickedListener
import com.techiq.wallpaperwonders.model.PexelsModel
import com.techiq.wallpaperwonders.utils.Constants
import com.techiq.wallpaperwonders.utils.PrefUtils

class NavigationDrawerAdapter(private val mContext: Context, private val dataSet: List<Any>) :
    RecyclerView.Adapter<NavigationDrawerAdapter.ItemViewHolder?>() {
    private var onItemClickedListener: OnItemClickedListener? = null
    var selectedPosition = 1
    var childAdapter: NavigationDrawerExpandableMenuAdapter? = null
    var listNavigationSubItems: MutableList<String> = ArrayList()
    var poweredBy = Constants.POWERED_BY_PIXABAY
    lateinit var sharedPref: PrefUtils
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val mBinder: RowNavigationDrawerBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.row_navigation_drawer,
            parent,
            false
        )
        sharedPref = PrefUtils(mContext)
        return ItemViewHolder(mBinder)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        poweredBy = sharedPref.getInt(Constants.POWERED_BY)
        if (poweredBy == Constants.POWERED_BY_PIXABAY) {
            holder.binding.tvItem.text = dataSet[position] as CharSequence?
        } else {
            val item = dataSet[position] as PexelsModel
            holder.binding.tvItem.text = item.menu
        }
        if (selectedPosition == position) {
            holder.binding.tvItem.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.colorSecondaryLight
                )
            )
        } else {
            holder.binding.tvItem.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    android.R.color.transparent
                )
            )
        }
        if (position == 0) holder.binding.tvItem.setTextColor(
            ContextCompat.getColor(
                mContext,
                R.color.colorPrimary
            )
        ) else holder.binding.tvItem.setTextColor(
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
        var binding: RowNavigationDrawerBinding

        init {
            this.binding = mBinder
            mBinder.tvItem.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            when (view.id) {
                R.id.tvItem -> {
                    if (poweredBy == Constants.POWERED_BY_PIXABAY) {
                        if (onItemClickedListener != null) onItemClickedListener!!.onItemClicked(
                            adapterPosition
                        )
                        selectedPosition = adapterPosition
                        notifyDataSetChanged()
                    } else {
                        when (adapterPosition) {
                            1, 2, 3 -> {
                                if (!binding.rvSubMenu.isVisible) {
                                    binding.rvSubMenu.visibility = View.VISIBLE
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
                                    binding.rvSubMenu.layoutManager = layoutManager
                                    binding.rvSubMenu.adapter = childAdapter
                                    selectedPosition = adapterPosition
                                    notifyDataSetChanged()
                                } else {
                                    binding.rvSubMenu.visibility = View.GONE
                                    listNavigationSubItems.clear()
                                }
                            }

                            else -> {
                                if (onItemClickedListener != null) onItemClickedListener!!.onItemClicked(
                                    adapterPosition
                                )
                                selectedPosition = adapterPosition
                                notifyDataSetChanged()
                            }
                        }
                    }
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