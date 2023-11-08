package com.techiq.wallpaperwonders.design.main

import android.util.Log
import android.view.View
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techiq.wallpaperwonders.repository.main.MainRepository
import com.techiq.wallpaperwonders.service.ApiState
import com.techiq.wallpaperwonders.utils.Constant
import com.techiq.wallpaperwonders.utils.PrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: MainRepository,
    private val propertyChangeRegistry: PropertyChangeRegistry,
    @Named(Constant.SHARED_COMMON) private val sharedPreferences: PrefUtils,
) : ViewModel(), Observable {
    val parentView: ObservableField<View> = ObservableField()
    private val _pixabayImagesResponse: MutableLiveData<ApiState> = MutableLiveData()
    private val _pexelsCollectionResponse: MutableLiveData<ApiState> = MutableLiveData()
    val pixabayImagesResponse: LiveData<ApiState>
        get() {
            return _pixabayImagesResponse
        }
    val pexelsCollectionResponse: LiveData<ApiState>
        get() {
            return _pexelsCollectionResponse
        }

    fun getPixabayImages(
        key: String?,
        category: String?,
        pretty: Boolean?,
        page: Int?,
        orientation: String?,
        per_page: Int?,
        image_type: String?,
        q: String?,
        safesearch: Boolean?,
        order: String?,
    ) {
        viewModelScope.launch {
            Constant.showProgress(parentView.get()!!.context)
            val response = repository.getImagesPixabay(
                parent = parentView.get(),
                isSuccessMessageShow = false,
                isFailureMessageShow = true,
                key = key,
                category = category,
                pretty = pretty,
                page = page,
                orientation = orientation,
                per_page = per_page,
                image_type = image_type,
                q = q,
                safesearch = safesearch,
                order = order
            )
            launch {
                Log.d(
                    "TAG",
                    "getApiStateResponseStatus: " + "inside viewModel" + response.response.toString()
                )
                Constant.dismissProgress(parentView.get()!!.context)
                _pixabayImagesResponse.postValue(response)
            }
        }
    }

    fun getCollectionPexels(authKey: String?, pretty: Boolean?, page: Int?, per_page: Int?) {
        viewModelScope.async {
            val response = repository.getCollectionPexels(
                parent = parentView.get(),
                isSuccessMessageShow = false,
                isFailureMessageShow = true,
                authKey = authKey, pretty = pretty, page = page, per_page = per_page
            )
            launch {
                Log.d(
                    "TAG",
                    "getApiStateResponseStatus: " + "inside viewModel" + response.response.toString()
                )
                Constant.dismissProgress(parentView.get()!!.context)
                _pexelsCollectionResponse.postValue(response)
            }
        }
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

}