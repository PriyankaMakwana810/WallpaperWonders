package com.techiq.wallpaperwonders.collections

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.techiq.wallpaperwonders.adapters.CollectionsAdapter
import com.techiq.wallpaperwonders.base.BaseFragment
import com.techiq.wallpaperwonders.databinding.FragmentCollectionsBinding
import com.techiq.wallpaperwonders.design.fullscreen.FullScreenViewActivity
import com.techiq.wallpaperwonders.design.main.MainActivity
import com.techiq.wallpaperwonders.design.main.MainViewModel
import com.techiq.wallpaperwonders.interfaces.LoadMoreListener
import com.techiq.wallpaperwonders.interfaces.OnItemClickedListener
import com.techiq.wallpaperwonders.model.response.pexels.collection.CollectionByIdResponse
import com.techiq.wallpaperwonders.model.response.pexels.collection.Media
import com.techiq.wallpaperwonders.service.Status
import com.techiq.wallpaperwonders.utils.Constant
import com.techiq.wallpaperwonders.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionsFragment : BaseFragment() {
    lateinit var binding: FragmentCollectionsBinding
    private val viewModelMain by viewModels<MainViewModel>()
    var collectionCategory: String? = null
    var pageNumber = 1
    var perPage = 100
    var list = ArrayList<Any?>()
    var collectionAdapter: CollectionsAdapter? = null
    var index = 21
    var isLast = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        dataFromBundle
        binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        dataFromBundle
        binding.apply {
            viewModelMain.parentView.set((activity as MainActivity).binding.parentView)
            setObservers()
        }
        prepareLayout()
        return binding.root
    }

    private val dataFromBundle: Unit
        get() {
            if (arguments?.containsKey(Constants.KEY_COLLECTION_CATEGORY) == true) {
                collectionCategory =
                    requireArguments().getString(Constants.KEY_COLLECTION_CATEGORY, "")
            }
        }

    private fun prepareLayout() {
        setUpRecyclerView()
        try {
            getCollectionFromPexels(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.llError.setOnClickListener {
            binding.llError.visibility = View.GONE
            resetQueryParameters()
            try {
                getCollectionFromPexels(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setObservers() {
        viewModelMain.pexelsCollectionByIdResponse.observe(viewLifecycleOwner) {
            Log.d("TAG", "Pexels Videos Response: " + it.response.toString())
            binding.progressBar.visibility = View.GONE
            when (it.localStatus) {
                Status.SUCCESS -> {
                    val response = it.response as CollectionByIdResponse
                    if (list.size != 0) {
                        list.removeAt(list.size - 1)
                        collectionAdapter?.notifyItemRemoved(list.size)
                    }
                    response.media.let { list.addAll(it) }
                    refreshData()
                    if (response.per_page < pageNumber * perPage) {
                        pageNumber += 1
                    } else {
                        isLast = true
                    }
                    Log.e("Response: ", it.response.toString())
                }

                Status.ERROR -> {
                    Constant.smallToastWithContext(requireContext(), it.localError.toString())
                }

                else -> {
                    Constant.smallToastWithContext(requireContext(), it.localError.toString())
                }
            }

        }
        /*        viewModel.collectionMediaData.observe(viewLifecycleOwner) {
                    viewModel.isLoading.value = false
                    if (it != null) {
                        binding?.progressBar?.visibility = View.GONE
                        if (list.size != 0) {
                            list.removeAt(list.size - 1)
                            collectionAdapter?.notifyItemRemoved(list.size)
                        }
                        it.media.let { list.addAll(it) }
                        refreshData()
                        if (it.per_page < pageNumber * perPage) {
                            pageNumber += 1
                        } else {
                            isLast = true
                        }
                        Log.e("onAuthenticateButtonClick: ", it.toString())
                    } else {
                        showToastShort("error while getting data!!")
                    }
                }*/
    }

    private fun resetQueryParameters() {
        pageNumber = 1
        index = 21
        list.clear()
        collectionAdapter?.notifyDataSetChanged()
        isLast = false
    }

    private fun setUpRecyclerView() {
        val gridLayoutManager = GridLayoutManager(mActivity, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                var spanCount = 1
                if (list.size > 0) {
                    spanCount = if (list[position] == null /*|| list[position] is NativeAd*/) {
                        3
                    } else {
                        1
                    }
                }
                return spanCount
            }
        }
        binding.rvVideosPhotos.layoutManager = gridLayoutManager
        collectionAdapter = CollectionsAdapter(
            glideUtils,
            list,
            binding.rvVideosPhotos
        )
        collectionAdapter?.setLoadMoreListener(object : LoadMoreListener {
            override fun onLoadMore() {
                if (!isLast && list.size != 0) {
                    list.add(null)
                    collectionAdapter?.notifyItemInserted(list.size - 1)
                    try {
                        getCollectionFromPexels(false)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
        collectionAdapter?.setOnItemClickListener(object : OnItemClickedListener {
            override fun onItemClicked(position: Int) {
                val intent = Intent(mActivity, FullScreenViewActivity::class.java)
                if ((list[position] as Media).type == "Video") {
                    val video: Media? = list[position] as Media?
                    intent.putExtra(
                        Constants.KEY_VIDEO_LINK,
                        video?.video_files?.filter { it.quality == "hd" }?.get(0)?.link
                    )
                    intent.putExtra(Constants.KEY_VIDEO_ID, video?.id.toString())
                } else {
                    val photos: Media? = list[position] as Media?
                    intent.putExtra(Constants.KEY_PREVIEW_IMAGE_LINK, photos?.src?.original)
                    intent.putExtra(Constants.KEY_LARGE_IMAGE_LINK, photos?.src?.original)
                    intent.putExtra(Constants.KEY_IMAGE_ID, photos?.id.toString())
                }
                startActivity(intent)

            }

            override fun onSubItemClicked(position: Int, subPosition: Int) {
            }
        })
        binding.rvVideosPhotos.adapter = collectionAdapter
    }


    private fun getCollectionFromPexels(shouldShowProgressBar: Boolean) {

        if (shouldShowProgressBar) binding.progressBar.visibility = View.VISIBLE
        viewModelMain.getCollectionByIdPexels(
            collectionCategory,
            Constants.AUTHORIZATION_KEY,
            true,
            1,
            80
        )

    }


    private fun refreshData() {
        collectionAdapter?.notifyDataSetChanged()
        collectionAdapter?.setLoaded()
        if (list.size > 0) {
            binding.llError.visibility = View.GONE
            binding.rvVideosPhotos.visibility = View.VISIBLE
        } else {
            binding.llError.visibility = View.VISIBLE
            binding.rvVideosPhotos.visibility = View.GONE
        }
    }
}