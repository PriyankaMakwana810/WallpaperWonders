package com.techiq.wallpaperwonders.service

import com.techiq.wallpaperwonders.model.request.LoginRequest
import com.techiq.wallpaperwonders.model.request.RegisterationRequest
import com.techiq.wallpaperwonders.model.response.Register.RegisterResponse
import com.techiq.wallpaperwonders.model.response.SignIn.SigninResponse
import com.techiq.wallpaperwonders.model.response.pexels.collection.CollectionByIdResponse
import com.techiq.wallpaperwonders.model.response.pexels.collection.CollectionResponse
import com.techiq.wallpaperwonders.model.response.pexels.images.PexelsImagesResponse
import com.techiq.wallpaperwonders.model.response.pexels.videos.PexelsVideosResponse
import com.techiq.wallpaperwonders.model.response.pixabay.PixabayImagesResponse
import com.techiq.wallpaperwonders.utils.Constant.SERVICE_WITH_GSON_SIGNIN
import com.techiq.wallpaperwonders.utils.Constant.SERVICE_WITH_PEXELS
import com.techiq.wallpaperwonders.utils.Constant.SERVICE_WITH_PIXABAY
import com.techiq.wallpaperwonders.utils.Constants
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class ApiClient @Inject constructor(
    @Named(SERVICE_WITH_GSON_SIGNIN) private val apiInterface: ApiInterface,
    @Named(SERVICE_WITH_PIXABAY) private val pixabayInterface: ApiInterface,
    @Named(SERVICE_WITH_PEXELS) private val pexelsInterface: ApiInterface,

    ) {
    suspend fun signin(loginRequest: LoginRequest): Response<SigninResponse> {
        return apiInterface.signin(loginRequest)
    }

    suspend fun register(registerationRequest: RegisterationRequest): Response<RegisterResponse> {
        return apiInterface.register(registerationRequest = registerationRequest)
    }
    suspend fun getImagesPixabay(
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
    ): Response<PixabayImagesResponse> {
        return pixabayInterface.getImagesPixabay(
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
    }
    suspend fun getImagesPexels(authKey: String?, query: String?,imageType:String?,pretty: Boolean?,page: Int?,per_page: Int?): Response<PexelsImagesResponse>{
        return pexelsInterface.getImagesPexels(authKey,query,imageType,pretty,page,per_page)
    }
    suspend fun getVideosPexels(authKey: String? = Constants.AUTHORIZATION_KEY,query: String?,orientation: String?,size: String,pretty: Boolean?,page: Int?,per_page: Int?): Response<PexelsVideosResponse>{
        return pexelsInterface.getVideosPexels(authKey,query,orientation,size,pretty,page,per_page)
    }
    suspend fun getCollectionByIdPexels(collectionId: String?,authKey: String? = Constants.AUTHORIZATION_KEY,pretty: Boolean?,page: Int?,per_page: Int?): Response<CollectionByIdResponse>{
        return pexelsInterface.getCollectionByIdPexels(collectionId, authKey, pretty, page, per_page)
    }
    suspend fun getCollectionPexels(
        authKey: String? = Constants.AUTHORIZATION_KEY,
        pretty: Boolean?,
        page: Int?,
        per_page: Int?,
    ): Response<CollectionResponse> {
        return pexelsInterface.getCollectionPexels(authKey, pretty, page, per_page)
    }

}