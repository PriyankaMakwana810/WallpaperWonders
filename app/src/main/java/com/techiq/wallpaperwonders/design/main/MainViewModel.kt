package com.techiq.wallpaperwonders.design.main

import android.util.Log
import android.view.View
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techiq.wallpaperwonders.repository.main.MainRepository
import com.techiq.wallpaperwonders.service.ApiState
import com.techiq.wallpaperwonders.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: MainRepository,
) : ViewModel(), Observable {
    val parentView: ObservableField<View> = ObservableField()
    private val _pixabayImagesResponse: MutableLiveData<ApiState> = MutableLiveData()
    private val _pexelsCollectionResponse: MutableLiveData<ApiState> = MutableLiveData()
    private val _pexelsCollectionByIdResponse: MutableLiveData<ApiState> = MutableLiveData()
    private val _pexelsImagesResponse: MutableLiveData<ApiState> = MutableLiveData()
    private val _pexelsVideosResponse: MutableLiveData<ApiState> = MutableLiveData()
    val pixabayImagesResponse: LiveData<ApiState>
        get() {
            return _pixabayImagesResponse
        }
    val pexelsCollectionResponse: LiveData<ApiState>
        get() {
            return _pexelsCollectionResponse
        }
    val pexelsCollectionByIdResponse: LiveData<ApiState>
        get() {
            return _pexelsCollectionByIdResponse
        }
    val pexelsImagesResponse: LiveData<ApiState>
        get() {
            return _pexelsImagesResponse
        }
    val pexelsVideosResponse: LiveData<ApiState>
        get() {
            return _pexelsVideosResponse
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
            Log.e("TAG", "getPixabayImages: API CALL")

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
                _pixabayImagesResponse.postValue(response)
            }
        }
    }

    fun getImagesPexels(
        authKey: String?,
        query: String?,
        imageType: String?,
        pretty: Boolean?,
        page: Int?,
        per_page: Int?,
    ) {
        viewModelScope.launch {
            val response = repository.getImagesPexels(
                parent = parentView.get(),
                isSuccessMessageShow = false,
                isFailureMessageShow = true,
                authKey = authKey,
                query = query,
                imageType = imageType,
                pretty = pretty,
                page = page,
                per_page = per_page
            )
            launch {
                Log.d(
                    "TAG",
                    "getApiStateResponseStatus: " + "inside viewModel" + response.response.toString()
                )
                Constant.dismissProgress(parentView.get()!!.context)
                _pexelsImagesResponse.postValue(response)
            }
        }
    }

    fun getVideosPexels(
        authKey: String?,
        query: String?,
        orientation: String?,
        size: String,
        pretty: Boolean?,
        page: Int?,
        per_page: Int?,
    ) {
        viewModelScope.launch {
            val response = repository.getVideosPexels(
                parent = parentView.get(),
                isSuccessMessageShow = false,
                isFailureMessageShow = true,
                authKey = authKey,
                query = query,
                orientation = orientation,
                size = size,
                pretty = pretty,
                page = page,
                per_page = per_page
            )
            launch {
                Log.d(
                    "TAG",
                    "getApiStateResponseStatus: " + "inside viewModel" + response.response.toString()
                )
                Constant.dismissProgress(parentView.get()!!.context)
                _pexelsVideosResponse.postValue(response)
            }
        }
    }

    fun getCollectionPexels(authKey: String?, pretty: Boolean?, page: Int?, per_page: Int?) {
        viewModelScope.launch {
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

    fun getCollectionByIdPexels(
        collectionId: String?,
        authKey: String?,
        pretty: Boolean?,
        page: Int?,
        per_page: Int?,
    ) {
        viewModelScope.launch {
            val response = repository.getCollectionByIdPexels(
                parent = parentView.get(),
                isSuccessMessageShow = false,
                isFailureMessageShow = true,
                collectionId = collectionId,
                authKey = authKey,
                pretty = pretty,
                page = page,
                per_page = per_page
            )
            launch {
                Log.d(
                    "TAG",
                    "getApiStateResponseStatus: " + "inside viewModel" + response.response.toString()
                )
                Constant.dismissProgress(parentView.get()!!.context)
                _pexelsCollectionByIdResponse.postValue(response)
            }
        }
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

}