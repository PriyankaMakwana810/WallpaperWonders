package com.techiq.wallpaperwonders.design.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
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
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainFragment : BaseFragment() {
    var binding: FragmentMainBinding? = null
    private val viewModelMain by viewModels<MainViewModel>()
    private lateinit var pixabayImagesResponse: PixabayImagesResponse
    var imageCategory: String? = null
    var poweredBy = 0
    var pageNumber = 1
    var perPage = 100
    var list = ArrayList<Any?>()
    var imagesAdapter: ImagesAdapter? = null
    var isLast = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        dataFromBundle
        if (binding == null) {
            binding =
                inflater.let {
                    DataBindingUtil.inflate(
                        it,
                        R.layout.fragment_main,
                        container,
                        false
                    )
                }
        }
        binding?.apply {
            viewModelMain.parentView.set((activity as MainActivity).binding.parentView)
        }
        setObservers()
        prepareLayout()
        return binding!!.root
    }

    private fun prepareLayout() {
        setUpRecyclerView()
        try {
            getImagesFromPixabay(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding?.llError?.setOnClickListener {
            binding?.llError!!.visibility = View.GONE
            resetQueryParameters()
            try {
                getImagesFromPixabay(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
    }

    private fun setObservers() {
        viewModelMain.pixabayImagesResponse.observe(viewLifecycleOwner) {
            Log.d("TAG", "PixabayImagesResponse: " + it.response.toString())
            when (it.localStatus) {
                Status.SUCCESS -> {
                    val response = it.response as PixabayImagesResponse
                    pixabayImagesResponse = it.response
                    smallToastWithContext(requireContext(), it.response.toString())
                    if (response != null) {
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
                        Log.e("onAuthenticateButtonClick: ", it.toString())

                    } else {
                        smallToastWithContext(requireContext(), getString(R.string.wrongCredential))
                    }
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
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val spanCount = 1
                return spanCount
            }
        }
        binding?.rvImages?.layoutManager = gridLayoutManager
        imagesAdapter = mContext.let {
            ImagesAdapter(
                glideUtils!!,
                list,
                binding!!.rvImages
            )
        }
        imagesAdapter?.setLoadMoreListener(object : LoadMoreListener {
            override fun onLoadMore() {
                if (!isLast && list.size != 0) {
                    list.add(null)
                    imagesAdapter?.notifyItemInserted(list.size - 1)
                    try {
                        getImagesFromPixabay(false)
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
        binding?.rvImages?.adapter = imagesAdapter
    }

    private val dataFromBundle: Unit
        get() {
            if (arguments?.containsKey(Constants.KEY_IMAGE_CATEGORY) == true) {
                imageCategory = requireArguments().getString(Constants.KEY_IMAGE_CATEGORY, "")
            }
        }

    private fun getImagesFromPixabay(shouldShowProgressBar: Boolean) {

        viewModelMain.getPixabayImages(
            "38431804-1c9201a3537e9066943244f83",
            imageCategory!!.lowercase(
                Locale.getDefault()
            ),
            true,
            pageNumber,
            "vertical",
            perPage,
            "photo",
            "",
            true,
            "trending"
        )

    }

    private fun refreshData() {
        imagesAdapter?.notifyDataSetChanged()
        imagesAdapter?.setLoaded()
        if (list.size > 0) {
            binding?.llError?.visibility = View.GONE
            binding?.rvImages?.visibility = View.VISIBLE
        } else {
            binding?.llError?.visibility = View.VISIBLE
            binding?.rvImages?.visibility = View.GONE
        }
    }
}