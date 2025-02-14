package com.techiq.wallpaperwonders.design.videos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.adapters.VideosAdapter
import com.techiq.wallpaperwonders.base.BaseFragment
import com.techiq.wallpaperwonders.databinding.FragmentVideosBinding
import com.techiq.wallpaperwonders.design.fullscreen.FullScreenViewActivity
import com.techiq.wallpaperwonders.design.main.MainActivity
import com.techiq.wallpaperwonders.design.main.MainViewModel
import com.techiq.wallpaperwonders.interfaces.LoadMoreListener
import com.techiq.wallpaperwonders.interfaces.OnItemClickedListener
import com.techiq.wallpaperwonders.model.response.pexels.videos.PexelsVideosResponse
import com.techiq.wallpaperwonders.model.response.pexels.videos.Video
import com.techiq.wallpaperwonders.service.Status
import com.techiq.wallpaperwonders.utils.Constant
import com.techiq.wallpaperwonders.utils.Constants
import com.techiq.wallpaperwonders.utils.Constants.AUTHORIZATION_KEY
import com.techiq.wallpaperwonders.utils.Constants.ORIENTATION
import com.techiq.wallpaperwonders.utils.Constants.SIZE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideosFragment : BaseFragment() {
    lateinit var binding: FragmentVideosBinding
    private val viewModelMain by viewModels<MainViewModel>()
    var videoCategory: String? = null
    var poweredBy = 0
    var pageNumber = 1
    var perPage = 100
    var list = ArrayList<Any?>()
    var videosAdapter: VideosAdapter? = null
    var index = 21
    var isLast = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentVideosBinding.inflate(inflater, container, false)
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
            if (arguments?.containsKey(Constants.KEY_VIDEO_CATEGORY) == true) {
                videoCategory = requireArguments().getString(Constants.KEY_VIDEO_CATEGORY, "")
            }
        }

    private fun prepareLayout() {
        poweredBy = sharedPref.getInt(Constants.POWERED_BY)
        setUpRecyclerView()
        try {
            if (poweredBy == Constants.POWERED_BY_PEXELS) {
                getVideosFromPexels(true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.llError.setOnClickListener {
            binding.llError.visibility = View.GONE
            resetQueryParameters()
            try {
                if (poweredBy == Constants.POWERED_BY_PEXELS) {
                    getVideosFromPexels(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setObservers() {
        viewModelMain.pexelsVideosResponse.observe(viewLifecycleOwner) {
            Log.d("TAG", "Pexels Videos Response: " + it.response.toString())
            binding.progressBar.visibility = View.GONE
            when (it.localStatus) {
                Status.SUCCESS -> {
                    val response = it.response as PexelsVideosResponse
                    if (list.size != 0) {
                        list.removeAt(list.size - 1)
                        videosAdapter?.notifyItemRemoved(list.size)
                    }
                    response.videos.let { list.addAll(it) }
                    refreshData()
                    if (it.response.per_page < pageNumber * perPage) {
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
    }

    private fun resetQueryParameters() {
        pageNumber = 1
        index = 21
        list.clear()
        videosAdapter?.notifyDataSetChanged()
        isLast = false
    }

    private fun setUpRecyclerView() {
        val gridLayoutManager = GridLayoutManager(mActivity, 3)
        binding.rvVideos.layoutManager = gridLayoutManager
        videosAdapter = VideosAdapter(glideUtils, list, binding.rvVideos)
        videosAdapter?.setLoadMoreListener(object : LoadMoreListener {
            override fun onLoadMore() {
                if (!isLast && list.size != 0) {
                    list.add(null)
                    videosAdapter?.notifyItemInserted(list.size - 1)
                    try {
                        if (poweredBy == Constants.POWERED_BY_PEXELS) {
                            getVideosFromPexels(false)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
        videosAdapter?.setOnItemClickListener(object : OnItemClickedListener {
            override fun onItemClicked(position: Int) {
                val intent = Intent(mActivity, FullScreenViewActivity::class.java)
                poweredBy = sharedPref.getInt(Constants.POWERED_BY)
                if (poweredBy == Constants.POWERED_BY_PEXELS) {
                    if (list[position] is Video) {
                        val videos: Video? = list[position] as Video?
                        intent.putExtra(
                            Constants.KEY_VIDEO_LINK,
                            videos?.video_files?.filter { it.quality == "hd" }?.get(0)?.link
                        )
                        intent.putExtra(Constants.KEY_VIDEO_ID, videos?.id.toString())

                    } else Log.e("TAG", "onItemClicked pexels: ${list[position]}")
                }
                startActivity(intent)
            }

            override fun onSubItemClicked(position: Int, subPosition: Int) {
            }
        })
        binding.rvVideos.adapter = videosAdapter
    }


    private fun getVideosFromPexels(shouldShowProgressBar: Boolean) {
        try {
            if (!Constant.isWifiConnectedWithInternet(requireContext())) {
                if (shouldShowProgressBar) binding.progressBar.visibility = View.VISIBLE
                viewModelMain.getVideosPexels(
                    AUTHORIZATION_KEY,
                    videoCategory!!,
                    ORIENTATION,
                    SIZE,
                    true,
                    pageNumber,
                    perPage
                )
            } else {
                if (Constant.isInternetAvailable(requireContext())) {
                    if (shouldShowProgressBar) binding.progressBar.visibility = View.VISIBLE
                    viewModelMain.getVideosPexels(
                        AUTHORIZATION_KEY,
                        videoCategory!!,
                        ORIENTATION,
                        SIZE,
                        true,
                        pageNumber,
                        perPage
                    )
                } else Constant.smallToastWithContext(
                    requireContext(),
                    getString(R.string.no_internet_connection)
                )
            }
        } catch (e: Exception) {
//            Constant.smallToastWithContext(requireContext(), "Something Went Wrong!")
            Log.e("TAG", "getVideosFromPexels: ${e.message}")
        }

    }

    private fun refreshData() {
        videosAdapter?.notifyDataSetChanged()
        videosAdapter?.setLoaded()
        if (list.size > 0) {
            binding.llError.visibility = View.GONE
            binding.rvVideos.visibility = View.VISIBLE
        } else {
            binding.llError.visibility = View.VISIBLE
            binding.rvVideos.visibility = View.GONE
        }
    }
}