package com.techiq.wallpaperwonders.design.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.adapters.NavigationDrawerAdapter
import com.techiq.wallpaperwonders.base.ActivityBase
import com.techiq.wallpaperwonders.collections.CollectionsFragment
import com.techiq.wallpaperwonders.databinding.ActivityMainBinding
import com.techiq.wallpaperwonders.design.downloaded.DownloadsFragment
import com.techiq.wallpaperwonders.design.videos.VideosFragment
import com.techiq.wallpaperwonders.interfaces.OnItemClickedListener
import com.techiq.wallpaperwonders.model.PexelsModel
import com.techiq.wallpaperwonders.model.response.pexels.collection.CollectionItem
import com.techiq.wallpaperwonders.model.response.pexels.collection.CollectionResponse
import com.techiq.wallpaperwonders.service.Status
import com.techiq.wallpaperwonders.utils.Constant.longToast
import com.techiq.wallpaperwonders.utils.Constant.smallToast
import com.techiq.wallpaperwonders.utils.Constants
import com.techiq.wallpaperwonders.utils.Constants.POWERED_BY
import com.techiq.wallpaperwonders.utils.Constants.POWERED_BY_PEXELS
import com.techiq.wallpaperwonders.utils.Constants.POWERED_BY_PIXABAY
import com.techiq.wallpaperwonders.utils.Constants.PexelsPhotos.getPexelsPhotos
import com.techiq.wallpaperwonders.utils.Constants.PexelsPhotos.getPexelsVideos
import com.techiq.wallpaperwonders.utils.Constants.Pixabay.getPixabayList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ActivityBase() {

    val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val viewModelMain by viewModels<MainViewModel>()
    private var adapter: NavigationDrawerAdapter? = null
    var listNavigationItems: MutableList<String> = ArrayList()
    var pexelsList: MutableList<PexelsModel> = arrayListOf()
    var list = ArrayList<CollectionItem>()
    private var doubleBackToExitPressedOnce = false
    var selectedCategoryPosition = 1
    var poweredBy = POWERED_BY_PIXABAY
    private var collectionList: List<String> = listOf()
    private var collectionIdList: List<String> = listOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            lifecycleOwner = this@MainActivity
            preference = sharedPref
            setObservers()
            viewModelMain.parentView.set(parentView)
        }
        getCollectionList()
        setSupportActionBar(binding.toolbar.toolbar)
        setUpNavigationDrawer()
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        binding.navigationView.navHeaderMain.changePoweredBy.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Choose Your Content From Following Options:")
                .setSingleChoiceItems(
                    arrayOf("Pixabay", "Pexels"), sharedPref.getInt(POWERED_BY)
                ) { dialog, item ->
                    when (item) {
                        0 -> {
                            dialog.dismiss()
                            sharedPref.putInt(POWERED_BY, POWERED_BY_PIXABAY)
                            setUpNavigationDrawer()
                            binding.drawerLayout.closeDrawer(GravityCompat.START)
                        }

                        1 -> {
                            dialog.dismiss()
                            sharedPref.putInt(POWERED_BY, POWERED_BY_PEXELS)
                            setUpNavigationDrawer()
                            binding.drawerLayout.closeDrawer(GravityCompat.START)
                        }
                    }
                }
            val dialog: AlertDialog = builder.create()
            dialog.show()

        }
        binding.navigationView.navHeaderMain.ivShareApp.setOnClickListener {
            try {
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                var sAux =
                    "\nHi, I found this amazing app for HD Wallpapers, Give it a try its free and easy to use\n\n"
                sAux =
                    """${sAux + "https://play.google.com/store/apps/details?id=" + applicationContext.packageName} """
                i.putExtra(Intent.EXTRA_TEXT, sAux)
                startActivity(Intent.createChooser(i, "choose one"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setUpNavigationDrawer() {
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        poweredBy = sharedPref.getInt(POWERED_BY)

        if (poweredBy == POWERED_BY_PEXELS) {
            binding.navigationView.navHeaderMain.tvPoweredBy.text = getString(R.string.powered_by)
            binding.navigationView.navHeaderMain.tvPoweredByLogo.setImageResource(R.drawable.img_pexels_logo)
            binding.navigationView.navHeaderMain.tvPoweredByLogo.visibility = View.VISIBLE
            pexelsList.clear()
            pexelsList.add(PexelsModel("Downloads"))
            pexelsList.add(PexelsModel("All Photos", getPexelsPhotos()))
            pexelsList.add(PexelsModel("All Videos", getPexelsVideos()))
            pexelsList.add(PexelsModel("All Collection", collectionList, collectionIdList))

            binding.toolbar.tvTitle.text = pexelsList[1].menu
            adapter = NavigationDrawerAdapter(this, pexelsList)

        } else {
            binding.navigationView.navHeaderMain.tvPoweredBy.text = getString(R.string.powered_by)
            binding.navigationView.navHeaderMain.tvPoweredByLogo.setImageResource(R.drawable.ic_pixabay_logo)

            listNavigationItems = getPixabayList()
            binding.toolbar.tvTitle.text = listNavigationItems[1]
            adapter = NavigationDrawerAdapter(this, listNavigationItems)
        }

        adapter!!.setOnItemClickListener(object : OnItemClickedListener {
            override fun onItemClicked(position: Int) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                if (poweredBy == POWERED_BY_PIXABAY) {
                    binding.toolbar.tvTitle.text = listNavigationItems[position]
                    pushMainFragment(position)
                    selectedCategoryPosition = position
                } else {
                    binding.toolbar.tvTitle.text = pexelsList[position].menu
                    pushMainFragment(position)
                    selectedCategoryPosition = position
                }
            }

            override fun onSubItemClicked(position: Int, subPosition: Int) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                binding.toolbar.tvTitle.text = pexelsList[position].subMenu?.get(subPosition)!!
                pushMainFragment(position, subPosition)
                selectedCategoryPosition = position
            }
        })

        val layoutManager = LinearLayoutManager(this)
        binding.navigationView.rvNavigation.layoutManager = layoutManager
        binding.navigationView.rvNavigation.adapter = adapter
        pushMainFragment(1)
    }

    private fun getCollectionList() {
        try {
            viewModelMain.getCollectionPexels(Constants.AUTHORIZATION_KEY, true, 1, 15)
        }catch (e: Exception){
//            smallToast("Something Went Wrong!")
            Log.e("TAG", "getCollectionFromPexels: ${e.message}" )
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            //showing dialog and then closing the application..
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                try {
                    if (supportFragmentManager.backStackEntryCount <= 1) {
                        if (doubleBackToExitPressedOnce) {
                            finish()
                            return
                        }
                        doubleBackToExitPressedOnce = true
                        longToast(getString(R.string.prompt_exit))
                        Handler(Looper.getMainLooper()).postDelayed({
                            doubleBackToExitPressedOnce = false
                        }, 2000)
                    } else {
                        supportFragmentManager.popBackStack()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }

    private fun setObservers() {
        viewModelMain.pexelsCollectionResponse.observe(this) {
            Log.d("TAG", "PixabayImagesResponse: " + it.response.toString())
            when (it.localStatus) {
                Status.SUCCESS -> {
                    val response = it.response as CollectionResponse
//                    smallToast(it.response.toString())
                    Log.e("TAG", "setObservers: Collections ${it.response}")
                    list = response.collections as ArrayList<CollectionItem>
                    collectionList = list.map { item -> item.title }
                    collectionIdList = list.map { item -> item.id }
                    Log.e("TAG", "setObservers:$collectionList ")
                    Log.e("TAG", "setObservers: $collectionIdList")
                    setUpNavigationDrawer()
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

    private fun pushMainFragment(position: Int, subPosition: Int? = null) {
        val fragment = MainFragment()
        val videoFragment = VideosFragment()
        val collectionsFragment = CollectionsFragment()
        val bundle = Bundle()

        if (poweredBy == POWERED_BY_PIXABAY) {
            if (position == 0) {
                pushFragment(DownloadsFragment(), false)
            } else if (listNavigationItems[position].equals("all", ignoreCase = true)) {
                bundle.putString(
                    Constants.KEY_IMAGE_CATEGORY,
                    ""
                )
                fragment.arguments = bundle
                pushFragment(fragment, false)
            } else {
                bundle.putString(Constants.KEY_IMAGE_CATEGORY, listNavigationItems[position])
                fragment.arguments = bundle
                pushFragment(fragment, false)
            }
        } else {
            when {
                position == 0 -> {
                    pushFragment(DownloadsFragment(), false)
                }

                position == 2 -> {
//                    for videos
                    bundle.putString(
                        Constants.KEY_VIDEO_CATEGORY,
                        pexelsList[position].subMenu!![subPosition!!]
                    )
                    videoFragment.arguments = bundle
                    pushFragment(videoFragment, false)
                }

                position == 3 -> {
//                    for collections
                    bundle.putString(
                        Constants.KEY_COLLECTION_CATEGORY,
                        pexelsList[position].subId!![subPosition!!]
                    )
                    collectionsFragment.arguments = bundle
                    pushFragment(collectionsFragment, false)
                }

                subPosition == null -> {
                    bundle.putString(
                        Constants.KEY_IMAGE_CATEGORY, pexelsList[position].menu
                    )
                    fragment.arguments = bundle
                    pushFragment(fragment, false)
                }

                else -> {
                    bundle.putString(
                        Constants.KEY_IMAGE_CATEGORY,
                        pexelsList[position].subMenu!![subPosition]
                    )
                    fragment.arguments = bundle
                    pushFragment(fragment, false)
                }
            }
        }
    }
}