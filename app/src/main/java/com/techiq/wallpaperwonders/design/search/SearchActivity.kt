package com.techiq.wallpaperwonders.design.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.adapters.ImagesAdapter
import com.techiq.wallpaperwonders.base.ActivityBase
import com.techiq.wallpaperwonders.databinding.ActivitySearchBinding
import com.techiq.wallpaperwonders.design.fullscreen.FullScreenViewActivity
import com.techiq.wallpaperwonders.design.main.MainViewModel
import com.techiq.wallpaperwonders.interfaces.LoadMoreListener
import com.techiq.wallpaperwonders.interfaces.OnItemClickedListener
import com.techiq.wallpaperwonders.model.response.pexels.images.PexelsImagesResponse
import com.techiq.wallpaperwonders.model.response.pexels.images.PexelsPhotos
import com.techiq.wallpaperwonders.model.response.pixabay.Hit
import com.techiq.wallpaperwonders.model.response.pixabay.PixabayImagesResponse
import com.techiq.wallpaperwonders.service.Status
import com.techiq.wallpaperwonders.utils.Constant.smallToast
import com.techiq.wallpaperwonders.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : ActivityBase(), View.OnClickListener {

    val binding: ActivitySearchBinding by binding(R.layout.activity_search)
    private val viewModelMain by viewModels<MainViewModel>()

    var pageNumber = 1
    var poweredBy = Constants.POWERED_BY_PIXABAY
    var perPage = 100
    var isLast = false
    var list = ArrayList<Any?>()
    var imagesAdapter: ImagesAdapter? = null
    private var searchString: String? = null
    private var lastQuery: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            lifecycleOwner = this@SearchActivity
            setObservers()
            viewModelMain.parentView.set(parentView)
        }
        setUpToolbar()
        prepareLayout()
        setObservers()
    }

    private fun prepareLayout() {
        setUpRecyclerView()
        binding.toolbar.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, st2art: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    binding.toolbar.ivClose.visibility = View.VISIBLE
                } else {
                    binding.toolbar.ivClose.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setUpRecyclerView() {
        val gridLayoutManager = GridLayoutManager(this, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (list[position] == null) {
                    3
                } else {
                    1
                }
            }
        }
        binding.rvImages.layoutManager = gridLayoutManager
        imagesAdapter =
            ImagesAdapter(glideUtils, list, binding.rvImages, this)
        imagesAdapter?.setLoadMoreListener(object : LoadMoreListener {
            override fun onLoadMore() {
                if (!isLast && list.size != 0) {
                    list.add(null)
                    imagesAdapter?.notifyItemInserted(list.size - 1)
                    try {
                        if (poweredBy == Constants.POWERED_BY_PEXELS) {
                            getImagesFromPexels(false)
                        } else {
                            getImagesFromPixabay(false)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
        imagesAdapter?.setOnItemClickListener(object : OnItemClickedListener {
            override fun onItemClicked(position: Int) {
                val intent = Intent(this@SearchActivity, FullScreenViewActivity::class.java)
                poweredBy = sharedPref.getInt(Constants.POWERED_BY)
                if (poweredBy == Constants.POWERED_BY_PEXELS) {
                    if (list[position] is PexelsPhotos) {
                        val photos: PexelsPhotos? = list[position] as PexelsPhotos?
                        intent.putExtra(Constants.KEY_PREVIEW_IMAGE_LINK, photos?.src?.original)
                        intent.putExtra(Constants.KEY_LARGE_IMAGE_LINK, photos?.src?.original)
                        intent.putExtra(Constants.KEY_IMAGE_ID, photos?.id.toString())

                    } else Log.e("TAG", "onItemClicked pexels: ${list[position]}")
                } else {
                    if (list[position] is Hit) {
                        val hit: Hit? = list[position] as Hit?
                        intent.putExtra(Constants.KEY_PREVIEW_IMAGE_LINK, hit?.webformatURL)
                        intent.putExtra(Constants.KEY_LARGE_IMAGE_LINK, hit?.largeImageURL)
                        intent.putExtra(Constants.KEY_IMAGE_ID, hit?.id.toString())

                    } else Log.e("TAG", "onItemClicked pexelsModels: ${list[position]}")
                }
                startActivity(intent)
            }

            override fun onSubItemClicked(position: Int, subPosition: Int) {

            }
        })
        binding.rvImages.adapter = imagesAdapter
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back)
        binding.toolbar.ivClose.setOnClickListener(this)
        binding.toolbar.etSearch.requestFocus()
        showSoftKeyboard(binding.toolbar.etSearch)
        binding.toolbar.etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchString = v.text.toString().trim { it <= ' ' }
                if (searchString!!.isNotEmpty() && !searchString.equals(
                        lastQuery,
                        ignoreCase = true
                    )
                ) {
                    lastQuery = searchString
                    try {
                        pageNumber = 1
                        list.clear()
                        isLast = false
                        poweredBy = sharedPref.getInt(Constants.POWERED_BY)
                        if (poweredBy == Constants.POWERED_BY_PEXELS) {
                            getImagesFromPexels(true)
                        } else {
                            getImagesFromPixabay(true)
                        }
                        hideSoftKeyboard()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    hideSoftKeyboard()
                }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivClose -> {
                binding.toolbar.etSearch.setText("")
                showSoftKeyboard(binding.toolbar.etSearch)
            }
        }
    }

    private fun getImagesFromPexels(shouldShowProgressBar: Boolean) {
        if (shouldShowProgressBar) binding.progressBar.visibility = View.VISIBLE
        viewModelMain.getImagesPexels(
            Constants.AUTHORIZATION_KEY,
            searchString,
            "photo",
            true,
            pageNumber,
            perPage
        )
    }

    private fun getImagesFromPixabay(shouldShowProgressBar: Boolean) {
        if (shouldShowProgressBar) binding.progressBar.visibility = View.VISIBLE
        viewModelMain.getPixabayImages(
            "38431804-1c9201a3537e9066943244f83",
            "",
            true,
            pageNumber,
            "vertical",
            perPage,
            "photo",
            searchString,
            true,
            ""
        )
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

    private fun setObservers() {
        viewModelMain.pixabayImagesResponse.observe(this) {
            Log.d("TAG", "PixabayImagesResponse: " + it.response.toString())
            binding.progressBar.visibility = View.GONE
            when (it.localStatus) {
                Status.SUCCESS -> {
                    val response = it.response as PixabayImagesResponse
                    if (list.size != 0) {
                        list.removeAt(list.size - 1)
                        imagesAdapter?.notifyItemRemoved(list.size)
                    }
                    response.hits?.let { list.addAll(it) }
                    refreshData()
                    if (response.totalHits!! > pageNumber * perPage) {
                        pageNumber += 1
                    } else {
                        isLast = true
                    }
                    Log.e("Response: ", response.toString())
                }

                Status.ERROR -> {
                    smallToast(it.localError.toString())
                }

                else -> {
                    smallToast(it.localError.toString())
                }
            }
        }
        viewModelMain.pexelsImagesResponse.observe(this) {
            Log.d("TAG", "PixabayImagesResponse: " + it.response.toString())
            binding.progressBar.visibility = View.GONE
            when (it.localStatus) {
                Status.SUCCESS -> {
                    val response = it.response as PexelsImagesResponse
                    if (list.size != 0) {
                        list.removeAt(list.size - 1)
                        imagesAdapter?.notifyItemRemoved(list.size)
                    }
                    response.photos?.let { list.addAll(it) }
                    refreshData()
                    if (response.per_page!! < pageNumber * perPage) {
                        pageNumber += 1
                    } else {
                        isLast = true
                    }
                    Log.e("response Pexels Photos API: ", it.toString())
                }

                Status.ERROR -> {
                    smallToast(it.localError.toString())
                }

                else -> {
                    smallToast(it.localError.toString())
                }
            }
        }
    }
}
