package com.techiq.wallpaperwonders.design.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.adapters.NavigationDrawerAdapter
import com.techiq.wallpaperwonders.base.ActivityBase
import com.techiq.wallpaperwonders.databinding.ActivityMainBinding
import com.techiq.wallpaperwonders.model.PexelsModel
import com.techiq.wallpaperwonders.model.response.collection.CollectionItem
import com.techiq.wallpaperwonders.utils.Constants.Pixabay.getPixabayList

class MainActivity : ActivityBase() {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val viewModelMain by viewModels<MainViewModel>()
    var adapter: NavigationDrawerAdapter? = null

    var listNavigationItems: MutableList<String> = ArrayList()
    var pexelsModelsList: MutableList<PexelsModel> = arrayListOf()
    private var doubleBackToExitPressedOnce = false
    var list = ArrayList<CollectionItem>()
    var selectedCategoryPosition = 1

    //    private val viewModel by lazy { PoweredByViewModel(this) }
    var poweredBy = 0
    private var collectionList: List<String> = listOf()
    private var collectionIdList: List<String> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            lifecycleOwner = this@MainActivity
            preference = sharedPref
        }

        setSupportActionBar(binding.toolbar.toolbar)
        setUpNavigationDrawer()
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
        binding.navigationView.navHeaderMain.tvPoweredBy.text = getString(R.string.powered_by)
        binding.navigationView.navHeaderMain.tvPoweredByLogo.setImageResource(R.drawable.ic_pixabay_logo)

        listNavigationItems = getPixabayList()
        binding.toolbar.tvTitle.text = listNavigationItems[1]
        adapter = NavigationDrawerAdapter(this, listNavigationItems)
        val layoutManager = LinearLayoutManager(this)
        binding.navigationView.rvNavigation.layoutManager = layoutManager
        binding.navigationView.rvNavigation.adapter = adapter

    }
}