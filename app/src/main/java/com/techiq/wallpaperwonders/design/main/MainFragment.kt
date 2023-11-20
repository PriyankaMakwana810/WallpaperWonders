package com.techiq.wallpaperwonders.design.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.adapters.ImagesAdapter
import com.techiq.wallpaperwonders.base.BaseFragment
import com.techiq.wallpaperwonders.databinding.FragmentMainBinding
import com.techiq.wallpaperwonders.interfaces.LoadMoreListener
import com.techiq.wallpaperwonders.interfaces.OnItemClickedListener
import com.techiq.wallpaperwonders.model.response.pixabay.PixabayImagesResponse
import com.techiq.wallpaperwonders.service.Status
import com.techiq.wallpaperwonders.utils.Constant.smallToastWithContext
import com.techiq.wallpaperwonders.utils.Constants
import com.techiq.wallpaperwonders.utils.Constants.POWERED_BY
import com.techiq.wallpaperwonders.utils.Constants.POWERED_BY_PEXELS
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainFragment : BaseFragment() {
    lateinit var binding: FragmentMainBinding
    private val viewModelMain by viewModels<MainViewModel>()
    private lateinit var pixabayImagesResponse: PixabayImagesResponse
    var imageCategory: String? = null
    var poweredBy = 0
    var pageNumber = 1
    var perPage = 100
    var list = ArrayList<Any?>()
    var imagesAdapter: ImagesAdapter? = null
    var isLast = false
    var selectedSortPosition = 0
    var sortType = arrayOf("popular", "latest")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        dataFromBundle
        binding.apply {
            viewModelMain.parentView.set((activity as MainActivity).binding.parentView)
            setObservers()
        }
        prepareLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Clear the existing menu items to avoid duplicates
                menu.clear()
                menuInflater.inflate(R.menu.main, menu) // Inflating your menu items
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_search -> {
                        // Handle the search action here
                        smallToastWithContext(requireContext(), "Search pending")
                        return true
                    }

                    R.id.action_sort -> {
                        val itemView = mActivity.findViewById<View>(R.id.action_sort)
                        val wrapper: Context =
                            android.view.ContextThemeWrapper(mActivity, R.style.MyPopupTheme)
                        val popup = androidx.appcompat.widget.PopupMenu(
                            wrapper,
                            itemView!!,
                            Gravity.END,
                            0,
                            R.style.MyPopupTheme
                        )
                        val inflater: MenuInflater = popup.menuInflater
                        inflater.inflate(R.menu.popup_menu, popup.menu)
                        popup.menu.getItem(selectedSortPosition).isChecked = true
                        popup.setOnMenuItemClickListener(object :
                            PopupMenu.OnMenuItemClickListener,
                            androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener {
                            override fun onMenuItemClick(item: MenuItem): Boolean {
                                when (item.itemId) {
                                    R.id.popular -> if (selectedSortPosition != 0) {
                                        selectedSortPosition = 0
                                        try {
                                            resetQueryParameters()
                                            imageCategory = "popular"
                                            fetchImages(true)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            Log.e("TAG", "onMenuItemClick: ${e.message} ")
                                        }
                                    }

                                    R.id.latest -> if (selectedSortPosition != 1) {
                                        selectedSortPosition = 1
                                        try {
                                            resetQueryParameters()
                                            imageCategory = "latest"
                                            fetchImages(true)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            Log.e("TAG", "onMenuItemClick: ${e.message} ")
                                        }
                                    }
                                }
                                return true
                            }
                        })
                        popup.show()
                        return true
                    }
                }
                return true
            }
        })

    }

    private fun fetchImages(shouldShowProgressBar: Boolean) {
        poweredBy = sharedPref.getInt(POWERED_BY)
        if (poweredBy == POWERED_BY_PEXELS) {
            getImagesFromPexels(shouldShowProgressBar)
        } else {
            getImagesFromPixabay(shouldShowProgressBar)
        }
    }


    private fun prepareLayout() {
        poweredBy = sharedPref.getInt(POWERED_BY)
        setUpRecyclerView()
        try {
            fetchImages(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.llError.setOnClickListener {
            binding.llError.visibility = View.GONE
            resetQueryParameters()
            try {
                fetchImages(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setObservers() {
        viewModelMain.pixabayImagesResponse.observe(viewLifecycleOwner) {
            Log.d("TAG", "PixabayImagesResponse: " + it.response.toString())
            binding.progressBar.visibility = View.GONE
            when (it.localStatus) {
                Status.SUCCESS -> {
                    val response = it.response as PixabayImagesResponse
                    pixabayImagesResponse = response
                    if (list.size != 0) {
                        list.removeAt(list.size - 1)
                        imagesAdapter?.notifyItemRemoved(list.size)
                    }
                    it.response.hits?.let { list.addAll(it) }
                    refreshData()
                    if (it.response.totalHits!! > pageNumber * perPage) {
                        pageNumber += 1
                    } else {
                        isLast = true
                    }
                    Log.e("Response: ", it.response.toString())
                }

                Status.ERROR -> {
                    smallToastWithContext(requireContext(), it.localError.toString())
                }

                else -> {
                    smallToastWithContext(requireContext(), it.localError.toString())
                }
            }
        }
    }

    private fun resetQueryParameters() {
        pageNumber = 1
        list.clear()
        imagesAdapter?.notifyDataSetChanged()
        isLast = false
    }

    private fun setUpRecyclerView() {
        val gridLayoutManager = GridLayoutManager(mActivity, 3)

        binding.rvImages.layoutManager = gridLayoutManager
        imagesAdapter = mContext.let {
            ImagesAdapter(
                glideUtils,
                list,
                binding.rvImages
            )
        }
        imagesAdapter?.setLoadMoreListener(object : LoadMoreListener {
            override fun onLoadMore() {
                if (!isLast && list.size != 0) {
                    list.add(null)
                    imagesAdapter?.notifyItemInserted(list.size - 1)
                    try {
                        fetchImages(false)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
        imagesAdapter?.setOnItemClickListener(object : OnItemClickedListener {
            override fun onItemClicked(position: Int) {
                smallToastWithContext(requireContext(), "image clicked")
            }

            override fun onSubItemClicked(position: Int, subPosition: Int) {
            }
        })
        binding.rvImages.adapter = imagesAdapter
    }

    private val dataFromBundle: Unit
        get() {
            if (arguments?.containsKey(Constants.KEY_IMAGE_CATEGORY) == true) {
                imageCategory = requireArguments().getString(Constants.KEY_IMAGE_CATEGORY, "")
            }
        }

    private fun getImagesFromPixabay(shouldShowProgressBar: Boolean) {
        if (shouldShowProgressBar) binding.progressBar.visibility = View.VISIBLE
        viewModelMain.getPixabayImages(
            Constants.PIXABAY_API_KEY,
            imageCategory!!.lowercase(
                Locale.getDefault()
            ),
            true,
            pageNumber,
            getString(R.string.vertical),
            perPage,
            "photo",
            "",
            true,
            sortType[selectedSortPosition]
        )

    }

    private fun getImagesFromPexels(shouldShowProgressBar: Boolean) {
        if (shouldShowProgressBar) binding.progressBar.visibility = View.VISIBLE
    }

    private fun refreshData() {
        imagesAdapter?.notifyDataSetChanged()
        imagesAdapter?.setLoaded()
        if (list.size > 0) {
            binding.llError.visibility = View.GONE
            binding.rvImages.visibility = View.VISIBLE
        } else {
            binding.llError.visibility = View.VISIBLE
            binding.rvImages.visibility = View.GONE
        }
    }
}